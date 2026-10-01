-- bcast 广播电视安全播出与播出事故处置管理 -- schema (chen-021)
-- 列名与基线实体契约（@TableName/@TableField）逐列对齐，改列必须同步实体。
-- 库：chen_021

CREATE TABLE IF NOT EXISTS t_bcast_case_flow (
  id bigint NOT NULL COMMENT '主键',
  biz_no varchar(64) DEFAULT NULL COMMENT '播出事故处置单代号',
  stage int DEFAULT NULL COMMENT '当前步次 0..3（发现核实/处置抢险/恢复确认/上报结案）',
  status int DEFAULT NULL COMMENT '处置会落 0未起 1在办 2已收了口',
  content varchar(255) DEFAULT NULL COMMENT '一段一记',
  last_action varchar(64) DEFAULT NULL COMMENT '最近一次挪步动作',
  del_flag int DEFAULT '0' COMMENT '删除标记 0正常 1删除',
  create_by varchar(64) DEFAULT NULL COMMENT '创建者',
  create_time datetime DEFAULT NULL COMMENT '创建时间',
  update_by varchar(64) DEFAULT NULL COMMENT '更新者',
  update_time datetime DEFAULT NULL COMMENT '更新时间',
  remark varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='播出事故处置单';

CREATE TABLE IF NOT EXISTS t_bcast_off_row (
  id bigint NOT NULL COMMENT '主键',
  batch_no varchar(64) DEFAULT NULL COMMENT '运行情况报送册批号',
  row_no int DEFAULT NULL COMMENT '原报行次',
  item_code varchar(64) DEFAULT NULL COMMENT '被对上的监测台代号',
  qty decimal(12,2) DEFAULT NULL COMMENT '本行停播分钟数',
  hours_num decimal(12,2) DEFAULT NULL COMMENT '本季满播小时数',
  status int DEFAULT NULL COMMENT '行落地情形 0未销 1已收下 2已退回',
  del_flag int DEFAULT '0' COMMENT '删除标记 0正常 1删除',
  create_by varchar(64) DEFAULT NULL COMMENT '创建者',
  create_time datetime DEFAULT NULL COMMENT '创建时间',
  update_by varchar(64) DEFAULT NULL COMMENT '更新者',
  update_time datetime DEFAULT NULL COMMENT '更新时间',
  remark varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='运行情况报送核收行';

CREATE TABLE IF NOT EXISTS t_bcast_pre_line (
  id bigint NOT NULL COMMENT '主键',
  rule_code varchar(64) DEFAULT NULL COMMENT '定档门槛线代号',
  rule_name varchar(128) DEFAULT NULL COMMENT '线名',
  th1_max decimal(12,2) DEFAULT NULL COMMENT '起算数值(分钟)',
  th2_max decimal(12,2) DEFAULT NULL COMMENT '过半数值(分钟)',
  th3_max decimal(12,2) DEFAULT NULL COMMENT '封顶数值(分钟)',
  eff_start datetime DEFAULT NULL COMMENT '立线之日',
  eff_end datetime DEFAULT NULL COMMENT '让位之日(不含)',
  priority int DEFAULT NULL COMMENT '让位顺位(数值大的先说话)',
  status int DEFAULT NULL COMMENT '线的情形 0在场 1已停用',
  del_flag int DEFAULT '0' COMMENT '删除标记 0正常 1删除',
  create_by varchar(64) DEFAULT NULL COMMENT '创建者',
  create_time datetime DEFAULT NULL COMMENT '创建时间',
  update_by varchar(64) DEFAULT NULL COMMENT '更新者',
  update_time datetime DEFAULT NULL COMMENT '更新时间',
  remark varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='事故定档门槛线';

CREATE TABLE IF NOT EXISTS t_bcast_site (
  id bigint NOT NULL COMMENT '主键',
  site_no varchar(64) DEFAULT NULL COMMENT '发射台站代号',
  site_name varchar(128) DEFAULT NULL COMMENT '台站全称',
  site_type varchar(32) DEFAULT NULL COMMENT '台站类别',
  road_name varchar(128) DEFAULT NULL COMMENT '属地那一路(省—市—县)',
  status int DEFAULT NULL COMMENT '底册情形 0在册 1已关停',
  del_flag int DEFAULT '0' COMMENT '删除标记 0正常 1删除',
  create_by varchar(64) DEFAULT NULL COMMENT '创建者',
  create_time datetime DEFAULT NULL COMMENT '创建时间',
  update_by varchar(64) DEFAULT NULL COMMENT '更新者',
  update_time datetime DEFAULT NULL COMMENT '更新时间',
  remark varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='发射台站底册';

CREATE TABLE IF NOT EXISTS t_bcast_stop_bill (
  id bigint NOT NULL COMMENT '主键',
  bill_no varchar(64) DEFAULT NULL COMMENT '停播检修核准单',
  node_no int DEFAULT NULL COMMENT '当前所在档口 0..2（台站/市广电局/省局）',
  sign_mode int DEFAULT NULL COMMENT '同档点齐办法 0一笔即可 1两笔点齐',
  need_count int DEFAULT NULL COMMENT '本档应点名数',
  sign_count int DEFAULT NULL COMMENT '本档已落名数',
  status int DEFAULT NULL COMMENT '核准情形 0在核 1已排定 2已挪回',
  del_flag int DEFAULT '0' COMMENT '删除标记 0正常 1删除',
  create_by varchar(64) DEFAULT NULL COMMENT '创建者',
  create_time datetime DEFAULT NULL COMMENT '创建时间',
  update_by varchar(64) DEFAULT NULL COMMENT '更新者',
  update_time datetime DEFAULT NULL COMMENT '更新时间',
  remark varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='停播检修核准单';

CREATE TABLE IF NOT EXISTS t_bcast_unit_card (
  id bigint NOT NULL COMMENT '主键',
  bill_no varchar(64) DEFAULT NULL COMMENT '台站核点卡卡号',
  node_no int DEFAULT NULL COMMENT '属地层级序',
  site_id int DEFAULT NULL COMMENT '所属发射台站',
  site_no varchar(64) DEFAULT NULL COMMENT '所属台站代号',
  should_count int DEFAULT NULL COMMENT '应核点位数',
  done_count int DEFAULT NULL COMMENT '已核点位数',
  lack_count int DEFAULT NULL COMMENT '还欠点位数',
  content varchar(255) DEFAULT NULL COMMENT '随卡交来的属地要件',
  status int DEFAULT NULL COMMENT '立卡进展 0待核 1已核讫 2压卡',
  del_flag int DEFAULT '0' COMMENT '删除标记 0正常 1删除',
  create_by varchar(64) DEFAULT NULL COMMENT '创建者',
  create_time datetime DEFAULT NULL COMMENT '创建时间',
  update_by varchar(64) DEFAULT NULL COMMENT '更新者',
  update_time datetime DEFAULT NULL COMMENT '更新时间',
  remark varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='台站核点卡立卡单';

CREATE TABLE IF NOT EXISTS t_bcast_zero_task (
  id bigint NOT NULL COMMENT '主键',
  item_no varchar(64) DEFAULT NULL COMMENT '零报告催办条目代号',
  site_id bigint DEFAULT NULL COMMENT '所对发射台站(底册id)',
  biz_type int DEFAULT '0' COMMENT '事由 0零报告催办 1保障期前设备核点',
  due_at datetime DEFAULT NULL COMMENT '应开口那一日收到哪个钟点为止(开口档,写死不许顺延)',
  amount decimal(12,2) DEFAULT '0' COMMENT '最可提前几个工作日开口(事前唯一可调)',
  tpl_ver varchar(32) DEFAULT NULL COMMENT '依照哪一版稿子播发',
  contact_phone varchar(32) DEFAULT NULL COMMENT '值机联系人手机(空着几季没人补=催不动)',
  content varchar(255) DEFAULT NULL COMMENT '事由与所对台站的记要',
  status int DEFAULT NULL COMMENT '条目情形 0候催 1催到 2催不动',
  done_time datetime DEFAULT NULL COMMENT '催到那一刻(status=1时必须落)',
  last_round_id bigint DEFAULT NULL COMMENT '最近一次经办的轮次id',
  block_stage varchar(64) DEFAULT NULL COMMENT '催不动:卡在哪一环',
  block_reason varchar(255) DEFAULT NULL COMMENT '催不动:原委(日后可按原委挑一批)',
  del_flag int DEFAULT '0' COMMENT '删除标记 0正常 1删除',
  create_by varchar(64) DEFAULT NULL COMMENT '创建者',
  create_time datetime DEFAULT NULL COMMENT '创建时间',
  update_by varchar(64) DEFAULT NULL COMMENT '更新者',
  update_time datetime DEFAULT NULL COMMENT '更新时间',
  remark varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='零报告催办到期单';

-- 不办公/封盘日历：国家法定假、省令停办整日不入工作日；保障期封盘仍算工作日，只压住催办
CREATE TABLE IF NOT EXISTS t_bcast_zero_skip (
  id bigint NOT NULL COMMENT '主键',
  skip_date date DEFAULT NULL COMMENT '占住的那一日',
  skip_type int DEFAULT NULL COMMENT '日子种类 0国家法定假 1省令停办 2保障期封盘 3调休补班(周末改按工作日)',
  day_name varchar(128) DEFAULT NULL COMMENT '日名(如国庆、省防台风令、二十大保障期)',
  status int DEFAULT '0' COMMENT '情形 0现行 1已撤',
  del_flag int DEFAULT '0' COMMENT '删除标记 0正常 1删除',
  create_by varchar(64) DEFAULT NULL COMMENT '创建者',
  create_time datetime DEFAULT NULL COMMENT '创建时间',
  update_by varchar(64) DEFAULT NULL COMMENT '更新者',
  update_time datetime DEFAULT NULL COMMENT '更新时间',
  remark varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='零报告催办不办公与封盘日历';

-- 一轮收口：每跑一轮落一行，三个数回库逐条点出来
CREATE TABLE IF NOT EXISTS t_bcast_zero_round (
  id bigint NOT NULL COMMENT '主键',
  round_no varchar(64) DEFAULT NULL COMMENT '轮次代号',
  run_at datetime DEFAULT NULL COMMENT '这一轮开口的钟点',
  win_date date DEFAULT NULL COMMENT '本轮所对那一日',
  frozen int DEFAULT '0' COMMENT '本轮是否撞保障期封盘 0否 1是',
  picked_count int DEFAULT '0' COMMENT '本轮够档捞起条数',
  pending_count int DEFAULT '0' COMMENT '收口:还欠开口(候催未到档/封盘压住)条数',
  done_count int DEFAULT '0' COMMENT '收口:本轮催到条数',
  fail_count int DEFAULT '0' COMMENT '收口:催不动条数',
  message varchar(255) DEFAULT NULL COMMENT '收口留言(整轮无到期写本轮无到期)',
  del_flag int DEFAULT '0' COMMENT '删除标记 0正常 1删除',
  create_by varchar(64) DEFAULT NULL COMMENT '创建者',
  create_time datetime DEFAULT NULL COMMENT '创建时间',
  update_by varchar(64) DEFAULT NULL COMMENT '更新者',
  update_time datetime DEFAULT NULL COMMENT '更新时间',
  remark varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='零报告催办轮次收口';

-- 两路送达痕迹：站内(县广电局)一条、手机(台站值机室)一条，送到哪路独记哪路
CREATE TABLE IF NOT EXISTS t_bcast_zero_channel (
  id bigint NOT NULL COMMENT '主键',
  task_id bigint DEFAULT NULL COMMENT '所属催办条目id',
  round_id bigint DEFAULT NULL COMMENT '所属轮次id',
  channel int DEFAULT NULL COMMENT '路别 0站内(县广电局收) 1手机(台站值机室收)',
  receiver varchar(128) DEFAULT NULL COMMENT '收方',
  sent_at datetime DEFAULT NULL COMMENT '送抵那一刻',
  tpl_ver varchar(32) DEFAULT NULL COMMENT '本路所依稿子版次',
  status int DEFAULT '0' COMMENT '送达情形 0已送抵 1未达',
  del_flag int DEFAULT '0' COMMENT '删除标记 0正常 1删除',
  create_by varchar(64) DEFAULT NULL COMMENT '创建者',
  create_time datetime DEFAULT NULL COMMENT '创建时间',
  update_by varchar(64) DEFAULT NULL COMMENT '更新者',
  update_time datetime DEFAULT NULL COMMENT '更新时间',
  remark varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='零报告催办两路送达痕迹';

-- 台站底册初始数据：id=0 在册启用（status=0）、id=1 已关停（status=1）
INSERT IGNORE INTO t_bcast_site (id, site_no, site_name, site_type, road_name, status, del_flag, create_by, create_time)
VALUES (0, 'TZ00', '苗岭转播台（无线发射，在册）', '无线发射台', '长岭省—河湾区—苗岭县', 0, 0, 'seed', NOW()),
       (1, 'TZ01', '滨河北微波站（线路迁改后关停）', '微波站', '长岭省—河湾区—滨河北', 1, 0, 'seed', NOW()),
       (2, 'TZ02', '青云山发射台（在册，设备核点用）', '无线发射台', '长岭省—河湾区—青云山县', 0, 0, 'seed', NOW());

-- 日历种子（以 2026-10 为演示月）：
-- 国庆法定假 10-01..10-07 整日不入工作日；10-10（周六）调休补班按工作日；
-- 10-12（周一）省防台风令停办不入工作日；10-15、10-16 重要保障期封盘（仍算工作日，只压住催办）。
INSERT IGNORE INTO t_bcast_zero_skip (id, skip_date, skip_type, day_name, status, del_flag, create_by, create_time)
VALUES (1001, '2026-10-01', 0, '国庆节放假', 0, 0, 'seed', NOW()),
       (1002, '2026-10-02', 0, '国庆节放假', 0, 0, 'seed', NOW()),
       (1003, '2026-10-03', 0, '国庆节放假', 0, 0, 'seed', NOW()),
       (1004, '2026-10-04', 0, '国庆节放假', 0, 0, 'seed', NOW()),
       (1005, '2026-10-05', 0, '国庆节放假', 0, 0, 'seed', NOW()),
       (1006, '2026-10-06', 0, '国庆节放假', 0, 0, 'seed', NOW()),
       (1007, '2026-10-07', 0, '国庆节放假', 0, 0, 'seed', NOW()),
       (1008, '2026-10-10', 3, '国庆调休补班', 0, 0, 'seed', NOW()),
       (1009, '2026-10-12', 1, '省防台风令停办', 0, 0, 'seed', NOW()),
       (1010, '2026-10-15', 2, '秋末重要保障期', 0, 0, 'seed', NOW()),
       (1011, '2026-10-16', 2, '秋末重要保障期', 0, 0, 'seed', NOW());

-- 条目种子。演示一轮开口时刻：2026-10-09（周五）09:00。
INSERT IGNORE INTO t_bcast_zero_task
(id, item_no, site_id, biz_type, due_at, amount, tpl_ver, contact_phone, content, status, done_time, block_stage, block_reason, del_flag, create_by, create_time)
VALUES
-- 档已踩过（08:00 止点已过），本轮催到：站内+手机两路各落痕迹
(2001, 'ZERO-20261009-01', 0, 0, '2026-10-09 08:00:00', 0, 'v3', '13900000001', '苗岭转播台 零报告催办', 0, NULL, NULL, NULL, 0, 'seed', NOW()),
-- 同一日止点 10:00，09:00 开口时档未踩过，本轮放着不动
(2002, 'ZERO-20261009-02', 0, 0, '2026-10-09 10:00:00', 0, 'v3', '13900000002', '苗岭转播台 零报告催办(午后止点)', 0, NULL, NULL, NULL, 0, 'seed', NOW()),
-- 止点 10-13 09:00，最可提前 2 个工作日：跨过省令停办(10-12)与周日(10-11)，落回调休补班(10-10)与 10-09，恰在 10-09 09:00 开窗踩线
(2003, 'ZERO-20261013-01', 0, 0, '2026-10-13 09:00:00', 2, 'v3', '13900000003', '苗岭转播台 零报告催办(提前2工作日)', 0, NULL, NULL, NULL, 0, 'seed', NOW()),
-- 止点 10-13 10:00、提前 2 工作日：10-09 10:00 才开窗，09:00 本轮未到档
(2004, 'ZERO-20261013-02', 0, 0, '2026-10-13 10:00:00', 2, 'v3', '13900000004', '苗岭转播台 零报告催办(提前开窗钟点未到)', 0, NULL, NULL, NULL, 0, 'seed', NOW()),
-- 止点落在保障期封盘日 10-15、不提前：10-09 档未到先候着；真到 15 号又撞封盘，到档也压住等下一轮
(2005, 'ZERO-20261015-01', 0, 0, '2026-10-15 08:00:00', 0, 'v3', '13900000005', '苗岭转播台 零报告催办(撞保障期封盘)', 0, NULL, NULL, NULL, 0, 'seed', NOW()),
-- 台站早已从底册摘走（site 1 关停）：当场催不动，卡环=底册核验，原委随条
(2006, 'ZERO-20261009-03', 1, 0, '2026-10-09 08:00:00', 0, 'v3', '13900000006', '滨河北微波站 零报告催办(台站已关停)', 0, NULL, NULL, NULL, 0, 'seed', NOW()),
-- 值机联系人空着几季没人补：当场催不动，卡环=联系人核验，原委随条
(2007, 'ZERO-20261009-04', 0, 0, '2026-10-09 08:00:00', 0, 'v3', NULL, '苗岭转播台 零报告催办(联系人空缺)', 0, NULL, NULL, NULL, 0, 'seed', NOW()),
-- 保障期前设备核点事由，台站在册、联系人齐全，本轮催到
(2008, 'CHECK-20261009-01', 2, 1, '2026-10-09 08:30:00', 0, 'v3', '13900000008', '青云山发射台 保障期前设备核点', 0, NULL, NULL, NULL, 0, 'seed', NOW()),
-- 上一轮已催到（status=1、done_time 已落）：下一轮不得当候催捞回
(2009, 'ZERO-20261008-01', 0, 0, '2026-10-08 08:00:00', 0, 'v2', '13900000009', '苗岭转播台 零报告催办(昨日已催到)', 1, '2026-10-08 09:05:00', NULL, NULL, 0, 'seed', NOW());

-- 2009 上一轮的轮次与两路旧痕迹
INSERT IGNORE INTO t_bcast_zero_round
(id, round_no, run_at, win_date, frozen, picked_count, pending_count, done_count, fail_count, message, del_flag, create_by, create_time)
VALUES (9001, 'ROUND-20261008-0900', '2026-10-08 09:00:00', '2026-10-08', 0, 1, 0, 1, 0, '正常收口', 0, 'seed', NOW());
INSERT IGNORE INTO t_bcast_zero_channel
(id, task_id, round_id, channel, receiver, sent_at, tpl_ver, status, del_flag, create_by, create_time)
VALUES (9101, 2009, 9001, 0, '苗岭县广电局', '2026-10-08 09:05:00', 'v2', 0, 0, 'seed', NOW()),
       (9102, 2009, 9001, 1, '台站值机室(手机)', '2026-10-08 09:05:00', 'v2', 0, 0, 'seed', NOW());

