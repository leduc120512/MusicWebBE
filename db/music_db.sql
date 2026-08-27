-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: music_db
-- ------------------------------------------------------
-- Server version	8.0.46

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

-- ---------------------------------------------------------------------------
--  music_db - full schema + seed data
--
--  Import:  mysql -u root -p < db/music_db.sql
--
--  Taken from MySQL 8.0.46 and normalised so it imports on a clean server:
--   * CREATE DATABASE pinned to utf8mb4 / utf8mb4_unicode_ci
--   * root@localhost DEFINER stripped from the 4 views, so any user can import
--   * collation unified on utf8mb4_unicode_ci
--
--  The same script also repairs MariaDB-only syntax (TEXT DEFAULT NULL,
--  current_timestamp(), SET NAMES utf8) if you ever re-dump from MariaDB.
--
--  All passwords are BCrypt. See db/README.md for the accounts you can sign in
--  with, and db/patches/2026-08-27-hash-plaintext-passwords.sql for the
--  migration that converted the last clear-text rows.
-- ---------------------------------------------------------------------------


--
-- Current Database: `music_db`
--

CREATE DATABASE IF NOT EXISTS `music_db`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE `music_db`;

--
-- Table structure for table `albums`
--

DROP TABLE IF EXISTS `albums`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `albums` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `cover_image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `release_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `artist_id` bigint NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_title` (`title`),
  KEY `idx_artist` (`artist_id`),
  KEY `idx_release_date` (`release_date`),
  KEY `idx_albums_title_fulltext` (`title`),
  KEY `idx_albums_release_date_desc` (`release_date`),
  CONSTRAINT `albums_ibfk_1` FOREIGN KEY (`artist_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `albums`
--

