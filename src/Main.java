import services.JDBCUtils;
import statemanager.StateManager;

import java.sql.*;
import services.Service;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Service.setupConnection("VSP");

        StateManager manager = StateManager.getInstance();

        manager.loop();
    }
}