// DBConnection.java 전체 수정 제안
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // 1. 주소를 127.0.0.1로 고정
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/BunrisuGo?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "qwer"; // 본인 비번 확인
    
    private static Connection connection = null;
    
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                System.out.println("====== DB 연결 시도 정보 ======");
                System.out.println("URL: " + URL);
                System.out.println("USER: " + USER);
                // 비번은 길이만 출력해서 체크
                System.out.println("PW 길이: " + (PASSWORD != null ? PASSWORD.length() : "null")); 
                
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("====== 데이터베이스 연결 성공! ======");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC 드라이버 로드 실패 (라이브러리 확인 필요): " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("====== 연결 실패 상세 ======");
            System.err.println("에러 코드: " + e.getErrorCode());
            System.err.println("SQL 상태: " + e.getSQLState());
            System.err.println("메시지: " + e.getMessage());
        }
        return connection;
    }
    
    // closeConnection 메소드는 기존 그대로...
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("데이터베이스 연결 종료");
            }
        } catch (SQLException e) {
            System.err.println("연결 종료 실패: " + e.getMessage());
        }
    }
}