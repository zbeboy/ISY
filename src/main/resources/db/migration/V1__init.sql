CREATE TABLE users_type(
  users_type_id INT AUTO_INCREMENT PRIMARY KEY ,
  users_type_name VARCHAR(50) NOT NULL
);

CREATE TABLE users(
  username VARCHAR(64) PRIMARY KEY,
  password VARCHAR(300) NOT NULL,
  enabled BOOLEAN NOT NULL ,
  users_type_id INT NOT NULL ,
  real_name VARCHAR(30) ,
  mobile VARCHAR(15) UNIQUE NOT NULL ,
  avatar VARCHAR(500) ,
  verify_mailbox BOOLEAN ,
  mailbox_verify_code VARCHAR(20) ,
  password_reset_key VARCHAR(20),
  mailbox_verify_valid DATETIME,
  password_reset_key_valid DATETIME,
  lang_key VARCHAR(20),
  join_date DATE,
  FOREIGN KEY(users_type_id) REFERENCES users_type(users_type_id)
);

CREATE TABLE role(
  role_id INT AUTO_INCREMENT PRIMARY KEY ,
  role_name VARCHAR(50) NOT NULL ,
  role_en_name VARCHAR(64) UNIQUE NOT NULL,
  role_type INT NOT NULL
);

CREATE TABLE authorities(
  username VARCHAR(64) NOT NULL ,
  authority VARCHAR(50) NOT NULL ,
  FOREIGN KEY (username) REFERENCES users(username),
  PRIMARY KEY (username,authority)
);

CREATE TABLE application(
  application_id INT AUTO_INCREMENT PRIMARY KEY ,
  application_name VARCHAR(30) NOT NULL ,
  application_sort INT ,
  application_pid INT NOT NULL,
  application_url VARCHAR(300) NOT NULL ,
  application_code VARCHAR(100) NOT NULL ,
  application_en_name VARCHAR(100) NOT NULL ,
  icon VARCHAR(20),
  application_data_url_start_with VARCHAR(300)
);

CREATE TABLE role_application(
  role_id INT NOT NULL ,
  application_id INT NOT NULL ,
  FOREIGN KEY (role_id) REFERENCES role(role_id),
  FOREIGN KEY (application_id) REFERENCES application(application_id),
  PRIMARY KEY (role_id,application_id)
);

CREATE TABLE persistent_logins(
  username VARCHAR(64) NOT NULL ,
  series VARCHAR(64) PRIMARY KEY,
  token VARCHAR(64) NOT NULL ,
  last_used TIMESTAMP NOT NULL
);

CREATE TABLE school(
  school_id INT AUTO_INCREMENT PRIMARY KEY ,
  school_name VARCHAR(200) NOT NULL ,
  school_is_del BOOLEAN
);

CREATE TABLE college(
  college_id INT AUTO_INCREMENT PRIMARY KEY ,
  college_name VARCHAR(200) NOT NULL ,
  college_is_del BOOLEAN,
  school_id INT NOT NULL ,
  FOREIGN KEY (school_id) REFERENCES school(school_id)
);

CREATE TABLE college_application(
  application_id INT NOT NULL ,
  college_id INT NOT NULL ,
  FOREIGN KEY (application_id) REFERENCES application(application_id),
  FOREIGN KEY (college_id) REFERENCES college(college_id),
  PRIMARY KEY (application_id,college_id)
);

CREATE TABLE college_role(
  role_id INT NOT NULL ,
  college_id INT NOT NULL ,
  FOREIGN KEY (role_id) REFERENCES role(role_id),
  FOREIGN KEY (college_id) REFERENCES college(college_id),
  PRIMARY KEY (role_id,college_id)
);

CREATE TABLE department(
  department_id INT AUTO_INCREMENT PRIMARY KEY ,
  department_name VARCHAR(200) NOT NULL ,
  department_is_del BOOLEAN,
  college_id INT NOT NULL ,
  FOREIGN KEY (college_id) REFERENCES college(college_id)
);

CREATE TABLE science(
  science_id INT AUTO_INCREMENT PRIMARY KEY ,
  science_name VARCHAR(200) NOT NULL ,
  science_is_del BOOLEAN,
  department_id INT NOT NULL ,
  FOREIGN KEY (department_id) REFERENCES department(department_id)
);

