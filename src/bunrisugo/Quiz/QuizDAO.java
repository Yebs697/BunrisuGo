package bunrisugo.quiz;

import bunrisugo.util.CommonDBConnection;
import java.sql.*;
import java.util.List;

public class QuizDAO {

    // 퀴즈 리스트를 통째로 받아서 DB에 저장하는 메서드
    public void saveQuizQuestions(List<ecoPJ.QuizQuestion> questions) {
        if (questions == null || questions.isEmpty()) return;

        Connection conn = null;
        PreparedStatement pstmtQuestion = null;
        PreparedStatement pstmtOption = null;
        ResultSet rs = null;

        // SQL: 문제 저장
        String sqlQuestion = "INSERT INTO Quiz_Question (question_text, hint_text, created_at) VALUES (?, ?, NOW())";
        // SQL: 보기 저장
        String sqlOption = "INSERT INTO Quiz_Option (question_id, option_text, is_correct, rationale) VALUES (?, ?, ?, ?)";

        try {
            conn = CommonDBConnection.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작

            pstmtQuestion = conn.prepareStatement(sqlQuestion, Statement.RETURN_GENERATED_KEYS);
            pstmtOption = conn.prepareStatement(sqlOption);

            for (ecoPJ.QuizQuestion q : questions) {
                // 1. 문제 저장
                pstmtQuestion.setString(1, q.getQuestion());
                pstmtQuestion.setString(2, q.getHint());
                pstmtQuestion.executeUpdate();

                // 2. 방금 저장한 문제 ID 가져오기
                rs = pstmtQuestion.getGeneratedKeys();
                int newQuestionId = 0;
                if (rs.next()) {
                    newQuestionId = rs.getInt(1);
                }
                rs.close();

                // 3. 보기 4개 저장
                if (newQuestionId > 0) {
                    for (ecoPJ.AnswerOption opt : q.getAnswerOptions()) {
                        pstmtOption.setInt(1, newQuestionId);
                        pstmtOption.setString(2, opt.getText());
                        pstmtOption.setBoolean(3, opt.isCorrect());
                        pstmtOption.setString(4, opt.getRationale());
                        pstmtOption.addBatch(); 
                    }
                }
            }
            
            pstmtOption.executeBatch(); // 보기 일괄 저장
            conn.commit(); // 최종 확정
            System.out.println("✅ 퀴즈 DB 백업 완료!");

        } catch (SQLException e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ex) {}
        } finally {
            // 자원 해제
            try {
                if (rs != null) rs.close();
                if (pstmtOption != null) pstmtOption.close();
                if (pstmtQuestion != null) pstmtQuestion.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (Exception e) {}
        }
    }
}