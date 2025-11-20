import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PointView extends JFrame {
    
    private JLabel lblTotalPoints;
    private JLabel lblCurrentPoints;
    private JButton btnRefresh;
    private JTable historyTable;
    private DefaultTableModel tableModel;
    
    private PointDAO pointDAO;
    private String userIdentifier = "testUser001";
    
    private static final Font FONT_TITLE = new Font("맑은 고딕", Font.BOLD, 18);
    private static final Font FONT_LABEL = new Font("맑은 고딕", Font.PLAIN, 14);
    private static final Font FONT_VALUE = new Font("맑은 고딕", Font.BOLD, 16);
    private static final Color COLOR_GREEN = new Color(76, 175, 80);
    private static final Color COLOR_PANEL_BG = new Color(245, 245, 245);
    
    public PointView() {
        pointDAO = new PointDAO();
        
        setTitle("분리수GO - 포인트 조회");
        setSize(550, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);
        setContentPane(mainPanel);
        
        JPanel topPanel = createTopPanel();
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = createCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        loadPointData();
        
        setVisible(true);
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
        
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        totalPanel.setBackground(COLOR_PANEL_BG);
        JLabel lblTotal = new JLabel("총 포인트:");
        lblTotal.setFont(FONT_LABEL);
        lblTotalPoints = new JLabel("0 p");
        lblTotalPoints.setFont(FONT_VALUE);
        lblTotalPoints.setForeground(COLOR_GREEN);
        totalPanel.add(lblTotal);
        totalPanel.add(lblTotalPoints);
        
        JPanel currentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        currentPanel.setBackground(COLOR_PANEL_BG);
        JLabel lblCurrent = new JLabel("현재 포인트:");
        lblCurrent.setFont(FONT_LABEL);
        lblCurrentPoints = new JLabel("0 p");
        lblCurrentPoints.setFont(FONT_VALUE);
        lblCurrentPoints.setForeground(COLOR_GREEN);
        currentPanel.add(lblCurrent);
        currentPanel.add(lblCurrentPoints);
        
        pointsPanel.add(totalPanel);
        pointsPanel.add(currentPanel);
        
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
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(Color.WHITE);
        
        JLabel lblHistory = new JLabel("내역");
        lblHistory.setFont(FONT_TITLE);
        lblHistory.setBorder(new EmptyBorder(10, 5, 10, 5));
        centerPanel.add(lblHistory, BorderLayout.NORTH);
        
        String[] columnNames = {"날짜", "유형", "변동량"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        historyTable = new JTable(tableModel);
        historyTable.setFont(FONT_LABEL);
        historyTable.setRowHeight(35);
        historyTable.setShowGrid(true);
        historyTable.setGridColor(new Color(230, 230, 230));
        
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        historyTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        historyTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        
        historyTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.RIGHT);
                
                String text = value.toString();
                if (text.startsWith("+")) {
                    setForeground(COLOR_GREEN);
                } else {
                    setForeground(Color.BLACK);
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        return centerPanel;
    }
    
    public void loadPointData() {
        int totalPoints = pointDAO.getTotalPoints(userIdentifier);
        lblTotalPoints.setText(totalPoints + " p");
        lblCurrentPoints.setText(totalPoints + " p");
        
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
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PointView());
    }
}
