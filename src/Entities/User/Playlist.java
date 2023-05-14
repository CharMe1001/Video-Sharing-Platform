package Entities.User;

import Entities.BaseEntity;
import Entities.Post.Video;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Playlist extends BaseEntity {
    private String name;
    private Integer ownerID;
    private enum Order{NORMAL, SHUFFLE};
    private Order parseOrder;
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

    public void read(Scanner sc) {
        System.out.print("Input playlist name: ");
        this.name = sc.nextLine();
    }

    public String toString() {
        String ret = "Playlist \"" + this.name + "\"(id = " + this.id + "):\n";
        if (this.videos == null || this.videos.isEmpty()) {
            ret += "This playlist is empty.\n";
        } else {
            ret += this.videos.get(0).toString();
        }
        return ret;
    }

    public List<Video> getVideos() {
        return this.videos;
    }

    public void switchOrdering() {
        this.parseOrder = this.parseOrder == Order.NORMAL ? Order.SHUFFLE : Order.NORMAL;
    }

    public String getColumns() {
        return super.getColumns() + "name, ownerID, parseOrder";
    }

    public String toSQLInsert(String className) {
        return super.toSQLInsert(className) + "'" + this.name + "', " + this.ownerID + ", '" + this.parseOrder.name() + "')";
    }

    public String getSQLUpdate(String className) {
        return super.getSQLUpdate(className) + "name = '" + this.name + "', ownerID = " + this.ownerID + ", parseOrder = '" + this.parseOrder.name() + "'";
    }

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

    public Integer getNext(Integer current) {
        if (this.parseOrder == Order.NORMAL) {
            return (current + 1) % this.videos.size();
        } else {
            return (new Random()).nextInt(this.videos.size() - 1);
        }
    }

    public void removeVideo(int position) {
        this.videos.remove(position);
    }
}
