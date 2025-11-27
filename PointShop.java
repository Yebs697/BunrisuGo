package bunrisugo.point;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

public class PointShop extends JFrame {
    private static final Font FONT_TITLE = new Font("맑은 고딕", Font.BOLD, 24);
    private static final Font FONT_PRODUCT = new Font("맑은 고딕", Font.BOLD, 14);
    private static final Font FONT_PRICE = new Font("맑은 고딕", Font.BOLD, 16);
    private static final Font FONT_BUTTON = new Font("맑은 고딕", Font.BOLD, 13);
    private static final Color COLOR_GREEN = new Color(76, 175, 80);
    private static final Color COLOR_RED = new Color(244, 67, 54);
    
    private JLabel lblCurrentPoints;
    private JPanel productsPanel;
    private PointDAO pointDAO;
    private String userIdentifier;
    
    private static final java.util.Map<String, Integer> PRODUCTS = new java.util.LinkedHashMap<String, Integer>() {{
        put("작은 기부", 5);
        put("중간 기부", 10);
        put("큰 기부", 100);
        put("껌", 500);
        put("에너지바", 1000);
        put("구글 기프트카드", 5000);
    }};
    
    private static final java.util.Map<String, String> PRODUCT_IMAGES = new java.util.LinkedHashMap<String, String>() {{
        put("작은 기부", "donation_small.png");
        put("중간 기부", "donation_medium.png");
        put("큰 기부", "donation_large.png");
        put("껌", "gum.png");
        put("에너지바", "energy_bar.png");
        put("구글 기프트카드", "google_giftcard.png");
    }};
    
    public PointShop(String userIdentifier) {
        this.userIdentifier = userIdentifier;
        this.pointDAO = new PointDAO();
        
        setTitle("분리수GO - 포인트 상점");
        setSize(700, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel topPanel = createTopPanel();
        JScrollPane scrollPane = createScrollableProductsPanel();
        JPanel bottomPanel = createBottomPanel();
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        updatePoints();
    }
    
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Color.WHITE);
        
        JLabel lblTitle = new JLabel("포인트 상점", JLabel.CENTER);
        lblTitle.setFont(FONT_TITLE);
        lblTitle.setForeground(COLOR_GREEN);
        topPanel.add(lblTitle, BorderLayout.NORTH);
        
