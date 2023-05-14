import Services.JDBCUtils;
import StateManager.StateManager;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import Services.Service;

public class Main {
    public static void main(String[] args) throws SQLException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

        Service.connection = DriverManager.getConnection("jdbc:sqlserver://localhost;database=VSP;encrypt=false;integratedSecurity=true");

        //JDBCUtils.connection = Service.connection;
        //JDBCUtils.DropEverything();
        //JDBCUtils.CreateTables();

        StateManager manager = StateManager.getInstance();

        manager.loop();
    }
}