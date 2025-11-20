package attendance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

public class AttendanceDAO {

    /**
     * (기존 코드)
     * 화면(View)에 필요한 4가지 정보를 새 DB 스키마에서 조회
     */
    public AttendanceDTO getAttendanceData(String userIdentifier) {
        AttendanceDTO dto = new AttendanceDTO();
        LocalDate today = LocalDate.now();

        String sql_points = "SELECT total_points FROM Points WHERE user_identifier = ?";
        String sql_last_attend = "SELECT attend_date, consecutive_count FROM Attendance " +
                                 "WHERE user_identifier = ? ORDER BY attend_date DESC LIMIT 1";
        String sql_cumulative = "SELECT COUNT(attend_id) FROM Attendance WHERE user_identifier = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt_points = conn.prepareStatement(sql_points);
             PreparedStatement pstmt_last = conn.prepareStatement(sql_last_attend);
             PreparedStatement pstmt_cumulative = conn.prepareStatement(sql_cumulative)) {

            // (이하 코드 100% 동일)
            pstmt_points.setString(1, userIdentifier);
            ResultSet rs_points = pstmt_points.executeQuery();
            if (rs_points.next()) {
                dto.setTotalPoints(rs_points.getInt("total_points"));
            } else {
                dto.setTotalPoints(0);
            }

            pstmt_cumulative.setString(1, userIdentifier);
            ResultSet rs_cumul = pstmt_cumulative.executeQuery();
            if (rs_cumul.next()) {
                dto.setCumulativeDays(rs_cumul.getInt(1));
            }

            pstmt_last.setString(1, userIdentifier);
            ResultSet rs_last = pstmt_last.executeQuery();

            if (rs_last.next()) {
                LocalDate lastDate = rs_last.getDate("attend_date").toLocalDate();
                int consecutive = rs_last.getInt("consecutive_count");

                if (lastDate.equals(today)) {
                    dto.setTodayAttended(true);
                    dto.setConsecutiveDays(consecutive);
                } else if (!lastDate.equals(today.minusDays(1))) {
                    dto.setTodayAttended(false);
                    // ★ 7일 보너스(7) 받고 0으로 리셋된 경우, 다음날 0으로 표시
                    if (consecutive == 0) {
                        dto.setConsecutiveDays(0);
                    } else {
                        dto.setConsecutiveDays(0);
                    }
                } else { // 어제 출석한 경우
                    // ★ 만약 어제 보너스(7) 받고 0으로 리셋됐다면
                    if (consecutive == 0) {
                        dto.setTodayAttended(false);
                        dto.setConsecutiveDays(0); // 오늘 1일차로 시작할 것이므로, 어제까지는 0일로 표시
                    } else {
                        dto.setTodayAttended(false);
                        dto.setConsecutiveDays(consecutive);
                    }
                }
            } else {
                dto.setTodayAttended(false);
                dto.setConsecutiveDays(0);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dto;
    }

    
    public String performCheckIn(String userIdentifier) {
        Connection conn = null;
        PreparedStatement pstmt_last = null;
        PreparedStatement pstmt_insert_attend = null;
        PreparedStatement pstmt_update_points = null;
        PreparedStatement pstmt_insert_history = null;
        ResultSet rs_last = null;
        
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        String sql_last_attend = "SELECT attend_date, consecutive_count FROM Attendance " +
                                 "WHERE user_identifier = ? ORDER BY attend_date DESC LIMIT 1";
        
        String sql_insert_attend = "INSERT INTO Attendance (user_identifier, attend_date, points_earned, is_consecutive_bonus, consecutive_count) " +
                                   "VALUES (?, ?, ?, ?, ?)";
        
        String sql_update_points = "UPDATE Points SET total_points = total_points + ?, last_update_date = NOW() " +
                                   "WHERE user_identifier = ?";
        
        String sql_insert_history = "INSERT INTO Point_History (user_identifier, change_type, points_change, change_date) " +
                                    "VALUES (?, ?, ?, NOW())";

        String sql_check_points = "SELECT point_id FROM Points WHERE user_identifier = ?";
        String sql_insert_points = "INSERT INTO Points (user_identifier, total_points, last_update_date) VALUES (?, 0, NOW())";
                                   
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작

            // (선행 작업 - 100% 동일)
            try (PreparedStatement pstmt_check = conn.prepareStatement(sql_check_points)) {
                pstmt_check.setString(1, userIdentifier);
                ResultSet rs_check = pstmt_check.executeQuery();
                if (!rs_check.next()) {
                    try (PreparedStatement pstmt_insert = conn.prepareStatement(sql_insert_points)) {
                        pstmt_insert.setString(1, userIdentifier);
                        pstmt_insert.executeUpdate();
                    }
                }
            }

            // 1. 마지막 출석 기록 조회 (100% 동일)
            pstmt_last = conn.prepareStatement(sql_last_attend);
            pstmt_last.setString(1, userIdentifier);
            rs_last = pstmt_last.executeQuery();

            int newConsecutive = 1;
            int pointsToAdd = 10;
            boolean bonusHit = false;
            String message = "출석 완료! +10p";

            if (rs_last.next()) { 
                LocalDate lastDate = rs_last.getDate("attend_date").toLocalDate();
                int oldConsecutive = rs_last.getInt("consecutive_count");

                // --- [핵심 수정] (원본의 1일 1회 제한 로직) ---
                if (lastDate.equals(today)) {
                    conn.rollback();
                    return "오늘은 이미 출석했습니다.";
                } else if (lastDate.equals(yesterday)) {
                    // ★ 만약 어제 보너스 받고 0으로 리셋됐다면, oldConsecutive는 0일 것
                    // 0 + 1 = 1이므로, 1일차로 자동 리셋됨
                    newConsecutive = oldConsecutive + 1;
                }
                // (연속 끊김) newConsecutive는 1
                // ---------------------------------------------
            }
            // (D) 최초 사용자 (newConsecutive는 1)

            // 2. 보너스 계산 (7일마다)
            if (newConsecutive % 7 == 0) {
                pointsToAdd += 30; // 10 + 30 = 40
                bonusHit = true;
                message = "출석 완료! +10p (7일 연속 보너스 +30p)";
                
                // --- [핵심 수정] ---
                // ★ 보너스를 획득했으므로, 연속 출석일을 0으로 리셋
                newConsecutive = 0; 
                // ---------------------
            }

            // 3. (작업 1) Attendance 테이블에 출석 로그 삽입
            pstmt_insert_attend = conn.prepareStatement(sql_insert_attend);
            pstmt_insert_attend.setString(1, userIdentifier);
            pstmt_insert_attend.setDate(2, java.sql.Date.valueOf(today));
            pstmt_insert_attend.setInt(3, pointsToAdd); 
            pstmt_insert_attend.setInt(4, bonusHit ? 1 : 0);
            pstmt_insert_attend.setInt(5, newConsecutive); // 7일차면 0이, 1~6일차면 1~6이 저장됨
            pstmt_insert_attend.executeUpdate();

            // (이하 코드 100% 동일)
            // 4. (작업 2) Points 테이블에 총 포인트 업데이트
            pstmt_update_points = conn.prepareStatement(sql_update_points);
            pstmt_update_points.setInt(1, pointsToAdd);
            pstmt_update_points.setString(2, userIdentifier);
            pstmt_update_points.executeUpdate();
            
            // 5. (작업 3) Point_History에 로그 삽입 (기본)
            pstmt_insert_history = conn.prepareStatement(sql_insert_history);
            pstmt_insert_history.setString(1, userIdentifier);
            pstmt_insert_history.setString(2, "출석 체크");
            pstmt_insert_history.setInt(3, 10);
            pstmt_insert_history.executeUpdate();
            
            // 6. (작업 4) Point_History에 로그 삽입 (보너스)
            if (bonusHit) {
                pstmt_insert_history.setString(1, userIdentifier);
                pstmt_insert_history.setString(2, "7일 연속 보너스");
                pstmt_insert_history.setInt(3, 30);
                pstmt_insert_history.executeUpdate();
            }

            conn.commit(); 
            return message;

        } catch (SQLException e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {} 
            return "오류가 발생했습니다.";
        } finally {
            // 자원 해제 (100% 동일)
            try {
                if (rs_last != null) rs_last.close();
                if (pstmt_last != null) pstmt_last.close();
                if (pstmt_insert_attend != null) pstmt_insert_attend.close();
                if (pstmt_update_points != null) pstmt_update_points.close();
                if (pstmt_insert_history != null) pstmt_insert_history.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * (기존 코드)
     * 포인트 획득 내역을 JTable용 모델로 반환하는 메서드
     */
    public DefaultTableModel getPointHistory(String userIdentifier) {
        
        // (이하 코드 100% 동일)
        Vector<String> columnNames = new Vector<>();
        columnNames.add("날짜");
        columnNames.add("활동 유형");
        columnNames.add("포인트");

        Vector<Vector<Object>> data = new Vector<Vector<Object>>();

        String sql = "SELECT change_date, change_type, points_change FROM Point_History " +
                     "WHERE user_identifier = ? ORDER BY change_date DESC LIMIT 50";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userIdentifier);
            ResultSet rs = pstmt.executeQuery();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getTimestamp("change_date").toLocalDateTime().format(formatter)); 
                row.add(rs.getString("change_type"));
                row.add("+" + rs.getInt("points_change"));
                data.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        return model;
    }
}