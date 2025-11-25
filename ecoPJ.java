package echoprj;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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

    private JLabel questionLabel;
    private JRadioButton[] answerButtons;
    private ButtonGroup buttonGroup;
    private JButton controlButton;
    private JTextArea feedbackArea;
    private JLabel statusLabel;

    public ecoPJ() {
        super("AI 기반 쓰레기 분리배출 퀴즈 (MySQL 연동 버전)");
        setupGUI();
        fetchQuestions(); // 퀴즈 데이터 로드 시작
    }

    // ------------------------------------------------
    // GUI 초기화 및 설정
    // ------------------------------------------------
    private void setupGUI() {
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        statusLabel = new JLabel("퀴즈를 생성 중입니다...");
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        add(statusLabel);
        
        questionLabel = new JLabel("질문 대기 중...");
        questionLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        add(questionLabel);

        buttonGroup = new ButtonGroup();
        answerButtons = new JRadioButton[4];
        for (int i = 0; i < answerButtons.length; i++) {
            answerButtons[i] = new JRadioButton("보기 " + (i + 1));
            buttonGroup.add(answerButtons[i]);
            answerButtons[i].setVisible(false);
            add(answerButtons[i]);
        }

        controlButton = new JButton("퀴즈 로드 중...");
        controlButton.setEnabled(false);
        add(controlButton);
        
        feedbackArea = new JTextArea(5, 30);
        feedbackArea.setEditable(false);
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        add(new JScrollPane(feedbackArea));

        pack();
        setLocationRelativeTo(null);
    }
    
    // ------------------------------------------------
    // 퀴즈 로드 및 표시 로직
    // ------------------------------------------------
    private void fetchQuestions() {
        new ApiQuizFetcher(this).execute();
    }
    
    // SwingWorker 완료 후 호출 (GUI 스레드)
    public void loadQuestions(List<QuizQuestion> newQuestions) {
        if (newQuestions == null || newQuestions.isEmpty()) {
            showError("AI가 유효한 퀴즈를 생성하지 못했거나, 응답이 비어있습니다.");
            return;
        }
        this.questions = newQuestions;
        statusLabel.setText("퀴즈 로드 완료. 총 " + questions.size() + "문제");
        controlButton.setEnabled(true);
        controlButton.setText("정답 제출 및 다음 문제");
        showQuestion(currentQuestionIndex);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "오류", JOptionPane.ERROR_MESSAGE);
        statusLabel.setText("오류 발생: " + message);
        controlButton.setText("종료");
        controlButton.setEnabled(true);
        controlButton.addActionListener(e -> System.exit(0));
    }

    private void showQuestion(int index) {
        if (index >= questions.size()) {
            showResult();
            return;
        }

        QuizQuestion q = questions.get(index);
        questionLabel.setText("<html><b>[문제 " + (index + 1) + "/" + questions.size() + "]</b> " + q.getQuestion() + "<br>힌트: " + q.getHint() + "</html>");
        feedbackArea.setText("");
        buttonGroup.clearSelection();

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
        questionLabel.setText("퀴즈 종료!");
        feedbackArea.setText("총 " + questions.size() + "문제 중 " + score + "개를 맞히셨습니다!");
        for (JRadioButton btn : answerButtons) {
            btn.setVisible(false);
        }
        controlButton.setVisible(false);
        statusLabel.setText("최종 점수: " + score + "/" + questions.size());
        
        // [DB 저장] 게임이 끝나면 DB에 저장합니다.
        saveScoreToDB(score, questions.size());
    }

    // ------------------------------------------------
    // [수정 완료] MySQL DB 저장 로직 (Point_History 테이블 맞춤)
    // ------------------------------------------------
    private void saveScoreToDB(int userScore, int totalQuestions) {
        // ⚠️ [수정 필수] 본인의 MySQL 아이디와 비밀번호를 입력하세요!
        String DB_ID = "root";    // 예: root
        String DB_PW = "1234";    // 예: 1234 (본인 비번으로 변경!)

        // MySQL 연결 정보 (DB이름: bunrisugo)
        String DB_URL = "jdbc:mysql://localhost:3306/bunrisugo?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8";
        
        // SQL문 수정: Point_History 테이블의 실제 컬럼명 사용
        // user_identifier: 유저ID, change_type: 변경타입, points_change: 점수
        String SQL = "INSERT INTO Point_History (user_identifier, change_type, points_change, change_date) VALUES (?, ?, ?, NOW())";

        try {
            // 1. 드라이버 로드
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // 2. 연결 및 전송
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_ID, DB_PW);
                 PreparedStatement pstmt = conn.prepareStatement(SQL)) {
                
                pstmt.setString(1, "GuestUser"); // user_identifier (임시 ID)
                pstmt.setString(2, "퀴즈완료");    // change_type (적립 사유)
                pstmt.setInt(3, userScore);      // points_change (획득 점수)

                int rows = pstmt.executeUpdate();
                if (rows > 0) {
                    System.out.println("✅ DB 저장 성공! (" + userScore + "점)");
                    JOptionPane.showMessageDialog(this, "결과가 DB(Point_History)에 저장되었습니다!");
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ MySQL 드라이버를 찾을 수 없습니다. (mysql-connector-j.jar 확인 필요)");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ DB 연결/저장 실패: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "DB 저장 실패: " + e.getMessage());
        }
    }

    // ------------------------------------------------
    // 이벤트 리스너 (Listener)
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
                JOptionPane.showMessageDialog(ecoPJ.this, "답변을 선택해 주세요.", "경고", JOptionPane.WARNING_MESSAGE);
                return;
            }

            QuizQuestion currentQuestion = questions.get(currentQuestionIndex);
            AnswerOption selectedOption = currentQuestion.getAnswerOptions().get(selectedIndex);

            if (selectedOption.isCorrect()) {
                score++;
                feedbackArea.setText("✅ 정답입니다! \n\n해설: " + selectedOption.getRationale());
            } else {
                feedbackArea.setText("❌ 오답입니다. \n\n해설: " + selectedOption.getRationale());
            }
            
            controlButton.setText("다음 문제로");
            for (ActionListener listener : controlButton.getActionListeners()) {
                controlButton.removeActionListener(listener);
            }
            controlButton.addActionListener(new NextQuestionListener());
        }
    }

    private class NextQuestionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            currentQuestionIndex++;
            showQuestion(currentQuestionIndex);
        }
    }
    
    // ------------------------------------------------
    // 4. 메인 메서드
    // ------------------------------------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ecoPJ().setVisible(true);
        });
    }

    // ------------------------------------------------
    // 5. 데이터 모델 클래스
    // ------------------------------------------------
    static class AnswerOption {
        public String text;
        public String rationale;
        public boolean isCorrect;

        public AnswerOption() {} 
        public String getText() { return text; }
        public String getRationale() { return rationale; }
        public boolean isCorrect() { return isCorrect; }
    }

    static class QuizQuestion {
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
    // 6. API 호출 로직 (PDF 읽기 기능 통합됨)
    // ------------------------------------------------
    static class ApiQuizFetcher extends SwingWorker<List<QuizQuestion>, Void> {

        private static final String API_KEY = "Your API KEY"; // 실제 키 확인 필요
        private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"; 
        
        // 📌 읽어올 PDF 파일 이름 설정
        private static final String PDF_FILE_NAME = "recycling_guide.pdf";
        
        private final ecoPJ gui;

        public ApiQuizFetcher(ecoPJ gui) {
            this.gui = gui;
        }

        // 📌 PDF 파일에서 텍스트를 추출하는 메서드
        private String extractTextFromPdf() {
            File file = new File(PDF_FILE_NAME);
            if (!file.exists()) {
                // 파일이 없을 경우 기본 텍스트 반환 (오류 방지)
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
            
            // 1. PDF 내용을 먼저 읽어옵니다.
            String pdfContent = extractTextFromPdf();
            System.out.println("📄 PDF 로드 완료 (" + pdfContent.length() + "자)");

            // 2. 프롬프트를 동적으로 생성합니다.
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
                gui.loadQuestions(get());
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