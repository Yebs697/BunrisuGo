package bunrisugo.attendance;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// ★ 팀원의 포인트 화면을 불러오기 위해 import (패키지명 확인 필요)
import bunrisugo.point.PointView; 

public class AttendanceView extends JFrame {

    // --- UI 컴포넌트 ---
    private JLabel lblTodayStatusVal, lblConsecutiveVal, lblCumulativeVal;
    private JButton btnCheckIn;
    private JButton btnShowPointDetail; // ★ 포인트 화면으로 가는 버튼

    // --- 데이터 & DAO ---
    private AttendanceDAO dao;
    private String currentUserIdentifier = "testUserDevice"; 

    // --- 디자인 상수 ---
    private static final Font FONT_TITLE = new Font("맑은 고딕", Font.BOLD, 24);
    private static final Font FONT_LABEL = new Font("맑은 고딕", Font.BOLD, 16);
    private static final Font FONT_VALUE = new Font("맑은 고딕", Font.BOLD, 20);
    private static final Font FONT_BUTTON = new Font("맑은 고딕", Font.BOLD, 20);

    private static final Color COLOR_BG_MAIN = new Color(240, 242, 245);
    private static final Color COLOR_CARD_BG = Color.WHITE;
    private static final Color COLOR_PRIMARY = new Color(52, 152, 219);
    private static final Color COLOR_PRIMARY_DARK = new Color(41, 128, 185);
    private static final Color COLOR_ACCENT = new Color(231, 76, 60);
    private static final Color COLOR_TEXT_DARK = new Color(44, 62, 80);
    private static final Color COLOR_TEXT_GRAY = new Color(120, 120, 120);
    
    // 포인트 버튼 색상 (초록색 계열 추천)
    private static final Color COLOR_POINT_BTN = new Color(46, 204, 113);
    private static final Color COLOR_POINT_BTN_DARK = new Color(39, 174, 96);

