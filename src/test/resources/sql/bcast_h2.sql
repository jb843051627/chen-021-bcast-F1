-- H2(MySQL 兼容) 零报告催办轮次测试建表，仅列实体实际映射的列。
DROP TABLE IF EXISTS t_bcast_zero_channel;
DROP TABLE IF EXISTS t_bcast_zero_round;
DROP TABLE IF EXISTS t_bcast_zero_task;
DROP TABLE IF EXISTS t_bcast_zero_skip;
DROP TABLE IF EXISTS t_bcast_site;

CREATE TABLE t_bcast_site (
  id BIGINT PRIMARY KEY,
  site_no VARCHAR(64),
  site_name VARCHAR(128),
  site_type VARCHAR(32),
  road_name VARCHAR(128),
  status INT,
  del_flag INT DEFAULT 0,
  create_by VARCHAR(64),
  create_time TIMESTAMP,
  update_by VARCHAR(64),
  update_time TIMESTAMP,
  remark VARCHAR(500)
);

CREATE TABLE t_bcast_zero_skip (
  id BIGINT PRIMARY KEY,
  skip_date DATE,
  skip_type INT,
  day_name VARCHAR(128),
  status INT,
  del_flag INT DEFAULT 0,
  create_by VARCHAR(64),
  create_time TIMESTAMP,
  update_by VARCHAR(64),
  update_time TIMESTAMP,
  remark VARCHAR(500)
);

CREATE TABLE t_bcast_zero_task (
  id BIGINT PRIMARY KEY,
  item_no VARCHAR(64),
  site_id BIGINT,
  biz_type INT,
  due_at TIMESTAMP,
  amount DECIMAL(12,2),
  tpl_ver VARCHAR(32),
  contact_phone VARCHAR(32),
  content VARCHAR(255),
  status INT,
  done_time TIMESTAMP,
  last_round_id BIGINT,
  block_stage VARCHAR(64),
  block_reason VARCHAR(255),
  del_flag INT DEFAULT 0,
  create_by VARCHAR(64),
  create_time TIMESTAMP,
  update_by VARCHAR(64),
  update_time TIMESTAMP,
  remark VARCHAR(500)
);

CREATE TABLE t_bcast_zero_round (
  id BIGINT PRIMARY KEY,
  round_no VARCHAR(64),
  run_at TIMESTAMP,
  win_date DATE,
  frozen INT DEFAULT 0,
  picked_count INT DEFAULT 0,
  pending_count INT DEFAULT 0,
  done_count INT DEFAULT 0,
  fail_count INT DEFAULT 0,
  message VARCHAR(255),
  del_flag INT DEFAULT 0,
  create_by VARCHAR(64),
  create_time TIMESTAMP,
  update_by VARCHAR(64),
  update_time TIMESTAMP,
  remark VARCHAR(500)
);

CREATE TABLE t_bcast_zero_channel (
  id BIGINT PRIMARY KEY,
  task_id BIGINT,
  round_id BIGINT,
  channel INT,
  receiver VARCHAR(128),
  sent_at TIMESTAMP,
  tpl_ver VARCHAR(32),
  status INT,
  del_flag INT DEFAULT 0,
  create_by VARCHAR(64),
  create_time TIMESTAMP,
  update_by VARCHAR(64),
  update_time TIMESTAMP,
  remark VARCHAR(500)
);
