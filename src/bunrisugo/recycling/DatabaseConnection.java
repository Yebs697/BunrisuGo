package bunrisugo.recycling;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/javapj?serverTimezone=UTC";
    private static final String ID = "root";
    private static final String PASSWORD = ""; 
    
    /**
     * 데이터베이스 연결을 생성합니다.
     * @return Connection 객체, 연결 실패 시 null
     */
    public static Connection getConnection() {
        return getConnection(null);
    }
    

    public static Connection getConnection(String password) {
        Connection conn = null;
        String pw = "";
        
//        if (pw == null) {
//            Scanner scanner = new Scanner(System.in);
//            System.out.println("Password: ");
//            pw = scanner.nextLine();
//        }
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // JDBC 드라이버를 로드함
            
            conn = DriverManager.getConnection(URL, ID, PASSWORD);
            // url과 id, 패스워드로 데이터베이스와 연결
            
            System.out.println("DB연결완료");
            // 오류없이 연결이 되는 경우 출력
            
        } catch(ClassNotFoundException e) {
            System.out.println("JDBC 드라이버 로드 오류");
            // JDBC 드라이버 로드 오류
            
        } catch(SQLException e) {
            System.out.println("DB 연결 오류");
            // DB 연결 오류
            e.printStackTrace();
        }
        
        return conn;
    }
    
    /**
     * 데이터베이스 연결을 닫습니다.
     * @param conn 닫을 Connection 객체
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("DB연결종료");
            } catch (SQLException e) {
                System.out.println("DB 연결 종료 오류");
                e.printStackTrace();
            }
        }
    }
}


