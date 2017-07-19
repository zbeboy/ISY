CREATE TABLE building (
  building_id     INT PRIMARY KEY AUTO_INCREMENT,
  building_name   VARCHAR(30) NOT NULL,
  building_is_del BOOLEAN,
  college_id      INT         NOT NULL,
  FOREIGN KEY (college_id) REFERENCES college (college_id)
);

CREATE TABLE schoolroom (
  schoolroom_id     INT PRIMARY KEY AUTO_INCREMENT,
  building_id       INT         NOT NULL,
  building_code     VARCHAR(10) NOT NULL,
  schoolroom_is_del BOOLEAN,
  FOREIGN KEY (building_id) REFERENCES building (building_id)
);

CREATE TABLE score_type (
  score_type_id   INT PRIMARY KEY AUTO_INCREMENT,
  score_type_name VARCHAR(20) NOT NULL
);

CREATE TABLE graduation_design_release (
  graduation_design_release_id VARCHAR(64) PRIMARY KEY,
  graduation_design_title      VARCHAR(100) NOT NULL,
  release_time                 TIMESTAMP    NOT NULL,
  username                     VARCHAR(64)  NOT NULL,
  start_time                   DATETIME     NOT NULL,
  end_time                     DATETIME     NOT NULL,
  fill_teacher_start_time      DATETIME     NOT NULL,
  fill_teacher_end_time        DATETIME     NOT NULL,
  graduation_design_is_del     BOOLEAN      NOT NULL,
  allow_grade                  VARCHAR(5)   NOT NULL,
  is_ok_teacher                BOOLEAN DEFAULT 0,
  is_ok_teacher_adjust         BOOLEAN DEFAULT 0,
  department_id                INT          NOT NULL,
  science_id                   INT          NOT NULL,
  FOREIGN KEY (username) REFERENCES users (username),
  FOREIGN KEY (department_id) REFERENCES department (department_id),
  FOREIGN KEY (science_id) REFERENCES science (science_id)
);

CREATE TABLE graduation_design_release_file (
  graduation_design_release_id VARCHAR(64) NOT NULL,
  file_id                      VARCHAR(64) NOT NULL,
  FOREIGN KEY (graduation_design_release_id) REFERENCES graduation_design_release (graduation_design_release_id),
  FOREIGN KEY (file_id) REFERENCES files (file_id)
);

CREATE TABLE graduation_design_teacher (
  graduation_design_teacher_id VARCHAR(64) PRIMARY KEY,
  graduation_design_release_id VARCHAR(64) NOT NULL,
  staff_id                     INT         NOT NULL,
  student_count                INT         NOT NULL,
  residue                      INT,
  username                     VARCHAR(64) NOT NULL,
  FOREIGN KEY (graduation_design_release_id) REFERENCES graduation_design_release (graduation_design_release_id),
  FOREIGN KEY (username) REFERENCES users (username),
  FOREIGN KEY (staff_id) REFERENCES staff (staff_id)
);

CREATE TABLE graduation_design_hope_tutor (
  graduation_design_teacher_id VARCHAR(64) NOT NULL,
  student_id                   INT         NOT NULL,
  FOREIGN KEY (graduation_design_teacher_id) REFERENCES graduation_design_teacher (graduation_design_teacher_id),
  FOREIGN KEY (student_id) REFERENCES student (student_id)
);

CREATE TABLE graduation_design_tutor (
  graduation_design_tutor_id   VARCHAR(64) PRIMARY KEY,
  graduation_design_teacher_id VARCHAR(64) NOT NULL,
  student_id                   INT         NOT NULL,
  FOREIGN KEY (graduation_design_teacher_id) REFERENCES graduation_design_teacher (graduation_design_teacher_id),
  FOREIGN KEY (student_id) REFERENCES student (student_id),
  UNIQUE (graduation_design_teacher_id, student_id)
);

CREATE TABLE graduation_design_plan (
  graduation_design_plan_id    VARCHAR(64) PRIMARY KEY,
  scheduling                   VARCHAR(100) NOT NULL,
  supervision_time             VARCHAR(100) NOT NULL,
  guide_content                VARCHAR(150) NOT NULL,
  note                         VARCHAR(100) NOT NULL,
  add_time                     DATETIME     NOT NULL,
  graduation_design_teacher_id VARCHAR(64)  NOT NULL,
  schoolroom_id                INT          NOT NULL,
  FOREIGN KEY (graduation_design_teacher_id) REFERENCES graduation_design_teacher (graduation_design_teacher_id),
  FOREIGN KEY (schoolroom_id) REFERENCES schoolroom (schoolroom_id)
);