LOCK TABLES `albums` WRITE;
/*!40000 ALTER TABLE `albums` DISABLE KEYS */;
INSERT INTO `albums` VALUES (14,'Ngày hay đêm','Hẹn kiếp sau có nhau trọn đời','/upload/uploadalbums/1718708470961_300.jpg','2025-09-12 02:57:18',32,'2025-09-12 02:57:18','2025-09-12 02:57:18'),(15,'Ngày rộng tháng dài','Sợ mai không còn thấy nhau','/upload/uploadalbums/1697557429415_300.jpg','2025-09-12 02:59:06',32,'2025-09-12 02:59:06','2025-09-12 02:59:06'),(16,'Ngày em đến ','Có nhau trọn đời','/upload/uploadalbums/1687161757974_300.jpg','2025-09-12 02:59:29',32,'2025-09-12 02:59:29','2025-09-12 02:59:29'),(17,'Sợ mai không còn thấy nhau','Sợ mai không còn thấy nhau','/upload/uploadalbums/1746085392251_300.jpg','2025-09-12 03:03:17',32,'2025-09-12 03:03:17','2025-09-12 03:03:17'),(18,'Có còn','Sợ mai không còn thấy nhau','/upload/uploadalbums/1753743750049_300.jpg','2025-09-12 03:04:49',32,'2025-09-12 03:04:49','2025-09-12 03:04:49'),(19,'23','23','/upload/uploadalbums/1694504420742_300.jpg','2025-09-12 03:13:45',32,'2025-09-12 03:13:45','2025-09-12 03:13:45'),(20,'234S','','/upload/uploadalbums/1757485954988_300.webp','2025-09-12 03:13:53',32,'2025-09-12 03:13:53','2025-09-12 03:13:53'),(21,'1','1','/upload/uploadalbums/11111.webp','2025-09-12 04:45:30',32,'2025-09-12 04:45:30','2025-09-12 04:45:30'),(22,'1','1','/upload/uploadalbums/22222.webp','2025-09-12 04:45:38',32,'2025-09-12 04:45:38','2025-09-12 04:45:38'),(23,'1','1','/upload/uploadalbums/333333.webp','2025-09-12 04:45:45',32,'2025-09-12 04:45:45','2025-09-12 04:45:45');
/*!40000 ALTER TABLE `albums` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `artist_news`
--

DROP TABLE IF EXISTS `artist_news`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `artist_news` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `published` bit(1) NOT NULL,
  `thumbnail` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `artist_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_artist_news_artist_created_at` (`artist_id`,`created_at`),
  KEY `idx_artist_news_artist_published_created_at` (`artist_id`,`published`,`created_at`),
  CONSTRAINT `FKgexv0u1e4ijixsyurggkybc4o` FOREIGN KEY (`artist_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `artist_news`
--

LOCK TABLES `artist_news` WRITE;
/*!40000 ALTER TABLE `artist_news` DISABLE KEYS */;
/*!40000 ALTER TABLE `artist_news` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `artist_profiles`
--

DROP TABLE IF EXISTS `artist_profiles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `artist_profiles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `bio` varchar(2500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `cover_image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `social_links` varchar(1000) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `stage_name` varchar(120) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `artist_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_fdwk997dd522noxbo6egqrpjm` (`artist_id`),
  CONSTRAINT `FKhmmxwdfyhfpaarkaoyekolkix` FOREIGN KEY (`artist_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `artist_profiles`
--

LOCK TABLES `artist_profiles` WRITE;
/*!40000 ALTER TABLE `artist_profiles` DISABLE KEYS */;
/*!40000 ALTER TABLE `artist_profiles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `artist_registration_requests`
--

DROP TABLE IF EXISTS `artist_registration_requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `artist_registration_requests` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `admin_note` text COLLATE utf8mb4_unicode_ci,
  `created_at` datetime(6) DEFAULT NULL,
  `portfolio_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `reason` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `reviewed_at` datetime(6) DEFAULT NULL,
  `status` enum('PENDING','APPROVED','REJECTED','CANCELLED') COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `reviewed_by` bigint DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKk9axcndo25ix4kti7otjbpv1s` (`reviewed_by`),
  KEY `idx_artist_request_user_status` (`user_id`,`status`,`created_at`),
  KEY `idx_artist_request_status` (`status`,`created_at`),
  CONSTRAINT `FKahthjhd7o8pvjbl98fkkchxql` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKk9axcndo25ix4kti7otjbpv1s` FOREIGN KEY (`reviewed_by`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `artist_registration_requests`
--

LOCK TABLES `artist_registration_requests` WRITE;
/*!40000 ALTER TABLE `artist_registration_requests` DISABLE KEYS */;
INSERT INTO `artist_registration_requests` VALUES (1,NULL,'2026-04-07 22:03:39.000000','https://example.com/pf','Muon lam tac gia','2026-04-09 10:51:37.000000','APPROVED','2026-04-09 10:51:37.000000',36,37),(2,NULL,'2026-04-09 10:52:16.000000','http://localhost:3000/','ưds','2026-04-09 10:52:25.000000','APPROVED','2026-04-09 10:52:25.000000',36,38),(3,NULL,'2026-04-09 15:57:20.000000','https://www.nhaccuatui.com/artist/322992','123','2026-04-09 16:37:54.000000','APPROVED','2026-04-09 16:37:54.000000',36,39),(4,NULL,'2026-04-12 08:25:24.000000',NULL,'tôi muốn thành tác giả cho ..','2026-04-12 08:25:36.000000','APPROVED','2026-04-12 08:25:36.000000',36,40),(5,NULL,'2026-05-04 22:24:16.000000',NULL,'123',NULL,'PENDING','2026-05-04 22:24:16.000000',NULL,38);
/*!40000 ALTER TABLE `artist_registration_requests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `artist_stats`
--

DROP TABLE IF EXISTS `artist_stats`;
/*!50001 DROP VIEW IF EXISTS `artist_stats`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `artist_stats` AS SELECT 
 1 AS `id`,
 1 AS `username`,
 1 AS `full_name`,
 1 AS `total_songs`,
 1 AS `total_albums`,
 1 AS `total_plays`,
 1 AS `follower_count`,
 1 AS `created_at`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `banners`
--

DROP TABLE IF EXISTS `banners`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `banners` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `image` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `note` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `banners`
--

LOCK TABLES `banners` WRITE;
/*!40000 ALTER TABLE `banners` DISABLE KEYS */;
INSERT INTO `banners` VALUES (9,'/upload/banner/11111.webp','Đi đâu để gặp em','2025-09-12 08:15:22'),(11,'/upload/banner/22222.webp','Chuyện này xưa','2025-09-12 08:59:59'),(12,'/upload/banner/333333.webp','Nắng có mang em về','2025-09-12 09:00:16'),(13,'/upload/banner/child.png','Child','2026-05-18 22:24:19'),(14,'/upload/banner/cpop.png','Cpop','2026-05-18 22:24:38'),(15,'/upload/banner/rainy.png','Rainy','2026-05-18 22:24:55'),(16,'/upload/banner/remix.png','Remix','2026-05-18 22:25:09'),(17,'/upload/banner/sad.png','Sad','2026-05-18 22:25:24'),(18,'/upload/banner/toptop.png','Toptop','2026-05-18 22:25:39');
/*!40000 ALTER TABLE `banners` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment_reports`
--

DROP TABLE IF EXISTS `comment_reports`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment_reports` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `admin_note` text COLLATE utf8mb4_unicode_ci,
  `created_at` datetime(6) DEFAULT NULL,
  `detail` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `reason` enum('SPAM','HARASSMENT','COPYRIGHT','OTHER') COLLATE utf8mb4_unicode_ci NOT NULL,
  `reviewed_at` datetime(6) DEFAULT NULL,
  `status` enum('PENDING','RESOLVED','REJECTED') COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `comment_id` bigint NOT NULL,
  `reporter_id` bigint NOT NULL,
  `reviewed_by` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKhsc3sb5vj4ophfaps1tg2kwd0` (`reporter_id`),
  KEY `FKfyo9840kxgwfc7io3y30q2y07` (`reviewed_by`),
  KEY `idx_comment_report_status` (`status`,`created_at`),
  KEY `idx_comment_report_comment_reporter_status` (`comment_id`,`reporter_id`,`status`),
  CONSTRAINT `FKfyo9840kxgwfc7io3y30q2y07` FOREIGN KEY (`reviewed_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FKhsc3sb5vj4ophfaps1tg2kwd0` FOREIGN KEY (`reporter_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKj9so403o1bquehodqxpyijjma` FOREIGN KEY (`comment_id`) REFERENCES `comments` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment_reports`
--

LOCK TABLES `comment_reports` WRITE;
/*!40000 ALTER TABLE `comment_reports` DISABLE KEYS */;
INSERT INTO `comment_reports` VALUES (1,NULL,'2026-04-09 15:59:34.000000','Comment nay dang spam','SPAM','2026-05-12 22:51:28.000000','RESOLVED','2026-05-12 22:51:28.000000',1,39,36),(2,NULL,'2026-05-12 22:49:59.000000','Comment nay dang spam','SPAM','2026-05-12 22:51:27.000000','RESOLVED','2026-05-12 22:51:27.000000',10,38,36),(3,NULL,'2026-05-12 22:50:00.000000','Comment nay dang spam','SPAM','2026-05-12 22:51:27.000000','RESOLVED','2026-05-12 22:51:27.000000',9,38,36),(4,NULL,'2026-05-12 22:50:02.000000','Comment nay dang spam','SPAM','2026-05-12 22:51:27.000000','RESOLVED','2026-05-12 22:51:27.000000',8,38,36),(5,NULL,'2026-05-12 22:50:05.000000','Comment nay dang spam','SPAM','2026-05-12 22:51:26.000000','RESOLVED','2026-05-12 22:51:26.000000',6,38,36);
/*!40000 ALTER TABLE `comment_reports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comments`
--

DROP TABLE IF EXISTS `comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comments` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `deleted` bit(1) NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `parent_id` bigint DEFAULT NULL,
  `song_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `ai_moderated_at` datetime(6) DEFAULT NULL,
  `ai_moderation_reason` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8omq0tc18jd43bu5tjh6jvraq` (`user_id`),
  KEY `idx_comment_song_root_created` (`song_id`,`parent_id`,`created_at`),
  KEY `idx_comment_parent_created` (`parent_id`,`created_at`),
  KEY `idx_comment_parent_deleted_created` (`parent_id`,`deleted`,`created_at`),
  CONSTRAINT `FK8omq0tc18jd43bu5tjh6jvraq` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKjss5ndgf3fog24fnj5oo19712` FOREIGN KEY (`song_id`) REFERENCES `songs` (`id`),
  CONSTRAINT `FKlri30okf66phtcgbe5pok7cc0` FOREIGN KEY (`parent_id`) REFERENCES `comments` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comments`
--

LOCK TABLES `comments` WRITE;
/*!40000 ALTER TABLE `comments` DISABLE KEYS */;
INSERT INTO `comments` VALUES (1,'ádf','2026-04-07 21:57:55.000000',_binary '\0','2026-04-07 21:57:55.000000',NULL,30,36,NULL,NULL),(2,'sdfsdf','2026-04-07 21:58:00.000000',_binary '\0','2026-04-07 21:58:00.000000',1,30,36,NULL,NULL),(3,'hv','2026-04-09 12:13:39.000000',_binary '\0','2026-04-09 12:13:39.000000',NULL,37,36,NULL,NULL),(4,'j','2026-04-09 16:39:04.000000',_binary '\0','2026-04-09 16:39:04.000000',NULL,30,39,NULL,NULL),(5,'haha','2026-05-12 22:19:29.000000',_binary '\0','2026-05-12 22:22:23.000000',NULL,40,38,NULL,NULL),(6,'ngu hả','2026-05-12 22:28:38.000000',_binary '\0','2026-05-12 22:28:38.000000',NULL,30,38,NULL,NULL),(7,'óc chó hả. mày bị ngu hả','2026-05-12 22:42:45.000000',_binary '\0','2026-05-12 22:42:45.000000',NULL,30,38,NULL,NULL),(8,'mày bị ngu bị óc chó hả','2026-05-12 22:44:29.000000',_binary '\0','2026-05-12 22:44:29.000000',NULL,30,38,NULL,NULL),(9,'mày bị ngu hả','2026-05-12 22:46:56.000000',_binary '\0','2026-05-12 22:46:56.000000',NULL,30,38,NULL,NULL),(10,'mày ngu hả. sao mày ngu thế','2026-05-12 22:47:27.000000',_binary '\0','2026-05-12 22:47:27.000000',NULL,30,38,NULL,NULL),(11,'ngu hả','2026-05-12 22:48:54.000000',_binary '\0','2026-05-12 22:48:54.000000',NULL,30,38,NULL,NULL),(12,'mày bị ngu hả','2026-05-12 22:49:37.000000',_binary '\0','2026-05-12 22:49:37.000000',NULL,30,38,NULL,NULL),(13,'con chó này mày ngu hả','2026-05-12 22:50:32.000000',_binary '\0','2026-05-12 22:50:32.000000',NULL,31,38,NULL,NULL),(14,'mày ngu hả','2026-05-12 22:51:52.000000',_binary '\0','2026-05-12 22:51:52.000000',NULL,31,38,NULL,NULL),(15,'mày ngu hả','2026-05-12 22:52:27.000000',_binary '\0','2026-05-12 22:52:27.000000',NULL,40,38,NULL,NULL),(16,'ngu hả','2026-05-12 22:53:40.000000',_binary '\0','2026-05-12 22:53:40.000000',NULL,32,38,NULL,NULL),(17,'ngu hả','2026-05-12 22:54:45.000000',_binary '\0','2026-05-12 22:54:45.000000',NULL,32,38,NULL,NULL),(18,'ngu hả','2026-05-12 22:54:58.000000',_binary '\0','2026-05-12 22:54:58.000000',NULL,32,38,NULL,NULL),(19,'ngu hả','2026-05-12 22:55:31.000000',_binary '\0','2026-05-12 22:55:31.000000',NULL,34,38,NULL,NULL),(20,'ngu hả','2026-05-12 22:55:54.000000',_binary '\0','2026-05-12 22:55:54.000000',NULL,32,38,NULL,NULL),(21,'ngu hả','2026-05-12 22:59:44.000000',_binary '\0','2026-05-12 22:59:44.000000',NULL,32,38,NULL,NULL),(22,'ngu hả','2026-05-12 23:01:56.000000',_binary '\0','2026-05-12 23:01:56.000000',NULL,32,38,NULL,NULL),(23,'ngu hả','2026-05-12 23:05:02.000000',_binary '\0','2026-05-12 23:05:02.000000',NULL,32,38,NULL,NULL),(24,'ngu hả','2026-05-12 23:06:11.000000',_binary '\0','2026-05-12 23:06:11.000000',NULL,32,38,NULL,NULL),(25,'ngu','2026-05-13 23:49:55.000000',_binary '\0','2026-05-13 23:49:55.000000',NULL,40,36,NULL,NULL),(26,'ngu','2026-05-13 23:50:30.000000',_binary '\0','2026-05-13 23:50:30.000000',NULL,36,38,NULL,NULL);
/*!40000 ALTER TABLE `comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `follows`
--

DROP TABLE IF EXISTS `follows`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `follows` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `follower_id` bigint NOT NULL,
  `following_id` bigint NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_follow` (`follower_id`,`following_id`),
  UNIQUE KEY `UK4faelgsm2rxl2jf3iyjy981ro` (`follower_id`,`following_id`),
  KEY `idx_follower` (`follower_id`),
  KEY `idx_following` (`following_id`),
  CONSTRAINT `follows_ibfk_1` FOREIGN KEY (`follower_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `follows_ibfk_2` FOREIGN KEY (`following_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `CONSTRAINT_1` CHECK ((`follower_id` <> `following_id`))
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `follows`
--

LOCK TABLES `follows` WRITE;
/*!40000 ALTER TABLE `follows` DISABLE KEYS */;
/*!40000 ALTER TABLE `follows` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `genres`
--

DROP TABLE IF EXISTS `genres`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `genres` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `genres`
--

LOCK TABLES `genres` WRITE;
/*!40000 ALTER TABLE `genres` DISABLE KEYS */;
INSERT INTO `genres` VALUES (1,'Pop','Popular music genre','2025-07-02 13:31:37','2025-07-02 13:31:37'),(2,'Rock','Rock music genre','2025-07-02 13:31:37','2025-07-02 13:31:37'),(3,'Hip Hop','Hip hop and rap music','2025-07-02 13:31:37','2025-07-02 13:31:37'),(4,'Electronic','Electronic dance music','2025-07-02 13:31:37','2025-07-02 13:31:37'),(5,'Jazz','Jazz music genre','2025-07-02 13:31:37','2025-07-02 13:31:37'),(6,'Classical','Classical music','2025-07-02 13:31:37','2025-07-02 13:31:37'),(7,'Country','Country music','2025-07-02 13:31:37','2025-07-02 13:31:37'),(8,'R&B','Rhythm and blues','2025-07-02 13:31:37','2025-07-02 13:31:37'),(9,'Indie','Independent music','2025-07-02 13:31:37','2025-07-02 13:31:37'),(10,'Folk','Folk music genre','2025-07-02 13:31:37','2025-07-02 13:31:37');
/*!40000 ALTER TABLE `genres` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `likes`
--

DROP TABLE IF EXISTS `likes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `likes` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `song_id` bigint NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_user_song` (`user_id`,`song_id`),
  UNIQUE KEY `UKacr1uj61iwtfga08oiflokh5g` (`user_id`,`song_id`),
  UNIQUE KEY `idx_likes_user_song` (`user_id`,`song_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_song` (`song_id`),
  KEY `idx_likes_song_created` (`song_id`,`created_at`),
  KEY `idx_likes_song_id` (`song_id`),
  KEY `idx_likes_user_id_created_at` (`user_id`,`created_at`),
  CONSTRAINT `likes_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `likes_ibfk_2` FOREIGN KEY (`song_id`) REFERENCES `songs` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `likes`
--

LOCK TABLES `likes` WRITE;
/*!40000 ALTER TABLE `likes` DISABLE KEYS */;
INSERT INTO `likes` VALUES (17,36,40,'2026-04-05 14:49:35'),(18,36,30,'2026-04-07 14:44:02'),(19,37,35,'2026-04-09 03:14:19'),(20,37,30,'2026-04-09 03:15:23'),(21,38,30,'2026-04-09 03:37:24'),(22,36,37,'2026-04-09 05:13:19'),(24,36,32,'2026-04-09 05:14:16'),(25,36,35,'2026-04-09 05:14:20'),(28,39,35,'2026-04-09 09:32:22'),(29,39,33,'2026-04-09 09:38:37'),(30,39,37,'2026-04-12 02:47:18');
/*!40000 ALTER TABLE `likes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `password_reset_token`
--

DROP TABLE IF EXISTS `password_reset_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `password_reset_token` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `expiry_date` datetime(6) NOT NULL,
  `token` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_g0guo4k8krgpwuagos61oc06j` (`token`),
  UNIQUE KEY `UK_f90ivichjaokvmovxpnlm5nin` (`user_id`),
  CONSTRAINT `FK83nsrttkwkb6ym0anu051mtxn` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `password_reset_token`
--

LOCK TABLES `password_reset_token` WRITE;
/*!40000 ALTER TABLE `password_reset_token` DISABLE KEYS */;
/*!40000 ALTER TABLE `password_reset_token` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `play_history`
--

DROP TABLE IF EXISTS `play_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `play_history` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `song_id` bigint NOT NULL,
  `played_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_song` (`song_id`),
  KEY `idx_played_at` (`played_at`),
  KEY `idx_play_history_user_played` (`user_id`,`played_at`),
  CONSTRAINT `play_history_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `play_history_ibfk_2` FOREIGN KEY (`song_id`) REFERENCES `songs` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=262 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `play_history`
--

LOCK TABLES `play_history` WRITE;
/*!40000 ALTER TABLE `play_history` DISABLE KEYS */;
INSERT INTO `play_history` VALUES (71,33,35,'2025-09-12 01:05:45'),(72,33,35,'2025-09-12 01:05:45'),(73,33,36,'2025-09-12 01:05:49'),(74,33,36,'2025-09-12 01:05:49'),(75,32,33,'2025-09-12 01:37:10'),(76,32,33,'2025-09-12 01:37:10'),(77,32,35,'2025-09-12 04:42:31'),(78,32,35,'2025-09-12 04:42:32'),(79,32,35,'2025-09-12 04:42:56'),(80,32,35,'2025-09-12 04:42:56'),(81,32,34,'2025-09-12 04:47:00'),(82,32,34,'2025-09-12 04:47:00'),(83,32,30,'2025-09-12 04:52:22'),(84,32,30,'2025-09-12 04:52:22'),(85,32,35,'2025-09-12 06:21:13'),(86,32,35,'2025-09-12 06:21:13'),(87,32,35,'2025-09-12 06:22:00'),(88,32,35,'2025-09-12 06:22:03'),(89,32,35,'2025-09-12 06:22:03'),(90,32,35,'2025-09-12 06:22:16'),(91,32,35,'2025-09-12 06:22:16'),(92,32,35,'2025-09-12 06:24:03'),(93,32,35,'2025-09-12 06:24:08'),(94,32,35,'2025-09-12 06:24:08'),(95,32,35,'2025-09-12 06:24:10'),(96,32,35,'2025-09-12 06:24:10'),(97,32,35,'2025-09-12 06:34:29'),(98,32,35,'2025-09-12 06:34:29'),(99,32,35,'2025-09-12 06:35:54'),(100,32,35,'2025-09-12 06:35:54'),(101,32,35,'2025-09-12 06:49:36'),(102,32,35,'2025-09-12 06:49:36'),(103,32,35,'2025-09-12 06:50:39'),(104,32,35,'2025-09-12 06:50:39'),(105,32,35,'2025-09-12 06:50:49'),(106,32,35,'2025-09-12 06:50:49'),(107,32,35,'2025-09-12 06:51:46'),(108,32,35,'2025-09-12 06:51:46'),(109,32,39,'2025-09-12 06:52:07'),(110,32,39,'2025-09-12 06:52:07'),(111,34,30,'2025-09-12 07:23:46'),(112,34,30,'2025-09-12 07:23:46'),(113,34,30,'2025-09-12 07:24:17'),(114,34,30,'2025-09-12 07:24:17'),(115,34,32,'2025-09-12 07:26:21'),(116,34,32,'2025-09-12 07:26:21'),(117,34,35,'2025-09-12 07:26:55'),(118,34,35,'2025-09-12 07:26:55'),(119,34,35,'2025-09-12 07:31:26'),(120,34,35,'2025-09-12 07:31:26'),(121,34,35,'2025-09-12 07:33:48'),(122,34,35,'2025-09-12 07:33:48'),(123,34,35,'2025-09-12 07:33:53'),(124,34,35,'2025-09-12 07:33:53'),(125,34,35,'2025-09-15 05:44:30'),(126,34,35,'2025-09-15 05:44:30'),(127,35,30,'2026-01-19 02:42:25'),(128,35,30,'2026-01-19 02:42:25'),(129,35,30,'2026-01-19 02:43:02'),(130,36,30,'2026-04-05 13:22:31'),(131,36,30,'2026-04-05 13:22:32'),(132,36,38,'2026-04-05 14:48:12'),(133,36,30,'2026-04-05 14:48:26'),(134,36,30,'2026-04-05 14:48:30'),(135,36,30,'2026-04-05 14:49:15'),(136,36,40,'2026-04-05 14:49:32'),(137,36,30,'2026-04-07 14:43:41'),(138,36,30,'2026-04-07 14:44:15'),(139,36,39,'2026-04-07 14:45:44'),(140,36,30,'2026-04-07 14:52:01'),(141,36,30,'2026-04-07 14:54:01'),(142,36,30,'2026-04-07 14:57:52'),(143,36,35,'2026-04-07 15:14:55'),(144,36,35,'2026-04-07 15:20:50'),(145,37,35,'2026-04-07 15:23:11'),(146,37,35,'2026-04-07 15:54:53'),(147,37,30,'2026-04-09 03:14:05'),(148,37,35,'2026-04-09 03:14:16'),(149,37,34,'2026-04-09 03:14:23'),(150,37,33,'2026-04-09 03:14:29'),(151,37,30,'2026-04-09 03:15:18'),(152,37,30,'2026-04-09 03:29:29'),(153,38,30,'2026-04-09 03:37:22'),(154,36,37,'2026-04-09 05:13:12'),(155,36,32,'2026-04-09 05:13:46'),(156,36,35,'2026-04-09 05:13:54'),(157,36,34,'2026-04-09 08:50:32'),(158,36,34,'2026-04-09 08:50:59'),(159,39,30,'2026-04-09 08:59:11'),(160,39,30,'2026-04-09 09:02:16'),(163,39,30,'2026-04-09 09:02:53'),(164,39,32,'2026-04-09 09:04:50'),(165,39,32,'2026-04-09 09:05:01'),(166,39,30,'2026-04-09 09:10:00'),(167,39,38,'2026-04-09 09:10:39'),(168,39,40,'2026-04-09 09:10:42'),(170,39,40,'2026-04-09 09:10:45'),(171,39,38,'2026-04-09 09:10:46'),(172,39,37,'2026-04-09 09:10:48'),(173,39,30,'2026-04-09 09:11:34'),(174,39,35,'2026-04-09 09:11:44'),(175,39,35,'2026-04-09 09:12:12'),(176,39,30,'2026-04-09 09:13:00'),(177,39,30,'2026-04-09 09:31:30'),(178,39,35,'2026-04-09 09:32:14'),(179,39,30,'2026-04-09 09:32:48'),(180,39,38,'2026-04-09 09:33:14'),(181,39,38,'2026-04-09 09:33:30'),(182,39,30,'2026-04-09 09:38:17'),(183,39,32,'2026-04-09 09:38:38'),(184,39,31,'2026-04-09 09:38:39'),(185,39,30,'2026-04-09 09:38:51'),(186,39,30,'2026-04-12 01:14:50'),(187,38,30,'2026-04-12 01:18:25'),(188,38,35,'2026-04-12 01:20:15'),(189,38,35,'2026-04-12 01:20:18'),(190,38,30,'2026-04-12 01:20:49'),(191,39,33,'2026-04-12 01:28:37'),(192,39,37,'2026-04-12 02:46:57'),(193,39,37,'2026-04-12 02:47:12'),(194,39,37,'2026-04-12 02:47:49'),(195,36,31,'2026-05-04 15:15:48'),(196,36,31,'2026-05-04 15:17:06'),(197,36,31,'2026-05-04 15:21:25'),(198,38,40,'2026-05-12 15:19:15'),(199,38,30,'2026-05-12 15:23:07'),(200,38,30,'2026-05-12 15:23:41'),(201,38,30,'2026-05-12 15:28:09'),(202,38,30,'2026-05-12 15:31:09'),(203,38,30,'2026-05-12 15:37:01'),(204,38,30,'2026-05-12 15:41:52'),(205,38,30,'2026-05-12 15:42:14'),(206,38,30,'2026-05-12 15:44:05'),(207,38,30,'2026-05-12 15:46:11'),(208,38,30,'2026-05-12 15:47:05'),(209,38,30,'2026-05-12 15:48:16'),(210,38,30,'2026-05-12 15:49:12'),(211,38,30,'2026-05-12 15:49:25'),(212,38,40,'2026-05-12 15:50:15'),(213,38,31,'2026-05-12 15:50:19'),(214,38,31,'2026-05-12 15:50:32'),(215,38,31,'2026-05-12 15:52:02'),(216,38,30,'2026-05-12 15:52:05'),(217,38,40,'2026-05-12 15:52:17'),(218,38,31,'2026-05-12 15:53:18'),(219,38,32,'2026-05-12 15:53:21'),(220,38,32,'2026-05-12 15:54:16'),(221,38,31,'2026-05-12 15:54:16'),(222,38,32,'2026-05-12 15:54:49'),(223,38,34,'2026-05-12 15:55:17'),(224,38,32,'2026-05-12 15:55:47'),(225,38,31,'2026-05-12 15:58:16'),(226,38,32,'2026-05-12 15:58:16'),(227,38,32,'2026-05-12 15:59:24'),(228,38,32,'2026-05-12 16:01:41'),(229,38,32,'2026-05-12 16:04:22'),(230,38,32,'2026-05-12 16:04:48'),(231,38,32,'2026-05-12 16:05:46'),(232,38,32,'2026-05-12 16:07:20'),(233,38,32,'2026-05-12 16:07:33'),(234,38,31,'2026-05-12 16:10:01'),(235,38,34,'2026-05-12 16:10:05'),(236,38,31,'2026-05-12 16:33:29'),(237,38,35,'2026-05-13 15:13:52'),(238,36,40,'2026-05-13 16:49:47'),(239,36,40,'2026-05-13 16:49:59'),(240,38,36,'2026-05-13 16:50:24'),(241,36,32,'2026-05-14 03:51:43'),(242,36,33,'2026-05-14 03:51:49'),(243,36,40,'2026-05-14 04:00:50'),(244,36,74,'2026-05-18 15:28:35'),(245,36,71,'2026-05-18 15:28:55'),(246,36,32,'2026-05-18 15:29:57'),(247,36,30,'2026-05-18 15:32:19'),(248,36,74,'2026-05-18 15:32:26'),(249,36,74,'2026-05-18 15:33:45'),(250,36,74,'2026-05-18 15:33:46'),(251,36,40,'2026-05-18 15:33:57'),(252,36,40,'2026-05-18 15:34:01'),(253,36,57,'2026-05-18 15:34:10'),(254,36,60,'2026-05-18 15:34:40'),(255,36,60,'2026-05-18 15:35:17'),(256,36,60,'2026-05-18 15:37:53'),(257,36,60,'2026-05-18 15:37:58'),(258,36,60,'2026-05-18 15:38:13'),(259,36,60,'2026-05-18 15:38:18'),(260,36,60,'2026-05-18 15:39:23'),(261,36,47,'2026-05-18 15:39:30');
/*!40000 ALTER TABLE `play_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `playlist_songs`
--

DROP TABLE IF EXISTS `playlist_songs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `playlist_songs` (
  `playlist_id` bigint NOT NULL,
  `song_id` bigint NOT NULL,
  `added_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`playlist_id`,`song_id`),
  KEY `idx_playlist` (`playlist_id`),
  KEY `idx_song` (`song_id`),
  KEY `idx_playlist_songs_song_id` (`song_id`),
  CONSTRAINT `playlist_songs_ibfk_1` FOREIGN KEY (`playlist_id`) REFERENCES `playlists` (`id`) ON DELETE CASCADE,
  CONSTRAINT `playlist_songs_ibfk_2` FOREIGN KEY (`song_id`) REFERENCES `songs` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `playlist_songs`
--

LOCK TABLES `playlist_songs` WRITE;
/*!40000 ALTER TABLE `playlist_songs` DISABLE KEYS */;
INSERT INTO `playlist_songs` VALUES (6,32,'2026-05-18 15:30:11');
/*!40000 ALTER TABLE `playlist_songs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `playlists`
--

DROP TABLE IF EXISTS `playlists`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `playlists` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `cover_image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `is_public` tinyint(1) DEFAULT '1',
  `user_id` bigint NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`),
  KEY `idx_user` (`user_id`),
  KEY `idx_public` (`is_public`),
  KEY `idx_playlists_updated_desc` (`updated_at`),
  KEY `idx_playlists_user_id` (`user_id`),
  CONSTRAINT `playlists_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `playlists`
--

LOCK TABLES `playlists` WRITE;
/*!40000 ALTER TABLE `playlists` DISABLE KEYS */;
INSERT INTO `playlists` VALUES (1,'My Favorites','My personal favorite songs',NULL,1,4,'2025-07-02 13:31:37','2025-07-02 13:31:37'),(2,'Workout Mix','High energy songs for workout',NULL,1,5,'2025-07-02 13:31:37','2025-07-02 13:31:37'),(3,'Chill Vibes','Relaxing songs for chill time',NULL,1,7,'2025-07-02 13:31:37','2025-07-02 13:31:37'),(4,'Road Trip','Perfect songs for road trips',NULL,1,8,'2025-07-02 13:31:37','2025-07-02 13:31:37'),(5,'Study Music','Background music for studying',NULL,0,4,'2025-07-02 13:31:37','2025-07-02 13:31:37'),(6,'ab lu','le xuand uc',NULL,0,36,'2026-05-18 15:30:11','2026-05-18 15:30:11');
/*!40000 ALTER TABLE `playlists` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `popular_songs`
--

DROP TABLE IF EXISTS `popular_songs`;
/*!50001 DROP VIEW IF EXISTS `popular_songs`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `popular_songs` AS SELECT 
 1 AS `id`,
 1 AS `title`,
 1 AS `artist_name`,
 1 AS `genre_name`,
 1 AS `play_count`,
 1 AS `like_count`,
 1 AS `created_at`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `popup_ads`
--

DROP TABLE IF EXISTS `popup_ads`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `popup_ads` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci,
  `created_at` datetime(6) DEFAULT NULL,
  `end_at` datetime(6) DEFAULT NULL,
  `image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `start_at` datetime(6) DEFAULT NULL,
  `target_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `popup_ads`
--

LOCK TABLES `popup_ads` WRITE;
/*!40000 ALTER TABLE `popup_ads` DISABLE KEYS */;
INSERT INTO `popup_ads` VALUES (1,_binary '\0',NULL,'2026-05-14 10:42:49.000000',NULL,'/upload/popup/d58866f7-8268-4635-8605-2c3b62ae7327_t-i-xu-ng--2-.jfif',NULL,NULL,'Nắng');
/*!40000 ALTER TABLE `popup_ads` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `public_playlists`
--

DROP TABLE IF EXISTS `public_playlists`;
/*!50001 DROP VIEW IF EXISTS `public_playlists`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `public_playlists` AS SELECT 
 1 AS `id`,
 1 AS `name`,
 1 AS `description`,
 1 AS `creator_name`,
 1 AS `song_count`,
 1 AS `created_at`,
 1 AS `updated_at`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `song_stats`
--

DROP TABLE IF EXISTS `song_stats`;
/*!50001 DROP VIEW IF EXISTS `song_stats`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `song_stats` AS SELECT 
 1 AS `id`,
 1 AS `title`,
 1 AS `artist_name`,
 1 AS `genre_name`,
 1 AS `play_count`,
 1 AS `like_count`,
 1 AS `total_plays`,
 1 AS `created_at`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `song_violation_reports`
--

DROP TABLE IF EXISTS `song_violation_reports`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `song_violation_reports` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `admin_note` text COLLATE utf8mb4_unicode_ci,
  `created_at` datetime(6) DEFAULT NULL,
  `description` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `evidence_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `reviewed_at` datetime(6) DEFAULT NULL,
  `status` enum('PENDING','RESOLVED','REJECTED') COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` enum('COPYRIGHT','PLAGIARISM','OTHER') COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `reporter_id` bigint NOT NULL,
  `reviewed_by` bigint DEFAULT NULL,
  `song_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3rcwu91h0vgk0icxlykny3i89` (`reviewed_by`),
  KEY `idx_song_violation_reporter_created` (`reporter_id`,`created_at`),
  KEY `idx_song_violation_status` (`status`,`created_at`),
  KEY `idx_song_violation_reports_song_status` (`song_id`,`status`),
  KEY `idx_song_violation_reports_reporter_status` (`reporter_id`,`status`),
  CONSTRAINT `FK3rcwu91h0vgk0icxlykny3i89` FOREIGN KEY (`reviewed_by`) REFERENCES `users` (`id`),
  CONSTRAINT `FK5tuceq0d0y6g9slcjb7w8f5ec` FOREIGN KEY (`reporter_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKplexielv7np09ksp8cn6f19td` FOREIGN KEY (`song_id`) REFERENCES `songs` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `song_violation_reports`
--

LOCK TABLES `song_violation_reports` WRITE;
/*!40000 ALTER TABLE `song_violation_reports` DISABLE KEYS */;
INSERT INTO `song_violation_reports` VALUES (1,NULL,'2026-04-07 21:44:27.000000','213','123123','2026-05-12 22:51:29.000000','RESOLVED','OTHER','2026-05-12 22:51:29.000000',36,36,30),(2,NULL,'2026-04-07 21:44:28.000000','213','123123','2026-05-12 22:51:29.000000','RESOLVED','OTHER','2026-05-12 22:51:29.000000',36,36,30),(3,NULL,'2026-04-07 21:58:11.000000','adssdf','','2026-05-12 22:51:28.000000','RESOLVED','COPYRIGHT','2026-05-12 22:51:28.000000',36,36,30),(4,NULL,'2026-04-07 22:07:28.000000','auto test report','https://example.com/evidence','2026-05-12 22:51:28.000000','RESOLVED','COPYRIGHT','2026-05-12 22:51:28.000000',37,36,40),(5,NULL,'2026-04-07 22:12:24.000000','dto-check','https://e','2026-05-12 22:51:28.000000','RESOLVED','COPYRIGHT','2026-05-12 22:51:28.000000',37,36,40),(6,'dto-fix-ok','2026-04-07 22:13:21.000000','dto-create-check','https://evidence','2026-04-07 22:13:32.000000','RESOLVED','COPYRIGHT','2026-04-07 22:13:32.000000',36,36,30),(7,NULL,'2026-04-07 22:17:36.000000','frontend enum check','https://e','2026-05-12 22:51:28.000000','RESOLVED','OTHER','2026-05-12 22:51:28.000000',36,36,30),(8,NULL,'2026-04-07 22:23:16.000000','123','','2026-05-12 22:51:28.000000','RESOLVED','COPYRIGHT','2026-05-12 22:51:28.000000',37,36,35);
/*!40000 ALTER TABLE `song_violation_reports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `songs`
--

DROP TABLE IF EXISTS `songs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `songs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `file_path` varchar(500) COLLATE utf8mb4_unicode_ci NOT NULL,
  `cover_image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `duration` int DEFAULT NULL,
  `play_count` bigint DEFAULT '0',
  `active` tinyint(1) DEFAULT '1',
  `artist_id` bigint NOT NULL,
  `album_id` bigint DEFAULT NULL,
  `genre_id` bigint DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `lyrics` longtext COLLATE utf8mb4_unicode_ci,
  `top` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `idx_title` (`title`),
  KEY `idx_artist` (`artist_id`),
  KEY `idx_album` (`album_id`),
  KEY `idx_genre` (`genre_id`),
  KEY `idx_play_count` (`play_count`),
  KEY `idx_active` (`active`),
  KEY `idx_created_at` (`created_at`),
  KEY `idx_songs_title_fulltext` (`title`),
  KEY `idx_songs_artist_genre` (`artist_id`,`genre_id`),
  KEY `idx_songs_active_created` (`active`,`created_at`),
  KEY `idx_songs_play_count_desc` (`play_count`),
  CONSTRAINT `songs_ibfk_1` FOREIGN KEY (`artist_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `songs_ibfk_2` FOREIGN KEY (`album_id`) REFERENCES `albums` (`id`) ON DELETE SET NULL,
  CONSTRAINT `songs_ibfk_3` FOREIGN KEY (`genre_id`) REFERENCES `genres` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `songs`
--

LOCK TABLES `songs` WRITE;
/*!40000 ALTER TABLE `songs` DISABLE KEYS */;
INSERT INTO `songs` VALUES (30,'Cơn Mưa Bất Chợt','Ca khúc ballad nhẹ nhàng về những cơn mưa đầu mùa.','/upload/test-1-1.mp3','/upload/1725605286776_300.jpg',215,99,0,33,14,1,'2025-09-12 00:57:10','2026-05-18 15:32:19','Cơn mưa bất chợt rơi trên vai em...',1),(31,'Phố Cũ Chiều Mưa','Giai điệu lãng mạn gợi nhớ kỷ niệm xưa.','/upload/test-3.mp3','/upload/1732374109039_300.jpg',240,24,1,33,15,1,'2025-09-12 00:57:10','2026-05-14 03:43:32','Chiều mưa rơi, lòng ai vương vấn...',1),(32,'Em Về Tinh Khôi','Bài hát trong trẻo về tình yêu đầu.','/upload/test-4-1.mp3','/upload/1687161757974_300.jpg',198,25,1,33,16,1,'2025-09-12 00:57:10','2026-05-18 15:29:57','Em về tinh khôi như cơn gió xuân...',1),(33,'Đêm Trăng Trên Sông','Khúc hát dân gian về trăng và sông nước.','/upload/test-10-1.mp3','/upload/1513679633652_300.jpg',230,6,1,33,14,1,'2025-09-12 00:57:10','2026-05-14 03:51:49','Ánh trăng nghiêng soi bóng sông dài...',1),(34,'Giấc Mơ Hoa Phượng','Nhạc nhẹ gợi nhớ tuổi học trò.','/upload/test-8.mp3','/upload/1753743750049_300.jpg',205,16,1,33,15,1,'2025-09-12 00:57:10','2026-05-12 16:10:05','Giấc mơ nào đỏ rực sân trường...',0),(35,'Biển Gọi Tên Anh1','Ca khúc về nỗi nhớ trong những ngày xa.','/upload/test-8-1.mp3','/upload/1718708470961_300.jpg',220,63,1,33,16,1,'2025-09-12 00:57:10','2026-05-13 16:38:41','Biển xanh vỗ sóng nhớ tên anh...',0),(36,'Hương Quê','Dân ca đượm hồn quê Việt.','/upload/test-8-2.mp3','/upload/1697557429415_300.jpg',180,6,1,33,16,1,'2025-09-12 00:57:10','2026-05-13 16:50:24','Cánh đồng lúa thơm hương quê nhà...',0),(37,'Nỗi Nhớ Mùa Đông','Giai điệu sâu lắng cho những ngày đông lạnh.','/upload/test-3-2.mp3','/upload/1727923521610_300.jpg',260,5,1,33,NULL,1,'2025-09-12 00:57:10','2026-04-12 02:47:49','Nỗi nhớ theo cơn gió mùa đông...',0),(38,'Bài Ca Tuổi Trẻ','Khúc ca sôi động về nhiệt huyết tuổi trẻ.','/upload/test-2.mp3','/upload/1754660311702_300.jpg',210,9,1,33,NULL,1,'2025-09-12 00:57:10','2026-04-09 09:33:30','Tuổi trẻ rực rỡ cháy đam mê...',0),(39,'Lối Nhỏ Về Quê','Bản nhạc nhẹ nhàng về con đường quê yên bình.','/upload/test-4-2.mp3','/upload/1670323567349_300.jpg',190,5,1,33,NULL,1,'2025-09-12 00:57:10','2026-04-07 14:45:43','Lối nhỏ quanh co dẫn về quê...',0),(40,'123','123123','/upload/18.mp3','/upload/zenlish-196-200-2-1.jpg',123,52,1,36,20,7,'2026-01-21 03:44:01','2026-05-18 15:34:01','213',1),(42,'1','1','/upload/50-52.mp3','/upload/f-2.jpg',1,0,1,36,19,3,'2026-05-16 09:20:46','2026-05-16 09:20:46','1',1),(43,'Tệ Hại Và Xấu Xí duc','Anh yêu cái cách em khiến anh tỏa sáng Anh yêu cái cách Em khiến anh nghĩ ngoài tình yêu của em Không có gì thỏa đáng','/upload/1-2.mp3','/upload/1-2.jpg',3,0,1,36,19,3,'2026-05-18 14:22:54','2026-05-18 14:23:57','I love you\r\nAnh yêu cái cách em khiến anh tỏa sáng\r\nAnh yêu cái cách\r\nEm khiến anh nghĩ ngoài tình yêu của em\r\nKhông có gì thỏa đáng\r\nAnh yêu cách em yêu anh\r\nCả từ trước khi anh\r\nKhoác lên mình hào nhoáng\r\nAnh có thể lắng nghe em gọi tên anh\r\nHàng ngàn năm nữa\r\nMà không thể nào chán\r\nAnd I love you\r\nAnh yêu cả những đêm\r\nEm khiến anh không ngủ\r\nHóa hiện thực tất cả những giấc mơ\r\nNgày bé mà anh cất trong tủ\r\nDạy anh biến tất cả những vết thương\r\nSâu thẳm trở thành những công cụ\r\nVà anh cố gắng và anh cố gắng\r\nNhưng rõ rang cố gắng là không đủ\r\nAnd I need you\r\nNhư không khí len vào từng nhịp thở\r\nNhư mọi tế bào vẫn ngăn cản anh\r\nĐi tìm em nhưng anh vẫn viện cớ\r\nAnh cần em vẫn như là cách\r\nMà em đã kéo anh ra khỏi\r\nVũng lầy quá nhiều lần\r\nCó ai đã từng đứng dưới ánh đèn spotlight\r\nMà không hề có bóng tối theo sau lưng\r\nAnd I needed you to love me\r\nThe bad and the ugly\r\nTệ hại và xấu xí\r\nNgu si và ấu trĩ\r\nPhơi bày tất cả bí mật\r\nTất cả nỗi sợ mà anh giấu kĩ\r\nNhững thước phim không qua hậu kì\r\nMột ván cờ anh phải đấu trí\r\nI fear you\r\nVì em chỉ yêu anh duy nhất\r\nKhi anh hoàn hảo\r\nVì anh biết vào bất cứ lúc nào\r\nCũng có người khác\r\nCó thể đặt ngang hàng Bảo\r\nVà anh sợ là bởi vì\r\nEm cũng chưa bao giờ\r\nTừng hứa là em sẽ ở lại\r\nAnh sợ 1 ngày em đã không còn đây\r\nNhưng chỉ còn lại\r\nMỗi mình anh là chờ mãi\r\nAnd I want you\r\nNhưng mà điều đó\r\nChưa từng là đơn giản phải không\r\nAnh đã giết bản thân mình bao nhiêu lần\r\nĐể có thể trở thành\r\nPhiên bản em hài lòng\r\nMột phiên bản mà anh biết em xứng đáng\r\nNhưng anh cũng muốn được là chính mình\r\nAnh muốn được nói những gì mà mình nghĩ\r\nVà không quan tâm là em sẽ nghĩ gì\r\nSo đèn mở nhạc lên\r\nKhán giả bước vào\r\nRa đây và tận hưởng tất cả\r\nNhững thứ từ bé mày từng ước nào\r\nNhìn đi bọn họ đang tới xem\r\nKhi nào thằng nhóc này\r\nTới ngày bỏ cuộc\r\nVỗ tay và nhìn một thằng điên\r\nCố gắng trao cho họ',1),(44,'Ngay Mua Thanh Xuan','Ca khuc nhac tre ve tinh yeu dau va nhung ngay thanh xuan.','/upload/4.mp3','/upload/3.jpg',218,0,1,33,17,2,'2026-05-18 14:30:10','2026-05-18 14:37:42','Verse 1: Chieu nay mua roi tren con pho quen\r\nEm di ngang qua nhu giac mo em dem\r\nChorus: Neu mai nay ta con gap lai\r\nXin giu trong tim mot thoi yeu ai',1),(45,'Dung Hoi Vi Sao','Ban pop nhe ve noi nho sau chia tay.','/upload/15-1.mp3','/upload/39.jpg',205,0,1,33,15,6,'2026-05-18 14:30:10','2026-05-18 15:00:30','Verse 1: Anh cat noi nho vao trong ngan keo\r\nNhung moi dem ve lai thay tim ngheo\r\nChorus: Dung hoi vi sao anh con thuong\r\nVi ten em van nam tren moi con duong',1),(46,'Cuoi Tuan Di Xa','Nhac tre vui tuoi ve mot cuoc hen cuoi tuan.','/upload/2.mp3','/upload/4.jpg',192,0,1,33,14,5,'2026-05-18 14:30:10','2026-05-18 14:42:01','Verse 1: Cuoi tuan minh len xe di that xa\r\nBo lai sau lung nhung ngay voi va\r\nChorus: Bat bai ca len cho troi xanh hon\r\nNam tay nhau qua tung con pho lon',1),(47,'Loi Hua Cu','Ca khuc ballad ve mot loi hua da cu.','/upload/3-1.mp3','/upload/5.jpg',231,1,1,33,18,1,'2026-05-18 14:30:10','2026-05-18 15:39:30','Verse 1: Loi hua nam nao gio bay theo gio\r\nAnh dung noi nay nghe long tan vo\r\nChorus: Neu da xa nhau xin dung quay dau\r\nDe noi dau nay ngu yen that lau',1),(48,'Sang Nhu Mat Troi','Ban pop hien dai ve su tu tin cua nguoi tre.','/upload/12-1.mp3','/upload/37.jpg',200,0,1,33,17,5,'2026-05-18 14:30:10','2026-05-18 15:01:05','Verse 1: Hom nay toi buoc ra duong voi nu cuoi\r\nBo qua sau lung nhung dieu roi boi\r\nChorus: Toi se sang nhu anh mat troi\r\nSong het minh cho tuoi tre len ngoi',1),(49,'Gan Nhau Qua Man Hinh','Tinh ca nhe nha ve nguoi yeu o xa.','/upload/5.mp3','/upload/6-1.jpg',224,0,1,33,16,7,'2026-05-18 14:30:10','2026-05-18 14:44:44','Verse 1: Thanh pho dem nay den vang hiu hat\r\nAnh nho em qua tung dong tin nhan\r\nChorus: Cach xa may cung khong lam phai\r\nTrai tim anh van huong ve ngay mai',0),(50,'Anh Mat Dau Tien','Ca khuc ve mot tinh yeu bat dau tu anh mat.','/upload/6.mp3','/upload/7.jpg',210,0,1,33,15,8,'2026-05-18 14:30:10','2026-05-18 14:45:29','Verse 1: Chi mot lan nhin em giua san truong\r\nTim anh lac mat tren cung duong\r\nChorus: Anh thich cach em mim cuoi\r\nLam ca the gioi bong dep hon thoi',0),(51,'He Len Tieng','Ban nhac tre co tiet tau sang khoai ve mua he.','/upload/7.mp3','/upload/8.jpg',198,0,1,33,16,9,'2026-05-18 14:30:10','2026-05-18 14:46:20','Verse 1: Bien xanh goi ten ngay he len tieng\r\nChan tran tren cat long nghe binh yen\r\nChorus: Nhay cung song va gio\r\nHat cho tuoi tre khong bao gio mo',0),(52,'Ai Don Em Ve','Ballad ve nguoi con thuong sau mot loi chia tay.','/upload/8.mp3','/upload/9.jpg',236,0,1,33,18,2,'2026-05-18 14:30:10','2026-05-18 14:47:11','Verse 1: Em noi minh dung lai o day\r\nAnh cuoi ma mat cay cay\r\nChorus: Roi ngay mai ai don em ve\r\nAi nghe em ke nhung dieu nho be',0),(53,'Dem Thanh Pho','Pop dance ve dem thanh pho va nhung trai tim tre.','/upload/9.mp3','/upload/11.jpg',214,0,1,33,16,7,'2026-05-18 14:30:10','2026-05-18 14:48:13','Verse 1: Den thanh pho sang nhu sao roi\r\nTa lac vao dem voi tieng cuoi\r\nChorus: Nhay len em oi dung nghi suy\r\nDem nay tuoi tre cu di di',0),(54,'San Truong Nam Ay','Ca khuc ve ky uc tuoi hoc tro trong sang.','/upload/10.mp3','/upload/10.jpg',219,0,1,33,18,10,'2026-05-18 14:30:10','2026-05-18 14:48:48','Verse 1: San truong xua con vang tieng ve\r\nTrang vo cu giu mau muc nhe\r\nChorus: Tuoi hoc tro nhu may troi bay\r\nMai trong tim chang the nao phai',0),(55,'Co Don Goi Ten','Ban ballad nhe ve noi co don trong dem.','/upload/11.mp3','/upload/12.jpg',228,0,1,33,17,5,'2026-05-18 14:30:10','2026-05-18 14:50:33','Verse 1: Dem qua dai chi minh anh thuc\r\nNghe mua roi tren nhung ky uc\r\nChorus: Co don oi xin dung goi ten\r\nDe anh quen mot nguoi da quen',0),(56,'Duong Den Uoc Mo','Nhac pop ve hanh trinh theo duoi uoc mo.','/upload/9-1.mp3','/upload/23.jpg',203,0,1,33,18,2,'2026-05-18 14:30:10','2026-05-18 14:59:10','Verse 1: Duong con xa nhung chan khong moi\r\nMang trong tim mot niem tin moi\r\nChorus: Chay ve phia anh sang ngay mai\r\nUoc mo kia se khong dung lai',1),(57,'Mua Tren Vai Em','Ca khuc tinh yeu nhe nhang trong chieu mua.','/upload/14.mp3','/upload/13.jpg',215,1,1,33,15,6,'2026-05-18 14:30:10','2026-05-18 15:34:10','Verse 1: Mua roi nhe tren vai ao em\r\nAnh dung yen nghe tim minh mem\r\nChorus: Neu co the dung roi xa nhau\r\nXin cho mua giu minh that lau',0),(58,'Ban Be Oi','Ban nhac tre vui ve tinh ban va tuoi tre.','/upload/15.mp3','/upload/14.jpg',196,0,1,33,14,2,'2026-05-18 14:30:10','2026-05-18 14:52:26','Verse 1: Ban be oi cung nhau di toi\r\nKhong ngai chi du duong xa xoi\r\nChorus: Ta cu cuoi vang ca bau troi\r\nTuoi tre nay chi den mot doi',0),(59,'Khong Tron Ven','Ballad buon ve mot moi tinh khong tron ven.','/upload/1-3.mp3','/upload/15.jpg',240,0,1,33,16,5,'2026-05-18 14:30:10','2026-05-18 14:53:02','Verse 1: Anh da giu nhung dieu khong noi\r\nDe mat em xa dan cu the thoi\r\nChorus: Yeu mot nguoi khong yeu lai minh\r\nLa noi dau chang the goi ten',0),(60,'Bat Dau Lai','Pop hien dai ve cam giac bat dau lai.','/upload/2-1.mp3','/upload/16.jpg',207,7,1,33,14,10,'2026-05-18 14:30:10','2026-05-18 15:39:23','Verse 1: Sang hom nay troi xanh den la\r\nAnh hoc quen nhung ngay da qua\r\nChorus: Bat dau lai tu mot niem vui\r\nTim binh yen sau nhung ngap ngui',1),(61,'Hen Dau Tien','Ca khuc lang man ve mot buoi hen dau.','/upload/3-2.mp3','/upload/17.jpg',212,0,1,33,18,4,'2026-05-18 14:30:10','2026-05-18 14:54:29','Verse 1: Quan ca phe goc pho quen\r\nEm den tre nhung anh van doi\r\nChorus: Hen dau tien sao tim boi hoi\r\nNhu ca the gioi chi co doi ta',0),(62,'Chuyen Di Thanh Xuan','Ban nhac ve thanh xuan va nhung chuyen di.','/upload/5-1.mp3','/upload/18.jpg',221,0,1,33,15,3,'2026-05-18 14:30:10','2026-05-18 14:56:40','Verse 1: Balo tren vai ta di qua nui doi\r\nDuong dai phia truoc long van goi moi\r\nChorus: Thanh xuan la nhung chuyen di xa\r\nLa khi ta song het long minh ma',0),(63,'Xin Loi Muon Mang','Ballad ve loi xin loi muon mang.','/upload/6-1.mp3','/upload/19.jpg',233,0,1,33,16,4,'2026-05-18 14:30:10','2026-05-18 14:58:19','Verse 1: Anh xin loi vi nhung lan im lang\r\nDe trai tim em buon den vo vang\r\nChorus: Neu thoi gian co quay tro lai\r\nAnh se yeu em nhieu hon ngay mai',0),(64,'Tu Do Bay Xa','Pop dance ve su tu do va khat khao song.','/upload/9-2.mp3','/upload/35-1.jpg',204,0,1,33,18,8,'2026-05-18 14:30:10','2026-05-18 15:18:26','Verse 1: Mo canh cua don nang len cao\r\nNghe trong tim mot giac mo chao\r\nChorus: Ta la gio bay qua muon loi\r\nSong tu do nhu anh mat troi',0),(65,'Nguoi Trong Tim','Tinh ca nhe ve mot nguoi luon o trong tim.','/upload/1-4.mp3','/upload/31.jpg',225,0,1,33,17,4,'2026-05-18 14:30:10','2026-05-18 15:17:45','Verse 1: Co mot nguoi anh giu trong tim\r\nQua bao nam van chang the im\r\nChorus: Du ngay thang co doi thay mau\r\nTen em van la dieu nhiem mau',1),(66,'Ngay Binh Yen','Nhac tre ve nhung ngay binh yen ben nhau.','/upload/4-2.mp3','/upload/32.jpg',208,0,1,33,16,3,'2026-05-18 14:30:10','2026-05-18 15:14:15','Verse 1: Sang thuc day thay em ke ben\r\nBinh yen nhu nang qua rem\r\nChorus: Chi can co em trong doi\r\nMoi ngay deu hoa thanh niem vui',0),(67,'Tap Quen Khong Em','Ballad buon ve viec hoc cach quen mot nguoi.','/upload/11-3.mp3','/upload/34.jpg',237,0,1,33,14,2,'2026-05-18 14:30:10','2026-05-18 15:13:40','Verse 1: Anh tap quen khong goi ten em\r\nTap di ve qua nhung con duong quen\r\nChorus: Quen mot nguoi dau de dang dau\r\nKhi trai tim van con rat sau',0),(68,'Xuan Ve Roi','Ca khuc vui tuoi ve mua xuan va hy vong.','/upload/7-3.mp3','/upload/28.jpg',199,0,1,33,15,5,'2026-05-18 14:30:10','2026-05-18 15:16:05','Verse 1: Xuan ve tren tung nhanh hoa mai\r\nMang niem vui den khap ngay dai\r\nChorus: Hat len nao cho doi tuoi moi\r\nUoc mong xanh theo gio len troi',0),(69,'Tin Nhan Chua Gui','Pop nhe ve nhung tin nhan chua gui.','/upload/7-2.mp3','/upload/35.jpg',206,0,1,33,17,6,'2026-05-18 14:30:10','2026-05-18 15:12:57','Verse 1: Tin nhan viet roi lai xoa di\r\nVi anh so em chang can gi\r\nChorus: Neu mot ngay em doc duoc long anh\r\nXin dung quay di qua nhanh',0),(70,'Yeu Gian Di','Ban nhac tre ve mot tinh yeu gian di.','/upload/11-2.mp3','/upload/37-1.jpg',213,0,1,33,17,3,'2026-05-18 14:30:10','2026-05-18 15:12:24','Verse 1: Khong can hoa hay nhung mon qua\r\nChi can em di cung anh qua\r\nChorus: Yeu gian di nhu chieu nang vang\r\nNam tay nhau qua nam thang lang',1),(71,'Ky Niem Cu','Ballad ve ky niem cu khong the xoa.','/upload/14-1.mp3','/upload/39-1.jpg',229,1,1,33,15,9,'2026-05-18 14:30:10','2026-05-18 15:28:55','Verse 1: Goc pho cu con in dau chan\r\nNhung ngay ta tung rat gan\r\nChorus: Ky niem oi xin dung quay ve\r\nDe tim anh thoi nho em nhieu the',0),(72,'Tin Vao Ngay Mai','Nhac pop ve niem tin vao ngay mai.','/upload/7-1.mp3','/upload/6-2.jpg',201,0,1,33,16,6,'2026-05-18 14:30:10','2026-05-18 15:11:02','Verse 1: Sau con mua troi lai sang hon\r\nSau noi dau tim lai lon khon\r\nChorus: Ngay mai den mang theo hy vong\r\nCho ta tin vao nhung gi trong long',0),(73,'Song Het Minh','Ca khuc ket lai album voi thong diep song het minh.','/upload/12-2.mp3','/upload/1-3.jpg',230,0,1,33,18,3,'2026-05-18 14:30:10','2026-05-18 15:08:41','Verse 1: Neu hom nay la ngay cuoi cung\r\nTa se cuoi that vang khap vung\r\nChorus: Song het minh cho tung phut giay\r\nDe thanh xuan khong troi qua tay',1),(74,'Lac Troi','Lac troi giưa doi','/upload/11-1.mp3','/upload/38.jpg',222,4,1,36,17,1,'2026-05-18 15:07:06','2026-05-18 15:33:45','Người theo hương hoa mây mù giăng lối\r\nLàn sương khói phôi phai đưa bước ai xa rồi\r\nĐơn côi mình ta vấn vương\r\nHồi ức, trong men say chiều mưa buồn\r\nNgăn giọt lệ ngừng khiến khoé mi sầu bi\r\nĐường xưa nơi cố nhân từ giã biệt li\r\nCánh hoa rụng rơi\r\nPhận duyên mong manh rẽ lối trong mơ ngày tương phùng\r\nTiếng khóc cuốn theo làn gió bay\r\nThuyền ai qua sông lỡ quên vớt ánh trăng tàn nơi này\r\nTrống vắng bóng ai dần hao gầy\r\n\r\nLòng ta xin nguyện khắc ghi trong tim tình nồng mê say\r\nMặc cho tóc mây vương lên đôi môi cay\r\nBâng khuâng mình ta lạc trôi giữa đời\r\nTa lạc trôi giữa trời\r\nĐôi chân lang thang về nơi đâu?\r\nBao yêu thương giờ nơi đâu?\r\nCâu thơ tình xưa vội phai mờ\r\nTheo làn sương tan biến trong cõi mơ\r\nMưa bụi vương trên làn mi mắt\r\nNgày chia lìa hoa rơi buồn hiu hắt\r\nTiếng đàn ai thêm sầu tương tư lặng mình trong chiều hoàng hôn,\r\nTan vào lời ca (Hey)\r\nLối mòn đường vắng một mình ta\r\nNắng chiều vàng úa nhuộm ngày qua\r\nXin đừng quay lưng xoá\r\nĐừng mang câu hẹn ước kia rời xa\r\nYên bình nơi nào đây\r\nChôn vùi theo làn mây\r\nEh-h-h-h-h, la-la-la-la-a-a\r\nNgười theo hương hoa mây mù giăng lối\r\nLàn sương khói phôi phai đưa bước ai xa rồi\r\nĐơn côi mình ta vấn vương, hồi ức trong men say chiều mưa buồn\r\nNgăn giọt lệ ngừng khiến khoé mi sầu bi\r\nĐường xưa nơi cố nhân từ giã biệt li\r\nCánh hoa rụng rơi\r\nPhận duyên mong manh rẽ lối trong mơ ngày tương phùng\r\nTiếng khóc cuốn theo làn gió bay\r\nThuyền ai qua sông lỡ quên vớt ánh trăng tàn nơi này\r\nTrống vắng bóng ai dần hao gầy\r\n\r\nLòng ta xin nguyện khắc ghi trong tim tình nồng mê say\r\nMặc cho tóc mây vương lên đôi môi cay\r\nBâng khuâng mình ta lạc trôi giữa đời\r\nTa lạc trôi giữa trời\r\n\r\nTa lạc trôi (Lạc trôi)\r\nTa lạc trôi giữa đời\r\nLạc trôi giữa trời\r\nYeah, ah-h-h-h-h-h\r\n\r\nTa đang lạc nơi nào (Lạc nơi nào, lạc nơi nào)\r\n\r\nTa đang lạc nơi nào\r\n\r\nLối mòn đường vắng một mình ta\r\nTa đang lạc nơi nào\r\n\r\nNắng chiều vàng úa nhuộm ngày qua\r\nTa đang lạc nơi nào',1);
/*!40000 ALTER TABLE `songs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_logins`
--

DROP TABLE IF EXISTS `user_logins`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_logins` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `login_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ip_address` varchar(45) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_agent` varchar(512) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `success` tinyint(1) NOT NULL DEFAULT '1',
  `failure_reason` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_user_login_user` (`user_id`),
  CONSTRAINT `fk_user_login_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_logins`
--

LOCK TABLES `user_logins` WRITE;
/*!40000 ALTER TABLE `user_logins` DISABLE KEYS */;
INSERT INTO `user_logins` VALUES (16,34,'2025-09-15 12:43:36','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36',1,NULL),(17,34,'2025-09-15 12:44:06','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36',1,NULL),(18,33,'2025-09-15 12:46:00','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36',1,NULL),(19,32,'2025-09-15 12:46:34','0:0:0:0:0:0:0:1','Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36',1,NULL);
/*!40000 ALTER TABLE `user_logins` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `full_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role` enum('ROLE_USER','ROLE_AUTHOR','ROLE_ADMIN') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `active` tinyint(1) DEFAULT '1',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `gender` enum('MALE','FEMALE','OTHER') COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`),
  KEY `idx_username` (`username`),
  KEY `idx_email` (`email`),
  KEY `idx_role` (`role`),
  KEY `idx_users_fullname_fulltext` (`full_name`)
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (2,'john_doe','john@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P2kNi3sUE4VMNi','John Doe',NULL,'ROLE_AUTHOR',1,'2025-07-02 13:31:37','2025-08-02 10:41:09',NULL),(3,'jane_smith','jane@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P2kNi3sUE4VMNi','Jane Smith',NULL,'ROLE_AUTHOR',1,'2025-07-02 13:31:37','2025-08-02 10:41:09',NULL),(4,'mike_wilson','mike@example.com1','$2a$10$AES/GO8FfO4F/xs7XdlTTu8xHJSALuGKYqfjU1GwphqNqKNgjhYhe','Mike Wilson123SDF123','','ROLE_USER',1,'2025-07-02 13:31:37','2025-08-02 10:41:09',NULL),(5,'sarah_jones','sarah@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P2kNi3sUE4VMNi','Sarah Jones',NULL,'ROLE_USER',1,'2025-07-02 13:31:37','2025-08-02 10:41:09',NULL),(6,'david_brown','david@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P2kNi3sUE4VMNi','David Brown',NULL,'ROLE_AUTHOR',1,'2025-07-02 13:31:37','2025-08-02 10:41:09',NULL),(7,'lisa_garcia','lisa@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P2kNi3sUE4VMNi','Lisa Garcia',NULL,'ROLE_USER',1,'2025-07-02 13:31:37','2025-08-02 10:41:09',NULL),(8,'tom_anderson','tom@example.com','$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P2kNi3sUE4VMNi','Tom Anderson',NULL,'ROLE_USER',1,'2025-07-02 13:31:37','2025-08-02 10:41:09',NULL),(12,'testuser1','test@exampl1e.com','$2a$10$K7ukE7UklIDCL9Hkp5CV/.rYvJ08OpBLRlQs2D8C.QSVv4EmYWnIe','Test User',NULL,'ROLE_USER',1,'2025-07-02 13:47:55','2025-07-02 13:47:55',NULL),(13,'testuser','test@example.com','$2a$10$MhBpPWfx/fUh7r1c8dSZkO3AaFcPlOoiARJCizhmYKe0aEiBOX7fC','Test User',NULL,'ROLE_USER',1,'2025-07-02 14:27:45','2025-08-02 10:41:09',NULL),(14,'testuser3','test@exa1mple.com','$2a$10$hs2odHQRd58ZUFe1TrGn9eghROs2V8/VyLB8Sn8EIWpTWM0y/Ye0.','Test User',NULL,'ROLE_USER',1,'2025-07-02 14:28:30','2025-08-02 10:41:09',NULL),(15,'leduc1','leduc11@gmail.com','$2a$10$D4VCjeSUV30aLi/tOJ.xheOKzbDA/PogkWR4bnud5L6seDp6mp5H.','Test User',NULL,'ROLE_USER',1,'2025-07-03 07:33:37','2025-08-02 10:41:09',NULL),(16,'testuser21','heeeeetest@example.com','$2a$10$eniI4XJ1Xd9isX3hjPo2TOiK981IbMIFS6DNrSYW2oH1US21JvIsa','Test User',NULL,'ROLE_USER',1,'2025-07-03 09:24:05','2025-08-02 10:41:09',NULL),(17,'hgjdf1','heeeeetest@gmail.com','$2a$10$7UkE7UK1IDCL9hkp5CV/.rYvJ080pBLR1Qs2D8C.QSVv4EmYinIe','Test User',NULL,'ROLE_USER',1,'2025-07-03 09:25:16','2025-08-02 10:41:09',NULL),(18,'newuser1','newuser1@example.com','$2a$10$T8PdhheUrqkLBpN79LPKBeJOOP09P97hNKzpHsUUUe7R.k6ICzBAO','Test New User',NULL,'ROLE_USER',1,'2025-07-03 09:34:02','2025-08-02 10:41:09',NULL),(19,'testok','testok@example.com','$2a$10$wYaTAGDoqgce8Kr5JaG3LuAFaDzWdtlY7HZSE.LPwoEaUQ4Vl100W','Demo Test',NULL,'ROLE_USER',1,'2025-07-03 09:36:08','2025-08-02 10:41:09',NULL),(20,'demo123','demo123@example.com','$2a$10$XbN6vjR/ZSDczuM1cMxoBuRHZB6LcJZjR4rWxbc/5RNhLMucLzxuW','Demo User',NULL,'ROLE_USER',1,'2025-07-03 09:45:05','2026-08-27 02:26:14',NULL),(21,'demo1231','dem1o123@example.com','$2a$10$t47SnK5o1VMyJEZPs6kgOeHIUtJOjLLsKQhSDRynnVYUntfPTe19.','Lê Đức',NULL,'ROLE_USER',1,'2025-07-03 10:46:14','2025-08-02 10:41:09',NULL),(22,'johnsmith','johnsmith@example.com','$2a$10$vTHzMe6jzWYfI7XE5ZEGluJn6ual9r8Wv/55zNB/7OZQ6AnO7R6Nq','John Smith',NULL,'ROLE_USER',1,'2025-07-03 10:47:41','2025-08-02 10:41:09',NULL),(23,'leductest','leductest@example.com','$2a$10$FC2fp1An9VUQRmXpEy.kv.K.D4wMiMfA01jkEerzZw0e9kxK8LvcS','Le Duc Test',NULL,'ROLE_USER',1,'2025-07-03 10:55:08','2025-08-02 10:41:09',NULL),(24,'leductest1111','leductes1t@example.com','$2a$10$lu92g9GQC6tNHJBnRXLnPOjaVVAPA27vU9pVo3yLP4HRGXylpdQ86','Le Duc Test',NULL,'ROLE_USER',1,'2025-07-03 15:42:17','2025-08-02 10:41:09',NULL),(25,'duc','leductes11t@example.com','$2a$10$AES/GO8FfO4F/xs7XdlTTu8xHJSALuGKYqfjU1GwphqNqKNgjhYhe','Le Duc Test',NULL,'ROLE_ADMIN',1,'2025-07-03 15:42:54','2025-08-02 10:41:09',NULL),(26,'duc1T1','leducte1s111t@example.com','$2a$10$oCunT5TW9vAVgUAH6HVdbemxyRLxgUOZW5s27G1ekzMgCAiGcVuoK','Le Duc Test',NULL,'ROLE_USER',1,'2025-07-03 15:48:56','2025-08-02 10:41:09',NULL),(27,'duc1T11','mailleduc05122004@gmail.com','$2a$10$gO8X1Ly71X73L6nUjUk00.BeqJ2J51kfnaM5UPapnwY.fZRAlGGey','duc1T11',NULL,'ROLE_USER',1,'2025-07-03 16:09:28','2025-08-02 10:41:09',NULL),(28,'JHB09H','leducteHH1Us111t@example.com','$2a$10$TqEKYzqBrJp0YlD32Fzs0ePLweB17xKlNCJnGOKuneQKBzPPCfvhy','Le Duc Test',NULL,'ROLE_USER',1,'2025-07-03 16:18:12','2025-08-02 10:41:09',NULL),(29,'JHB109H','leducteHH112Us111t@example.com','$2a$10$PhGEeXwA1iWV14D6xa7vCulcnKouBs5xyuGak32jU4gWN4S6IJQey','Le Duc Test',NULL,'ROLE_USER',1,'2025-08-02 10:44:26','2025-08-03 08:58:02',NULL),(30,'JHB1019H','leducteHH1121Us111t@example.com','$2a$10$PfyWB6h251VMci0iVEKCW.iQm332.JvIKD/nyfLPKe61Mtu.J6U4y','Le Duc Test',NULL,'ROLE_USER',1,'2025-08-02 10:45:08','2025-08-03 08:58:02',NULL),(31,'JHB123019H','leducte23HH1121Us111t@example.com','$2a$10$n0G2N0oq8UgUzBxKFHhcYeCgdbD94pp7ZOCNMZIZtx9XerLQJWQvy','Le Duc Test',NULL,'ROLE_ADMIN',1,'2025-08-02 10:45:20','2025-08-02 10:45:44',NULL),(32,'duc2','rleducte23HH1121Us1111t@example.com','$2a$10$CgpPHU0BGKmvfceM9dr4T.E7RRqC884L7wyAWD08gnNsvpmryenFC','Le Duc Test',NULL,'ROLE_ADMIN',1,'2025-08-02 10:48:39','2025-08-02 11:05:18',NULL),(33,'duc21','le1ducte23HH1121Us1111t@example.com','$2a$10$F875NQ74ZIGozXxxxHmFqOUl2tyFqq6.so8wBns87nUKUas70o61G','Nguyen Van Chung',NULL,'ROLE_AUTHOR',1,'2025-08-02 11:07:51','2025-09-12 01:08:48',NULL),(34,'user','TUEIOSHDF2@GMAIL.COM','$2a$10$CgpPHU0BGKmvfceM9dr4T.E7RRqC884L7wyAWD08gnNsvpmryenFC','TIEP','/upload/userImg/4b103d63-7fbd-43da-abf1-86ad02bcbc1c_mui-la-gi-1.webp','ROLE_USER',1,'2025-08-03 09:14:45','2025-09-11 16:21:00',NULL),(35,'duc1','duc@gmail.com','$2a$10$DFF45ejLTvGkPisU3b296O5Mhbf2ycLqbVM6g6VEyW7AEpRGaqxD6','Đức Lê','/upload/userImg/e077c6a7-3f8b-4c7b-b659-23bf4ca50760_image.jpg','ROLE_AUTHOR',1,'2026-01-19 02:36:14','2026-01-19 03:17:35',NULL),(36,'duc12','duc@gmail.com1','$2a$10$f8oYETqQnASJxkMdqGkxsuqi.GhXA9YfCvU3TqczdQfbTgAxFsk06','Đức Lê',NULL,'ROLE_ADMIN',1,'2026-01-21 03:35:38','2026-08-27 02:26:14',NULL),(37,'testu48962','testu48962@example.com','$2a$10$oLK3/TQ04jEQzjSu3rW4h.nILR/B/.CPPDsyxwkkcqZaT.fQH6YC2','Test User',NULL,'ROLE_AUTHOR',1,'2026-04-07 14:50:20','2026-08-27 02:26:14','OTHER'),(38,'hunghn','mailledu2c05122004@gmail.com','$2a$10$hBZIIyGhGh0iQq7b6nk3pu1meXjVRG5T6zaBA/ZCIiiamjDdiu78u','hunghn','/upload/userImg/19c94ec3-c161-4729-8591-63ad5e801bd9_Thiết kế chưa có tên (3).png','ROLE_USER',1,'2026-04-09 03:34:47','2026-08-27 02:26:14','FEMALE'),(39,'user1','leduc2004lienquan@gmail.com','$2a$10$hNTsisVoiTeONjaiKYux2u4bUBPHxxYnLiotwx/ZW3MPOU0BVa3UW','user1','/upload/userImg/0dbb1818-6936-432a-9456-51d762144623_f-3.jpg','ROLE_AUTHOR',1,'2026-04-09 08:55:10','2026-08-27 02:26:14','MALE'),(40,'user12','leduc2004lien2quan@gmail.com','$2a$10$16eobRPBQNdjTAHcXJUQb.95UG4r0qz2LExSHDXHcj2Vk1h2vbVPW','user12','/upload/userImg/01db96e9-8d82-4424-bc8b-3a28b59fd56e_f-6.jpg','ROLE_AUTHOR',1,'2026-04-12 01:24:57','2026-08-27 02:26:14','FEMALE');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'music_db'
--

--
-- Current Database: `music_db`
--

USE `music_db`;

--
-- Final view structure for view `artist_stats`
--

/*!50001 DROP VIEW IF EXISTS `artist_stats`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_unicode_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50001 VIEW `artist_stats` AS select `u`.`id` AS `id`,`u`.`username` AS `username`,`u`.`full_name` AS `full_name`,count(distinct `s`.`id`) AS `total_songs`,count(distinct `a`.`id`) AS `total_albums`,sum(`s`.`play_count`) AS `total_plays`,count(distinct `f`.`follower_id`) AS `follower_count`,`u`.`created_at` AS `created_at` from (((`users` `u` left join `songs` `s` on(((`u`.`id` = `s`.`artist_id`) and (`s`.`active` = 1)))) left join `albums` `a` on((`u`.`id` = `a`.`artist_id`))) left join `follows` `f` on((`u`.`id` = `f`.`following_id`))) where (`u`.`role` in ('AUTHOR','ADMIN')) group by `u`.`id`,`u`.`username`,`u`.`full_name`,`u`.`created_at` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `popular_songs`
--

/*!50001 DROP VIEW IF EXISTS `popular_songs`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_unicode_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50001 VIEW `popular_songs` AS select `s`.`id` AS `id`,`s`.`title` AS `title`,`u`.`full_name` AS `artist_name`,`g`.`name` AS `genre_name`,`s`.`play_count` AS `play_count`,count(distinct `l`.`id`) AS `like_count`,`s`.`created_at` AS `created_at` from (((`songs` `s` left join `users` `u` on((`s`.`artist_id` = `u`.`id`))) left join `genres` `g` on((`s`.`genre_id` = `g`.`id`))) left join `likes` `l` on((`s`.`id` = `l`.`song_id`))) where (`s`.`active` = 1) group by `s`.`id`,`s`.`title`,`u`.`full_name`,`g`.`name`,`s`.`play_count`,`s`.`created_at` order by `s`.`play_count` desc,count(distinct `l`.`id`) desc */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `public_playlists`
--

