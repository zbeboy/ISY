CREATE TABLE users_type(
  users_type_id INT AUTO_INCREMENT PRIMARY KEY ,
  users_type_name VARCHAR(50) NOT NULL
);

CREATE TABLE users(
  username VARCHAR(200) PRIMARY KEY,
  password VARCHAR(800) NOT NULL,
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
  role_en_name VARCHAR(64) UNIQUE NOT NULL
);

CREATE TABLE authorities(
  username VARCHAR(200) NOT NULL ,
  authority VARCHAR(50) NOT NULL ,
  FOREIGN KEY (username) REFERENCES users(username)
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
  FOREIGN KEY (application_id) REFERENCES application(application_id)
);

CREATE TABLE persistent_logins(
  username VARCHAR(200) NOT NULL ,
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
  FOREIGN KEY (college_id) REFERENCES college(college_id)
);

CREATE TABLE college_role(
  role_id INT NOT NULL ,
  college_id INT NOT NULL ,
  FOREIGN KEY (role_id) REFERENCES role(role_id),
  FOREIGN KEY (college_id) REFERENCES college(college_id)
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
  username VARCHAR(200) NOT NULL ,
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
  username VARCHAR(200) NOT NULL ,
  FOREIGN KEY (department_id) REFERENCES department(department_id),
  FOREIGN KEY (username) REFERENCES users(username)
);

CREATE TABLE system_log(
  system_log_id VARCHAR(100) PRIMARY KEY ,
  behavior VARCHAR(200) NOT NULL ,
  operating_time DATETIME NOT NULL ,
  username VARCHAR(200) NOT NULL ,
  ip_address VARCHAR(50) NOT NULL
);

CREATE TABLE system_mailbox(
  system_mailbox_id VARCHAR(100) PRIMARY KEY ,
  send_time DATETIME,
  accept_mail VARCHAR(200)
);

CREATE TABLE system_sms(
  system_sms_id VARCHAR(100) PRIMARY KEY ,
  send_time DATETIME,
  accept_phone VARCHAR(15)
);

CREATE TABLE files(
  file_id VARCHAR(100) PRIMARY KEY,
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
  internship_release_id VARCHAR(100) PRIMARY KEY ,
  internship_title VARCHAR(100) NOT NULL ,
  release_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  username VARCHAR(200) NOT NULL ,
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
  internship_release_id VARCHAR(100) NOT NULL ,
  science_id INT NOT NULL ,
  FOREIGN KEY (internship_release_id) REFERENCES internship_release(internship_release_id),
  FOREIGN KEY (science_id) REFERENCES science(science_id)
);

CREATE TABLE internship_file(
  internship_release_id VARCHAR(100) NOT NULL ,
  file_id VARCHAR(100) NOT NULL ,
  FOREIGN KEY (internship_release_id) REFERENCES internship_release(internship_release_id),
  FOREIGN KEY (file_id) REFERENCES files(file_id)
);

CREATE TABLE internship_teacher_distribution(
  staff_id INT NOT NULL ,
  student_id INT NOT NULL ,
  internship_release_id VARCHAR(100) NOT NULL ,
  username VARCHAR(200) NOT NULL ,
  FOREIGN KEY (staff_id) REFERENCES staff(staff_id),
  FOREIGN KEY (student_id) REFERENCES student(student_id),
  FOREIGN KEY (internship_release_id) REFERENCES internship_release(internship_release_id),
  FOREIGN KEY (username) REFERENCES users(username)
);

CREATE TABLE internship_apply(
  internship_apply_id VARCHAR(100) PRIMARY KEY ,
  student_id INT NOT NULL ,
  internship_release_id VARCHAR(100) NOT NULL ,
  internship_apply_state INT NOT NULL DEFAULT 0,
  reason VARCHAR(500) ,
  change_fill_start_time DATETIME,
  change_fill_end_time DATETIME,
  apply_time DATETIME NOT NULL ,
  FOREIGN KEY (student_id) REFERENCES student(student_id),
  FOREIGN KEY (internship_release_id) REFERENCES internship_release(internship_release_id)
);

