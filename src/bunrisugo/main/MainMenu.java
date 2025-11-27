package bunrisugo.main;

import bunrisugo.recycling.SearchFrame;
import bunrisugo.recycling.DatabaseConnection;
import bunrisugo.attendance.AttendanceView;
import bunrisugo.quiz.ecoPJ;
import bunrisugo.point.PointView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

public class MainMenu extends JFrame {
    
    public MainMenu() {
        setTitle("분리수GO - 메인 메뉴");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // 메인 패널 설정
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(240, 242, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // 타이틀 패널
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(240, 242, 245));
        JLabel titleLabel = new JLabel("분리수GO");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 36));
        titleLabel.setForeground(new Color(52, 152, 219));
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // 버튼 패널
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(2, 2, 20, 20));
        buttonPanel.setBackground(new Color(240, 242, 245));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        // 재활용품 검색 버튼
        JButton recyclingBtn = createMenuButton("재활용품 검색", new Color(52, 152, 219));
        recyclingBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openRecyclingSearch();
            }
        });
        buttonPanel.add(recyclingBtn);
        
        // 출석 체크 버튼
        JButton attendanceBtn = createMenuButton("출석 체크", new Color(46, 204, 113));
        attendanceBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openAttendance();
            }
        });
        buttonPanel.add(attendanceBtn);
        
        // 포인트 조회 버튼
        JButton pointBtn = createMenuButton("포인트 조회", new Color(241, 196, 15));
        pointBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openPointView();
            }
        });
        buttonPanel.add(pointBtn);
        
        // 퀴즈 버튼
        JButton quizBtn = createMenuButton("환경 퀴즈", new Color(231, 76, 60));
        quizBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openQuiz();
            }
        });
        buttonPanel.add(quizBtn);
        
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        
        // 하단 정보 패널
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(new Color(240, 242, 245));
        JLabel infoLabel = new JLabel("환경을 생각하는 분리수거 앱");
        infoLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        infoLabel.setForeground(new Color(127, 140, 141));
        infoPanel.add(infoLabel);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);
        
        setContentPane(mainPanel);
        setVisible(true);
    }
    
    private JButton createMenuButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        button.setPreferredSize(new Dimension(200, 100));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        
        
        return button;
    }
    
    private void openRecyclingSearch() {
        try {
            Connection conn = DatabaseConnection.getConnection();
            if (conn != null) {
                new SearchFrame(conn);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "데이터베이스 연결에 실패했습니다.", 
                    "연결 오류", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "재활용품 검색 기능을 열 수 없습니다.\n" + e.getMessage(), 
                "오류", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void openAttendance() {
        try {
            new AttendanceView();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "출석 체크 기능을 열 수 없습니다.\n" + e.getMessage(), 
                "오류", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void openPointView() {
        try {
        	// 이렇게 바꾸세요! (괄호 안에 아이디 넣기)
        	PointView view = new PointView("testUserDevice");
        	view.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "포인트 조회 기능을 열 수 없습니다.\n" + e.getMessage(), 
                "오류", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void openQuiz() {
        try {
            new ecoPJ();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "퀴즈 기능을 열 수 없습니다.\n" + e.getMessage(), 
                "오류", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainMenu();
            }
        });
    }
}

