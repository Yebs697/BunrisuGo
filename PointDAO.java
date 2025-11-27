import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class PointDAO {
    
    // 히스토리 유형 상수 정의 (표준화)
    public static final String TYPE_QUIZ = "퀴즈 정답";
    public static final String TYPE_ATTENDANCE = "출석 체크";
    public static final String TYPE_ATTENDANCE_BONUS = "7일 연속 보너스";
    public static final String TYPE_INFO = "정보 제공";
    public static final String TYPE_SHOP = "상점 구매";
    public static final String TYPE_EVENT = "이벤트";

    private Connection conn;
    
    public PointDAO() {
        this.conn = DBConnection.getConnection();
    }
    
    /**
     * 현재 총 포인트 조회
     */
    public int getTotalPoints(String userIdentifier) {
        // DB 연결 확인
        if (conn == null) return 0;

        String sql = "SELECT total_points FROM Points WHERE user_identifier = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userIdentifier);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total_points");
            } else {
                // 정보 없으면 초기화 후 0 반환
                initializeUserPoints(userIdentifier);
                return 0;
            }
        } catch (SQLException e) {
            System.err.println("총 포인트 조회 실패: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * 총 획득 포인트 조회 (단순 합산용, 차감 내역 제외)
     */
    public int getTotalEarnedPoints(String userIdentifier) {
        if (conn == null) return 0;

        String sql = "SELECT SUM(points_change) as total_earned " +
                    "FROM Point_History " +
                    "WHERE user_identifier = ? AND points_change > 0";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userIdentifier);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total_earned");
            } else {
                return 0;
            }
        } catch (SQLException e) {
            System.err.println("총 획득 포인트 조회 실패: " + e.getMessage());
            return 0;
        }
    }
    
    // 신규 사용자 포인트 초기화
    private void initializeUserPoints(String userIdentifier) {
        String sql = "INSERT INTO Points (user_identifier, total_points, last_update_date) VALUES (?, 0, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userIdentifier);
            pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.executeUpdate();
            System.out.println("사용자 포인트 초기화 완료: " + userIdentifier);
        } catch (SQLException e) {
            System.err.println("사용자 포인트 초기화 실패: " + e.getMessage());
        }
    }
    
    /**
     * 포인트 증감 처리 (트랜잭션 적용)
     * @param pointsChange 양수: 증가, 음수: 감소
     */
    public boolean addPoints(String userIdentifier, String changeType, int pointsChange) {
        
        // 1. 유효성 검사: 잔액 부족 시 차감 방지
        if (pointsChange < 0) {
            int currentPoints = getTotalPoints(userIdentifier);
            if (currentPoints + pointsChange < 0) {
                System.err.println("[오류] 잔액 부족 (현재: " + currentPoints + "p, 요청: " + pointsChange + "p)");
                return false; 
            }
        }

        Connection txConn = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        
        try {
            txConn = DBConnection.getConnection();
            
            // 2. 트랜잭션 시작
            txConn.setAutoCommit(false);
            
            // 3. Points 테이블 갱신 (없으면 생성, 있으면 합산)
            String updatePointsSql = "INSERT INTO Points (user_identifier, total_points, last_update_date) " +
                                    "VALUES (?, ?, ?) " +
                                    "ON DUPLICATE KEY UPDATE " +
                                    "total_points = total_points + ?, " +
                                    "last_update_date = ?";
            
            LocalDateTime now = LocalDateTime.now();
            pstmt1 = txConn.prepareStatement(updatePointsSql);
            pstmt1.setString(1, userIdentifier);
            pstmt1.setInt(2, pointsChange); // 신규 생성 시 초기값
            pstmt1.setTimestamp(3, Timestamp.valueOf(now));
            pstmt1.setInt(4, pointsChange); // 기존 존재 시 더할 값
            pstmt1.setTimestamp(5, Timestamp.valueOf(now));
            pstmt1.executeUpdate();
            
            // 4. 히스토리 기록 추가
            String insertHistorySql = "INSERT INTO Point_History (user_identifier, change_type, points_change, change_date) " +
                                     "VALUES (?, ?, ?, ?)";
            
            pstmt2 = txConn.prepareStatement(insertHistorySql);
            pstmt2.setString(1, userIdentifier);
            pstmt2.setString(2, changeType);
            pstmt2.setInt(3, pointsChange);
            pstmt2.setTimestamp(4, Timestamp.valueOf(now));
            pstmt2.executeUpdate();
            
            // 5. 작업 확정 (커밋)
            txConn.commit();
            System.out.println("[성공] 포인트 반영: " + pointsChange + "p [" + changeType + "]");
            return true;
            
        } catch (SQLException e) {
            // 작업 실패 시 원상 복구 (롤백)
            try {
                if (txConn != null) {
                    txConn.rollback();
                    System.err.println("[실패] 트랜잭션 롤백됨");
                }
            } catch (SQLException ex) {
                System.err.println("롤백 실패: " + ex.getMessage());
            }
            System.err.println("포인트 처리 SQL 오류: " + e.getMessage());
            return false;
        } finally {
            // 리소스 정리 및 오토커밋 복구
            try {
                if (pstmt1 != null) pstmt1.close();
                if (pstmt2 != null) pstmt2.close();
                if (txConn != null) txConn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("리소스 정리 실패: " + e.getMessage());
            }
        }
    }
    
    /**
     * 포인트 내역 조회 (최근 50개)
     */
    public List<PointHistoryDTO> getPointHistory(String userIdentifier) {
        List<PointHistoryDTO> historyList = new ArrayList<>();
        if (conn == null) return historyList;

        String sql = "SELECT history_id, user_identifier, change_type, points_change, change_date " +
                    "FROM Point_History " +
                    "WHERE user_identifier = ? " +
                    "ORDER BY change_date DESC " +
                    "LIMIT 50"; 
        
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
        }
        
        return historyList;
    }
}