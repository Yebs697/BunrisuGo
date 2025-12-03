-- MySQL dump 10.13  Distrib 9.4.0, for Win64 (x86_64)
--
-- Host: localhost    Database: javapj
-- ------------------------------------------------------
-- Server version	9.4.0

CREATE DATABASE IF NOT EXISTS javapj;
USE javapj;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `common_methods`
--

DROP TABLE IF EXISTS `common_methods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `common_methods` (
  `id` int NOT NULL,
  `material_id` int DEFAULT NULL,
  `step_number` int DEFAULT NULL,
  `description` text,
  PRIMARY KEY (`id`),
  KEY `material_id` (`material_id`),
  CONSTRAINT `common_methods_ibfk_1` FOREIGN KEY (`material_id`) REFERENCES `materials` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `common_methods`
--

LOCK TABLES `common_methods` WRITE;
/*!40000 ALTER TABLE `common_methods` DISABLE KEYS */;
INSERT INTO `common_methods` VALUES (1,1,1,'종이는 골판지,기타 종이로 구분하여 배출'),(2,1,2,'물에 젖거나 이물질이 묻지 않도록 하고 끈으로 묶거나 마대 등에 담아 배출'),(3,2,1,'내용물,빨대,비닐을 제거하고 물로 헹군 후 배출'),(4,2,2,'일반팩과 멸균팩은 구분 없이 배출(재활용업체에서 선별 분리)'),(5,2,3,'종이팩 수거함으로 배출하고, 수거함이 없는 경우 다른 종이류와 구분될 수 있도록 끈 등으로 묶어 종이류 수거함으로 배출'),(6,3,1,'라벨과 내용물을 제거하고 물로 헹군 후 압착하여 뚜껑을 닫아 배출'),(7,4,1,'내용물과 이물질, 부속품을 제거하고 물로 헹군 후 배출'),(8,4,2,'PET, PP, PE, PS 등 재질 구분 없이 배출(선별업체에서 선별)'),(9,4,3,'치약용기와 같이 물로 헹굴수 없는 경우 내용물만 비우고 배출'),(10,5,1,'이물질과 내용물을 제거하고 흩날리지 않도록 투명비닐봉투에 모아서 배출'),(11,5,2,'PP,PE,PS 등 재질 구분없이 배출(비닐은 재질을 분리하지 않고 재활용)'),(12,5,3,'양파 등 농산물을 담는 그물망은 비닐로 함께 배출'),(13,6,1,'부착상표, 테이프 등 스티로폼과 다른 재질은 제거한 배출(재활용 품질 영향)'),(14,6,2,'EPP, EPS, EPE 등 재질과 색상 구분 없이 배출(유색도 EPR 대상품목)'),(15,6,3,'TV, 컴퓨터 등 전자제품 구입시 발생한 발포합성수지는 가급적 구입처로 반납'),(16,7,1,'이물질이 존재하지 않도록 하여 유리병수거함으로 배출'),(17,7,2,'깨진 유리병은 불연성종량제봉투로 배출하거나 신문지 등에 싸서 종량제봉투로 배출'),(18,8,1,'음료, 주류캔, 식료품캔, 기타캔류는 금속캔 수거함에 배출'),(19,8,2,'공기구, 철사, 생활철 등 고철은 고철 수거함에 배출'),(20,9,1,'의류는 다른 사람이 다시 재사용하기 때문에 깨끗한 상태로 의류수거함에 배출'),(21,9,2,'양말과 신발 등 세트로 구성된 것은 묶어서 배출'),(22,9,3,'의류수거함이 없는 문전수거 지역 등에서는 물기에 젖지 않도록 마대 등에 담거나 묶어서 배출'),(23,10,1,'전지는 전용수거함에 배출하거나 전자제품 대리점 등 역회수루트를 통하여 배출'),(24,10,2,'리튬전지는 화재사고의 원인이 되므로 재활용폐기물 또는 일반종량제폐기물로 배출하지 않도록 하며 전용 수거함으로 배출하도록 주의해야 함'),(25,11,1,'형광등과 LED를 깨지지 않도록 분리하여 형광등수거함에 배출'),(26,11,2,'LED조명 중 평판형, 십자형, 원반형 등 모듈과 컨버터가 일체형인 것은 불연성종량제폐기물 또는 대형폐기물 배출'),(27,11,3,'조명이 깨진 경우에는 신문지와 테이프를 이용하여 불연성종량제폐기물로 배출'),(28,12,1,'재사용가능한 전기전자제품은 지역 재활용센터에 판매하거나 무상수거 요청'),(29,12,2,'전기전자제품 신규 구입시 대리점을 통해 역회수 요청'),(30,12,3,'폐가전제품 무상방문수거 서비스(1933-0903)를 이용하여 배출\n- 대상 품목은 \'배출예약시스템(15990903.or.kr)\'에서 확인'),(31,12,4,'소형전지전자제품은 전용수거함에 배출하는 등 지자체에서 정하는 방법에 따라 배출\n- 5개 이상시 배출할 경우 무상수거서비스 이용 가능');
/*!40000 ALTER TABLE `common_methods` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exeption_methods`
--