CREATE TABLE organize(
  organize_id INT AUTO_INCREMENT PRIMARY KEY ,
  organize_name VARCHAR(200) NOT NULL ,
  organize_is_del BOOLEAN,
  science_id INT NOT NULL ,
  grade VARCHAR(5) NOT NULL ,
  FOREIGN KEY (science_id) REFERENCES science(science_id)
);

CREATE TABLE political_landscape(
  political_landscape_id INT AUTO_INCREMENT PRIMARY KEY ,
  political_landscape_name VARCHAR(30) NOT NULL
);

CREATE TABLE nation(
  nation_id INT AUTO_INCREMENT PRIMARY KEY ,
  nation_name VARCHAR(30) UNIQUE NOT NULL
);

CREATE TABLE student(
  student_id INT AUTO_INCREMENT PRIMARY KEY ,
  student_number VARCHAR(20) UNIQUE NOT NULL ,
  birthday DATE,
  sex VARCHAR(2),
  id_card VARCHAR(20) UNIQUE ,
  family_residence VARCHAR(600),
  political_landscape_id INT,
  nation_id INT,
  dormitory_number VARCHAR(15),
  parent_name VARCHAR(10),
  parent_contact_phone VARCHAR(15),
  place_origin VARCHAR(500),
  organize_id INT NOT NULL ,
  username VARCHAR(64) NOT NULL ,
  FOREIGN KEY (organize_id) REFERENCES organize(organize_id),
  FOREIGN KEY (username) REFERENCES users(username)
);

CREATE TABLE staff(
  staff_id INT AUTO_INCREMENT PRIMARY KEY ,
  staff_number VARCHAR(20) UNIQUE NOT NULL ,
  birthday DATE,
  sex VARCHAR(2),
  id_card VARCHAR(20) UNIQUE ,
  family_residence VARCHAR(600),
  political_landscape_id INT,
  nation_id INT,
  post VARCHAR(500),
  department_id INT NOT NULL ,
  username VARCHAR(64) NOT NULL ,
  FOREIGN KEY (department_id) REFERENCES department(department_id),
  FOREIGN KEY (username) REFERENCES users(username)
);

CREATE TABLE system_log(
  system_log_id VARCHAR(64) PRIMARY KEY ,
  behavior VARCHAR(200) NOT NULL ,
  operating_time DATETIME NOT NULL ,
  username VARCHAR(64) NOT NULL ,
  ip_address VARCHAR(50) NOT NULL
);

CREATE TABLE system_mailbox(
  system_mailbox_id VARCHAR(64) PRIMARY KEY ,
  send_time DATETIME,
  accept_mail VARCHAR(200)
);

CREATE TABLE system_sms(
  system_sms_id VARCHAR(64) PRIMARY KEY ,
  send_time DATETIME,
  accept_phone VARCHAR(15)
);

CREATE TABLE files(
  file_id VARCHAR(64) PRIMARY KEY,
  size LONG,
  original_file_name VARCHAR(300),
  new_name VARCHAR(300),
  relative_path VARCHAR(800),
  ext VARCHAR(20)
);

CREATE TABLE internship_type(
  internship_type_id INT AUTO_INCREMENT PRIMARY KEY ,
  internship_type_name VARCHAR(100) NOT NULL
);

CREATE TABLE internship_release(
  internship_release_id VARCHAR(64) PRIMARY KEY ,
  internship_title VARCHAR(100) NOT NULL ,
  release_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  username VARCHAR(64) NOT NULL ,
  allow_grade VARCHAR(5) NOT NULL ,
  teacher_distribution_start_time DATETIME NOT NULL ,
  teacher_distribution_end_time DATETIME NOT NULL ,
  start_time DATETIME NOT NULL ,
  end_time DATETIME NOT NULL ,
  internship_release_is_del BOOLEAN NOT NULL ,
  department_id INT NOT NULL ,
  internship_type_id INT NOT NULL ,
  FOREIGN KEY (username) REFERENCES users(username),
  FOREIGN KEY (department_id) REFERENCES department(department_id),
  FOREIGN KEY (internship_type_id) REFERENCES internship_type(internship_type_id)
);