CREATE TABLE internship_change_history(
  internship_change_history_id VARCHAR(100) PRIMARY KEY ,
  reason VARCHAR(500) NOT NULL ,
  change_fill_start_time DATETIME,
  change_fill_end_time DATETIME,
  student_id INT NOT NULL ,
  internship_release_id VARCHAR(100) NOT NULL ,
  apply_time DATETIME NOT NULL ,
  state INT NOT NULL ,
  FOREIGN KEY (student_id) REFERENCES student(student_id),
  FOREIGN KEY (internship_release_id) REFERENCES internship_release(internship_release_id)
);

CREATE TABLE internship_change_company_history(
  internship_change_company_history_id VARCHAR(100) PRIMARY KEY ,
  student_id INT NOT NULL ,
  internship_release_id VARCHAR(100) NOT NULL ,
  company_name VARCHAR(200),
  company_address  VARCHAR(500),
  company_contacts VARCHAR(10),
  company_tel VARCHAR(20),
  change_time DATETIME NOT NULL ,
  FOREIGN KEY (student_id) REFERENCES student(student_id),
  FOREIGN KEY (internship_release_id) REFERENCES internship_release(internship_release_id)
);

CREATE TABLE internship_college(
  internship_college_id VARCHAR(100) PRIMARY KEY ,
  student_id INT NOT NULL ,
  internship_release_id VARCHAR(100) NOT NULL ,
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
  FOREIGN KEY (internship_release_id) REFERENCES internship_release(internship_release_id)
);

CREATE TABLE internship_company(
  internship_company_id VARCHAR(100) PRIMARY KEY ,
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
  internship_release_id VARCHAR(100) NOT NULL ,
  FOREIGN KEY (student_id) REFERENCES student(student_id),
  FOREIGN KEY (internship_release_id) REFERENCES internship_release(internship_release_id)
);

CREATE TABLE graduation_practice_college(
  graduation_practice_college_id VARCHAR(100) PRIMARY KEY ,
  student_id INT NOT NULL ,
  internship_release_id VARCHAR(100) NOT NULL,
  FOREIGN KEY (student_id) REFERENCES student(student_id),
  FOREIGN KEY (internship_release_id) REFERENCES internship_release(internship_release_id)
);

CREATE TABLE graduation_practice_unify(
  graduation_practice_unify_id VARCHAR(100) PRIMARY KEY ,
  student_id INT NOT NULL ,
  internship_release_id VARCHAR(100) NOT NULL ,
  FOREIGN KEY (student_id) REFERENCES student(student_id),
  FOREIGN KEY (internship_release_id) REFERENCES internship_release(internship_release_id)
);

CREATE TABLE graduation_practice_company(
  graduation_practice_company_id VARCHAR(100) PRIMARY KEY ,
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
  internship_release_id VARCHAR(100) NOT NULL ,
  FOREIGN KEY (student_id) REFERENCES student(student_id),
  FOREIGN KEY (internship_release_id) REFERENCES internship_release(internship_release_id)
);

CREATE TABLE internship_journal(
  internship_journal_id VARCHAR(100) PRIMARY KEY ,
  student_name VARCHAR(10) NOT NULL ,
  student_number VARCHAR(20) NOT NULL ,
  organize VARCHAR(10) NOT NULL ,
  school_guidance_teacher VARCHAR(10) NOT NULL ,
  graduation_practice_company_name VARCHAR(200) NOT NULL ,
  internship_journal_content TEXT NOT NULL ,
  internship_journal_date DATE NOT NULL ,
  create_date DATETIME NOT NULL ,
  student_id INT NOT NULL ,
  internship_release_id VARCHAR(100) NOT NULL ,
  internship_journal_word VARCHAR(500) NOT NULL ,
  FOREIGN KEY (student_id) REFERENCES student(student_id),
  FOREIGN KEY (internship_release_id) REFERENCES internship_release(internship_release_id)
);

