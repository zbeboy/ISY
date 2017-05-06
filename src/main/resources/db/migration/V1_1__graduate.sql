CREATE TABLE graduation_design_release(
  graduation_design_release_id VARCHAR(64) PRIMARY KEY ,
  graduation_design_title VARCHAR(100) NOT NULL ,
  release_time TIMESTAMP NOT NULL ,
  username VARCHAR(64) NOT NULL ,
  start_time DATETIME NOT NULL ,
  end_time DATETIME NOT NULL ,
  fill_teacher_start_time DATETIME NOT NULL ,
  fill_teacher_end_time DATETIME NOT NULL ,
  graduation_design_is_del BOOLEAN NOT NULL ,
  allow_grade VARCHAR(5) NOT NULL ,
  department_id INT NOT NULL ,
  science_id INT NOT NULL ,
  FOREIGN KEY (username) REFERENCES users(username),
  FOREIGN KEY (department_id) REFERENCES department(department_id),
  FOREIGN KEY (science_id) REFERENCES science(science_id)
);

CREATE TABLE graduation_design_release_file(
  graduation_design_release_id VARCHAR(64) NOT NULL ,
  file_id VARCHAR(64) NOT NULL ,
  FOREIGN KEY (graduation_design_release_id) REFERENCES graduation_design_release(graduation_design_release_id),
  FOREIGN KEY (file_id) REFERENCES files(file_id)
);

CREATE TABLE graduation_design_teacher(
  graduation_design_teacher_id VARCHAR(64) PRIMARY KEY ,
  graduation_design_release_id VARCHAR(64) NOT NULL ,
  staff_id INT NOT NULL ,
  student_count INT NOT NULL ,
  username VARCHAR(64) NOT NULL ,
  FOREIGN KEY (graduation_design_release_id) REFERENCES graduation_design_release(graduation_design_release_id),
  FOREIGN KEY (username) REFERENCES users(username),
  FOREIGN KEY (staff_id) REFERENCES staff(staff_id)
);

CREATE TABLE graduation_design_hope_tutor(
  graduation_design_teacher_id VARCHAR(64) NOT NULL ,
  student_id INT NOT NULL ,
  FOREIGN KEY (graduation_design_teacher_id) REFERENCES  graduation_design_teacher(graduation_design_teacher_id),
  FOREIGN KEY (student_id) REFERENCES student(student_id)
);

CREATE TABLE graduation_design_tutor(
  graduation_design_teacher_id VARCHAR(64) NOT NULL ,
  student_id INT NOT NULL ,
  FOREIGN KEY (graduation_design_teacher_id) REFERENCES  graduation_design_teacher(graduation_design_teacher_id),
  FOREIGN KEY (student_id) REFERENCES student(student_id)
);

CREATE TABLE graduation_design_plan(
  graduation_design_plan_id VARCHAR(64) PRIMARY KEY ,
  scheduling VARCHAR(100) NOT NULL ,
  supervision_time VARCHAR(100) NOT NULL ,
  guide_location VARCHAR(100) NOT NULL ,
  guide_content VARCHAR(150) NOT NULL ,
  note VARCHAR(100) NOT NULL ,
  graduation_design_teacher_id VARCHAR(64) NOT NULL ,
  FOREIGN KEY (graduation_design_teacher_id) REFERENCES  graduation_design_teacher(graduation_design_teacher_id)
);

CREATE TABLE graduation_design_presubject(
  graduation_design_presubject_id VARCHAR(64) PRIMARY KEY ,
  presubject_title VARCHAR(100) NOT NULL ,
  presubject_plan TEXT NOT NULL ,
  update_time DATETIME NOT NULL ,
  graduation_design_release_id VARCHAR(64) NOT NULL ,
  student_id INT NOT NULL ,
  FOREIGN KEY (graduation_design_release_id) REFERENCES graduation_design_release(graduation_design_release_id),
  FOREIGN KEY (student_id) REFERENCES student(student_id)
);

CREATE TABLE graduation_design_declare_data(
  graduation_design_declare_data_id VARCHAR(64) PRIMARY KEY ,
  graduation_date VARCHAR(30),
  department_name VARCHAR(200),
  science_name VARCHAR(200),
  organize_names VARCHAR(150),
  organize_peoples VARCHAR(150),
  graduation_design_release_id VARCHAR(64) NOT NULL ,
  FOREIGN KEY (graduation_design_release_id) REFERENCES graduation_design_release(graduation_design_release_id)
);

CREATE TABLE graduation_design_subject_type(
  subject_type_id INT PRIMARY KEY AUTO_INCREMENT,
  subject_type_name VARCHAR(30) NOT NULL
);

CREATE TABLE graduation_design_subject_origin_type(
  origin_type_id INT PRIMARY KEY AUTO_INCREMENT,
  origin_type_name VARCHAR(30) NOT NULL
);

CREATE TABLE graduation_design_declare(
  graduation_design_declare_id VARCHAR(64) PRIMARY KEY ,
  graduation_design_topic VARCHAR(100),
  subject_type_id INT,
  origin_type_id INT,
  is_new_subject BOOLEAN,
  is_new_teacher_make BOOLEAN,
  is_new_subject_make BOOLEAN,
  is_old_subject_change BOOLEAN,
  old_subject_uses_times BOOLEAN,
  plan_period VARCHAR(10),
  guide_teacher VARCHAR(30),
  academic_title_name VARCHAR(30),
  assistant_teacher VARCHAR(30),
  assistant_teacher_academic VARCHAR(30),
  guide_times INT,
  guide_peoples INT,
  student_number VARCHAR(20),
  student_name VARCHAR(30),
  graduation_design_declare_data_id VARCHAR(64) NOT NULL ,
  student_id INT NOT NULL ,
  staff_id INT NOT NULL ,
  FOREIGN KEY (graduation_design_declare_data_id) REFERENCES graduation_design_declare_data(graduation_design_declare_data_id),
  FOREIGN KEY (student_id) REFERENCES student(student_id),
  FOREIGN KEY (staff_id) REFERENCES staff(staff_id)
);