CREATE TABLE internship_release_science(
  internship_release_id VARCHAR(64) NOT NULL ,
  science_id INT NOT NULL ,
  FOREIGN KEY (internship_release_id) REFERENCES internship_release(internship_release_id),
  FOREIGN KEY (science_id) REFERENCES science(science_id),
  PRIMARY KEY (internship_release_id,science_id)
);

CREATE TABLE internship_file(
  internship_release_id VARCHAR(64) NOT NULL ,
  file_id VARCHAR(100) NOT NULL ,
  FOREIGN KEY (internship_release_id) REFERENCES internship_release(internship_release_id),
  FOREIGN KEY (file_id) REFERENCES files(file_id),
  PRIMARY KEY (internship_release_id,file_id)
);

CREATE TABLE internship_teacher_distribution(
  staff_id INT NOT NULL ,
  student_id INT NOT NULL ,
  internship_release_id VARCHAR(64) NOT NULL ,
  username VARCHAR(200) NOT NULL ,
  FOREIGN KEY (staff_id) REFERENCES staff(staff_id),
  FOREIGN KEY (student_id) REFERENCES student(student_id),
  FOREIGN KEY (internship_release_id) REFERENCES internship_release(internship_release_id),
  FOREIGN KEY (username) REFERENCES users(username),
  PRIMARY KEY (staff_id,student_id,internship_release_id)
);

CREATE TABLE internship_apply(
  internship_apply_id VARCHAR(64) PRIMARY KEY ,
  student_id INT NOT NULL ,
  internship_release_id VARCHAR(64) NOT NULL ,
  internship_apply_state INT NOT NULL DEFAULT 0,
  reason VARCHAR(500) ,
  change_fill_start_time DATETIME,
  change_fill_end_time DATETIME,
  apply_time DATETIME NOT NULL ,
  internship_file_id VARCHAR(64),
  FOREIGN KEY (student_id) REFERENCES student(student_id),
  FOREIGN KEY (internship_release_id) REFERENCES internship_release(internship_release_id),
  UNIQUE (student_id,internship_release_id)
);

CREATE TABLE internship_change_history(
  internship_change_history_id VARCHAR(64) PRIMARY KEY ,
  reason VARCHAR(500) ,
  change_fill_start_time DATETIME,
  change_fill_end_time DATETIME,
  student_id INT NOT NULL ,
  internship_release_id VARCHAR(64) NOT NULL ,
  apply_time DATETIME NOT NULL ,
  state INT NOT NULL ,
  FOREIGN KEY (student_id) REFERENCES student(student_id),
  FOREIGN KEY (internship_release_id) REFERENCES internship_release(internship_release_id)
);

CREATE TABLE internship_change_company_history(
  internship_change_company_history_id VARCHAR(64) PRIMARY KEY ,
  student_id INT NOT NULL ,
  internship_release_id VARCHAR(64) NOT NULL ,
  company_name VARCHAR(200),
  company_address  VARCHAR(500),
  company_contacts VARCHAR(10),
  company_tel VARCHAR(20),
  change_time DATETIME NOT NULL ,
  FOREIGN KEY (student_id) REFERENCES student(student_id),
  FOREIGN KEY (internship_release_id) REFERENCES internship_release(internship_release_id)
);