CREATE TABLE internship_regulate(
  internship_regulate_id VARCHAR(100) PRIMARY KEY ,
  student_name VARCHAR(10) NOT NULL ,
  student_number VARCHAR(20) NOT NULL ,
  student_tel VARCHAR(15) NOT NULL ,
  internship_content VARCHAR(200) NOT NULL ,
  internship_progress VARCHAR(200) NOT NULL ,
  report_way VARCHAR(20) NOT NULL ,
  report_date DATE NOT NULL ,
  school_guidance_teacher VARCHAR(10) NOT NULL ,
  tliy VARCHAR(200) NOT NULL ,
  create_date DATETIME NOT NULL ,
  student_id INT NOT NULL ,
  internship_release_id VARCHAR(100) NOT NULL ,
  staff_id INT NOT NULL ,
  FOREIGN KEY (student_id) REFERENCES student(student_id),
  FOREIGN KEY (internship_release_id) REFERENCES internship_release(internship_release_id),
  FOREIGN KEY (staff_id) REFERENCES staff(staff_id)
);

INSERT INTO users_type(users_type_name) VALUES ('学生');
INSERT INTO users_type(users_type_name) VALUES ('教职工');
INSERT INTO users_type(users_type_name) VALUES ('系统');

INSERT INTO users(username, password, enabled, users_type_id, real_name, mobile, avatar,
                   verify_mailbox, mailbox_verify_code,
                  password_reset_key, mailbox_verify_valid,
                  password_reset_key_valid, lang_key, join_date)
VALUES ('863052317@qq.com','$2a$10$HKXHRhnhlC1aZQ4hukD0S.zYep/T5A7FULBo7S2UrJsqQCThUxdo2',1,3,'赵银','13987614709','/images/avatar.jpg',1,'','',NULL ,NULL ,'zh-CN','2016-08-18');

INSERT INTO role(role_name, role_en_name) VALUES ('系统','ROLE_SYSTEM');
INSERT INTO role(role_name, role_en_name) VALUES ('管理员','ROLE_ADMIN');

INSERT INTO authorities(username, authority) VALUES ('863052317@qq.com','ROLE_SYSTEM');

INSERT INTO application(application_name, application_sort,
                        application_pid, application_url,
                        application_code, application_en_name, icon)
VALUES ('实习',200,0,'#','internship','internship','fa-coffee');
INSERT INTO application(application_name, application_sort,
                        application_pid, application_url,
                        application_code, application_en_name, icon)
VALUES ('数据',800,0,'#','datas','datas','fa-hdd-o');
INSERT INTO application(application_name, application_sort,
                        application_pid, application_url,
                        application_code, application_en_name, icon)
VALUES ('平台',900,0,'#','platform','platform','fa-list');
INSERT INTO application(application_name, application_sort,
                        application_pid, application_url,
                        application_code, application_en_name, icon)
VALUES ('系统',1000,0,'#','system','system','fa-sitemap');

INSERT INTO application(application_name, application_sort,
                        application_pid, application_url,
                        application_code, application_en_name, icon,application_data_url_start_with)
VALUES ('实习发布',201,1,'/web/menu/internship/release','internship_release','internship_release','','/web/internship/release');
INSERT INTO application(application_name, application_sort,
                        application_pid, application_url,
                        application_code, application_en_name, icon,application_data_url_start_with)
VALUES ('实习教师分配',202,1,'/web/menu/internship/teacher_distribution','internship_teacher_distribution','internship_teacher_distribution','','/web/internship/teacher_distribution');
INSERT INTO application(application_name, application_sort,
                        application_pid, application_url,
                        application_code, application_en_name, icon,application_data_url_start_with)
VALUES ('实习申请',203,1,'/web/menu/internship/apply','internship_apply','internship_apply','','/web/internship/apply');
INSERT INTO application(application_name, application_sort,
                        application_pid, application_url,
                        application_code, application_en_name, icon,application_data_url_start_with)
VALUES ('实习审核',204,1,'/web/menu/internship/review','internship_review','internship_review','','/web/internship/review');
INSERT INTO application(application_name, application_sort,
                        application_pid, application_url,
                        application_code, application_en_name, icon,application_data_url_start_with)
VALUES ('实习统计',205,1,'/web/menu/internship/statistical','internship_statistical','internship_statistical','','/web/internship/statistical');
INSERT INTO application(application_name, application_sort,
                        application_pid, application_url,
                        application_code, application_en_name, icon,application_data_url_start_with)
VALUES ('实习日志',206,1,'/web/menu/internship/journal','internship_journal','internship_journal','','/web/internship/journal');
INSERT INTO application(application_name, application_sort,
                        application_pid, application_url,
                        application_code, application_en_name, icon,application_data_url_start_with)
