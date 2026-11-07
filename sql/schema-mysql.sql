-- 云枢 OA · MySQL 8 建表脚本（生产部署用；结构与 classpath:db/schema.sql 完全一致）
-- 执行：mysql -uroot -p -e "CREATE DATABASE yunshu_oa DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
--       mysql -uroot -p yunshu_oa < sql/schema-mysql.sql
--       mysql -uroot -p yunshu_oa < sql/data.sql
SET NAMES utf8mb4;
-- 云枢 OA · 数据库结构（H2 MySQL 兼容模式 / MySQL 8 通用）
-- 生产环境使用同仓库 sql/schema-mysql.sql（含字符集与注释）
CREATE TABLE IF NOT EXISTS sys_dept (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, parent_id BIGINT DEFAULT 0, dept_name VARCHAR(64) NOT NULL,
  dept_code VARCHAR(32), leader_id BIGINT, sort_no INT DEFAULT 0, status TINYINT DEFAULT 1,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS sys_post (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, post_name VARCHAR(64) NOT NULL, post_code VARCHAR(32),
  sort_no INT DEFAULT 0, status TINYINT DEFAULT 1
);
CREATE TABLE IF NOT EXISTS sys_user (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(64) NOT NULL UNIQUE, password VARCHAR(128) NOT NULL,
  real_name VARCHAR(64) NOT NULL, avatar VARCHAR(255), dept_id BIGINT, post_id BIGINT,
  phone VARCHAR(32), email VARCHAR(128), status TINYINT DEFAULT 1, hire_date VARCHAR(20),
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS sys_role (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, role_code VARCHAR(64) NOT NULL UNIQUE, role_name VARCHAR(64) NOT NULL,
  data_scope VARCHAR(20) DEFAULT 'DEPT', remark VARCHAR(255), create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS sys_menu (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, parent_id BIGINT DEFAULT 0, menu_name VARCHAR(64) NOT NULL,
  path VARCHAR(128), component VARCHAR(128), perm_code VARCHAR(96), icon VARCHAR(64),
  menu_type VARCHAR(10) DEFAULT 'C', sort_no INT DEFAULT 0, status TINYINT DEFAULT 1
);
CREATE TABLE IF NOT EXISTS sys_user_role (user_id BIGINT NOT NULL, role_id BIGINT NOT NULL);
CREATE TABLE IF NOT EXISTS sys_role_menu (role_id BIGINT NOT NULL, menu_id BIGINT NOT NULL);

CREATE TABLE IF NOT EXISTS att_checkin (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, user_id BIGINT NOT NULL, check_date VARCHAR(20) NOT NULL,
  check_in VARCHAR(8), check_out VARCHAR(8), in_status VARCHAR(20), out_status VARCHAR(20),
  work_hours DECIMAL(5,2), location VARCHAR(128), create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS att_leave_balance (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, user_id BIGINT NOT NULL, year_no VARCHAR(8),
  annual_total DECIMAL(5,1) DEFAULT 10, annual_used DECIMAL(5,1) DEFAULT 0,
  comp_total DECIMAL(5,1) DEFAULT 0, comp_used DECIMAL(5,1) DEFAULT 0
);

CREATE TABLE IF NOT EXISTS wf_process_def (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, proc_key VARCHAR(64) NOT NULL UNIQUE, proc_name VARCHAR(96) NOT NULL,
  biz_type VARCHAR(32) NOT NULL, nodes_json VARCHAR(2000) NOT NULL, status TINYINT DEFAULT 1,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS wf_instance (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, proc_key VARCHAR(64), biz_type VARCHAR(32), biz_id BIGINT,
  title VARCHAR(160) NOT NULL, applicant_id BIGINT NOT NULL, applicant_name VARCHAR(64),
  current_node VARCHAR(64), current_node_name VARCHAR(64), status VARCHAR(20) DEFAULT 'RUNNING',
  form_json VARCHAR(2000), create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, finish_time TIMESTAMP
);
CREATE TABLE IF NOT EXISTS wf_task (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, instance_id BIGINT NOT NULL, node_key VARCHAR(64),
  node_name VARCHAR(64), assignee_id BIGINT, assignee_name VARCHAR(64), action VARCHAR(20),
  comment VARCHAR(500), status VARCHAR(20) DEFAULT 'PENDING',
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, finish_time TIMESTAMP
);

CREATE TABLE IF NOT EXISTS oa_notice (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, title VARCHAR(160) NOT NULL, content VARCHAR(3000),
  category VARCHAR(32), publisher_id BIGINT, publisher_name VARCHAR(64), pinned TINYINT DEFAULT 0,
  view_count INT DEFAULT 0, publish_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS oa_notice_read (notice_id BIGINT NOT NULL, user_id BIGINT NOT NULL, read_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP);

CREATE TABLE IF NOT EXISTS doc_folder (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, parent_id BIGINT DEFAULT 0, name VARCHAR(96) NOT NULL,
  owner_id BIGINT, create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS doc_file (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, folder_id BIGINT DEFAULT 0, name VARCHAR(180) NOT NULL,
  ext VARCHAR(20), file_size BIGINT DEFAULT 0, version INT DEFAULT 1, storage_path VARCHAR(255),
  uploader_id BIGINT, uploader_name VARCHAR(64), download_count INT DEFAULT 0,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS prj_project (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, project_code VARCHAR(32), name VARCHAR(128) NOT NULL,
  owner_id BIGINT, owner_name VARCHAR(64), status VARCHAR(20) DEFAULT 'ACTIVE',
  start_date VARCHAR(20), end_date VARCHAR(20), description VARCHAR(1000),
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS prj_task (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, project_id BIGINT NOT NULL, title VARCHAR(160) NOT NULL,
  description VARCHAR(1000), assignee_id BIGINT, assignee_name VARCHAR(64), priority VARCHAR(20),
  status VARCHAR(20) DEFAULT 'TODO', progress INT DEFAULT 0, due_date VARCHAR(20),
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, finish_time TIMESTAMP
);
CREATE TABLE IF NOT EXISTS prj_task_comment (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, task_id BIGINT NOT NULL, user_id BIGINT,
  user_name VARCHAR(64), content VARCHAR(1000), create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS cal_meeting_room (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(96) NOT NULL, capacity INT,
  location VARCHAR(128), equipment VARCHAR(255), status TINYINT DEFAULT 1
);
CREATE TABLE IF NOT EXISTS cal_event (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, title VARCHAR(160) NOT NULL, event_type VARCHAR(32),
  room_id BIGINT, organizer_id BIGINT, organizer_name VARCHAR(64), start_time VARCHAR(30),
  end_time VARCHAR(30), attendees VARCHAR(500), remark VARCHAR(500),
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sys_oper_log (
  id BIGINT AUTO_INCREMENT PRIMARY KEY, user_id BIGINT, user_name VARCHAR(64), module VARCHAR(64),
  operation VARCHAR(128), method VARCHAR(160), request_uri VARCHAR(200), ip VARCHAR(64),
  cost_ms BIGINT, success TINYINT DEFAULT 1, create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_checkin_user_date ON att_checkin(user_id, check_date);
CREATE INDEX idx_wf_instance_applicant ON wf_instance(applicant_id);
CREATE INDEX idx_wf_task_assignee ON wf_task(assignee_id, status);
CREATE INDEX idx_task_project ON prj_task(project_id, status);
