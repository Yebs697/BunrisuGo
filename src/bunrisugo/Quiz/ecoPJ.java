package bunrisugo.quiz;

import com.google.gson.Gson;
import bunrisugo.point.PointDAO;
import bunrisugo.point.PointShop; 
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ecoPJ extends JFrame {

    // ------------------------------------------------
    // GUI 멤버 변수
    // ------------------------------------------------
    private List<QuizQuestion> questions = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;

    // 디자인용 폰트 및 색상 상수
    private static final Font FONT_TITLE = new Font("맑은 고딕", Font.BOLD, 24);
    private static final Font FONT_QUESTION = new Font("맑은 고딕", Font.BOLD, 18);
    private static final Font FONT_OPTION = new Font("맑은 고딕", Font.PLAIN, 15);
    private static final Font FONT_NORMAL = new Font("맑은 고딕", Font.PLAIN, 14);
    
    private static final Color COLOR_PRIMARY = new Color(46, 204, 113); // 에메랄드 그린
    private static final Color COLOR_BACKGROUND = new Color(245, 245, 245); // 연한 회색 배경
    private static final Color COLOR_WHITE = Color.WHITE;
    private static final Color COLOR_TEXT = new Color(50, 50, 50);

    private JLabel titleLabel;
    private JLabel questionLabel;
    private JLabel hintLabel; 
    private JRadioButton[] answerButtons;
    private ButtonGroup buttonGroup;
    private JButton controlButton;
    private JTextArea feedbackArea;
    private JLabel statusLabel;
    private JProgressBar progressBar; 

    public ecoPJ() {
        super("분리수GO - AI 퀴즈");
        setupGUI();
        fetchQuestions(); 
    }

    // ------------------------------------------------
    // GUI 초기화 및 설정 (왼쪽 정렬 적용됨)
    // ------------------------------------------------
    private void setupGUI() {
        // 1. 메인 프레임 설정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 750); 
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_BACKGROUND);

        // 2. 상단 헤더 패널 (제목 + 진행바)
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_WHITE);
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        titleLabel = new JLabel("환경 상식 퀴즈", JLabel.CENTER);
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(COLOR_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        statusLabel = new JLabel("문제를 생성하고 있습니다...", JLabel.CENTER);
        statusLabel.setFont(FONT_NORMAL);
        statusLabel.setForeground(Color.GRAY);
        statusLabel.setBorder(new EmptyBorder(10, 0, 0, 0));
        headerPanel.add(statusLabel, BorderLayout.CENTER);

        progressBar = new JProgressBar(0, 100);
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
        progressBar.setForeground(COLOR_PRIMARY);
        progressBar.setBackground(new Color(230, 230, 230));
        progressBar.setBorderPainted(false);
        progressBar.setPreferredSize(new Dimension(100, 15));
        headerPanel.add(progressBar, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);

        // 3. 중앙 패널 (문제 + 보기)
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(COLOR_BACKGROUND);
        centerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // 질문 영역 (카드 형태)
        JPanel questionPanel = new JPanel(new BorderLayout());
        questionPanel.setBackground(COLOR_WHITE);
        questionPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));
        questionPanel.setMaximumSize(new Dimension(600, 150)); 
        
        // [수정] 문제 박스도 왼쪽 정렬 라인에 맞춤
        questionPanel.setAlignmentX(Component.LEFT_ALIGNMENT); 

        questionLabel = new JLabel("<html><div style='text-align: center;'>잠시만 기다려주세요...</div></html>", JLabel.CENTER);
        questionLabel.setFont(FONT_QUESTION);
        questionLabel.setForeground(COLOR_TEXT);
        questionPanel.add(questionLabel, BorderLayout.CENTER);

        hintLabel = new JLabel("", JLabel.CENTER);
        hintLabel.setFont(new Font("맑은 고딕", Font.ITALIC, 13));
        hintLabel.setForeground(new Color(100, 100, 100));
        questionPanel.add(hintLabel, BorderLayout.SOUTH);

        centerPanel.add(questionPanel);
        centerPanel.add(Box.createVerticalStrut(20)); // 간격

        // 보기 버튼들
        buttonGroup = new ButtonGroup();
        answerButtons = new JRadioButton[4];
        for (int i = 0; i < answerButtons.length; i++) {
            answerButtons[i] = new JRadioButton("보기 " + (i + 1));
            answerButtons[i].setFont(FONT_OPTION);
            answerButtons[i].setBackground(COLOR_BACKGROUND); 
            answerButtons[i].setForeground(COLOR_TEXT);
            answerButtons[i].setFocusPainted(false);
            answerButtons[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            answerButtons[i].setBorder(new EmptyBorder(5, 10, 5, 10)); 
            answerButtons[i].setVisible(false);
            
            // [수정] 버튼 왼쪽 정렬 적용!
            answerButtons[i].setAlignmentX(Component.LEFT_ALIGNMENT); 
            
            buttonGroup.add(answerButtons[i]);
            centerPanel.add(answerButtons[i]);
            centerPanel.add(Box.createVerticalStrut(10)); 
        }

        add(centerPanel, BorderLayout.CENTER);

        // 4. 하단 패널 (피드백 + 버튼)
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(COLOR_WHITE);
        bottomPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        feedbackArea = new JTextArea(3, 30);
        feedbackArea.setFont(FONT_NORMAL);
        feedbackArea.setEditable(false);
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        feedbackArea.setBackground(new Color(250, 250, 250));
        feedbackArea.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(220, 220, 220)),
                new EmptyBorder(10, 10, 10, 10)
        ));
        bottomPanel.add(feedbackArea, BorderLayout.CENTER);

        controlButton = new JButton("로딩 중...");
        controlButton.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        controlButton.setBackground(Color.GRAY);
        controlButton.setForeground(Color.WHITE);
        controlButton.setFocusPainted(false);
        controlButton.setBorderPainted(false); 
        controlButton.setPreferredSize(new Dimension(100, 50));
        controlButton.setEnabled(false);
        controlButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JPanel btnPanel = new JPanel(new BorderLayout());
        btnPanel.setBackground(COLOR_WHITE);
        btnPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        btnPanel.add(controlButton, BorderLayout.CENTER);

        bottomPanel.add(btnPanel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
        
        setLocationRelativeTo(null); 
    }
    
    // ------------------------------------------------
    // 퀴즈 로드 및 표시 로직
    // ------------------------------------------------
    private void fetchQuestions() {
        new ApiQuizFetcher(this).execute();
    }
    
    // SwingWorker 완료 후 호출
    public void loadQuestions(List<QuizQuestion> newQuestions) {
        if (newQuestions == null || newQuestions.isEmpty()) {
            showError("AI가 유효한 퀴즈를 생성하지 못했거나, 응답이 비어있습니다.");
            return;
        }
        this.questions = newQuestions;
        statusLabel.setText("총 " + questions.size() + "문제");
        
        controlButton.setEnabled(true);
        controlButton.setBackground(COLOR_PRIMARY); 
        controlButton.setText("정답 제출");
        
        progressBar.setValue(0);
        progressBar.setString("0 / " + questions.size());
        
        showQuestion(currentQuestionIndex); 
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "오류", JOptionPane.ERROR_MESSAGE);
        statusLabel.setText("오류 발생");
        controlButton.setText("종료");
        controlButton.setBackground(new Color(231, 76, 60)); 
        controlButton.setEnabled(true);
        controlButton.addActionListener(e -> System.exit(0));
    }

    private void showQuestion(int index) {
        if (index >= questions.size()) {
            showResult();
            return;
        }

        QuizQuestion q = questions.get(index);
        
        // HTML 태그를 써서 자동 줄바꿈 처리
        questionLabel.setText("<html><div style='text-align: center; width: 400px;'>" + q.getQuestion() + "</div></html>");
        hintLabel.setText("💡 힌트: " + q.getHint());
        
        feedbackArea.setText("");
        feedbackArea.setBackground(new Color(250, 250, 250)); 
        buttonGroup.clearSelection();

        // 진행바 업데이트
        int progress = (int) (((double) (index + 1) / questions.size()) * 100);
        progressBar.setValue(progress);
        progressBar.setString((index + 1) + " / " + questions.size());

        for (int i = 0; i < answerButtons.length; i++) {
            if (i < q.getAnswerOptions().size()) {
                answerButtons[i].setText(q.getAnswerOptions().get(i).getText());
                answerButtons[i].setVisible(true);
            } else {
                answerButtons[i].setVisible(false);
            }
        }
        
        for (ActionListener listener : controlButton.getActionListeners()) {
            controlButton.removeActionListener(listener);
        }
        controlButton.setText("정답 제출");
        controlButton.addActionListener(new SubmitListener());
    }
    
    private void showResult() {
        titleLabel.setText("퀴즈 종료!");
        questionLabel.setText("수고하셨습니다!");
        hintLabel.setText("");
        
        feedbackArea.setText("총 " + questions.size() + "문제 중 " + score + "개를 맞히셨습니다!\n결과를 저장하는 중입니다...");
        feedbackArea.setBackground(new Color(255, 249, 196)); 
        
        for (JRadioButton btn : answerButtons) {
            btn.setVisible(false);
        }
        controlButton.setVisible(false);
        statusLabel.setText("최종 점수: " + score + "점");
        
        // [DB 저장]
        saveScoreToDB(score, questions.size());
    }

    // ------------------------------------------------
    // MySQL DB 저장 로직
    // ------------------------------------------------
    private void saveScoreToDB(int userScore, int totalQuestions) {
        // 1. 포인트 계산
        final int POINTS_PER_ANSWER = 10;
        int earnedPoints = userScore * POINTS_PER_ANSWER;

        if (earnedPoints <= 0) {
            JOptionPane.showMessageDialog(this, "아쉽게도 획득한 포인트가 없습니다.\n다음엔 더 잘해봐요!");
            return;
        }

        // 2. PointDAO 사용
        bunrisugo.point.PointDAO pointDAO = new bunrisugo.point.PointDAO();
        boolean success = pointDAO.addPoints("testUserDevice", bunrisugo.point.PointDAO.TYPE_QUIZ, earnedPoints);

        // 3. 결과 알림
        if (success) {
            feedbackArea.setText("총 " + questions.size() + "문제 중 " + score + "점 획득!\n" + earnedPoints + " 포인트가 적립되었습니다.");
            
            Object[] options = {"확인", "포인트 상점 가기"};
            int choice = JOptionPane.showOptionDialog(this,
                    "퀴즈 완료! " + earnedPoints + " 포인트를 받았습니다.",
                    "적립 완료",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]);
            
            if (choice == 1) {
                new bunrisugo.point.PointShop("GuestUser").setVisible(true);
            }
        } else {
            showError("포인트 저장 실패! (DB 연결 확인 필요)");
        }
    }

    // ------------------------------------------------
    // 이벤트 리스너
    // ------------------------------------------------
    private class SubmitListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedIndex = -1;
            for (int i = 0; i < answerButtons.length; i++) {
                if (answerButtons[i].isSelected()) {
                    selectedIndex = i;
                    break;
                }
            }

            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(ecoPJ.this, "정답을 선택해 주세요.", "알림", JOptionPane.WARNING_MESSAGE);
                return;
            }

            QuizQuestion currentQuestion = questions.get(currentQuestionIndex);
            AnswerOption selectedOption = currentQuestion.getAnswerOptions().get(selectedIndex);

            if (selectedOption.isCorrect()) {
                score++;
                feedbackArea.setText("✅ 정답입니다!\n\n해설: " + selectedOption.getRationale());
                feedbackArea.setForeground(new Color(39, 174, 96)); // 초록색 텍스트
            } else {
                feedbackArea.setText("❌ 오답입니다.\n\n해설: " + selectedOption.getRationale());
                feedbackArea.setForeground(new Color(192, 57, 43)); // 빨간색 텍스트
            }
            
            controlButton.setText("다음 문제 >");
            for (ActionListener listener : controlButton.getActionListeners()) {
                controlButton.removeActionListener(listener);
            }
            controlButton.addActionListener(new NextQuestionListener());
        }
    }

    private class NextQuestionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            feedbackArea.setForeground(Color.BLACK); // 텍스트 색상 복구
            currentQuestionIndex++;
            showQuestion(currentQuestionIndex);
        }
    }
    
    // ------------------------------------------------
    // 메인 메서드
    // ------------------------------------------------
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            new ecoPJ().setVisible(true);
        });
    }

    // ------------------------------------------------
    // 데이터 모델 (QuizDAO 연동을 위해 public static)
    // ------------------------------------------------
    public static class AnswerOption {
        public String text;
        public String rationale;
        public boolean isCorrect;

        public AnswerOption() {} 
        public String getText() { return text; }
        public String getRationale() { return rationale; }
        public boolean isCorrect() { return isCorrect; }
    }

    public static class QuizQuestion {
        public int questionNumber;
        public String question;
        public String hint;
        public List<AnswerOption> answerOptions;

        public QuizQuestion() {}
        public int getQuestionNumber() { return questionNumber; }
        public String getQuestion() { return question; }
        public String getHint() { return hint; }
        public List<AnswerOption> getAnswerOptions() { return answerOptions; }
    }
    
    // ------------------------------------------------
    // API 호출 로직
    // ------------------------------------------------
    static class ApiQuizFetcher extends SwingWorker<List<QuizQuestion>, Void> {

        // ⚠️ 입력하신 새 API 키를 적용했습니다.
        private static final String API_KEY = ""; 
        
        
        private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"; 
        
        private static final String PDF_FILE_NAME = "recycling_guide.pdf";
        
        private final ecoPJ gui;

        public ApiQuizFetcher(ecoPJ gui) {
            this.gui = gui;
        }

        private String extractTextFromPdf() {
            File file = new File(PDF_FILE_NAME);
            if (!file.exists()) {
                System.out.println("⚠️ PDF 파일을 찾을 수 없음: " + file.getAbsolutePath());
                return "- 깨진 유리는 종량제 봉투에 버려야 한다.\n- 칫솔은 재활용이 불가능하다.";
            }

            try (PDDocument document = PDDocument.load(file)) {
                PDFTextStripper stripper = new PDFTextStripper();
                return stripper.getText(document);
            } catch (IOException e) {
                e.printStackTrace();
                return "PDF 읽기 오류 발생";
            }
        }

        @Override
        protected List<QuizQuestion> doInBackground() throws Exception {
            HttpClient client = HttpClient.newHttpClient();
            String pdfContent = extractTextFromPdf();
            System.out.println("📄 PDF 로드 완료 (" + pdfContent.length() + "자)");

            String prompt = String.format("""
                당신은 쓰레기 분리배출 교육 전문가입니다.
                다음 [핵심 자료]의 내용만을 바탕으로 일반인이 가장 헷갈려하는 퀴즈 5문제를 출제하세요.
                
                [핵심 자료 시작]
                %s
                [핵심 자료 끝]

                규칙:
                1. 자료에 없는 내용은 절대 지어내지 마세요.
                2. 응답은 오직 JSON 배열 포맷으로만 작성하세요. (부가 설명 금지)
                3. JSON 구조: questionNumber, question, hint, answerOptions(text, rationale, isCorrect)
                """, pdfContent);

            String requestBody = createApiRequestBody(prompt); 
            String fullUrl = API_URL + "?key=" + API_KEY; 

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(fullUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String fullResponseJson = response.body();
                String quizJson = extractQuizArrayFromJson(fullResponseJson);
                
                Gson gson = new Gson();
                TypeToken<List<QuizQuestion>> token = new TypeToken<List<QuizQuestion>>() {};
                return gson.fromJson(quizJson, token.getType());

            } else {
                throw new IOException("API 호출 실패. 응답 코드: " + response.statusCode() + ", 메시지: " + response.body());
            }
        }

        @Override
        protected void done() {
            try {
                List<QuizQuestion> questions = get();
                gui.loadQuestions(questions);
                
                // [DB 저장] 생성된 퀴즈를 DB에 백업
                new Thread(() -> {
                    QuizDAO quizDAO = new QuizDAO();
                    quizDAO.saveQuizQuestions(questions);
                }).start();

            } catch (InterruptedException | ExecutionException e) {
                String errorMsg = "퀴즈 로드 중 오류 발생: " + e.getMessage();
                if (e.getCause() != null) {
                    errorMsg = "퀴즈 로드 중 오류 발생: " + e.getCause().getMessage();
                }
                gui.showError(errorMsg);
                e.printStackTrace();
            }
        }

        private String createApiRequestBody(String prompt) {
            String escapedPrompt = prompt
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");

            return String.format("""
                {
                  "contents": [{ "parts": [{ "text": "%s" }] }],
                  "generationConfig": { "responseMimeType": "application/json" }
                }
                """, escapedPrompt);
        }

        private String extractQuizArrayFromJson(String fullResponse) {
            Gson gson = new Gson();
            try {
                JsonObject rootObject = gson.fromJson(fullResponse, JsonObject.class);
                if (rootObject.has("candidates")) {
                    JsonArray candidates = rootObject.getAsJsonArray("candidates");
                    if (candidates.size() > 0) {
                        JsonObject content = candidates.get(0).getAsJsonObject().getAsJsonObject("content");
                        if (content.has("parts")) {
                            JsonArray parts = content.getAsJsonArray("parts");
                            if (parts.size() > 0) {
                                String quizJsonText = parts.get(0).getAsJsonObject().get("text").getAsString();
                                if (quizJsonText.startsWith("```")) {
                                    quizJsonText = quizJsonText.substring(quizJsonText.indexOf('\n') + 1);
                                    if (quizJsonText.endsWith("```")) {
                                        quizJsonText = quizJsonText.substring(0, quizJsonText.lastIndexOf("```")).trim();
                                    }
                                }
                                return quizJsonText;
                            }
                        }
                    }
                }
                throw new JsonSyntaxException("Gemini 응답 구조 오류: " + fullResponse);
            } catch (JsonSyntaxException e) {
                throw new JsonSyntaxException("파싱 오류: " + fullResponse, e);
            } catch (Exception e) {
                throw new RuntimeException("일반 오류: " + e.getMessage(), e);
            }
        }
    }
}