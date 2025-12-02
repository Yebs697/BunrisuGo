package bunrisugo.point;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PointView extends JFrame {
    private static final Font FONT_TITLE = new Font("맑은 고딕", Font.BOLD, 20);
    private static final Font FONT_LABEL = new Font("맑은 고딕", Font.PLAIN, 16);
    private static final Font FONT_VALUE = new Font("맑은 고딕", Font.BOLD, 18);
    private static final Color COLOR_GREEN = new Color(76, 175, 80);
    private static final Color COLOR_PANEL_BG = new Color(245, 245, 245);
    
    private String userIdentifier;
    private PointDAO pointDAO;
    private JLabel lblCurrentPoints;
    private JLabel lblTotalEarnedPoints;
    private JButton btnRefresh;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    
    public PointView(String userIdentifier) {
        this.userIdentifier = userIdentifier;
        this.pointDAO = new PointDAO();
        
        setTitle("분리수GO - 포인트 조회");
        setSize(550, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JPanel topPanel = createTopPanel();
        JPanel centerPanel = createCenterPanel();
        JPanel bottomPanel = createBottomPanel();
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        loadPointData();
    }
    
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JPanel pointsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        pointsPanel.setBackground(COLOR_PANEL_BG);
        pointsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_GREEN, 2),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        JPanel currentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        currentPanel.setBackground(COLOR_PANEL_BG);
        
        JLabel lblCurrent = new JLabel("현재 포인트:");
        lblCurrent.setFont(FONT_LABEL);
        
        lblCurrentPoints = new JLabel("0 p");
        lblCurrentPoints.setFont(FONT_VALUE);
        lblCurrentPoints.setForeground(COLOR_GREEN);
        
        currentPanel.add(lblCurrent);
        currentPanel.add(lblCurrentPoints);
        
        JPanel earnedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        earnedPanel.setBackground(COLOR_PANEL_BG);
        
        JLabel lblEarned = new JLabel("총 획득 포인트:");
        lblEarned.setFont(FONT_LABEL);
        
        lblTotalEarnedPoints = new JLabel("0 p");
        lblTotalEarnedPoints.setFont(FONT_VALUE);
        lblTotalEarnedPoints.setForeground(COLOR_GREEN);
        
        earnedPanel.add(lblEarned);
        earnedPanel.add(lblTotalEarnedPoints);
        
        pointsPanel.add(currentPanel);
        pointsPanel.add(earnedPanel);
        
        topPanel.add(pointsPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        
        btnRefresh = new JButton("새로고침");
        btnRefresh.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        btnRefresh.setBackground(COLOR_GREEN);
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btnRefresh.addActionListener(e -> loadPointData());
        
        buttonPanel.add(btnRefresh);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return topPanel;
    }
    
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setBackground(Color.WHITE);
        
        JLabel lblTitle = new JLabel("내역");
        lblTitle.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        lblTitle.setBorder(new EmptyBorder(10, 0, 10, 0));
        centerPanel.add(lblTitle, BorderLayout.NORTH);
        
        String[] columnNames = {"날짜", "유형", "변동량"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        historyTable = new JTable(tableModel);
        historyTable.setFont(new Font("맑은 고딕", Font.PLAIN, 13));
        historyTable.setRowHeight(30);
        historyTable.getTableHeader().setFont(new Font("맑은 고딕", Font.BOLD, 13));
        historyTable.getTableHeader().setBackground(COLOR_PANEL_BG);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < historyTable.getColumnCount(); i++) {
            historyTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        historyTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                
                String text = value.toString();
                if (text.startsWith("+")) {
                    setForeground(COLOR_GREEN);
                } else if (text.startsWith("-")) {
                    setForeground(Color.RED);
                } else {
                    setForeground(Color.BLACK);
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        return centerPanel;
    }
    
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBackground(Color.WHITE);
        
        JButton btnShop = new JButton("포인트 상점");
        btnShop.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        btnShop.setBackground(new Color(255, 152, 0));
        btnShop.setForeground(Color.WHITE);
        btnShop.setFocusPainted(false);
        btnShop.setPreferredSize(new Dimension(200, 40));
        btnShop.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnShop.addActionListener(e -> {
            new PointShop(userIdentifier).setVisible(true);
        });
        
        bottomPanel.add(btnShop);
        
        return bottomPanel;
    }
    
    public void loadPointData() {
        int currentPoints = pointDAO.getTotalPoints(userIdentifier);
        lblCurrentPoints.setText(currentPoints + " p");
        
        int totalEarnedPoints = pointDAO.getTotalEarnedPoints(userIdentifier);
        lblTotalEarnedPoints.setText(totalEarnedPoints + " p");
        
        List<PointHistoryDTO> historyList = pointDAO.getPointHistory(userIdentifier);
        tableModel.setRowCount(0);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        for (PointHistoryDTO history : historyList) {
            String date = history.getChangeDate().format(formatter);
            String type = history.getChangeType();
            String change = (history.getPointsChange() > 0 ? "+" : "") + history.getPointsChange() + " p";
            
            tableModel.addRow(new Object[]{date, type, change});
        }
    }
}