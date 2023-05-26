package entities.user;

import entities.BaseEntity;
import entities.post.Video;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Playlist extends BaseEntity {
    // The name of the playlist.
    private String name;

    // The id of the user who owns the playlist.
    private Integer ownerID;

    private enum Order{NORMAL, SHUFFLE};
    // The ordering of the videos.
    private Order parseOrder;

    // The list of videos in the playlist.
    private List<Video> videos;

    public Playlist() {
        this.name = "";
        this.ownerID = null;
        this.parseOrder = Order.NORMAL;
    }

    public Playlist(Integer ownerID) {
        this.name = "";
        this.ownerID = ownerID;
        this.parseOrder = Order.NORMAL;
    }

    public Playlist(String name, Integer ownerID) {
        this.name = name;
        this.ownerID = ownerID;
        this.parseOrder = Order.NORMAL;
    }

    // Reads the playlist data using the scanner.
    public void read(Scanner sc) {
        System.out.print("Input playlist name: ");
        this.name = sc.nextLine();
    }

    @Override
    public String toString() {
        String ret = "Playlist \"" + this.name + "\"(id = " + this.id + "):\n";
        if (this.videos == null || this.videos.isEmpty()) {
            ret += "This playlist is empty.\n";
        } else {
            ret += this.videos.get(0).toString();
        }
        return ret;
    }

    // Returns the name of the playlist.
    public String getName() {
        return this.name;
    }

    // Sets the name of the playlist.
    public void setName(String newName) {
        this.name = newName;
    }

    // Returns the id of the owner of the playlist.
    public Integer getOwnerID() {return this.ownerID;}

    // Returns the list of videos.
    public List<Video> getVideos() {
        return this.videos;
    }

    // Switches the ordering of the playlist.
    public void switchOrdering() {
        this.parseOrder = this.parseOrder == Order.NORMAL ? Order.SHUFFLE : Order.NORMAL;
    }

    @Override
    public String getColumns() {
        return super.getColumns() + "name, ownerID, parseOrder";
    }

    @Override
    public String toSQLInsert(String className) {
        return super.toSQLInsert(className) + "'" + this.name + "', " + this.ownerID + ", '" + this.parseOrder.name() + "')";
    }

    @Override
    public String getSQLUpdate(String className) {
        return super.getSQLUpdate(className) + "name = '" + this.name + "', ownerID = " + this.ownerID + ", parseOrder = '" + this.parseOrder.name() + "'";
    }

    @Override
    protected void getDataFromSelect(ResultSet src) throws SQLException {
        super.getDataFromSelect(src);

        try {
            this.name = src.getString("name");
        } catch (SQLException sqlE) {
            System.out.println("Error getting name of playlist with id = " + this.id + "!");
            throw sqlE;
        }

        try {
            this.ownerID = src.getInt("ownerID");
        } catch (SQLException sqlE) {
            System.out.println("Error getting id of owner of playlist with id = " + this.id + "!");
            throw sqlE;
        }

        try {
            this.parseOrder = Order.valueOf(src.getString("parseOrder"));
        } catch (SQLException sqlE) {
            System.out.println("Error getting the parse order of playlist with id = " + this.id + "!");
            throw sqlE;
        }
    }

    // Gets the list of videos from the ResultSet object.
    public void getVideosFromSelect(ResultSet src) throws SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.videos = new ArrayList<>();

        while (true) {
            try {
                if (!src.next()) {
                    break;
                }
            } catch (SQLException sqlE) {
                System.out.println("Error retrieving next video of playlist with id = " + this.id + "!");
                throw sqlE;
            }

            try {
                this.videos.add((Video)BaseEntity.getFromSelect(src));
            } catch (Exception e) {
                System.out.println("Error getting data of video when parsing through playlist with id = " + this.id + "!");
                throw e;
            }
        }
    }

    // Gets the index of the video after the one given as a parameter using the ordering.
    public Integer getNext(Integer current) {
        if (this.parseOrder == Order.NORMAL) {
            return (current + 1) % this.videos.size();
        } else {
            return (new Random()).nextInt(this.videos.size() - 1);
        }
    }

    // Removes a video at the given position from the list.
    public void removeVideo(int position) {
        this.videos.remove(position);
    }
}
