package attendance;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/BunrisuGo";
    private static final String DB_USER = "root"; // MySQL 아이디
    private static final String DB_PASS = "1234"; // MySQL 비밀번호

    
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL 드라이버 로드 실패");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }
}