DROP TABLE IF EXISTS `exeption_methods`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exeption_methods` (
  `id` int NOT NULL,
  `items_id` int DEFAULT NULL,
  `step_number` int DEFAULT NULL,
  `description` text,
  PRIMARY KEY (`id`),
  KEY `items_id` (`items_id`),
  CONSTRAINT `exeption_methods_ibfk_1` FOREIGN KEY (`items_id`) REFERENCES `items` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exeption_methods`
--

LOCK TABLES `exeption_methods` WRITE;
/*!40000 ALTER TABLE `exeption_methods` DISABLE KEYS */;
INSERT INTO `exeption_methods` VALUES (1,1,1,'테이프, 송장, 철핀, 알루미늄박을 제거하고 접어서 배출'),(2,1,2,'테이프를 제거하기 곤란한 경우에는 칼이나 가위로 잘라서 배출'),(3,5,1,'비닐, 플라스틱, 코팅된 전단지, 노트,달력 스프링 등 제거'),(4,5,2,'종이컵은 압착하여 봉투에 넣거나 묶어서 기타종이로 배출'),(5,5,3,'1회용 종이컵이 자원순환보증금제 대상인 경우 해당 매장, 무인회수기에 반환'),(6,5,4,'양면이 코팅되어 찢어지지 않는 경우 일반종량제폐기물로 배출'),(7,24,1,'유리병 수거함에 배출하고 수거함이 없는 경우 재활용폐기물 통합배출'),(8,24,2,'3색은 구분없이 배출(재활용업체에서 3색과 혼색 유리병으로 선별)'),(9,26,1,'빈용기반환 수집소 또는 무인회수기에 반환하여 보증금 환급'),(10,26,2,'빈용기는 유리병 그대로 재사용하기 때문에 깨끗하고 깨지지 않도록 배출'),(11,27,1,'내용물을 비우고 물로 헹군 후 배출'),(12,28,1,'폭발,화재 예방을 위해 노즐을 눌러 가스를 제거한 후 배출'),(13,29,1,'가위, 칼, 송곳 등 날카로운 금속은 종이와 테이프를 이용하여 안전하게 감싼 후 일반종량제 폐기물로 배출'),(14,29,2,'우산, 철삽, 스테이플러와 나무, 플라스틱 등 재질이 많이 섞인 경우 일반종량제 폐기물로 배출');
/*!40000 ALTER TABLE `exeption_methods` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `item_notes`
--

DROP TABLE IF EXISTS `item_notes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `item_notes` (
  `id` int NOT NULL,
  `materials_id` int DEFAULT NULL,
  `note_order` int DEFAULT NULL,
  `notes` varchar(100) DEFAULT NULL,
  `content` text,
  PRIMARY KEY (`id`),
  KEY `materials_id` (`materials_id`),
  CONSTRAINT `item_notes_ibfk_1` FOREIGN KEY (`materials_id`) REFERENCES `materials` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `item_notes`
--

LOCK TABLES `item_notes` WRITE;
/*!40000 ALTER TABLE `item_notes` DISABLE KEYS */;
INSERT INTO `item_notes` VALUES (1,1,1,'택배 송장','일반종량제폐기물로 배출해야하는 품목'),(2,1,2,'영수증(감열지)','일반종량제폐기물로 배출해야하는 품목'),(3,1,3,'색지','일반종량제폐기물로 배출해야하는 품목'),(4,1,4,'종이호일','일반종량제폐기물로 배출해야하는 품목'),(5,1,5,'코팅전단지','일반종량제폐기물로 배출해야하는 품목'),(6,3,1,'식품용기','플라스틱류로 배출해야하는 품목'),(7,3,2,'워셔액 용기','플라스틱류로 배출해야하는 품목'),(8,3,3,'컵(커피컵)','플라스틱류로 배출해야하는 품목'),(9,3,4,'플라스틱용기','플라스틱류로 배출해야하는 품목'),(10,4,1,'펌프(펌핑용기)','일반종량제폐기물로 배출해야하는 품목'),(11,4,2,'멜라민 그릇','일반종량제폐기물로 배출해야하는 품목'),(12,4,3,'일회용 면도기','일반종량제폐기물로 배출해야하는 품목'),(13,4,4,'파일철','일반종량제폐기물로 배출해야하는 품목'),(14,4,5,'CD,DVD','일반종량제폐기물로 배출해야하는 품목'),(15,5,1,'오염된 비닐','일반종량제폐기물로 배출해야하는 품목'),(16,5,2,'돗자리','일반종량제폐기물로 배출해야하는 품목'),(17,5,3,'천막','일반종량제폐기물로 배출해야하는 품목'),(18,5,4,'식탁매트(PVC)','일반종량제폐기물로 배출해야하는 품목'),(19,5,5,'고무장갑','일반종량제폐기물로 배출해야하는 품목'),(20,7,1,'거울','불연성종량제봉투로 배출해야하는 품목'),(21,7,2,'내열 유리제품','불연성종량제봉투로 배출해야하는 품목'),(22,7,3,'크리스털 제품','불연성종량제봉투로 배출해야하는 품목'),(23,7,4,'도자기','불연성종량제봉투로 배출해야하는 품목'),(24,9,1,'인형','일반종량제폐기물 또는 대형폐기물로 배출해야 하는 품목'),(25,9,2,'인라인스케이트','일반종량제폐기물 또는 대형폐기물로 배출해야 하는 품목'),(26,9,3,'보냉가방','일반종량제폐기물 또는 대형폐기물로 배출해야 하는 품목'),(27,9,4,'솜이불, 베개 등 침구류','일반종량제폐기물 또는 대형폐기물로 배출해야 하는 품목');
/*!40000 ALTER TABLE `item_notes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `items`
--

DROP TABLE IF EXISTS `items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `items` (
  `id` int NOT NULL,
  `material_id` int DEFAULT NULL,
  `name` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `material_id` (`material_id`),
  CONSTRAINT `items_ibfk_1` FOREIGN KEY (`material_id`) REFERENCES `materials` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `items`
--

LOCK TABLES `items` WRITE;
/*!40000 ALTER TABLE `items` DISABLE KEYS */;
INSERT INTO `items` VALUES (1,1,'골판지류'),(2,1,'신문지'),(3,1,'과자상자(백판지)'),(4,1,'책자'),(5,1,'기타종이류'),(6,2,'일반팩'),(7,2,'주스팩'),(8,2,'소주팩'),(9,2,'두유팩'),(10,3,'먹는샘물'),(11,3,'음료'),(12,4,'PET용기'),(13,4,'PP용기'),(14,4,'PE용기'),(15,4,'PS용기'),(16,4,'OTHER용기'),(17,5,'비닐포장재'),(18,5,'1회용 비닐봉투'),(19,5,'필름류'),(20,6,'식품용기'),(21,6,'과일 난좌'),(22,6,'전자제품 포장용기'),(23,6,'단열제'),(24,7,'3색 유리병'),(25,7,'이색'),(26,7,'빈용기보증금대상유리병'),(27,8,'금속캔'),(28,8,'기타캔류'),(29,8,'고철'),(30,9,'재킷'),(31,9,'티셔츠'),(32,9,'바지'),(33,9,'신발'),(34,9,'가방'),(35,10,'수은전지'),(36,10,'리튬1차전지'),(37,10,'알칼리망간전지'),(38,10,'니켈,카드뮴전지'),(39,10,'리튬이온전지'),(40,10,'납축전지'),(41,11,'직관형형광램프'),(42,11,'환형형광램프'),(43,11,'안정기내장형램프'),(44,11,'전구형'),(45,11,'직관형'),(46,12,'냉장고'),(47,12,'TV(디스플레이기기)'),(48,12,'휴대폰(통신,사무기기)'),(49,12,'선풍기(일반전기 전자제품)');
/*!40000 ALTER TABLE `items` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `materials`
--

DROP TABLE IF EXISTS `materials`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `materials` (
  `id` int NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `materials`
--

LOCK TABLES `materials` WRITE;
/*!40000 ALTER TABLE `materials` DISABLE KEYS */;
INSERT INTO `materials` VALUES (1,'종이'),(2,'종이팩'),(3,'무색페트병'),(4,'플라스틱류'),(5,'비닐류'),(6,'발포합성수지'),(7,'유리병'),(8,'금속류'),(9,'의류 및 원단'),(10,'전지'),(11,'조명제품'),(12,'전기전자제품');
/*!40000 ALTER TABLE `materials` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `searchitem`
--

DROP TABLE IF EXISTS `searchitem`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `searchitem` (
  `id` int NOT NULL,
  `material_id` int DEFAULT NULL,
  `searchkeyword` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `searchitem`
--

LOCK TABLES `searchitem` WRITE;
/*!40000 ALTER TABLE `searchitem` DISABLE KEYS */;
INSERT INTO `searchitem` VALUES (1,1,'종이'),(2,1,'신문지'),(3,1,'골판지'),(4,2,'종이팩'),(5,2,'주스팩'),(6,2,'우유팩'),(7,3,'무색페트병'),(8,3,'생수병'),(9,3,'투명페트'),(10,4,'플라스틱'),(11,4,'PET용기'),(12,4,'플라스틱용기'),(13,5,'비닐'),(14,5,'비닐봉투'),(15,5,'포장비닐'),(16,6,'스티로폼'),(17,6,'발포합성수지'),(18,6,'전자제품포장재'),(19,7,'유리병'),(20,7,'빈병'),(21,7,'색유리병'),(22,8,'캔'),(23,8,'고철'),(24,8,'금속류'),(25,9,'의류'),(26,9,'헌옷'),(27,9,'신발'),(28,10,'전지'),(29,10,'건전지'),(30,10,'리튬전지'),(31,11,'형광등'),(32,11,'LED램프'),(33,11,'조명제품'),(34,12,'전자제품'),(35,12,'폐가전'),(36,12,'냉장고');
/*!40000 ALTER TABLE `searchitem` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-11-18 13:57:07