        JPanel pointsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        pointsPanel.setBackground(new Color(245, 245, 245));
        pointsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_GREEN, 2),
            new EmptyBorder(15, 30, 15, 30)
        ));
        
        JLabel lblLabel = new JLabel("보유 포인트:");
        lblLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        
        lblCurrentPoints = new JLabel("0 p");
        lblCurrentPoints.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        lblCurrentPoints.setForeground(COLOR_GREEN);
        
        pointsPanel.add(lblLabel);
        pointsPanel.add(lblCurrentPoints);
        
        topPanel.add(pointsPanel, BorderLayout.CENTER);
        
        return topPanel;
    }
    
    private JScrollPane createScrollableProductsPanel() {
        // 세로로 나열되는 패널
        productsPanel = new JPanel();
        productsPanel.setLayout(new BoxLayout(productsPanel, BoxLayout.Y_AXIS));
        productsPanel.setBackground(Color.WHITE);
        productsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // 각 상품 카드 추가
        for (java.util.Map.Entry<String, Integer> entry : PRODUCTS.entrySet()) {
            String productName = entry.getKey();
            int price = entry.getValue();
            String imageFileName = PRODUCT_IMAGES.get(productName);
            
            JPanel productCard = createProductCard(productName, price, imageFileName);
            productsPanel.add(productCard);
            productsPanel.add(Box.createRigidArea(new Dimension(0, 10))); // 간격
        }
        
        // 스크롤 패널로 감싸기
        JScrollPane scrollPane = new JScrollPane(productsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // 스크롤 속도
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        return scrollPane;
    }
    
    private JPanel createProductCard(String productName, int price, String imageFileName) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120)); // 카드 높이 고정
        
        // 왼쪽: 이미지
        JLabel lblImage = createImageLabel(imageFileName);
        card.add(lblImage, BorderLayout.WEST);
        
        // 중앙: 상품명 + 가격
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        infoPanel.setBackground(Color.WHITE);
        
        JLabel lblName = new JLabel(productName);
        lblName.setFont(FONT_PRODUCT);
        lblName.setToolTipText(productName);
        
        JLabel lblPrice = new JLabel(price + " p");
        lblPrice.setFont(FONT_PRICE);
        lblPrice.setForeground(COLOR_GREEN);
        
        infoPanel.add(lblName);
        infoPanel.add(lblPrice);
        card.add(infoPanel, BorderLayout.CENTER);
        
        // 오른쪽: 구매 버튼
        JButton btnBuy = new JButton("구매하기");
        btnBuy.setFont(FONT_BUTTON);
        btnBuy.setBackground(COLOR_GREEN);
        btnBuy.setForeground(Color.WHITE);
        btnBuy.setFocusPainted(false);
        btnBuy.setPreferredSize(new Dimension(100, 40));
        btnBuy.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnBuy.addActionListener(e -> purchaseProduct(productName, price));
        
        card.add(btnBuy, BorderLayout.EAST);
        
        return card;
    }
    
    private JLabel createImageLabel(String imageFileName) {
        JLabel lblImage = new JLabel();
        lblImage.setPreferredSize(new Dimension(100, 80));
        lblImage.setHorizontalAlignment(JLabel.CENTER);
        lblImage.setVerticalAlignment(JLabel.CENTER);
        lblImage.setOpaque(true);
        lblImage.setBackground(new Color(240, 240, 240));
        
        try {
            String[] paths = {
                "src/bunrisugo/point/image/" + imageFileName
            };
            
            ImageIcon icon = null;
            String successPath = null;
            
            for (String path : paths) {
                try {
                    File imgFile = new File(path);
                    if (imgFile.exists()) {
                        icon = new ImageIcon(imgFile.getAbsolutePath());
                        if (icon.getIconWidth() > 0) {
                            successPath = path;
                            System.out.println("이미지 로드 성공: " + path);
                            break;
                        }
                    }
                } catch (Exception e) {
                }
            }
            
            if (icon != null && icon.getIconWidth() > 0) {
                Image scaledImage = icon.getImage().getScaledInstance(100, 80, Image.SCALE_SMOOTH);
                lblImage.setIcon(new ImageIcon(scaledImage));
                lblImage.setText("");
            } else {
                lblImage.setText("<html><center>" + imageFileName.replace(".png", "").replace("_", " ") + "</center></html>");
                lblImage.setFont(new Font("맑은 고딕", Font.PLAIN, 11));
                lblImage.setForeground(Color.GRAY);
                System.err.println("이미지 로드 실패: " + imageFileName);
                System.err.println("다음 경로를 확인하세요:");
                for (String path : paths) {
                    System.err.println("  - " + path);
                }
            }
            
        } catch (Exception e) {
            lblImage.setText("<html><center>이미지<br>로드 실패</center></html>");
            lblImage.setFont(new Font("맑은 고딕", Font.PLAIN, 10));
            lblImage.setForeground(Color.RED);
            System.err.println("이미지 로드 예외: " + imageFileName + " - " + e.getMessage());
            e.printStackTrace();
        }
        
        return lblImage;
    }
    
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBackground(Color.WHITE);
        
        JButton btnClose = new JButton("닫기");
        btnClose.setFont(FONT_BUTTON);
        btnClose.setBackground(Color.GRAY);
        btnClose.setForeground(Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setPreferredSize(new Dimension(120, 35));
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnClose.addActionListener(e -> dispose());
        
        bottomPanel.add(btnClose);
        
        return bottomPanel;
    }
    
    private void purchaseProduct(String productName, int price) {
        int currentPoints = pointDAO.getTotalPoints(userIdentifier);
        
        if (currentPoints < price) {
            JOptionPane.showMessageDialog(
                this,
                String.format("보유 포인트가 부족합니다.\n\n필요 포인트: %d p\n보유 포인트: %d p\n부족 포인트: %d p",
                    price, currentPoints, price - currentPoints),
                "포인트 부족",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            String.format("'%s'을(를) 구매하시겠습니까?\n\n현재 포인트: %d p\n구매 후 포인트: %d p",
                productName, currentPoints, currentPoints - price),
            "구매 확인",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = pointDAO.addPoints(userIdentifier, PointDAO.TYPE_SHOP + ": " + productName, -price);
            
            if (success) {
                JOptionPane.showMessageDialog(
                    this,
                    String.format("구매가 완료되었습니다!\n\n상품: %s\n사용 포인트: %d p",
                        productName, price),
                    "구매 완료",
                    JOptionPane.INFORMATION_MESSAGE
                );
                updatePoints();
            } else {
                JOptionPane.showMessageDialog(
                    this,
                    "구매 중 오류가 발생했습니다.\n다시 시도해주세요.",
                    "오류",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    
    private void updatePoints() {
        int currentPoints = pointDAO.getTotalPoints(userIdentifier);
        lblCurrentPoints.setText(currentPoints + " p");
    }
}