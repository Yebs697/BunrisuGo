package attendance;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AttendanceView extends JFrame {

    // --- UI 컴포넌트 ---
    private JLabel lblTodayStatusVal, lblConsecutiveVal, lblCumulativeVal, lblPointsVal;
    private JButton btnCheckIn, btnRefresh;
    private JTable historyTable;
    private DefaultTableModel tableModel;

    // --- 데이터 & DAO ---
    private AttendanceDAO dao;
    private String currentUserIdentifier = "testUserDevice";

    // --- 디자인 상수 ---
    private static final Font FONT_TITLE = new Font("맑은 고딕", Font.BOLD, 18);
    private static final Font FONT_SUBTITLE = new Font("맑은 고딕", Font.BOLD, 14);
    private static final Font FONT_LABEL = new Font("맑은 고딕", Font.PLAIN, 13);
    private static final Font FONT_VALUE = new Font("맑은 고딕", Font.BOLD, 15);
    private static final Font FONT_BUTTON = new Font("맑은 고딕", Font.BOLD, 14);

    private static final Color COLOR_BG_MAIN = new Color(240, 242, 245); 
    private static final Color COLOR_CARD_BG = Color.WHITE;              
    private static final Color COLOR_PRIMARY = new Color(52, 152, 219);  
    private static final Color COLOR_PRIMARY_DARK = new Color(41, 128, 185); 
    private static final Color COLOR_ACCENT = new Color(231, 76, 60);    
    private static final Color COLOR_TEXT_DARK = new Color(44, 62, 80);  

    public AttendanceView() {
        dao = new AttendanceDAO();

        setTitle("분리수GO - 출석 체크");
        setSize(420, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(COLOR_BG_MAIN);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // --- 1. 상단 타이틀 ---
        JLabel titleLabel = new JLabel("오늘의 출석체크");
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 22));
        titleLabel.setForeground(COLOR_TEXT_DARK);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        // --- 2. 출석 현황 카드 ---
        RoundedPanel statusCard = new RoundedPanel();
        statusCard.setLayout(new BorderLayout(15, 15));
        statusCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        statusCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JPanel infoGrid = new JPanel(new GridLayout(3, 2, 10, 10));
        infoGrid.setOpaque(false);

        addInfoRow(infoGrid, "오늘 출석", lblTodayStatusVal = new JLabel("X"));
        addInfoRow(infoGrid, "연속 출석", lblConsecutiveVal = new JLabel("0일차"));
        addInfoRow(infoGrid, "누적 출석", lblCumulativeVal = new JLabel("0일차"));
        
        lblTodayStatusVal.setForeground(COLOR_ACCENT);

        statusCard.add(infoGrid, BorderLayout.CENTER);

        btnCheckIn = new RoundedButton("출석\n하기");
        btnCheckIn.setPreferredSize(new Dimension(100, 0));
        statusCard.add(btnCheckIn, BorderLayout.EAST);

        mainPanel.add(statusCard);
        mainPanel.add(Box.createVerticalStrut(20));

        // --- 3. 포인트 & 히스토리 카드 ---
        RoundedPanel historyCard = new RoundedPanel();
        historyCard.setLayout(new BorderLayout(10, 10));
        historyCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel pointHeader = new JPanel(new BorderLayout());
        pointHeader.setOpaque(false);
        
        JLabel lblPointTitle = new JLabel("내 포인트");
        lblPointTitle.setFont(FONT_SUBTITLE);
        lblPointTitle.setForeground(Color.GRAY);
        
        lblPointsVal = new JLabel("0 p");
        lblPointsVal.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        lblPointsVal.setForeground(COLOR_PRIMARY);

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(lblPointTitle);
        textPanel.add(lblPointsVal);
        
        btnRefresh = new RoundedButton("새로고침");
        btnRefresh.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        btnRefresh.setPreferredSize(new Dimension(80, 30));
        // 새로고침 버튼 색상 커스텀
        ((RoundedButton)btnRefresh).setCustomColor(new Color(149, 165, 166), new Color(127, 140, 141));

        pointHeader.add(textPanel, BorderLayout.WEST);
        pointHeader.add(btnRefresh, BorderLayout.EAST);
        
        historyCard.add(pointHeader, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"날짜", "활동", "포인트"}, 0);
        historyTable = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        styleTable(historyTable);

        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        historyCard.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(historyCard);

        // --- 이벤트 리스너 ---
        btnCheckIn.addActionListener(e -> {
            String message = dao.performCheckIn(currentUserIdentifier);
            JOptionPane.showMessageDialog(AttendanceView.this, message, "알림", JOptionPane.INFORMATION_MESSAGE);
            updateUI();
        });

        btnRefresh.addActionListener(e -> {
            // 약간의 딜레이를 줘서 버튼 눌림 효과를 사용자가 느끼게 함 (선택사항)
            Timer timer = new Timer(100, evt -> updateUI());
            timer.setRepeats(false);
            timer.start();
        });

        updateUI();
        setVisible(true);
    }

    // --- Helper Methods ---

    private void addInfoRow(JPanel panel, String title, JLabel valueLabel) {
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_LABEL);
        lblTitle.setForeground(Color.GRAY);
        valueLabel.setFont(FONT_VALUE);
        valueLabel.setForeground(COLOR_TEXT_DARK);
        panel.add(lblTitle);
        panel.add(valueLabel);
    }

    private void styleTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setRowHeight(35);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(230, 230, 230));
        table.setFont(FONT_LABEL);
        table.setSelectionBackground(new Color(232, 240, 254));
        table.setSelectionForeground(Color.BLACK);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(Color.DARK_GRAY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(60);
    }

    private void updateUI() {
        AttendanceDTO data = dao.getAttendanceData(currentUserIdentifier);
        
        lblPointsVal.setText(String.format("%,d p", data.getTotalPoints()));
        lblConsecutiveVal.setText(data.getConsecutiveDays() + "일");
        lblCumulativeVal.setText(data.getCumulativeDays() + "일");

        if (data.isTodayAttended()) {
            lblTodayStatusVal.setText("출석 완료");
            lblTodayStatusVal.setForeground(COLOR_PRIMARY);
            btnCheckIn.setEnabled(false);
            btnCheckIn.setText("완료");
            ((RoundedButton)btnCheckIn).setCustomColor(new Color(189, 195, 199), new Color(189, 195, 199));
        } else {
            lblTodayStatusVal.setText("미출석");
            lblTodayStatusVal.setForeground(COLOR_ACCENT);
            btnCheckIn.setEnabled(true);
            btnCheckIn.setText("출석하기");
            ((RoundedButton)btnCheckIn).setCustomColor(COLOR_PRIMARY, COLOR_PRIMARY_DARK);
        }

        tableModel = dao.getPointHistory(currentUserIdentifier);
        historyTable.setModel(tableModel);
        styleTable(historyTable);
    }

    // --- ★★★ 수정된 RoundedPanel (그대로 유지) ---
    class RoundedPanel extends JPanel {
        private int cornerRadius = 20;
        public RoundedPanel() { super(); setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COLOR_CARD_BG);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            g2.setColor(new Color(230, 230, 230));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, cornerRadius, cornerRadius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // --- ★★★ 수정된 RoundedButton (눌림 효과 추가됨!) ★★★ ---
    class RoundedButton extends JButton {
        private Color normalColor = COLOR_PRIMARY;
        private Color hoverColor = COLOR_PRIMARY_DARK;
        private boolean isHovered = false;
        private boolean isPressed = false; // 눌림 상태 변수 추가

        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setFont(FONT_BUTTON);
            setForeground(Color.WHITE);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            // 마우스 이벤트 리스너 수정
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if(isEnabled()) isHovered = true;
                    repaint();
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    isHovered = false;
                    isPressed = false; // 나가면 눌림 해제
                    repaint();
                }
                @Override
                public void mousePressed(MouseEvent e) {
                    if(isEnabled()) {
                        isPressed = true; // 눌림 시작
                        repaint();
                    }
                }
                @Override
                public void mouseReleased(MouseEvent e) {
                    if(isEnabled()) {
                        isPressed = false; // 눌림 끝
                        repaint();
                    }
                }
            });
        }
        
        public void setCustomColor(Color normal, Color hover) {
            this.normalColor = normal;
            this.hoverColor = hover;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 1. 배경색 결정 로직
            if (isEnabled()) {
                if (isPressed) {
                    g2.setColor(hoverColor.darker()); // 누르면 더 어둡게
                } else if (isHovered) {
                    g2.setColor(hoverColor);
                } else {
                    g2.setColor(normalColor);
                }
            } else {
                g2.setColor(new Color(200, 200, 200));
            }

            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

            // 2. 텍스트 그리기 (위치 계산)
            FontMetrics fm = g2.getFontMetrics();
            Rectangle stringBounds = fm.getStringBounds(this.getText(), g2).getBounds();
            int textX = (getWidth() - stringBounds.width) / 2;
            int textY = (getHeight() - stringBounds.height) / 2 + fm.getAscent();

            // ★ 눌렸을 때 텍스트를 2px 아래로 이동 (물리적 버튼 느낌)
            if (isPressed) {
                textY += 2; 
            }

            g2.setColor(getForeground());
            g2.drawString(getText(), textX, textY);

            g2.dispose();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { e.printStackTrace(); }
        SwingUtilities.invokeLater(AttendanceView::new);
    }
} 