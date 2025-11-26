import javax.swing.*;
import java.awt.*;

public class PointTestMain extends JFrame {
    
    private PointDAO pointDAO;
    private String userIdentifier = "testUser001";
    
    public PointTestMain() {
        pointDAO = new PointDAO();
        
        setTitle("포인트 시스템 테스트");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblTitle = new JLabel("포인트 시스템 테스트", JLabel.CENTER);
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        mainPanel.add(lblTitle);
        
        JButton btnAddQuiz = new JButton("퀴즈 정답 포인트 추가 (+5p)");
        btnAddQuiz.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        btnAddQuiz.addActionListener(e -> {
            boolean success = pointDAO.addPoints(userIdentifier, "퀴즈", 5);
            if (success) {
                JOptionPane.showMessageDialog(this, "퀴즈 포인트 5p 추가 완료");
            } else {
                JOptionPane.showMessageDialog(this, "포인트 추가 실패", "오류", JOptionPane.ERROR_MESSAGE);
            }
        });
        mainPanel.add(btnAddQuiz);
        
        JButton btnAddAttendance = new JButton("출석 체크 포인트 추가 (+10p)");
        btnAddAttendance.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        btnAddAttendance.addActionListener(e -> {
            boolean success = pointDAO.addPoints(userIdentifier, "출석 체크", 10);
            if (success) {
                JOptionPane.showMessageDialog(this, "출석 체크 포인트 10p 추가 완료");
            } else {
                JOptionPane.showMessageDialog(this, "포인트 추가 실패", "오류", JOptionPane.ERROR_MESSAGE);
            }
        });
        mainPanel.add(btnAddAttendance);
        
        JButton btnAddInfo = new JButton("정보 제공 포인트 추가 (+3p)");
        btnAddInfo.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        btnAddInfo.addActionListener(e -> {
            boolean success = pointDAO.addPoints(userIdentifier, "정보 제공", 3);
            if (success) {
                JOptionPane.showMessageDialog(this, "정보 제공 포인트 3p 추가 완료");
            } else {
                JOptionPane.showMessageDialog(this, "포인트 추가 실패", "오류", JOptionPane.ERROR_MESSAGE);
            }
        });
        mainPanel.add(btnAddInfo);
        
        JButton btnOpenPointView = new JButton("포인트 조회 화면 열기");
        btnOpenPointView.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        btnOpenPointView.setBackground(new Color(76, 175, 80));
        btnOpenPointView.setForeground(Color.WHITE);
        btnOpenPointView.addActionListener(e -> {
        	new PointView(userIdentifier).setVisible(true);
        });
        mainPanel.add(btnOpenPointView);
        
        add(mainPanel);
        setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PointTestMain());
    }
}