    public AttendanceView() {
        dao = new AttendanceDAO();

        setTitle("분리수GO - 출석 체크");
        setSize(400, 600); // 버튼 추가로 세로 길이 확보
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(COLOR_BG_MAIN);
        mainPanel.setBorder(new EmptyBorder(30, 20, 30, 20));
        setContentPane(mainPanel);

        // --- 1. 상단 타이틀 ---
        JLabel titleLabel = new JLabel("오늘의 출석체크", SwingConstants.CENTER);
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(COLOR_TEXT_DARK);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // --- 2. 중앙 현황 카드 ---
        RoundedPanel statusCard = new RoundedPanel();
        statusCard.setLayout(new GridLayout(3, 1, 10, 10));
        statusCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        statusCard.add(createStatusRow("오늘 출석 여부", lblTodayStatusVal = new JLabel("-")));
        statusCard.add(createStatusRow("연속 출석일", lblConsecutiveVal = new JLabel("0일")));
        statusCard.add(createStatusRow("총 누적 출석일", lblCumulativeVal = new JLabel("0일")));

        lblTodayStatusVal.setForeground(COLOR_ACCENT);
        mainPanel.add(statusCard, BorderLayout.CENTER);

        // --- 3. 하단 버튼 패널 (출석 + 포인트이동) ---
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1, 0, 15)); // 2줄 배치
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(25, 0, 0, 0));

        // 3-1. 출석하기 버튼
        btnCheckIn = new RoundedButton("출석하고 포인트 받기");
        btnCheckIn.setPreferredSize(new Dimension(0, 60));
        bottomPanel.add(btnCheckIn);

        // 3-2. ★ 포인트 상세 내역 버튼 (팀원 화면 연결)
        btnShowPointDetail = new RoundedButton("내 포인트 상세 내역 보기");
        btnShowPointDetail.setPreferredSize(new Dimension(0, 50));
        ((RoundedButton)btnShowPointDetail).setCustomColor(COLOR_POINT_BTN, COLOR_POINT_BTN_DARK);
        bottomPanel.add(btnShowPointDetail);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // --- 이벤트 리스너 ---
        
        // [출석하기]
        btnCheckIn.addActionListener(e -> {
            String message = dao.performCheckIn(currentUserIdentifier);
            JOptionPane.showMessageDialog(AttendanceView.this, message, "알림", JOptionPane.INFORMATION_MESSAGE);
            updateUI();
        });

        // [포인트 내역 보기] ★ 여기가 연결 고리!
        btnShowPointDetail.addActionListener(e -> {
            try {
                // ★ 내 ID를 팀원 화면에 넘겨줌
                new PointView(currentUserIdentifier).setVisible(true);
            } catch (Exception ex) {
                // 팀원이 아직 생성자를 안 고쳤을 경우를 대비한 안내
                JOptionPane.showMessageDialog(this, 
                    "포인트 화면을 여는 중 오류가 발생했습니다.\nPointView 코드가 수정되었는지 확인해주세요!", 
                    "연결 오류", JOptionPane.ERROR_MESSAGE);
            }
        });

        updateUI();
        setVisible(true);
    }

    // --- Helper Methods ---

    private JPanel createStatusRow(String title, JLabel valueLabel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_LABEL);
        lblTitle.setForeground(COLOR_TEXT_GRAY);
        
        valueLabel.setFont(FONT_VALUE);
        valueLabel.setForeground(COLOR_TEXT_DARK);
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        panel.add(lblTitle, BorderLayout.WEST);
        panel.add(valueLabel, BorderLayout.EAST);
        return panel;
    }

    private void updateUI() {
        AttendanceDTO data = dao.getAttendanceData(currentUserIdentifier);
        
        lblConsecutiveVal.setText(data.getConsecutiveDays() + "일째");
        lblCumulativeVal.setText(data.getCumulativeDays() + "일");

        if (data.isTodayAttended()) {
            lblTodayStatusVal.setText("출석 완료!");
            lblTodayStatusVal.setForeground(COLOR_PRIMARY);
            btnCheckIn.setEnabled(false);
            btnCheckIn.setText("오늘 출석 완료");
            ((RoundedButton)btnCheckIn).setCustomColor(new Color(189, 195, 199), new Color(189, 195, 199));
        } else {
            lblTodayStatusVal.setText("미출석");
            lblTodayStatusVal.setForeground(COLOR_ACCENT);
            btnCheckIn.setEnabled(true);
            btnCheckIn.setText("출석하고 포인트 받기");
            ((RoundedButton)btnCheckIn).setCustomColor(COLOR_PRIMARY, COLOR_PRIMARY_DARK);
        }
    }

    // --- RoundedPanel ---
    class RoundedPanel extends JPanel {
        private int cornerRadius = 25;
        public RoundedPanel() { super(); setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COLOR_CARD_BG);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            g2.setColor(new Color(220, 220, 220));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, cornerRadius, cornerRadius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // --- RoundedButton ---
    class RoundedButton extends JButton {
        private Color normalColor = COLOR_PRIMARY;
        private Color hoverColor = COLOR_PRIMARY_DARK;
        private boolean isHovered = false;
        private boolean isPressed = false;

        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setFont(FONT_BUTTON);
            setForeground(Color.WHITE);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) { if(isEnabled()) isHovered = true; repaint(); }
                @Override
                public void mouseExited(MouseEvent e) { isHovered = false; isPressed = false; repaint(); }
                @Override
                public void mousePressed(MouseEvent e) { if(isEnabled()) { isPressed = true; repaint(); } }
                @Override
                public void mouseReleased(MouseEvent e) { if(isEnabled()) { isPressed = false; repaint(); } }
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

            if (isEnabled()) {
                if (isPressed) g2.setColor(hoverColor.darker());
                else if (isHovered) g2.setColor(hoverColor);
                else g2.setColor(normalColor);
            } else {
                g2.setColor(new Color(200, 200, 200));
            }

            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

            FontMetrics fm = g2.getFontMetrics();
            Rectangle stringBounds = fm.getStringBounds(this.getText(), g2).getBounds();
            int textX = (getWidth() - stringBounds.width) / 2;
            int textY = (getHeight() - stringBounds.height) / 2 + fm.getAscent();

            if (isPressed) textY += 2;

            g2.setColor(getForeground());
            g2.drawString(getText(), textX, textY);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception e) {}
        SwingUtilities.invokeLater(AttendanceView::new);
    }
}