CREATE TABLE graduation_design_presubject (
  graduation_design_presubject_id VARCHAR(64) PRIMARY KEY,
  presubject_title                VARCHAR(100) NOT NULL,
  presubject_plan                 TEXT         NOT NULL,
  update_time                     DATETIME     NOT NULL,
  public_level                    INT          NOT NULL,
  graduation_design_release_id    VARCHAR(64)  NOT NULL,
  student_id                      INT          NOT NULL,
  FOREIGN KEY (graduation_design_release_id) REFERENCES graduation_design_release (graduation_design_release_id),
  FOREIGN KEY (student_id) REFERENCES student (student_id),
  UNIQUE (graduation_design_release_id, student_id)
);

CREATE TABLE graduation_design_declare_data (
  graduation_design_declare_data_id VARCHAR(64) PRIMARY KEY,
  graduation_date                   VARCHAR(30),
  department_name                   VARCHAR(200),
  science_name                      VARCHAR(200),
  organize_names                    VARCHAR(150),
  organize_peoples                  VARCHAR(150),
  graduation_design_release_id      VARCHAR(64) NOT NULL,
  FOREIGN KEY (graduation_design_release_id) REFERENCES graduation_design_release (graduation_design_release_id)
);

CREATE TABLE graduation_design_subject_type (
  subject_type_id   INT PRIMARY KEY AUTO_INCREMENT,
  subject_type_name VARCHAR(30) NOT NULL
);

CREATE TABLE graduation_design_subject_origin_type (
  origin_type_id   INT PRIMARY KEY AUTO_INCREMENT,
  origin_type_name VARCHAR(30) NOT NULL
);

CREATE TABLE graduation_design_declare (
  subject_type_id                 INT,
  origin_type_id                  INT,
  is_new_subject                  BOOLEAN,
  is_new_teacher_make             BOOLEAN,
  is_new_subject_make             BOOLEAN,
  is_old_subject_change           BOOLEAN,
  old_subject_uses_times          INT,
  plan_period                     VARCHAR(10),
  assistant_teacher               VARCHAR(30),
  assistant_teacher_academic      VARCHAR(30),
  guide_times                     INT,
  guide_peoples                   INT,
  is_ok_apply                     BOOLEAN DEFAULT 0,
  graduation_design_presubject_id VARCHAR(64) NOT NULL UNIQUE,
  FOREIGN KEY (graduation_design_presubject_id) REFERENCES graduation_design_presubject (graduation_design_presubject_id)
);

CREATE TABLE graduation_design_datum_type (
  graduation_design_datum_type_id   INT PRIMARY KEY AUTO_INCREMENT,
  graduation_design_datum_type_name VARCHAR(50) NOT NULL
);

CREATE TABLE graduation_design_datum (
  graduation_design_datum_id      VARCHAR(64) PRIMARY KEY,
  version                         VARCHAR(10),
  file_id                         VARCHAR(64) NOT NULL,
  graduation_design_datum_type_id INT         NOT NULL,
  graduation_design_tutor_id      VARCHAR(64) NOT NULL,
  update_time                     DATETIME    NOT NULL,
  FOREIGN KEY (graduation_design_datum_type_id) REFERENCES graduation_design_datum_type (graduation_design_datum_type_id),
  FOREIGN KEY (file_id) REFERENCES files (file_id),
  FOREIGN KEY (graduation_design_tutor_id) REFERENCES graduation_design_tutor (graduation_design_tutor_id),
  UNIQUE (graduation_design_datum_type_id, graduation_design_tutor_id)
);

CREATE TABLE defense_arrangement (
  defense_arrangement_id       VARCHAR(64) PRIMARY KEY,
  paper_start_time             DATETIME    NOT NULL,
  paper_end_time               DATETIME    NOT NULL,
  defense_start_time           DATETIME    NOT NULL,
  defense_end_time             DATETIME    NOT NULL,
  interval_time                INT         NOT NULL,
  defense_note                 VARCHAR(100),
  graduation_design_release_id VARCHAR(64) NOT NULL,
  FOREIGN KEY (graduation_design_release_id) REFERENCES graduation_design_release (graduation_design_release_id)
);