CREATE TABLE graduation_design_declare_ok(
  graduation_design_declare_ok_id VARCHAR(64) PRIMARY KEY ,
  graduation_design_declare_id VARCHAR(64) NOT NULL ,
  FOREIGN KEY (graduation_design_declare_id) REFERENCES graduation_design_declare(graduation_design_declare_id)
);

CREATE TABLE graduation_design_datum_type(
  graduation_design_datum_type_id INT PRIMARY KEY AUTO_INCREMENT,
  graduation_design_datum_type_name VARCHAR(25) NOT NULL
);

CREATE TABLE graduation_design_datum(
  graduation_design_datum_id VARCHAR(64) NOT NULL ,
  version VARCHAR(10) ,
  file_id VARCHAR(64) NOT NULL ,
  graduation_design_datum_type_id INT NOT NULL ,
  graduation_design_release_id VARCHAR(64) NOT NULL ,
  FOREIGN KEY (graduation_design_release_id) REFERENCES graduation_design_release(graduation_design_release_id),
  FOREIGN KEY (file_id) REFERENCES files(file_id)
);

CREATE TABLE defense_arrangement(
  defense_arrangement_id VARCHAR(64) PRIMARY KEY ,
  paper_start_time DATETIME NOT NULL ,
  paper_end_time DATETIME NOT NULL ,
  defense_start_date DATE NOT NULL ,
  defense_end_date DATE NOT NULL ,
  defense_start_time VARCHAR(20) NOT NULL ,
  defense_end_time VARCHAR(20) NOT NULL ,
  defense_note VARCHAR(100),
  graduation_design_release_id VARCHAR(64) NOT NULL ,
  FOREIGN KEY (graduation_design_release_id) REFERENCES graduation_design_release(graduation_design_release_id)
);

CREATE TABLE building(
  building_id INT PRIMARY KEY AUTO_INCREMENT,
  building_name VARCHAR(30) NOT NULL ,
  college_id INT NOT NULL ,
  FOREIGN KEY (college_id) REFERENCES college(college_id)
);

CREATE TABLE schoolroom(
  schoolroom_id INT PRIMARY KEY AUTO_INCREMENT,
  building_id INT NOT NULL ,
  building_code VARCHAR(10) NOT NULL ,
  FOREIGN KEY (building_id) REFERENCES building(building_id)
);

CREATE TABLE defense_group(
  defense_group_id VARCHAR(64) PRIMARY KEY ,
  defense_group_name VARCHAR(20) NOT NULL ,
  defense_group_number INT NOT NULL ,
  schoolroom_id INT NOT NULL ,
  note VARCHAR(100) ,
  group_leader INT NOT NULL ,
  defense_arrangement_id VARCHAR(64) NOT NULL ,
  FOREIGN KEY (schoolroom_id) REFERENCES schoolroom(schoolroom_id),
  FOREIGN KEY (group_leader) REFERENCES staff(staff_id),
  FOREIGN KEY (defense_arrangement_id) REFERENCES defense_arrangement(defense_arrangement_id)
);

CREATE TABLE defense_group_member(
  group_member_id VARCHAR(64) PRIMARY KEY ,
  staff_id INT NOT NULL ,
  defense_group_id VARCHAR(64) NOT NULL ,
  note VARCHAR(100),
  FOREIGN KEY (staff_id) REFERENCES staff(staff_id),
  FOREIGN KEY (defense_group_id) REFERENCES defense_group(defense_group_id)
);

CREATE TABLE defense_order(
  defense_order_id VARCHAR(64) PRIMARY KEY ,
  student_number VARCHAR(20) NOT NULL ,
  student_name VARCHAR(30) NOT NULL ,
  subject VARCHAR(100) NOT NULL ,
  defense_date DATE NOT NULL ,
  defense_time VARCHAR(20) NOT NULL ,
  staff_name VARCHAR(30) NOT NULL ,
  student_id INT NOT NULL ,
  staff_id INT NOT NULL ,
  group_member_id VARCHAR(64) NOT NULL ,
  FOREIGN KEY (student_id) REFERENCES student(student_id),
  FOREIGN KEY (staff_id) REFERENCES staff(staff_id),
  FOREIGN KEY (group_member_id) REFERENCES defense_group_member(group_member_id)
);

CREATE TABLE score_type(
  score_type_id INT PRIMARY KEY AUTO_INCREMENT,
  score_type_name VARCHAR(20) NOT NULL
);

CREATE TABLE graduate_bill(
  graduate_bill_id VARCHAR(64) PRIMARY KEY ,
  score_type_id INT NOT NULL ,
  graduation_design_release_id VARCHAR(64) NOT NULL ,
  graduation_design_declare_id VARCHAR(64) NOT NULL ,
  FOREIGN KEY (graduation_design_release_id) REFERENCES graduation_design_release(graduation_design_release_id),
  FOREIGN KEY (graduation_design_declare_id) REFERENCES graduation_design_declare(graduation_design_declare_id)
);

CREATE TABLE graduate_archives(
  graduate_archives_id VARCHAR(64) PRIMARY KEY ,
  graduate_bill_id VARCHAR(64) NOT NULL ,
  is_excellent BOOLEAN NOT NULL DEFAULT 0,
  archive_number VARCHAR(100)  NOT NULL ,
  note VARCHAR(100)
);