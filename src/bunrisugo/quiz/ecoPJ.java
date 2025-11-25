package bunrisugo.quiz;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    private JLabel questionLabel;
    private JRadioButton[] answerButtons;
    private ButtonGroup buttonGroup;
    private JButton controlButton;
    private JTextArea feedbackArea;
    private JLabel statusLabel;

    public ecoPJ() {
        super("AI 기반 쓰레기 분리배출 퀴즈");
        setupGUI();
        setVisible(true); // 창 표시
        fetchQuestions(); // 퀴즈 데이터 로드 시작
    }

    // ------------------------------------------------
    // GUI 초기화 및 설정
    // ------------------------------------------------
    private void setupGUI() {
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // 메인 메뉴에서 호출될 때 전체 앱 종료 방지

        statusLabel = new JLabel("AI로부터 퀴즈 문제를 로드 중입니다...");
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
    // 5. 데이터 모델 클래스 (필드를 public으로 변경하여 Gson 접근 오류 해결)
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
    // 6. API 호출 로직 (SwingWorker 중첩 클래스 - PDF 지식 기반으로 수정)
    // ------------------------------------------------
    static class ApiQuizFetcher extends SwingWorker<List<QuizQuestion>, Void> {

        // ⚠️ 사용자님의 유효한 Gemini Key입니다. (실제 사용 시 재확인 필요)
        private static final String API_KEY = "AIzaSyAQohKyzJn_gXm6Q7RXW4tDu9dReOWgyQk";
        private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"; 
        private final ecoPJ gui;

        // 💡 [추가] 퀴즈의 근거가 될 PDF 핵심 내용을 문자열로 정의합니다.
        private static final String QUIZ_REFERENCE_DATA = """
            - 깨진 유리, 전구, 도자기류, 내열식기류는 재활용 '유리병'이 아니며, 불연성 마대 또는 종량제 봉투로 배출해야 한다.
            - 음식물류 폐기물이 아닌 것: 닭/소/돼지의 뼈, 조개/게 등 갑각류 껍데기, 복숭아/감 등 딱딱한 씨, 달걀 껍데기, 양파/마늘 껍질, 옥수수대, 한약재 찌꺼기, 티백. 이것들은 종량제 봉투로 배출한다.
            - 마트 영수증(감열지), 비닐 코팅된 종이, 사진은 재활용 '종이류'가 아니며, 종량제 봉투로 배출한다.
            - 알약 포장재(플라스틱+알루미늄)는 '복합 재질'이므로 재활용이 어려워 종량제 봉투로 배출한다.
            - 폐의약품은 약국이나 보건소의 전용수거함으로 배출해야 한다.
            - 칫솔, 고무장갑, 물티슈, 볼펜, 라이터는 재활용품이 아니며, 종량제 봉투로 배출한다.
            """;

        // 💡 [수정] AI_PROMPT를 수정하여 위 자료를 AI에게 전달합니다.
        private static final String AI_PROMPT = String.format("""
            당신은 '재활용품 분리배출 가이드라인' 문서를 기반으로 퀴즈를 출제하는 전문가입니다.

            먼저, 다음 [핵심 자료]를 정독하세요.
            [핵심 자료 시작]
            %s
            [핵심 자료 끝]

            이제, 반드시 위 [핵심 자료]의 내용에만 근거하여 '중급 난이도'의 헷갈리기 쉬운 분리배출 퀴즈 5문제를 JSON 배열로만 응답하세요.
            자료에 언급되지 않은 내용은 퀴즈로 만들지 마세요.
            
            규칙:
            1. 각 퀴즈 객체는 'questionNumber', 'question', 'hint', 4개의 'answerOptions' 배열을 포함해야 합니다.
            2. 'answerOptions'의 각 요소는 'text', 'rationale'(해설), 'isCorrect'(boolean)를 포함해야 합니다.
            3. 응답은 순수한 JSON 배열이어야 하며, 다른 텍스트는 포함하지 마세요.
            """, QUIZ_REFERENCE_DATA); // 퀴즈 자료를 프롬프트에 삽입

        public ApiQuizFetcher(ecoPJ gui) {
            this.gui = gui;
        }

        @Override
        protected List<QuizQuestion> doInBackground() throws Exception {
            HttpClient client = HttpClient.newHttpClient();
            // 💡 AI_PROMPT가 이미 위에서 포맷팅되었으므로 그대로 사용합니다.
            String requestBody = createApiRequestBody(AI_PROMPT); 
            
            // 1. Gemini Key를 URL 쿼리 파라미터로 추가하여 fullUrl 생성
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
                // 💡 오류 메시지를 좀 더 구체적으로 표시
                String errorMsg = "퀴즈 로드 중 오류 발생: " + e.getMessage();
                if (e.getCause() != null) {
                    errorMsg = "퀴즈 로드 중 오류 발생: " + e.getCause().getMessage();
                }
                gui.showError(errorMsg);
            }
        }

        // 2. 요청 본문 구조를 Gemini API 형식으로 변경 및 JSON 이스케이프 강화
        private String createApiRequestBody(String prompt) {
            // 프롬프트 내의 특수문자 이스케이프 처리 (JSON 파싱 오류 해결)
            String escapedPrompt = prompt
                .replace("\\", "\\\\") // 역슬래시를 먼저 이스케이프
                .replace("\"", "\\\"") // 큰따옴표를 이스케이프
                .replace("\n", "\\n"); // 개행 문자를 이스케이프

            String json = String.format("""
                {
                  "contents": [
                    {
                      "parts": [
                        {
                          "text": "%s"
                        }
                      ]
                    }
                  ],
                  "generationConfig": { 
                    "responseMimeType": "application/json" 
                  }
                }
                """, escapedPrompt); // 강화된 이스케이프 프롬프트 사용
            return json;
        }

        // 💡 3. JSON 구조 오류 (Expected BEGIN_ARRAY but was BEGIN_OBJECT) 해결 로직
        private String extractQuizArrayFromJson(String fullResponse) {
            Gson gson = new Gson();
            try {
                // 1. 전체 응답을 최상위 객체로 파싱
                JsonObject rootObject = gson.fromJson(fullResponse, JsonObject.class);

                // 2. 퀴즈 배열이 담긴 필드를 추출 (Gemini API의 표준 응답 경로)
                if (rootObject.has("candidates")) {
                    JsonArray candidates = rootObject.getAsJsonArray("candidates");
                    if (candidates.size() > 0) {
                        JsonObject content = candidates.get(0)
                                            .getAsJsonObject().getAsJsonObject("content");
                        if (content.has("parts")) {
                            JsonArray parts = content.getAsJsonArray("parts");
                            if (parts.size() > 0) {
                                // 3. 'text' 필드에 담긴 퀴즈 JSON 문자열 추출
                                String quizJsonText = parts.get(0)
                                                            .getAsJsonObject().get("text").getAsString();
                                
                                // 4. LLM이 넣어준 마크다운 블록(```json...) 제거
                                if (quizJsonText.startsWith("```")) {
                                    quizJsonText = quizJsonText.substring(quizJsonText.indexOf('\n') + 1);
                                    if (quizJsonText.endsWith("```")) {
                                        quizJsonText = quizJsonText.substring(0, quizJsonText.lastIndexOf("```")).trim();
                                    }
                                }
                                
                                return quizJsonText; // 순수한 JSON 배열 문자열 반환
                            }
                        }
                    }
                }
                // 퀴즈 데이터를 예상 경로에서 찾지 못했을 경우
                throw new JsonSyntaxException("Gemini 응답 구조에서 퀴즈 데이터를 찾을 수 없습니다: " + fullResponse);
            } catch (JsonSyntaxException e) {
                // 파싱 오류 발생 시 원본 응답을 다시 확인해보도록 오류 메시지를 던집니다.
                throw new JsonSyntaxException("응답 파싱 오류. 응답 원본 확인 필요: " + fullResponse, e);
            } catch (Exception e) {
                throw new RuntimeException("응답 처리 중 일반 오류 발생: " + e.getMessage(), e);
            }
        }
    }
}