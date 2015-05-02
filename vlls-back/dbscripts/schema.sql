DROP SCHEMA IF EXISTS vlls;
CREATE SCHEMA vlls;

USE vlls;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;

CREATE TABLE `role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`)
) CHARACTER SET utf8;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `password` varchar(128) DEFAULT NULL,
  `email` varchar(64) NOT NULL,
  `first_name` varchar(50) DEFAULT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `point` int(11) DEFAULT NULL,
  `birthday` DATE DEFAULT NULL,
  `bio` TEXT DEFAULT NULL,
  `avatar` varchar(128) DEFAULT NULL,
  `phone` char(20) DEFAULT NULL,
  `fb_id` varchar(128) DEFAULT NULL,
  `fb_access_token` varchar(256) DEFAULT NULL,
  `gender` TINYINT(1) DEFAULT NULL,
  `is_active` BIT DEFAULT TRUE,
  `role_id` int(11) NOT NULL,
  `last_login` DATETIME DEFAULT NULL,
  `last_update` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `Fk_user_role_id_idx` (`role_id`),
  
  CONSTRAINT `Fk_user_role`
  FOREIGN KEY (`role_id`) 
  REFERENCES `role` (`id`)
    ON DELETE NO ACTION 
    ON UPDATE NO ACTION
) CHARACTER SET utf8;

ALTER TABLE `user` ADD CONSTRAINT UC_user_name UNIQUE (`name`);
ALTER TABLE `user` ADD CONSTRAINT UC_user_email UNIQUE (`email`);

CREATE INDEX user_name_idx ON `user`(`name`);

--
-- Table structure for table `setting`
--

DROP TABLE IF EXISTS `setting`;

CREATE TABLE `setting` (
  `name` varchar(64) NOT NULL,
  `value` varchar(64) NOT NULL,
  `user_id` int(11) NOT NULL,
  PRIMARY KEY (`name`),
  KEY `Fk_setting_user_id_idx` (`user_id`),

  CONSTRAINT `Fk_setting_user`
  FOREIGN KEY (`user_id`)
  REFERENCES `user` (`id`)
    ON DELETE NO ACTION 
    ON UPDATE NO ACTION
) CHARACTER SET utf8;


--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;

CREATE TABLE `category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`)
) CHARACTER SET utf8;

--
-- Table structure for table `language`
--

DROP TABLE IF EXISTS `language`;

