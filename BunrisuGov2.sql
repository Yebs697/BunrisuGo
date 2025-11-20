

CREATE SCHEMA IF NOT EXISTS `BunrisuGo` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `BunrisuGo` ;


-- Table `Category` - 1. 분류 카테고리

CREATE TABLE IF NOT EXISTS `Category` (
  `category_id` INT NOT NULL AUTO_INCREMENT,
  `category_name` VARCHAR(50) NOT NULL, -- 대분류 명칭 (13개[종이, 종이팩, 무색페트병, 플라스틱류, 비닐류, 발포합성수지, 유리병, 금속류, 의류 및 원단, 전지, 조명제품, 전기전자제품, 그 외(기타)])
  `general_exception` TEXT NULL, -- 카테고리 전체에 대한 일반적인 예외 사항
  PRIMARY KEY (`category_id`),
  UNIQUE INDEX `category_name_UNIQUE` (`category_name` ASC) VISIBLE
) ENGINE = InnoDB;



-- Table `Item` - 2. 분류 세부 품목

CREATE TABLE IF NOT EXISTS `Item` (
  `item_id` INT NOT NULL AUTO_INCREMENT,
  `category_id` INT NOT NULL, -- 소속된 카테고리 (FK)
  `item_name` VARCHAR(50) NOT NULL, -- 세부 품목 명칭 (예: PET, 우유팩)
  `image_url` VARCHAR(255) NULL, -- 품목 대표 이미지 경로
  `search_keywords` TEXT NULL, -- 검색을 위한 키워드
  PRIMARY KEY (`item_id`),
  INDEX `fk_Item_Category_idx` (`category_id` ASC) VISIBLE,
  CONSTRAINT `fk_Item_Category`
    FOREIGN KEY (`category_id`)
    REFERENCES `Category` (`category_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE = InnoDB;

-- table 'recycling_method' - 2.1 상세 재활용 방법

CREATE TABLE IF NOT EXISTS `Item_Detail` (
  `detail_id` INT NOT NULL AUTO_INCREMENT,
  `item_id` INT NOT NULL, -- 상위 품목 (FK)
  `method_description` TEXT NOT NULL, -- 상세 재활용 방법 설명
  `item_exception` TEXT NULL, -- 세부 품목별 개별 예외 사항
  PRIMARY KEY (`detail_id`),
  INDEX `fk_Item_Detail_Item_idx` (`item_id` ASC) VISIBLE,
  CONSTRAINT `fk_Item_Detail_Item`
    FOREIGN KEY (`item_id`)
    REFERENCES `Item` (`item_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE = InnoDB;



-- Table `Quiz` - 3. 퀴즈

CREATE TABLE IF NOT EXISTS `Quiz` (
  `quiz_id` INT NOT NULL AUTO_INCREMENT,
  `question_type` VARCHAR(20) NOT NULL, -- 퀴즈 유형 (OX, 객관식 4지선다)
  `question_text` TEXT NOT NULL, -- 퀴즈 질문 내용
  `correct_answer` VARCHAR(255) NOT NULL, -- 정답
  `explanation` TEXT NULL, -- 정답에 대한 해설
  `related_item_id` INT NULL, -- 관련 품목 정보 (추가할지 검토 중인 기능)
  PRIMARY KEY (`quiz_id`),
  INDEX `fk_Quiz_Item_idx` (`related_item_id` ASC) VISIBLE,
  CONSTRAINT `fk_Quiz_Item`
    FOREIGN KEY (`related_item_id`)
    REFERENCES `Item` (`item_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE = InnoDB;



-- Table `Attendance` - 4. 출석 기록

CREATE TABLE IF NOT EXISTS `Attendance` (
  `attend_id` INT NOT NULL AUTO_INCREMENT,
  `user_identifier` VARCHAR(50) NOT NULL, -- 사용자 식별자 (기기 ID)
  `attend_date` DATE NOT NULL, -- 출석 날짜
  `points_earned` INT NOT NULL, -- 지급된 포인트
  `is_consecutive_bonus` TINYINT DEFAULT 0, -- 연속 출석 보너스 포인트 지급 여부 (BOOLEAN 대신 TINYINT 사용)
  `consecutive_count` INT NOT NULL, -- 이 시점의 연속 출석 일수
  PRIMARY KEY (`attend_id`),
  UNIQUE INDEX `uk_user_date` (`user_identifier` ASC, `attend_date` ASC) VISIBLE
) ENGINE = InnoDB;



-- Table `Points` - 5. 포인트 (총 포인트 관리)

CREATE TABLE IF NOT EXISTS `Points` (
  `point_id` INT NOT NULL AUTO_INCREMENT,
  `user_identifier` VARCHAR(50) NOT NULL, -- 사용자 식별자 (Unique 설정으로 사용자별 1개 레코드만 유지)
  `total_points` INT NOT NULL DEFAULT 0, -- 현재 보유하고 있는 합 포인트
  `last_update_date` DATETIME NOT NULL, -- 포인트 최종 변경 시각
  PRIMARY KEY (`point_id`),
  UNIQUE INDEX `user_identifier_UNIQUE` (`user_identifier` ASC) VISIBLE
) ENGINE = InnoDB;



-- Table `Point_History` - 6. 포인트 내역 (상세 내역 기록)

CREATE TABLE IF NOT EXISTS `Point_History` (
  `history_id` INT NOT NULL AUTO_INCREMENT,
  `user_identifier` VARCHAR(50) NOT NULL, -- 관련 사용자 식별자
  `change_type` VARCHAR(50) NOT NULL, -- 변경 유형 (예: '출석', '퀴즈정답')
  `points_change` INT NOT NULL, -- 변경된 포인트 양 (+ 또는 - 값)
  `change_date` DATETIME NOT NULL,
  PRIMARY KEY (`history_id`)
) ENGINE = InnoDB;