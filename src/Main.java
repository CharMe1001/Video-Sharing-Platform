import services.JDBCUtils;
import statemanager.StateManager;

import java.sql.*;
import services.Service;

public class Main {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        Service.setupConnection("VSP");

        JDBCUtils.getConnection(Service.connection);
        JDBCUtils.dropEverything();
        JDBCUtils.createTables();

        StateManager manager = StateManager.getInstance();

        manager.loop();
    }
}