CREATE TABLE `language` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `code` varchar(5) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`)
) CHARACTER SET utf8;

--
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;

CREATE TABLE `course` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `description` varchar(1024) DEFAULT NULL,
  `avatar` varchar(128) DEFAULT NULL,
  `is_public` BIT(1) DEFAULT 0,
  `lang_teach_id` int(11) NOT NULL,
  `lang_speak_id` int(11) NOT NULL,
  `num_of_student` int(11) DEFAULT NULL,
  `recommended` int(11) DEFAULT NULL,
  `creator_id` int(11) NOT NULL,
  `category_id` int(11) NOT NULL,
  `is_active` BIT DEFAULT TRUE,
  `last_update` DATETIME DEFAULT NULL,
  `created_date` DATETIME  DEFAULT NULL,
  `rating` int default null,
  PRIMARY KEY (`id`),
  KEY `Fk_course_category_idx` (`category_id`),
  KEY `Fk_course_user_idx` (`creator_id`),
  KEY `Fk_course_language_teach_idx` (`lang_teach_id`),
  KEY `Fk_course_language_speak_idx` (`lang_speak_id`),
  KEY `Fk_course_last_update_idx` (`last_update`),

  CONSTRAINT `Fk_course_category` 
  FOREIGN KEY (`category_id`) 
  REFERENCES `category` (`id`)
    ON DELETE NO ACTION 
    ON UPDATE NO ACTION,
  CONSTRAINT `Fk_course_user` 
  FOREIGN KEY (`creator_id`)
  REFERENCES `user` (`id`)
    ON DELETE NO ACTION 
    ON UPDATE NO ACTION,
  CONSTRAINT `Fk_course_lang_teach`
  FOREIGN KEY (`lang_teach_id`)
  REFERENCES `language` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `Fk_course_lang_speak`
  FOREIGN KEY (`lang_speak_id`)
  REFERENCES `language` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) CHARACTER SET utf8;

CREATE INDEX course_name_idx ON `course`(`name`);
CREATE INDEX course_is_public_idx ON `course`(`is_public`);

--
-- Table structure for table `level`
--

DROP TABLE IF EXISTS `level`;

CREATE TABLE `level` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) NOT NULL,
  `description` varchar(256) DEFAULT NULL,
  `course_id` int(11) NOT NULL,
  `is_active` BIT DEFAULT TRUE,
  `last_update` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `Fk_level_course_idx` (`course_id`),
  KEY `Fk_level_last_update_idx` (`last_update`),

  CONSTRAINT `Fk_level_course`
  FOREIGN KEY (`course_id`)
  REFERENCES `course` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) CHARACTER SET utf8;

--
-- Table structure for table `item`
--

DROP TABLE IF EXISTS `item`;

CREATE TABLE `item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(128) DEFAULT NULL,
  `meaning` varchar(512) NOT NULL,
  `pronun` varchar(64) DEFAULT NULL,
  `type` varchar(128) DEFAULT NULL,
  `audio` varchar(256) DEFAULT NULL,
  `level_id` int(11) NOT NULL,
  `is_active` BIT DEFAULT TRUE,
  `last_update` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `Fk_item_level_idx` (`level_id`),
  KEY `Fk_item_last_update_idx` (`last_update`),

  CONSTRAINT `Fk_item_level`
  FOREIGN KEY (`level_id`)
  REFERENCES `level` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) CHARACTER SET utf8;

--
-- Table structure for table `picture`
--

DROP TABLE IF EXISTS `picture`;

CREATE TABLE `picture` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `source` varchar(256) NOT NULL,
  `uploader_id` int(11) NOT NULL,
  `html_format` varchar(512) NOT NULL,
  `item_id` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `Fk_picture_user_idx` (`uploader_id`),
  KEY `Fk_picture_item_idx` (`item_id`),

  CONSTRAINT `Fk_picture_user`
  FOREIGN KEY (`uploader_id`)
  REFERENCES `user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,

  CONSTRAINT `Fk_picture_item`
  FOREIGN KEY (`item_id`)
  REFERENCES `item` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) CHARACTER SET utf8;

--
-- Table structure for table `liked_picture`
--

DROP TABLE IF EXISTS `liked_picture`;

CREATE TABLE `liked_picture` (
  `user_id` int(11) NOT NULL,
  `picture_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`, `picture_id`),
  KEY `Fk_liked_picture_user_idx` (`user_id`),
  KEY `Fk_liked_picture_picture_idx` (`picture_id`),

  CONSTRAINT `Fk_liked_picture_user`
  FOREIGN KEY (`user_id`)
  REFERENCES `user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,

  CONSTRAINT `Fk_liked_picture_picture`
  FOREIGN KEY (`picture_id`)
  REFERENCES `picture` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) CHARACTER SET utf8;

--
-- Table structure for table `learning_course`
--

DROP TABLE IF EXISTS `learning_course`;

