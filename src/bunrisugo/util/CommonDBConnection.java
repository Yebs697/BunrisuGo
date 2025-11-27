package bunrisugo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 출석체크, 포인트 등 공통 기능을 위한 데이터베이스 연결 클래스
 * BunrisuGo 데이터베이스 사용
 */
public class CommonDBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/BunrisuGo?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = ""; //자신의 비밀번호로 변경
    
    private static Connection connection = null;
    
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL 드라이버 로드 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 데이터베이스 연결을 가져옵니다.
     * @return Connection 객체
     * @throws SQLException 연결 실패 시
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("BunrisuGo 데이터베이스 연결 성공");
        }
        return connection;
    }
    
    /**
     * 데이터베이스 연결을 닫습니다.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("BunrisuGo 데이터베이스 연결 종료");
            }
        } catch (SQLException e) {
            System.err.println("연결 종료 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
}