/*!50001 DROP VIEW IF EXISTS `public_playlists`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_unicode_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50001 VIEW `public_playlists` AS select `p`.`id` AS `id`,`p`.`name` AS `name`,`p`.`description` AS `description`,`u`.`full_name` AS `creator_name`,count(`ps`.`song_id`) AS `song_count`,`p`.`created_at` AS `created_at`,`p`.`updated_at` AS `updated_at` from ((`playlists` `p` left join `users` `u` on((`p`.`user_id` = `u`.`id`))) left join `playlist_songs` `ps` on((`p`.`id` = `ps`.`playlist_id`))) where (`p`.`is_public` = 1) group by `p`.`id`,`p`.`name`,`p`.`description`,`u`.`full_name`,`p`.`created_at`,`p`.`updated_at` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `song_stats`
--

/*!50001 DROP VIEW IF EXISTS `song_stats`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_unicode_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50001 VIEW `song_stats` AS select `s`.`id` AS `id`,`s`.`title` AS `title`,`u`.`full_name` AS `artist_name`,`g`.`name` AS `genre_name`,`s`.`play_count` AS `play_count`,count(distinct `l`.`id`) AS `like_count`,count(distinct `ph`.`id`) AS `total_plays`,`s`.`created_at` AS `created_at` from ((((`songs` `s` left join `users` `u` on((`s`.`artist_id` = `u`.`id`))) left join `genres` `g` on((`s`.`genre_id` = `g`.`id`))) left join `likes` `l` on((`s`.`id` = `l`.`song_id`))) left join `play_history` `ph` on((`s`.`id` = `ph`.`song_id`))) where (`s`.`active` = 1) group by `s`.`id`,`s`.`title`,`u`.`full_name`,`g`.`name`,`s`.`play_count`,`s`.`created_at` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-08-27  9:26:45