CREATE TABLE internship_college(
  internship_college_id VARCHAR(64) PRIMARY KEY ,
  student_id INT NOT NULL ,
  internship_release_id VARCHAR(64) NOT NULL ,
  student_name VARCHAR(15) NOT NULL ,
  college_class VARCHAR(50) NOT NULL ,
  student_sex VARCHAR(2) NOT NULL ,
  student_number VARCHAR(20) NOT NULL ,
  phone_number VARCHAR(15) NOT NULL ,
  qq_mailbox VARCHAR(100) NOT NULL ,
  parental_contact VARCHAR(20) NOT NULL ,
  headmaster VARCHAR(10) NOT NULL ,
  headmaster_contact VARCHAR(20) NOT NULL ,
  internship_college_name VARCHAR(200) NOT NULL ,
  internship_college_address VARCHAR(500) NOT NULL ,
  internship_college_contacts VARCHAR(10) NOT NULL ,
  internship_college_tel VARCHAR(20) NOT NULL ,
  school_guidance_teacher VARCHAR(10) NOT NULL ,
  school_guidance_teacher_tel VARCHAR(20) NOT NULL ,
  start_time DATE NOT NULL ,
  end_time DATE NOT NULL ,
  commitment_book BOOLEAN,
  safety_responsibility_book BOOLEAN,
  practice_agreement BOOLEAN,
  internship_application BOOLEAN,
  practice_receiving BOOLEAN,
  security_education_agreement BOOLEAN,
  parental_consent BOOLEAN,
  FOREIGN KEY (student_id) REFERENCES student(student_id),
  FOREIGN KEY (internship_release_id) REFERENCES internship_release(internship_release_id),
  UNIQUE (student_id,internship_release_id)
);

CREATE TABLE internship_company(
  internship_company_id VARCHAR(64) PRIMARY KEY ,
  student_name VARCHAR(15) NOT NULL ,
  college_class VARCHAR(50) NOT NULL ,
  student_sex VARCHAR(2) NOT NULL ,
  student_number VARCHAR(20) NOT NULL ,
  phone_number VARCHAR(15) NOT NULL ,
  qq_mailbox VARCHAR(100) NOT NULL ,
  parental_contact VARCHAR(20) NOT NULL ,
  headmaster VARCHAR(10) NOT NULL ,
  headmaster_contact VARCHAR(20) NOT NULL ,
  internship_company_name VARCHAR(200) NOT NULL ,
  internship_company_address VARCHAR(500) NOT NULL ,
  internship_company_contacts VARCHAR(10) NOT NULL ,
  internship_company_tel VARCHAR(20) NOT NULL ,
  school_guidance_teacher VARCHAR(10) NOT NULL ,
  school_guidance_teacher_tel VARCHAR(20) NOT NULL ,
  start_time DATE NOT NULL ,
  end_time DATE NOT NULL ,
  commitment_book BOOLEAN,
  safety_responsibility_book BOOLEAN,
  practice_agreement BOOLEAN,
  internship_application BOOLEAN,
  practice_receiving BOOLEAN,
  security_education_agreement BOOLEAN,
  parental_consent BOOLEAN,
  student_id INT NOT NULL ,
  internship_release_id VARCHAR(64) NOT NULL ,
  FOREIGN KEY (student_id) REFERENCES student(student_id),
  FOREIGN KEY (internship_release_id) REFERENCES internship_release(internship_release_id),
  UNIQUE (student_id,internship_release_id)
);

CREATE TABLE graduation_practice_college(
  graduation_practice_college_id VARCHAR(64) PRIMARY KEY ,
  student_id INT NOT NULL ,
  internship_release_id VARCHAR(64) NOT NULL,
  FOREIGN KEY (student_id) REFERENCES student(student_id),
  FOREIGN KEY (internship_release_id) REFERENCES internship_release(internship_release_id),
  UNIQUE (student_id,internship_release_id)
);

CREATE TABLE graduation_practice_unify(
  graduation_practice_unify_id VARCHAR(64) PRIMARY KEY ,
  student_id INT NOT NULL ,
  internship_release_id VARCHAR(64) NOT NULL ,
  FOREIGN KEY (student_id) REFERENCES student(student_id),
  FOREIGN KEY (internship_release_id) REFERENCES internship_release(internship_release_id),
  UNIQUE (student_id,internship_release_id)
);

