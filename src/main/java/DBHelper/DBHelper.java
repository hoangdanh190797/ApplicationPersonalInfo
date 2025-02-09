package DBHelper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//;user=udemy;password=123456;databaseName=UdemyDemoDB

public class DBHelper {
    public static final  String DB_NAME = "PersonalInfoDB";
    public static final String USERNAME = "AdminPersonalInfoDB";
    public static final String PASSWORD = "123456";
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        return getConnection(DB_NAME, USERNAME, PASSWORD);
    }
    public static Connection getConnection(String dbName, String username, String password)
            throws ClassNotFoundException, SQLException {
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        String dbURL =
                "jdbc:sqlserver://localhost;databaseName=%s;encrypt=true;trustServerCertificate=true;";
        Connection conn = DriverManager.getConnection(String.format(dbURL, dbName), username, password);
        return conn;
    }
}