CREATE TABLE `learning_course` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `course_id` int(11) NOT NULL,
  `pin_status` BIT NOT NULL,
  `progress` int DEFAULT NULL,
  `point` int DEFAULT 0,
  `last_update` DATETIME DEFAULT NULL,
  `last_sync` DATETIME DEFAULT NULL,
  `rating` int DEFAULT null,
  `next_revise_time` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `Fk_learning_course_course_idx` (`course_id`),
  KEY `Fk_learning_course_user_idx` (`user_id`),
  KEY `Fk_learning_course_last_update_idx` (`last_update`),
  KEY `Fk_learning_course_next_revise_time_idx` (`next_revise_time`),

  CONSTRAINT `Fk_learning_course_course`
  FOREIGN KEY (`course_id`)
  REFERENCES `course` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `Fk_learning_course_user`
  FOREIGN KEY (`user_id`)
  REFERENCES `user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) CHARACTER SET utf8;

ALTER TABLE `learning_course` ADD CONSTRAINT UC_learning_course_user_course
UNIQUE (`user_id`, `course_id`);

--
-- Table structure for table `learning_level`
--

DROP TABLE IF EXISTS `learning_level`;

CREATE TABLE `learning_level` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `learning_course_id` int(11) NOT NULL,
  `level_id` int(11) NOT NULL,
  `progress` int DEFAULT NULL,
  `last_update` DATETIME DEFAULT NULL,
  `last_sync` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `Fk_learning_level_level_idx` (`level_id`),
  KEY `Fk_learning_level_learning_course_idx` (`learning_course_id`),
  KEY `Fk_learning_level_last_update_idx` (`last_update`),

  CONSTRAINT `Fk_learning_level_learning_course`
  FOREIGN KEY (`learning_course_id`)
  REFERENCES `learning_course` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `Fk_learning_level_level`
  FOREIGN KEY (`level_id`)
  REFERENCES `level` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) CHARACTER SET utf8;

ALTER TABLE `learning_level` ADD CONSTRAINT UC_learning_level_learning_course_level
UNIQUE (`learning_course_id`, `level_id`);

--
-- Table structure for table `learning_item`
--

DROP TABLE IF EXISTS `learning_item`;

CREATE TABLE `learning_item` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `item_id` int(11) NOT NULL,
  `learning_level_id` int(11) NOT NULL,
  `picture_id` int(11) DEFAULT NULL,
  `right_no` int DEFAULT NULL,
  `wrong_no` int DEFAULT NULL,
  `last_update` DATETIME DEFAULT NULL,
  `revised_no` INT DEFAULT NULL,
  `next_revise_time` DATETIME DEFAULT NULL,
  `penalty_rate` DOUBLE DEFAULT NULL,
  `last_learn_session_result` BIT DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `Fk_learning_item_item_idx` (`item_id`),
  KEY `Fk_learning_item_learning_level_idx` (`learning_level_id`),
  KEY `Fk_learning_item_picture_idx` (`picture_id`),
  KEY `Fk_learning_item_last_update_idx` (`last_update`),
  KEY `Fk_learning_item_next_revise_time_idx` (`next_revise_time`),

  CONSTRAINT `Fk_learning_item_picture`
  FOREIGN KEY (`picture_id`)
  REFERENCES `picture` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,

  CONSTRAINT `Fk_learning_item_item`
  FOREIGN KEY (`item_id`)
  REFERENCES `item` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,

  CONSTRAINT `Fk_learning_item_learning_level`
  FOREIGN KEY (`learning_level_id`)
  REFERENCES `learning_level` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) CHARACTER SET utf8;

ALTER TABLE `learning_item` ADD CONSTRAINT UC_learning_item_learning_level_item
UNIQUE (`item_id`, `learning_level_id`);

--
-- Table structure for table `question`
--

DROP TABLE IF EXISTS `question`;

CREATE TABLE `question` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `text` varchar(256) NOT NULL,
  `type` tinyint(1) NOT NULL,
  `answer_type` bit(1) NOT NULL, -- name or meaning
  `incorrect_answer_type` tinyint(1) DEFAULT NULL,
  `incorrect_answer_num` int DEFAULT NULL,
  `incorrect_answer_raw` varchar (512) DEFAULT NULL,
  `correct_answer_id` int(11) NOT NULL,
  `is_system_generated` BIT DEFAULT FALSE,
  PRIMARY KEY (`id`),
  KEY `Fk_question_item_idx` (`correct_answer_id`),

  CONSTRAINT `Fk_question_item`
  FOREIGN KEY (`correct_answer_id`)
  REFERENCES `item` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) CHARACTER SET utf8;

--
-- Table structure for table `friendship`
--

DROP TABLE IF EXISTS `friendship`;

CREATE TABLE `friendship` (
  `friend_one_id` int(11) NOT NULL,
  `friend_two_id` int(11) NOT NULL,
  `is_friend` BIT(1) DEFAULT 0,
  `last_update` DATETIME DEFAULT NULL,
  PRIMARY KEY (`friend_one_id`, `friend_two_id`),
  KEY `Fk_friendship_user1_idx` (`friend_one_id`),
  KEY `Fk_friendship_user2_idx` (`friend_two_id`),
  KEY `Fk_friendship_is_friend_idx` (`is_friend`),
  KEY `Fk_friendship_last_update_idx` (`last_update`),

  CONSTRAINT `Fk_friendship_user1`
  FOREIGN KEY (`friend_one_id`)
  REFERENCES `user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,

  CONSTRAINT `Fk_friendship_user2`
  FOREIGN KEY (`friend_two_id`)
    REFERENCES `user` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION
) CHARACTER SET utf8;

