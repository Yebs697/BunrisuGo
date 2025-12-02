package bunrisugo.recycling;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.sql.*;
import java.util.List;
import java.util.Map;


public class CardPanel extends JFrame {
	private MaterialDAO materialDAO;
	
    public CardPanel(Connection conn, int materialId) {
    	materialDAO = new MaterialDAO(conn);

        setTitle("재활용품 안내 프로그램");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); // 화면 중앙 정렬

        Container contentPane = getContentPane();
        contentPane.setLayout(new FlowLayout()); 

        JPanel headerPanel = new JPanel();				 		//맨위 판넬
        headerPanel.setPreferredSize(new Dimension(800, 40));
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        
        String materialName = materialDAO.getMaterialName(materialId); //소재이름
        JLabel titleLabel = new JLabel(materialName);			//소재이름 라벨
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        headerPanel.add(titleLabel);
        contentPane.add(headerPanel);

        // 대상품목 이미지 개수와 높이
        int imageCount = 0;
        int maxImageHeight = 0;
        int imageIndex = 1;
        while (true) {
            ImageIcon imageIcon = loadImage("sub01_01_1_" + materialId + "_a" + imageIndex);
            if (imageIcon == null) {
                break;
            }
            imageCount++;
            maxImageHeight = Math.max(maxImageHeight, imageIcon.getIconHeight());
            imageIndex++;
        }
        
        JPanel contentPanel = new JPanel();						//품목 불러오기
        contentPanel.setLayout(new FlowLayout());
        
