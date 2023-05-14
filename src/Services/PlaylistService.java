package Services;

import Entities.BaseEntity;
import Entities.User.Playlist;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PlaylistService extends Service<Playlist>{
    private static PlaylistService instance = null;
    public static PlaylistService getInstance() {
        if (PlaylistService.instance == null) {
            PlaylistService.instance = new PlaylistService();
        }

        return PlaylistService.instance;
    }

    private Playlist openPlaylist;

    public PlaylistService() { openPlaylist = null; }

    public void openPlaylist(Integer id) {
        this.openPlaylist = this.get(id);
        System.out.println("Opened playlist with id = " + id + ".");
    }

    public void closePlaylist() {
        this.openPlaylist = null;
    }

    public List<Playlist> getAllFromUser(Integer ownerID) {
        String sqlGet = "SELECT * FROM PLAYLIST WHERE ownerID = " + ownerID;
        ResultSet res;

        try {
            Statement getStmt = Service.connection.createStatement();
            res = getStmt.executeQuery(sqlGet);
        } catch (SQLException sqlE) {
            System.out.println("Error getting playlists belonging to user with id = " + ownerID + "!");
            return null;
        }

        List<Playlist> playlists = new ArrayList<>();
        try {
            while(true) {
                try {
                    if (!res.next()) {
                        break;
                    }
                } catch (SQLException sqlE) {
                    System.out.println("Error retrieving next playlist for user with id = " + ownerID + "!");
                    throw sqlE;
                }

                Playlist playlist = (Playlist)BaseEntity.getFromSelect(res);
                this.selectVideos(playlist);

                playlists.add(playlist);
            }
        } catch (Exception e) {
            System.out.println("Error getting data of playlists belonging to user with id = " + ownerID + "!");
            System.out.println(e.getMessage());
            return null;
        }

        return playlists;
    }

    public int add(Scanner sc, Integer ownerID) {
        Playlist playlist = new Playlist(ownerID);
        playlist.read(sc);

        try {
            super.add(playlist);
        } catch (Exception e) {
            System.out.println("Error adding new playlist to database.");
            System.out.println(e.getMessage());
            return 1;
        }

        return 0;
    }

    public Playlist get(Integer id) {
        Playlist playlist = super.get(id);
        this.selectVideos(playlist);

        return playlist;
    }

    private void selectVideos(Playlist playlist) {
        String sqlGetVideos = "SELECT * FROM PLAYLISTCONTENT pc JOIN POST p ON p.id = pc.postID WHERE pc.playlistID = " + playlist.getID();
        ResultSet videos;

        try {
            Statement videosStmt = Service.connection.createStatement();
            videos = videosStmt.executeQuery(sqlGetVideos);
        } catch (SQLException sqlE) {
            System.out.println("Error getting list of videos from database for playlist with id = " + playlist.getID() + "!");
            System.out.println(sqlE.getMessage());
            return;
        }

        try {
            playlist.getVideosFromSelect(videos);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