--
-- Table structure for table `similar_course`
--

DROP TABLE IF EXISTS `similar_course`;

CREATE TABLE `similar_course` (
  `course_one_id` int(11) NOT NULL,
  `course_two_id` int(11) NOT NULL,
  `similar_rate` double DEFAULT 0,
  PRIMARY KEY (`course_one_id`, `course_two_id`),
  KEY `Fk_similar_course1_idx` (`course_one_id`),
  KEY `Fk_similar_course2_idx` (`course_two_id`),

  CONSTRAINT `Fk_similar_course1`
  FOREIGN KEY (`course_one_id`)
  REFERENCES `course` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,

  CONSTRAINT `Fk_similar_course2`
  FOREIGN KEY (`course_two_id`)
  REFERENCES `course` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) CHARACTER SET utf8;

--
-- Table structure for table `course_rating_rate`
--

DROP TABLE IF EXISTS `course_rating_rate`;
CREATE TABLE `course_rating_rate` (
  `user_id` INT(11) NOT NULL,
  `course_id` INT(11) NOT NULL,
  `rating_similar` DOUBLE  NOT NULL,
  PRIMARY KEY (`user_id`, `course_id`),
  KEY `Fk_course_rating_rate_course_idx` (`course_id`),
  KEY `Fk_course_rating_rate_user_idx` (`user_id`),

  CONSTRAINT `Fk_course_rating_rate_course`
  FOREIGN KEY (`course_id`)
  REFERENCES `course` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `Fk_course_rating_rate_user`
  FOREIGN KEY (`user_id`)
  REFERENCES `user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) CHARACTER SET utf8;

--
-- Table structure for table `conversation`
--

DROP TABLE IF EXISTS `conversation`;
CREATE TABLE `conversation` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_one_id` INT(11) NOT NULL,
  `user_two_id` INT(11) NOT NULL,
  `last_update` DATETIME DEFAULT NULL,
  `read` BIT DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `Fk_conversation_user_one_id_idx` (`user_one_id`),
  KEY `Fk_conversation_user_two_id_idx` (`user_two_id`),

  CONSTRAINT `Fk_conversation_user_one`
  FOREIGN KEY (`user_two_id`)
  REFERENCES `user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `Fk_conversation_user_two`
  FOREIGN KEY (`user_two_id`)
  REFERENCES `user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) CHARACTER SET utf8;

--
-- Table structure for table `conversation_reply`
--

DROP TABLE IF EXISTS `conversation_reply`;
CREATE TABLE `conversation_reply` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `reply` TEXT NOT NULL,
  `user_id` INT(11) NOT NULL,
  `time` DATETIME DEFAULT NULL,
  `conversation_id` INT(11) NOT NULL,
  `read` BIT DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `Fk_conversation_reply_user_idx` (`user_id`),
  KEY `Fk_conversation_reply_conversation_idx` (`conversation_id`),

  CONSTRAINT `Fk_conversation_reply_user`
  FOREIGN KEY (`user_id`)
  REFERENCES `user` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `Fk_conversation_reply_conversation`
  FOREIGN KEY (`conversation_id`)
  REFERENCES `conversation` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) CHARACTER SET utf8;