CREATE TABLE defense_time (
  day_defense_start_time VARCHAR(20) NOT NULL,
  day_defense_end_time   VARCHAR(20) NOT NULL,
  sort_time              INT         NOT NULL,
  defense_arrangement_id VARCHAR(64) NOT NULL,
  FOREIGN KEY (defense_arrangement_id) REFERENCES defense_arrangement (defense_arrangement_id)
);

CREATE TABLE defense_group (
  defense_group_id       VARCHAR(64) PRIMARY KEY,
  defense_group_name     VARCHAR(20) NOT NULL,
  schoolroom_id          INT         NOT NULL,
  note                   VARCHAR(100),
  leader_id              VARCHAR(64),
  secretary_id           INT,
  defense_arrangement_id VARCHAR(64) NOT NULL,
  create_time            DATETIME    NOT NULL,
  FOREIGN KEY (schoolroom_id) REFERENCES schoolroom (schoolroom_id),
  FOREIGN KEY (defense_arrangement_id) REFERENCES defense_arrangement (defense_arrangement_id)
);

CREATE TABLE defense_group_member (
  graduation_design_teacher_id VARCHAR(64) NOT NULL UNIQUE,
  defense_group_id             VARCHAR(64) NOT NULL,
  note                         VARCHAR(100),
  FOREIGN KEY (graduation_design_teacher_id) REFERENCES graduation_design_teacher (graduation_design_teacher_id),
  FOREIGN KEY (defense_group_id) REFERENCES defense_group (defense_group_id)
);

CREATE TABLE defense_order (
  defense_order_id VARCHAR(64) PRIMARY KEY,
  student_number   VARCHAR(20) NOT NULL,
  student_name     VARCHAR(30) NOT NULL,
  subject          VARCHAR(100),
  defense_date     DATE        NOT NULL,
  defense_time     VARCHAR(20) NOT NULL,
  staff_name       VARCHAR(30) NOT NULL,
  score_type_id    INT,
  sort_num         INT         NOT NULL,
  student_id       INT         NOT NULL,
  defense_group_id VARCHAR(64) NOT NULL,
  FOREIGN KEY (student_id) REFERENCES student (student_id),
  FOREIGN KEY (defense_group_id) REFERENCES defense_group (defense_group_id)
);

CREATE TABLE graduate_bill (
  graduate_bill_id                VARCHAR(64) PRIMARY KEY,
  graduation_design_release_id    VARCHAR(64) NOT NULL,
  graduation_design_presubject_id VARCHAR(64) NOT NULL UNIQUE,
  FOREIGN KEY (graduation_design_release_id) REFERENCES graduation_design_release (graduation_design_release_id),
  FOREIGN KEY (graduation_design_presubject_id) REFERENCES graduation_design_presubject (graduation_design_presubject_id)
);

CREATE TABLE graduate_archives (
  graduate_bill_id VARCHAR(64)  NOT NULL UNIQUE,
  is_excellent     BOOLEAN      NOT NULL DEFAULT 0,
  archive_number   VARCHAR(100) NOT NULL,
  note             VARCHAR(100),
  FOREIGN KEY (graduate_bill_id) REFERENCES graduate_bill (graduate_bill_id)
);

INSERT INTO graduation_design_subject_type (subject_type_name) VALUES ('软件型');
INSERT INTO graduation_design_subject_type (subject_type_name) VALUES ('论文型');
INSERT INTO graduation_design_subject_type (subject_type_name) VALUES ('工程技术研究型');
INSERT INTO graduation_design_subject_type (subject_type_name) VALUES ('工程设计型');
INSERT INTO graduation_design_subject_type (subject_type_name) VALUES ('分理工论文型');

INSERT INTO graduation_design_subject_origin_type (origin_type_name) VALUES ('生产');
INSERT INTO graduation_design_subject_origin_type (origin_type_name) VALUES ('科研');
INSERT INTO graduation_design_subject_origin_type (origin_type_name) VALUES ('教学');
INSERT INTO graduation_design_subject_origin_type (origin_type_name) VALUES ('其他');

INSERT INTO graduation_design_datum_type (graduation_design_datum_type_name) VALUES ('任务书');
INSERT INTO graduation_design_datum_type (graduation_design_datum_type_name) VALUES ('开题报告');
INSERT INTO graduation_design_datum_type (graduation_design_datum_type_name) VALUES ('论文');
INSERT INTO graduation_design_datum_type (graduation_design_datum_type_name) VALUES ('PPT');