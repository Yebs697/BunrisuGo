package Recycling;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;

public class SearchFrame extends JFrame {
    private JTextField searchField;
    private JButton searchButton;
    private Connection conn;
    private MaterialDAO materialDAO;
    private CardPanel currentCardPanel;
    
    public SearchFrame(Connection conn) {
        this.conn = conn;
        this.materialDAO = new MaterialDAO(conn);
        
        setTitle("재활용품 검색 프로그램");
        setSize(500, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        
        // 검색 패널
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout());
        searchPanel.setBorder(BorderFactory.createEmptyBorder(50, 20, 20, 20));
        
        JLabel searchLabel = new JLabel("검색어:");
        searchLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        searchPanel.add(searchLabel);
        
        searchField = new JTextField(20);
        searchField.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        searchPanel.add(searchField);
        
        searchButton = new JButton("검색");
        searchButton.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
        searchPanel.add(searchButton);
        
        // 엔터 키로도 검색 가능하도록
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
        
        contentPane.add(searchPanel, BorderLayout.CENTER);
        
        setVisible(true);
    }
    
    private void performSearch() {
        String keyword = searchField.getText().trim();
        
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "검색어를 입력해주세요.", 
                "알림", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            // 검색 수행
            int materialId = materialDAO.searchMaterialId(keyword);
            
            if (materialId == -1) {
                JOptionPane.showMessageDialog(this, 
                    "검색 결과가 없습니다.\n다른 키워드로 검색해주세요.\n\n입력한 키워드: " + keyword, 
                    "검색 결과 없음", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // 기존 CardPanel이 있으면 닫기
            if (currentCardPanel != null) {
                currentCardPanel.dispose();
            }
            
            // 새로운 CardPanel 열기
            currentCardPanel = new CardPanel(conn, materialId);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "검색 중 오류가 발생했습니다.\n\n오류 내용: " + e.getMessage(), 
                "오류", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        Connection conn = DatabaseConnection.getConnection();
        if (conn != null) {
            new SearchFrame(conn);
        }
    }
}

