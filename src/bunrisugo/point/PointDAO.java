package bunrisugo.point;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import bunrisugo.util.CommonDBConnection;

public class PointDAO {
    private Connection conn;
    
    public PointDAO() {
        try {
            this.conn = CommonDBConnection.getConnection();
        } catch (SQLException e) {
            System.err.println("데이터베이스 연결 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public int getTotalPoints(String userIdentifier) {
        if (conn == null) {
            System.err.println("데이터베이스 연결이 없습니다.");
            return 0;
        }
        
        String sql = "SELECT total_points FROM Points WHERE user_identifier = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userIdentifier);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total_points");
            } else {
                initializeUserPoints(userIdentifier);
                return 0;
            }
        } catch (SQLException e) {
            System.err.println("총 포인트 조회 실패: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
    
    private void initializeUserPoints(String userIdentifier) {
        if (conn == null) {
            System.err.println("데이터베이스 연결이 없어 포인트를 초기화할 수 없습니다.");
            return;
        }
        
        String sql = "INSERT INTO Points (user_identifier, total_points, last_update_date) VALUES (?, 0, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userIdentifier);
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.executeUpdate();
            System.out.println("사용자 포인트 초기화 완료");
        } catch (SQLException e) {
            System.err.println("사용자 포인트 초기화 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public boolean addPoints(String userIdentifier, String changeType, int pointsChange) {
        Connection conn = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        
        try {
            conn = CommonDBConnection.getConnection();
            conn.setAutoCommit(false);
            
            String updatePointsSql = "INSERT INTO Points (user_identifier, total_points, last_update_date) " +
                                    "VALUES (?, ?, ?) " +
                                    "ON DUPLICATE KEY UPDATE " +
                                    "total_points = total_points + ?, " +
                                    "last_update_date = ?";
            
            LocalDateTime now = LocalDateTime.now();
            pstmt1 = conn.prepareStatement(updatePointsSql);
            pstmt1.setString(1, userIdentifier);
            pstmt1.setInt(2, pointsChange);
            pstmt1.setTimestamp(3, Timestamp.valueOf(now));
            pstmt1.setInt(4, pointsChange);
            pstmt1.setTimestamp(5, Timestamp.valueOf(now));
            pstmt1.executeUpdate();
            
            String insertHistorySql = "INSERT INTO Point_History (user_identifier, change_type, points_change, change_date) " +
                                     "VALUES (?, ?, ?, ?)";
            
            pstmt2 = conn.prepareStatement(insertHistorySql);
            pstmt2.setString(1, userIdentifier);
            pstmt2.setString(2, changeType);
            pstmt2.setInt(3, pointsChange);
            pstmt2.setTimestamp(4, Timestamp.valueOf(now));
            pstmt2.executeUpdate();
            
            conn.commit();
            System.out.println("포인트 추가 성공: " + pointsChange + "p");
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                System.err.println("롤백 실패: " + ex.getMessage());
            }
            System.err.println("포인트 추가 실패: " + e.getMessage());
            return false;
        } finally {
            try {
                if (pstmt1 != null) pstmt1.close();
                if (pstmt2 != null) pstmt2.close();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("리소스 정리 실패: " + e.getMessage());
            }
        }
    }
    
    public List<PointHistoryDTO> getPointHistory(String userIdentifier) {
        List<PointHistoryDTO> historyList = new ArrayList<>();
        
        if (conn == null) {
            System.err.println("데이터베이스 연결이 없습니다.");
            return historyList;
        }
        
        String sql = "SELECT history_id, user_identifier, change_type, points_change, change_date " +
                    "FROM Point_History " +
                    "WHERE user_identifier = ? " +
                    "ORDER BY change_date DESC " +
                    "LIMIT 10";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userIdentifier);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                PointHistoryDTO history = new PointHistoryDTO();
                history.setHistoryId(rs.getInt("history_id"));
                history.setUserIdentifier(rs.getString("user_identifier"));
                history.setChangeType(rs.getString("change_type"));
                history.setPointsChange(rs.getInt("points_change"));
                history.setChangeDate(rs.getTimestamp("change_date").toLocalDateTime());
                historyList.add(history);
            }
        } catch (SQLException e) {
            System.err.println("포인트 히스토리 조회 실패: " + e.getMessage());
            e.printStackTrace();
        }
        
        return historyList;
    }
}