VALUES ('实习监管',207,1,'/web/menu/internship/regulate','internship_regulate','internship_regulate','','/web/internship/regulate');

INSERT INTO application(application_name, application_sort,
                        application_pid, application_url,
                        application_code, application_en_name, icon,application_data_url_start_with)
VALUES ('学校数据',801,2,'/web/menu/data/school','data_school','data_school','','/web/data/school');
INSERT INTO application(application_name, application_sort,
                        application_pid, application_url,
                        application_code, application_en_name, icon,application_data_url_start_with)
VALUES ('院数据',802,2,'/web/menu/data/college','data_college','data_college','','/web/data/college');
INSERT INTO application(application_name, application_sort,
                        application_pid, application_url,
                        application_code, application_en_name, icon,application_data_url_start_with)
VALUES ('系数据',803,2,'/web/menu/data/department','data_department','data_department','','/web/data/department');
INSERT INTO application(application_name, application_sort,
                        application_pid, application_url,
                        application_code, application_en_name, icon,application_data_url_start_with)
VALUES ('专业数据',804,2,'/web/menu/data/science','data_science','data_science','','/web/data/science');
INSERT INTO application(application_name, application_sort,
                        application_pid, application_url,
                        application_code, application_en_name, icon,application_data_url_start_with)
VALUES ('班级数据',805,2,'/web/menu/data/organize','data_organize','data_organize','','/web/data/organize');
INSERT INTO application(application_name, application_sort,
                        application_pid, application_url,
                        application_code, application_en_name, icon,application_data_url_start_with)
VALUES ('教职工数据',806,2,'/web/menu/data/staff','data_staff','data_staff','','/web/data/staff');
INSERT INTO application(application_name, application_sort,
                        application_pid, application_url,
                        application_code, application_en_name, icon,application_data_url_start_with)
VALUES ('学生数据',807,2,'/web/menu/data/student','data_student','data_student','','/web/data/student');
INSERT INTO application(application_name, application_sort,
                        application_pid, application_url,
                        application_code, application_en_name, icon,application_data_url_start_with)
VALUES ('民族数据',808,2,'/web/menu/data/nation','data_nation','data_nation','','/web/data/nation');
INSERT INTO application(application_name, application_sort,
                        application_pid, application_url,
                        application_code, application_en_name, icon,application_data_url_start_with)
VALUES ('政治面貌数据',809,2,'/web/menu/data/politics','data_politics','data_politics','','/web/data/politics');

INSERT INTO application(application_name, application_sort,
                        application_pid, application_url,
                        application_code, application_en_name, icon,application_data_url_start_with)
VALUES ('平台用户',901,3,'/web/menu/platform/users','platform_users','platform_users','','/web/platform/users');
INSERT INTO application(application_name, application_sort,
                        application_pid, application_url,
                        application_code, application_en_name, icon,application_data_url_start_with)
VALUES ('平台角色',902,3,'/web/menu/platform/role','platform_role','platform_role','','/web/platform/role');

INSERT INTO application(application_name, application_sort,
                        application_pid, application_url,
                        application_code, application_en_name, icon,application_data_url_start_with)
VALUES ('系统应用',1001,4,'/web/menu/system/application','system_application','system_application','','/web/system/application');
INSERT INTO application(application_name, application_sort,
                        application_pid, application_url,
                        application_code, application_en_name, icon,application_data_url_start_with)
VALUES ('系统日志',1002,4,'/web/menu/system/log','system_log','system_log','','/web/system/log');
INSERT INTO application(application_name, application_sort,
                        application_pid, application_url,
                        application_code, application_en_name, icon,application_data_url_start_with)
VALUES ('系统短信',1003,4,'/web/menu/system/sms','system_sms','system_sms','','/web/system/sms');
INSERT INTO application(application_name, application_sort,
                        application_pid, application_url,
                        application_code, application_en_name, icon,application_data_url_start_with)
VALUES ('系统邮件',1004,4,'/web/menu/system/mailbox','system_mailbox','system_mailbox','','/web/system/mailbox');