        // 이미지 개수에 따라 패널 높이 동적 설정 (텍스트 공간 포함)
        if (imageCount > 0) {
            int imagesPerRow = Math.max(1, 650 / 120); // 패널 너비 750px - 여백 100px, 이미지+텍스트 너비 120px + 여백 고려
            int rows = (int) Math.ceil((double) imageCount / imagesPerRow);
            int calculatedHeight = Math.max(100, rows * (maxImageHeight + 40) + 40); 
            contentPanel.setPreferredSize(new Dimension(750, calculatedHeight));
        } else {
            contentPanel.setPreferredSize(new Dimension(750, 100));
        }
        
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
        	    BorderFactory.createTitledBorder("대상 품목"), // 바깥쪽 테두리
        	   new EmptyBorder(20, 50, 20, 50)
        ));
        contentPane.add(contentPanel);
        
        // 대상품목 이미지 로드 및 표시
        List<String> items = materialDAO.getItems(materialId); // 품목 리스트 가져오기
        imageIndex = 1;
        int itemIndex = 0;
        while (true) {
            ImageIcon imageIcon = loadImage("sub01_01_1_" + materialId + "_a" + imageIndex);
            if (imageIcon == null) {
                break; // 이미지가 없으면 종료
            }
            
            // 이미지와 텍스트를 포함하는 패널 생성
            JPanel imageTextPanel = new JPanel();
            imageTextPanel.setLayout(new BoxLayout(imageTextPanel, BoxLayout.Y_AXIS));
            imageTextPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // 이미지 라벨
            JLabel imageLabel = new JLabel(imageIcon);
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            imageTextPanel.add(imageLabel);
            
            // 텍스트 라벨 (품목 이름)
            String itemName = (itemIndex < items.size()) ? items.get(itemIndex) : "";
            JLabel textLabel = new JLabel(itemName);
            textLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
            textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            textLabel.setHorizontalAlignment(JLabel.CENTER);
            imageTextPanel.add(textLabel);
            
            contentPanel.add(imageTextPanel);
            imageIndex++;
            itemIndex++;
        }

        
        // 예외품목 이미지 개수 먼저 확인
        int exImageCount = 0;
        int maxImageHeight1 = 0;
        int exImageIndex = 1;
        while (true) {
            ImageIcon imageIcon = loadImage("sub01_01_1_" + materialId + "_b" + exImageIndex);
            if (imageIcon == null) {
                break;
            }
            exImageCount++;
            maxImageHeight1 = Math.max(maxImageHeight1, imageIcon.getIconHeight());
            exImageIndex++;
        }
        
        JPanel exMaterialPanel = new JPanel();					//예외품목
        exMaterialPanel.setLayout(new FlowLayout());
        
        // 이미지 개수에 따라 패널 높이 
        if (exImageCount > 0) {
            // 이미지가 한 줄에 들어갈 수 있는 개수 추정
            int imagesPerRow = Math.max(1, 650 / 120); // 이미지+텍스트 너비 120px + 여백 고려
            int rows = (int) Math.ceil((double) exImageCount / imagesPerRow);
            int calculatedHeight = Math.max(100, rows * (maxImageHeight1 + 40) + 40);
            exMaterialPanel.setPreferredSize(new Dimension(750, calculatedHeight));
        } else {
            exMaterialPanel.setPreferredSize(new Dimension(750, 100));
        }
        
        // 예외 품목 제목에 content 추가
        String exMaterialContent = materialDAO.getExMaterialContent(materialId);
        String exMaterialTitle = exMaterialContent.isEmpty() 
            ? "로 배출해야되는 품목" 
            : exMaterialContent;
        
        exMaterialPanel.setBorder(BorderFactory.createCompoundBorder(
        	    BorderFactory.createTitledBorder(exMaterialTitle), // 바깥쪽 테두리
        	   new EmptyBorder(20, 50, 20, 50)
        ));

        // 예외품목 이미지 로드 및 표시
        List<String> exMaterials = materialDAO.getExMaterial(materialId);
        exImageIndex = 1;
        int exMaterialIndex = 0;
        boolean hasExImages = false;
        while (true) {
            ImageIcon imageIcon = loadImage("sub01_01_1_" + materialId + "_b" + exImageIndex);
            if (imageIcon == null) {
                break; // 이미지가 없으면 종료
            }
            
            // 이미지와 텍스트를 포함하는 패널 생성
            JPanel imageTextPanel = new JPanel();
            imageTextPanel.setLayout(new BoxLayout(imageTextPanel, BoxLayout.Y_AXIS));
            imageTextPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // 이미지 라벨
            JLabel imageLabel = new JLabel(imageIcon);
            imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            imageTextPanel.add(imageLabel);
            
            // 텍스트 라벨 (예외품목 이름)
            String exMaterialName = "";
            if (exMaterialIndex < exMaterials.size()) {
                String exMaterial = exMaterials.get(exMaterialIndex);
                String[] temp = exMaterial.split(",");
                if (temp.length > 0) {
                    exMaterialName = temp[0];
                }
            }
            JLabel textLabel = new JLabel(exMaterialName);
            textLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
            textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            textLabel.setHorizontalAlignment(JLabel.CENTER);
            imageTextPanel.add(textLabel);
            
            exMaterialPanel.add(imageTextPanel);
            hasExImages = true;
            exImageIndex++;
            exMaterialIndex++;
        }
        
        if (!hasExImages) {
            contentPane.remove(exMaterialPanel);
            contentPane.revalidate();
            contentPane.repaint();
        } else {
            contentPane.add(exMaterialPanel);
        }
        

        List<String> commonMethods = materialDAO.getMethod(materialId); // 품목 리스트 가져오기
        
        JPanel methodPanel = new JPanel();						//품목 불러오기
        methodPanel.setLayout(new BoxLayout(methodPanel, BoxLayout.Y_AXIS));
        
        // 텍스트 라인 수에 따라 패널 높이 동적 설정
        int methodLineCount = commonMethods.size();
        int calculatedMethodHeight = Math.max(100, methodLineCount * 30 + 40); // 각 라인 30px + 여백
        methodPanel.setPreferredSize(new Dimension(750, calculatedMethodHeight));
        
        methodPanel.setBorder(BorderFactory.createCompoundBorder(
        	    BorderFactory.createTitledBorder("배출방법"), // 바깥쪽 테두리
        	   new EmptyBorder(20, 50, 20, 50)               // 안쪽 여백 (위, 왼쪽, 아래, 오른쪽)
        ));
        
        for (String commonMethod : commonMethods) {						//추후에 이미지까지 같이 저장기능 추가해야함
            JLabel methodLabel = new JLabel(commonMethod);
            methodLabel .setPreferredSize(new Dimension(750, 30));
            methodLabel .setFont(new Font("맑은 고딕", Font.PLAIN, 12));
            methodPanel.add(methodLabel);
        }
        contentPane.add(methodPanel);
        
        Map<String, List<String>> exMethodMap = materialDAO.getExMethod(materialId);

        JPanel exMethodPanel = new JPanel();
        exMethodPanel.setLayout(new BoxLayout(exMethodPanel, BoxLayout.Y_AXIS));
        
        // 예외 배출 방법 항목 수에 따라 패널 높이 동적 설정
        int exMethodLineCount = 0;
        for (Map.Entry<String, List<String>> entry : exMethodMap.entrySet()) {
            exMethodLineCount++; // 항목 제목
            exMethodLineCount += entry.getValue().size(); // 각 항목의 설명 라인
        }
        int calculatedExMethodHeight = Math.max(100, exMethodLineCount * 25 + 20); // 각 라인 25px + 여백
        exMethodPanel.setPreferredSize(new Dimension(750, calculatedExMethodHeight));
        
        exMethodPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("예외 배출 방법"),
            new EmptyBorder(10, 20, 10, 20)  // 안쪽 여백 (위, 왼쪽, 아래, 오른쪽)
        ));

        for (Map.Entry<String, List<String>> entry : exMethodMap.entrySet()) {
            JLabel itemLabel = new JLabel("[" + entry.getKey() + "]");
            itemLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
            exMethodPanel.add(itemLabel);

            for (String desc : entry.getValue()) {
                JLabel descLabel = new JLabel("- " + desc);
                descLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
                exMethodPanel.add(descLabel);
            }
        }

        if (!exMethodMap.isEmpty()) {
            contentPane.add(exMethodPanel);
        }

        setVisible(true);
        
    }
    
    /**
     * 이미지 파일을 로드합니다.
     * @param imageName 이미지 파일명 (확장자 제외)
     * @return ImageIcon 객체, 파일이 없으면 null
     */
    private ImageIcon loadImage(String imageName) {
        String[] extensions = {".png", ".jpg", ".jpeg", ".gif"};
        
        for (String ext : extensions) {
            File imageFile = new File("recImage", imageName + ext);
            String imagePath = imageFile.getPath();
            
            if (imageFile.exists()) {
                ImageIcon originalIcon = new ImageIcon(imagePath);
                if (originalIcon.getIconWidth() > 0 && originalIcon.getIconHeight() > 0) {
                    return resizeImage(originalIcon, 80);
                }
            }
        }
        
        return null; // 이미지 파일을 찾지 못한 경우
    }
    
    /**
     * 이미지 크기를 조정합니다.
     * @param originalIcon 원본 ImageIcon
     * @param maxWidth 최대 너비 (픽셀)
     * @return 크기가 조정된 ImageIcon
     */
    private ImageIcon resizeImage(ImageIcon originalIcon, int maxWidth) {
        int originalWidth = originalIcon.getIconWidth();
        int originalHeight = originalIcon.getIconHeight();
        
        // 이미 최대 너비보다 작으면 원본 반환
        if (originalWidth <= maxWidth) {
            return originalIcon;
        }
        
        // 비율을 유지하면서 크기 조정
        double ratio = (double) maxWidth / originalWidth;
        int newWidth = maxWidth;
        int newHeight = (int) (originalHeight * ratio);
        
        Image resizedImage = originalIcon.getImage().getScaledInstance(
            newWidth, newHeight, Image.SCALE_SMOOTH
        );
        
        return new ImageIcon(resizedImage);
    }
    
    public static void main(String[] args) {
    	Connection conn = bunrisugo.recycling.DatabaseConnection.getConnection();
    	if (conn != null) {
    		new CardPanel(conn, 8);
    	}
    }
}