CREATE TABLE graduation_practice_company(
  graduation_practice_company_id VARCHAR(64) PRIMARY KEY ,
  student_name VARCHAR(15) NOT NULL ,
  college_class VARCHAR(50) NOT NULL ,
  student_sex VARCHAR(2) NOT NULL ,
  student_number VARCHAR(20) NOT NULL ,
  phone_number VARCHAR(15) NOT NULL ,
  qq_mailbox VARCHAR(100) NOT NULL ,
  parental_contact VARCHAR(20) NOT NULL ,
  headmaster VARCHAR(10) NOT NULL ,
  headmaster_contact VARCHAR(20) NOT NULL ,
  graduation_practice_company_name VARCHAR(200) NOT NULL ,
  graduation_practice_company_address VARCHAR(500) NOT NULL ,
  graduation_practice_company_contacts VARCHAR(10) NOT NULL ,
  graduation_practice_company_tel VARCHAR(20) NOT NULL ,
  school_guidance_teacher VARCHAR(10) NOT NULL ,
  school_guidance_teacher_tel VARCHAR(20) NOT NULL ,
  start_time DATE NOT NULL ,
  end_time DATE NOT NULL ,
  commitment_book BOOLEAN,
  safety_responsibility_book BOOLEAN,
  practice_agreement BOOLEAN,
  internship_application BOOLEAN,
  practice_receiving BOOLEAN,
  security_education_agreement BOOLEAN,
  parental_consent BOOLEAN,
  student_id INT NOT NULL ,
  internship_release_id VARCHAR(64) NOT NULL ,
  FOREIGN KEY (student_id) REFERENCES student(student_id),
  FOREIGN KEY (internship_release_id) REFERENCES internship_release(internship_release_id),
  UNIQUE (student_id,internship_release_id)
);

CREATE TABLE internship_journal(
  internship_journal_id VARCHAR(64) PRIMARY KEY ,
  student_name VARCHAR(10) NOT NULL ,
  student_number VARCHAR(20) NOT NULL ,
  organize VARCHAR(10) NOT NULL ,
  school_guidance_teacher VARCHAR(10) NOT NULL ,
  graduation_practice_company_name VARCHAR(200) NOT NULL ,
  internship_journal_content TEXT NOT NULL ,
  internship_journal_date DATE NOT NULL ,
  create_date DATETIME NOT NULL ,
  student_id INT NOT NULL ,
  internship_release_id VARCHAR(64) NOT NULL ,
  internship_journal_word VARCHAR(500) NOT NULL ,
  FOREIGN KEY (student_id) REFERENCES student(student_id),
  FOREIGN KEY (internship_release_id) REFERENCES internship_release(internship_release_id)
);

CREATE TABLE internship_regulate(
  internship_regulate_id VARCHAR(64) PRIMARY KEY ,
  student_name VARCHAR(10) NOT NULL ,
  student_number VARCHAR(20) NOT NULL ,
  student_tel VARCHAR(15) NOT NULL ,
  internship_content VARCHAR(200) NOT NULL ,
  internship_progress VARCHAR(200) NOT NULL ,
  report_way VARCHAR(20) NOT NULL ,
  report_date DATE NOT NULL ,
  school_guidance_teacher VARCHAR(10) NOT NULL ,
  tliy VARCHAR(200) ,
  create_date DATETIME NOT NULL ,
  student_id INT NOT NULL ,
  internship_release_id VARCHAR(64) NOT NULL ,
  staff_id INT NOT NULL ,
  FOREIGN KEY (student_id) REFERENCES student(student_id),
  FOREIGN KEY (internship_release_id) REFERENCES internship_release(internship_release_id),
  FOREIGN KEY (staff_id) REFERENCES staff(staff_id)
);

CREATE TABLE system_alert_type(
  system_alert_type_id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(10) NOT NULL ,
  icon VARCHAR(30) NOT NULL
);

CREATE TABLE system_alert(
  system_alert_id VARCHAR(64) PRIMARY KEY ,
  alert_content VARCHAR(10) NOT NULL ,
  alert_date DATETIME NOT NULL ,
  link_id VARCHAR(64),
  is_see BOOLEAN,
  username VARCHAR(64) NOT NULL ,
  system_alert_type_id INT NOT NULL,
  FOREIGN KEY (system_alert_type_id) REFERENCES system_alert_type(system_alert_type_id)
);

CREATE TABLE system_message(
  system_message_id VARCHAR(64) PRIMARY KEY ,
  message_title VARCHAR(50) NOT NULL ,
  message_content VARCHAR(800) NOT NULL ,
  message_date DATETIME NOT NULL ,
  send_users VARCHAR(64) NOT NULL ,
  accept_users VARCHAR(64) NOT NULL ,
  is_see BOOLEAN
);