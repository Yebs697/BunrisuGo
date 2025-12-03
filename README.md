
# 자바 응용 프로젝트 12분반 2팀

## BunrisuGo - 재활용 분리배출 가이드 프로그램 

#### 개발 팀원
- 예병성 (2020243085)
- 송용준 (2021243040)
- 전희재 (2021243076)
- 김재현 (2022243109)

---------------------------------

## 1. 프로그램 주제 및 목표
#### 프로젝트 명
**분리수GO** (분리수거 + GO)

#### 주제
쓰레기 분리배출 가이드 프로그램

### 개발 목표
- 올바른 재활용 분리배출 방법을 안내하여 환경보호 실천 지원 (상세 설명은 최종 보고서 참고)
- 퀴즈와 출석체크를 통한 포인트 적립제도로 지속적인 사용 동기 부여
- AI 활용한 퀴즈로 분리수거 지식 학습
- 적립한 포인트를 사용할 수 있는 상점 사용 동기 강화


---

## 2. 프로그램 설치 가이드

### 2.1 필수 요구사항

#### Java 개발 환경
- **JDK 11 이상** (Java HTTP Client 사용을 위해 필수)
- **IDE**: Eclipse 또는 IntelliJ IDEA 권장

#### 데이터베이스
- **MySQL 8.0 이상**
- MySQL Server가 실행 중이어야 합니다

#### 외부 라이브러리
다음 JAR 파일들을 프로젝트 Build Path에 추가해야 합니다:

| 라이브러리 | 용도 | 다운로드 링크 |
|-----------|------|--------------|
| **MySQL Connector/J** | JDBC 드라이버 (필수) | [다운로드](https://dev.mysql.com/downloads/connector/j/) |
| **Gson** | AI 퀴즈 JSON 파싱 | [다운로드](https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/) |
| **Apache PDFBox** | AI 퀴즈 PDF 처리 | [다운로드](https://pdfbox.apache.org/download.html) |

**PDFBox 필수 파일 (3개):**
- `pdfbox-3.0.0.jar`
- `pdfbox-io-3.0.0.jar`
- `fontbox-3.0.0.jar`

> **참고:** Gson과 PDFBox는 AI 퀴즈 기능에만 사용됩니다. 다른 기능(재활용품 검색, 출석체크, 포인트)은 MySQL Connector만 있으면 작동합니다.

---

### 2.2 데이터베이스 설정

#### Step 1: MySQL 서버 실행 확인
```bash
# Windows
net start MySQL80

# Mac/Linux
sudo systemctl start mysql
```

#### Step 2: 데이터베이스 생성
MySQL에 접속하여 두 개의 데이터베이스를 생성합니다:

```bash
# MySQL 접속
mysql -u root -p
```

```sql
-- BunrisuGo 데이터베이스 생성 (출석, 포인트, 퀴즈용)
CREATE DATABASE BunrisuGo DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

-- javapj 데이터베이스 생성 (재활용품 검색용)
CREATE DATABASE javapj DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
```

#### Step 3: 테이블 생성
프로젝트 루트에 있는 `BunrisuGov2.sql` 파일을 실행합니다:

```bash
# 방법 1: 명령어로 실행
mysql -u root -p BunrisuGo < BunrisuGov2.sql

# 방법 2: MySQL 내부에서 실행
mysql -u root -p
use BunrisuGo;
source /path/to/BunrisuGov2.sql;
```

> **참고:** `javapj` 데이터베이스의 테이블은 별도 제공되는 SQL 파일로 생성하거나, 재활용품 데이터를 수동으로 입력해야 합니다.

#### Step 4: Java 코드에 MySQL 비밀번호 설정

프로젝트에서 데이터베이스 연결을 위해 두 개의 파일에 비밀번호를 설정해야 합니다.

**파일 1: `src/bunrisugo/util/CommonDBConnection.java`**  
(출석 체크, 포인트, 퀴즈 기능용)

```java
public class CommonDBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/BunrisuGo?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "여기에_MySQL_비밀번호_입력"; // ← 수정
    
    // ...
}
```

**파일 2: `src/bunrisugo/recycling/DatabaseConnection.java`**  
(재활용품 검색 기능용)

```java
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/javapj?serverTimezone=UTC";
    private static final String ID = "root";
    private static final String PASSWORD = "여기에_MySQL_비밀번호_입력"; // ← 수정
    
    // ...
}
```

> **중요:** 두 파일 모두 **동일한 비밀번호**를 입력해야 합니다. (MySQL `root` 계정은 하나의 비밀번호만 사용)

---
### 2.3 리소스 파일 배치

프로젝트가 정상적으로 작동하려면 이미지와 PDF 파일을 올바른 위치에 배치해야 합니다.

#### 디렉토리 구조
```
프로젝트_루트/
├── src/
│   └── bunrisugo/
│       ├── main/
│       ├── recycling/
│       ├── attendance/
│       ├── point/
│       ├── quiz/
│       └── util/
├── recImage/              # 재활용품 검색 이미지
│   ├── sub01_01_1_1_a1.png
│   ├── sub01_01_1_1_a2.png
│   ├── sub01_01_1_1_b1.png
│   └── ...
├── image/                 # 포인트 상점 이미지
│   ├── donation_small.png
│   ├── donation_medium.png
│   ├── donation_large.png
│   ├── gum.png
│   ├── energy_bar.png
│   └── google_giftcard.png
├── recycling_guide.pdf    # AI 퀴즈용 재활용 가이드 PDF
├── BunrisuGov2.sql        # 데이터베이스 스키마
└── README.md

---
### 2.4 API 키 설정

AI 퀴즈 기능을 사용하려면 Google Gemini API 키가 필요합니다.

#### API 키 발급 방법
1. https://aistudio.google.com/app/apikey 접속
2. Google 계정으로 로그인
3. **"Create API Key"** 버튼 클릭
4. API 키 복사

#### 코드에 API 키 설정

**파일:** `src/bunrisugo/quiz/ecoPJ.java`

```java
static class ApiQuizFetcher extends SwingWorker<List<QuizQuestion>, Void> {
    
    private static final String API_KEY = "여기에_발급받은_API키_붙여넣기"; // ← 수정
    
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";
    
    // ...
}
```

> **참고:**
> - API 키 없이도 프로그램의 다른 기능(검색, 출석, 포인트)은 정상 작동합니다
> - AI 퀴즈만 사용 불가능하며, 실행 시 오류 메시지가 표시됩니다

-----

## 3. 프로그램 실행 가이드

### 3.1 메인 프로그램 실행

#### Eclipse에서 실행
1. `src/bunrisugo/main/MainMenu.java` 파일 열기
2. 파일 우클릭 → **Run As** → **Java Application**
3. 메인 메뉴 창이 실행됩니다

#### 명령줄에서 실행
```bash
# 프로젝트 루트 디렉토리에서
javac -d bin -cp "lib/*" src/bunrisugo/main/MainMenu.java
java -cp "bin:lib/*" bunrisugo.main.MainMenu

# Windows의 경우
java -cp "bin;lib/*" bunrisugo.main.MainMenu
```

---


4. 코드 문의처




