INSERT INTO internship_type(internship_type_name) VALUES ('顶岗实习(留学院)');
INSERT INTO internship_type(internship_type_name) VALUES ('校外自主实习(去单位)');
INSERT INTO internship_type(internship_type_name) VALUES ('毕业实习(校内)');
INSERT INTO internship_type(internship_type_name) VALUES ('毕业实习(学校统一组织校外实习)');
INSERT INTO internship_type(internship_type_name) VALUES ('毕业实习(学生校外自主实习)');

INSERT INTO role_application(role_id, application_id) VALUES (1,1);
INSERT INTO role_application(role_id, application_id) VALUES (1,2);
INSERT INTO role_application(role_id, application_id) VALUES (1,3);
INSERT INTO role_application(role_id, application_id) VALUES (1,4);
INSERT INTO role_application(role_id, application_id) VALUES (1,5);
INSERT INTO role_application(role_id, application_id) VALUES (1,6);
INSERT INTO role_application(role_id, application_id) VALUES (1,7);
INSERT INTO role_application(role_id, application_id) VALUES (1,8);
INSERT INTO role_application(role_id, application_id) VALUES (1,9);
INSERT INTO role_application(role_id, application_id) VALUES (1,10);
INSERT INTO role_application(role_id, application_id) VALUES (1,11);
INSERT INTO role_application(role_id, application_id) VALUES (1,12);
INSERT INTO role_application(role_id, application_id) VALUES (1,13);
INSERT INTO role_application(role_id, application_id) VALUES (1,14);
INSERT INTO role_application(role_id, application_id) VALUES (1,15);
INSERT INTO role_application(role_id, application_id) VALUES (1,16);
INSERT INTO role_application(role_id, application_id) VALUES (1,17);
INSERT INTO role_application(role_id, application_id) VALUES (1,18);
INSERT INTO role_application(role_id, application_id) VALUES (1,19);
INSERT INTO role_application(role_id, application_id) VALUES (1,20);
INSERT INTO role_application(role_id, application_id) VALUES (1,21);
INSERT INTO role_application(role_id, application_id) VALUES (1,22);
INSERT INTO role_application(role_id, application_id) VALUES (1,23);
INSERT INTO role_application(role_id, application_id) VALUES (1,24);
INSERT INTO role_application(role_id, application_id) VALUES (1,25);
INSERT INTO role_application(role_id, application_id) VALUES (1,26);

INSERT INTO role_application(role_id, application_id) VALUES (2,1);
INSERT INTO role_application(role_id, application_id) VALUES (2,2);
INSERT INTO role_application(role_id, application_id) VALUES (2,3);
INSERT INTO role_application(role_id, application_id) VALUES (2,5);
INSERT INTO role_application(role_id, application_id) VALUES (2,6);
INSERT INTO role_application(role_id, application_id) VALUES (2,7);
INSERT INTO role_application(role_id, application_id) VALUES (2,8);
INSERT INTO role_application(role_id, application_id) VALUES (2,9);
INSERT INTO role_application(role_id, application_id) VALUES (2,10);
INSERT INTO role_application(role_id, application_id) VALUES (2,11);
INSERT INTO role_application(role_id, application_id) VALUES (2,14);
INSERT INTO role_application(role_id, application_id) VALUES (2,15);
INSERT INTO role_application(role_id, application_id) VALUES (2,16);
INSERT INTO role_application(role_id, application_id) VALUES (2,17);
INSERT INTO role_application(role_id, application_id) VALUES (2,18);
INSERT INTO role_application(role_id, application_id) VALUES (2,19);
INSERT INTO role_application(role_id, application_id) VALUES (2,20);
INSERT INTO role_application(role_id, application_id) VALUES (2,22);

INSERT INTO political_landscape(political_landscape_name) VALUES ('群众');
INSERT INTO political_landscape(political_landscape_name) VALUES ('共青团员');
INSERT INTO political_landscape(political_landscape_name) VALUES ('中共预备党员');
INSERT INTO political_landscape(political_landscape_name) VALUES ('中共党员');
INSERT INTO political_landscape(political_landscape_name) VALUES ('民革党员');
INSERT INTO political_landscape(political_landscape_name) VALUES ('民盟盟员');
INSERT INTO political_landscape(political_landscape_name) VALUES ('民建会员');
INSERT INTO political_landscape(political_landscape_name) VALUES ('民进会员');
INSERT INTO political_landscape(political_landscape_name) VALUES ('农工党党员');
INSERT INTO political_landscape(political_landscape_name) VALUES ('致公党党员');
INSERT INTO political_landscape(political_landscape_name) VALUES ('九三学社社员');
INSERT INTO political_landscape(political_landscape_name) VALUES ('台盟盟员');
INSERT INTO political_landscape(political_landscape_name) VALUES ('无党派民主人士');

INSERT INTO nation(nation_name) VALUES('汉族');
INSERT INTO nation(nation_name) VALUES('蒙古族');
INSERT INTO nation(nation_name) VALUES('回族');
INSERT INTO nation(nation_name) VALUES('藏族');
INSERT INTO nation(nation_name) VALUES('维吾尔族');
INSERT INTO nation(nation_name) VALUES('苗族');
INSERT INTO nation(nation_name) VALUES('彝族');
INSERT INTO nation(nation_name) VALUES('壮族');
INSERT INTO nation(nation_name) VALUES('布依族');
INSERT INTO nation(nation_name) VALUES('朝鲜族');
INSERT INTO nation(nation_name) VALUES('满族');
INSERT INTO nation(nation_name) VALUES('侗族');
INSERT INTO nation(nation_name) VALUES('瑶族');
INSERT INTO nation(nation_name) VALUES('白族');
INSERT INTO nation(nation_name) VALUES('土家族');
INSERT INTO nation(nation_name) VALUES('哈尼族');
INSERT INTO nation(nation_name) VALUES('哈萨克族');
INSERT INTO nation(nation_name) VALUES('傣族');
INSERT INTO nation(nation_name) VALUES('黎族');
INSERT INTO nation(nation_name) VALUES('傈僳族');
INSERT INTO nation(nation_name) VALUES('佤族');
INSERT INTO nation(nation_name) VALUES('畲族');
INSERT INTO nation(nation_name) VALUES('高山族');
INSERT INTO nation(nation_name) VALUES('拉祜族');
INSERT INTO nation(nation_name) VALUES('水族');
INSERT INTO nation(nation_name) VALUES('东乡族');
INSERT INTO nation(nation_name) VALUES('纳西族');
INSERT INTO nation(nation_name) VALUES('景颇族');
INSERT INTO nation(nation_name) VALUES('柯尔克孜族');
INSERT INTO nation(nation_name) VALUES('土族');
INSERT INTO nation(nation_name) VALUES('达斡尔族');
INSERT INTO nation(nation_name) VALUES('仫佬族');
INSERT INTO nation(nation_name) VALUES('羌族');
INSERT INTO nation(nation_name) VALUES('布朗族');
INSERT INTO nation(nation_name) VALUES('撒拉族');
INSERT INTO nation(nation_name) VALUES('毛难族');
INSERT INTO nation(nation_name) VALUES('仡佬族');
INSERT INTO nation(nation_name) VALUES('锡伯族');
INSERT INTO nation(nation_name) VALUES('阿昌族');
INSERT INTO nation(nation_name) VALUES('普米族');
INSERT INTO nation(nation_name) VALUES('塔吉克族');
INSERT INTO nation(nation_name) VALUES('怒族');
INSERT INTO nation(nation_name) VALUES('乌孜别克族');
INSERT INTO nation(nation_name) VALUES('俄罗斯族');
INSERT INTO nation(nation_name) VALUES('鄂温克族');
INSERT INTO nation(nation_name) VALUES('崩龙族');
INSERT INTO nation(nation_name) VALUES('保安族');
INSERT INTO nation(nation_name) VALUES('裕固族');
INSERT INTO nation(nation_name) VALUES('京族');
INSERT INTO nation(nation_name) VALUES('塔塔尔族');
INSERT INTO nation(nation_name) VALUES('独龙族');
INSERT INTO nation(nation_name) VALUES('鄂伦春族');
INSERT INTO nation(nation_name) VALUES('赫哲族');
INSERT INTO nation(nation_name) VALUES('门巴族');
INSERT INTO nation(nation_name) VALUES('珞巴族');
INSERT INTO nation(nation_name) VALUES('基诺族');