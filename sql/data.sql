-- 演示数据：所有账号初始密码均为 123456（PBKDF2-HMAC-SHA256 / 120000 轮）
DELETE FROM sys_user_role; DELETE FROM sys_role_menu;
DELETE FROM sys_dept; DELETE FROM sys_post; DELETE FROM sys_user; DELETE FROM sys_role; DELETE FROM sys_menu;
DELETE FROM att_checkin; DELETE FROM att_leave_balance; DELETE FROM wf_process_def;
DELETE FROM wf_instance; DELETE FROM wf_task; DELETE FROM oa_notice; DELETE FROM oa_notice_read;
DELETE FROM doc_folder; DELETE FROM doc_file; DELETE FROM prj_project; DELETE FROM prj_task;
DELETE FROM prj_task_comment; DELETE FROM cal_meeting_room; DELETE FROM cal_event; DELETE FROM sys_oper_log;

INSERT INTO sys_dept (id,parent_id,dept_name,dept_code,leader_id,sort_no,status) VALUES
 (1,0,'云枢科技','YS',1,1,1),(2,1,'总经理办公室','YS-GM',1,1,1),(3,1,'人力资源部','YS-HR',2,2,1),
 (4,1,'研发中心','YS-RD',3,3,1),(5,4,'平台架构组','YS-RD-A',3,1,1),(6,4,'前端应用组','YS-RD-F',4,2,1),
 (7,1,'市场营销部','YS-MK',5,4,1),(8,1,'财务部','YS-FI',6,5,1);

INSERT INTO sys_post (id,post_name,post_code,sort_no,status) VALUES
 (1,'总经理','GM',1,1),(2,'人力资源经理','HRM',2,1),(3,'研发总监','RD-DIR',3,1),
 (4,'前端负责人','FE-LEAD',4,1),(5,'市场总监','MK-DIR',5,1),(6,'财务主管','FI-LEAD',6,1),
 (7,'后端工程师','BE',7,1),(8,'前端工程师','FE',8,1),(9,'产品运营','OPS',9,1);

INSERT INTO sys_user (id,username,password,real_name,avatar,dept_id,post_id,phone,email,status,hire_date) VALUES
 (1,'admin','pbkdf2$2747b3b307c24805b4d54b9bdc05a53baf2a5ac079bb19a04bd7944b7db62eca','周明远','',2,1,'13800000001','admin@yunshu.com',1,'2021-03-01'),
 (2,'hr','pbkdf2$2747b3b307c24805b4d54b9bdc05a53baf2a5ac079bb19a04bd7944b7db62eca','林晓雯','',3,2,'13800000002','hr@yunshu.com',1,'2021-06-15'),
 (3,'devlead','pbkdf2$2747b3b307c24805b4d54b9bdc05a53baf2a5ac079bb19a04bd7944b7db62eca','陈立恒','',5,3,'13800000003','devlead@yunshu.com',1,'2021-08-02'),
 (4,'felead','pbkdf2$2747b3b307c24805b4d54b9bdc05a53baf2a5ac079bb19a04bd7944b7db62eca','苏雨桐','',6,4,'13800000004','felead@yunshu.com',1,'2022-01-10'),
 (5,'market','pbkdf2$2747b3b307c24805b4d54b9bdc05a53baf2a5ac079bb19a04bd7944b7db62eca','赵启帆','',7,5,'13800000005','market@yunshu.com',1,'2022-02-21'),
 (6,'finance','pbkdf2$2747b3b307c24805b4d54b9bdc05a53baf2a5ac079bb19a04bd7944b7db62eca','何静怡','',8,6,'13800000006','finance@yunshu.com',1,'2022-04-11'),
 (7,'zhangwei','pbkdf2$2747b3b307c24805b4d54b9bdc05a53baf2a5ac079bb19a04bd7944b7db62eca','张伟','',5,7,'13800000007','zhangwei@yunshu.com',1,'2022-07-01'),
 (8,'liyan','pbkdf2$2747b3b307c24805b4d54b9bdc05a53baf2a5ac079bb19a04bd7944b7db62eca','李妍','',6,8,'13800000008','liyan@yunshu.com',1,'2023-03-06'),
 (9,'wanghao','pbkdf2$2747b3b307c24805b4d54b9bdc05a53baf2a5ac079bb19a04bd7944b7db62eca','王浩','',5,7,'13800000009','wanghao@yunshu.com',1,'2023-05-22'),
 (10,'chenxi','pbkdf2$2747b3b307c24805b4d54b9bdc05a53baf2a5ac079bb19a04bd7944b7db62eca','陈曦','',7,9,'13800000010','chenxi@yunshu.com',1,'2023-09-18'),
 (11,'zhaomin','pbkdf2$2747b3b307c24805b4d54b9bdc05a53baf2a5ac079bb19a04bd7944b7db62eca','赵敏','',3,9,'13800000011','zhaomin@yunshu.com',1,'2024-01-08'),
 (12,'sunlei','pbkdf2$2747b3b307c24805b4d54b9bdc05a53baf2a5ac079bb19a04bd7944b7db62eca','孙磊','',8,9,'13800000012','sunlei@yunshu.com',1,'2024-04-01');

INSERT INTO sys_role (id,role_code,role_name,data_scope,remark) VALUES
 (1,'ADMIN','系统管理员','ALL','拥有全部菜单与数据权限'),
 (2,'HR','人事专员','DEPT','考勤、请假与组织维护'),
 (3,'MANAGER','部门经理','DEPT','审批本部门单据、查看部门报表'),
 (4,'EMPLOYEE','普通员工','SELF','仅本人数据');

INSERT INTO sys_menu (id,parent_id,menu_name,path,component,perm_code,icon,menu_type,sort_no) VALUES
 (1,0,'工作台','/dashboard','Dashboard','dashboard:view','DataBoard','C',1),
 (2,0,'组织权限','/org','','org:view','OfficeBuilding','M',2),
 (3,2,'部门管理','/org/dept','OrgDept','org:dept:list','Folder','C',1),
 (4,2,'员工管理','/org/user','OrgUser','org:user:list','User','C',2),
 (5,2,'角色权限','/org/role','OrgRole','org:role:list','Lock','C',3),
 (6,0,'考勤管理','/attendance','Attendance','attendance:view','AlarmClock','C',3),
 (7,0,'审批中心','/approval','','approval:view','Stamp','M',4),
 (8,7,'我的申请','/approval/mine','ApprovalMine','approval:mine','Document','C',1),
 (9,7,'待我审批','/approval/todo','ApprovalTodo','approval:todo','Tickets','C',2),
 (10,7,'流程定义','/approval/def','ApprovalDef','approval:def','Operation','C',3),
 (11,0,'公告通知','/notice','Notice','notice:view','Bell','C',5),
 (12,0,'云文档','/doc','DocCenter','doc:view','FolderOpened','C',6),
 (13,0,'任务协作','/work','','work:view','List','M',7),
 (14,13,'项目看板','/work/board','WorkBoard','work:board','Grid','C',1),
 (15,13,'项目列表','/work/project','WorkProject','work:project','Files','C',2),
 (16,0,'日程会议','/calendar','Calendar','calendar:view','Calendar','C',8),
 (17,0,'系统审计','/log','SysLog','log:view','Monitor','C',9),
 (18,0,'发起审批','','','approval:start','EditPen','F',1),
 (19,0,'审批操作','','','approval:audit','CircleCheck','F',2),
 (20,0,'文件管理','','','doc:manage','UploadFilled','F',3),
 (21,0,'系统配置','','','system:config','Setting','F',4);

INSERT INTO sys_user_role (user_id,role_id) VALUES
 (1,1),(2,2),(3,3),(4,3),(5,3),(6,2),(7,4),(8,4),(9,4),(10,4),(11,4),(12,4);
INSERT INTO sys_role_menu (role_id,menu_id) SELECT 1,id FROM sys_menu;
INSERT INTO sys_role_menu (role_id,menu_id) SELECT 2,id FROM sys_menu WHERE id IN (1,2,3,4,6,7,8,9,10,11,12,13,14,15,16,18,19);
INSERT INTO sys_role_menu (role_id,menu_id) SELECT 3,id FROM sys_menu WHERE id IN (1,2,3,4,6,7,8,9,10,11,12,13,14,15,16,18,19,20);
INSERT INTO sys_role_menu (role_id,menu_id) SELECT 4,id FROM sys_menu WHERE id IN (1,6,7,8,9,11,12,13,14,15,16,18);

INSERT INTO wf_process_def (id,proc_key,proc_name,biz_type,nodes_json,status) VALUES
 (1,'leave','请假审批流','LEAVE','[{"key":"leader","name":"直属主管审批","assignee":"leader"},{"key":"hr","name":"人事备案","assignee":"hr"}]',1),
 (2,'expense','费用报销流','EXPENSE','[{"key":"leader","name":"部门负责人审批","assignee":"leader"},{"key":"finance","name":"财务复核","assignee":"finance"},{"key":"gm","name":"总经理终审","assignee":"gm"}]',1),
 (3,'overtime','加班申请流','OVERTIME','[{"key":"leader","name":"直属主管审批","assignee":"leader"}]',1),
 (4,'business_trip','出差申请流','TRIP','[{"key":"leader","name":"直属主管审批","assignee":"leader"},{"key":"hr","name":"人事备案","assignee":"hr"}]',1);

INSERT INTO oa_notice (id,title,content,category,publisher_id,publisher_name,pinned,view_count,publish_time) VALUES
 (1,'关于 2026 年冬季作息时间调整的通知','各位同事：自 11 月 1 日起执行冬季作息，上班时间 09:00，下班时间 18:00，午休 12:00-13:30。请各部门做好考勤排班同步。','制度',2,'林晓雯',1,86,'2026-11-01 09:10:00'),
 (2,'云枢 OA 协同平台 V1.0 正式上线','平台已上线组织权限、考勤打卡、审批中心、公告、云文档、任务看板与会议预约七大模块。默认账号见部署手册，首次登录请修改密码。','公告',1,'周明远',1,142,'2026-11-03 10:00:00'),
 (3,'第四季度绩效考核启动说明','第四季度绩效自评通道已开启，请于 12 月 15 日前在个人中心完成自评，部门经理于 12 月 22 日前完成复评。','人事',2,'林晓雯',0,63,'2026-11-04 14:20:00'),
 (4,'研发中心代码评审规范（2026 修订版）','所有合并请求需至少一名 Reviewer 通过，单元测试覆盖率不低于 60%，流水线全绿后方可合入主干。','制度',3,'陈立恒',0,41,'2026-11-05 16:35:00'),
 (5,'11 月全员大会暨产品路线图分享','时间：11 月 20 日 14:00，地点：3F 多功能厅。议程包含 Q4 复盘与 2027 产品路线图，请提前安排工作。','会议',1,'周明远',0,38,'2026-11-05 17:05:00'),
 (6,'差旅报销标准更新提醒','自本月起市内交通凭票据实报销，出差住宿标准按城市分级执行，详见财务共享盘《差旅标准 2026》。','财务',6,'何静怡',0,29,'2026-11-06 09:40:00'),
 (7,'信息安全与数据合规培训通知','本周五 15:00 线上开展数据分类分级与个人信息保护培训，全体同事须参加并完成随堂测验。','培训',2,'林晓雯',0,22,'2026-11-06 11:15:00'),
 (8,'会议室预约系统切换说明','会议室预约已迁移至 OA 日程会议模块，支持冲突检测，原邮件预约方式自 11 月 10 日起停用。','公告',4,'苏雨桐',0,17,'2026-11-06 15:30:00');

INSERT INTO oa_notice_read (notice_id,user_id) VALUES (1,1),(2,1),(3,1),(1,7),(2,7),(4,7),(2,8),(5,8),(1,9);

INSERT INTO doc_folder (id,parent_id,name,owner_id) VALUES
 (1,0,'公司制度库',1),(2,0,'研发中心',3),(3,0,'财务共享盘',6),(4,0,'市场素材',5),(5,2,'架构设计',3),(6,2,'接口文档',3);
INSERT INTO doc_file (id,folder_id,name,ext,file_size,version,storage_path,uploader_id,uploader_name,download_count) VALUES
 (1,1,'员工手册-2026版.pdf','pdf',2457600,3,'seed/员工手册-2026版.pdf',2,'林晓雯',58),
 (2,1,'考勤与休假管理制度.pdf','pdf',839680,2,'seed/考勤与休假管理制度.pdf',2,'林晓雯',41),
 (3,1,'信息安全管理规范.docx','docx',356352,1,'seed/信息安全管理规范.docx',1,'周明远',27),
 (4,2,'云枢OA-系统设计说明书.docx','docx',1835008,5,'seed/云枢OA-系统设计说明书.docx',3,'陈立恒',34),
 (5,5,'微服务拆分方案-v2.1.pdf','pdf',4718592,2,'seed/微服务拆分方案-v2.1.pdf',3,'陈立恒',19),
 (6,6,'开放接口文档-v1.0.md','md',92160,4,'seed/开放接口文档-v1.0.md',4,'苏雨桐',46),
 (7,3,'差旅报销标准-2026.xlsx','xlsx',147456,2,'seed/差旅报销标准-2026.xlsx',6,'何静怡',31),
 (8,3,'Q4预算执行表.xlsx','xlsx',262144,1,'seed/Q4预算执行表.xlsx',6,'何静怡',12),
 (9,4,'品牌视觉规范.zip','zip',12582912,1,'seed/品牌视觉规范.zip',5,'赵启帆',9),
 (10,4,'双十一活动复盘.pptx','pptx',6291456,2,'seed/双十一活动复盘.pptx',10,'陈曦',23);

INSERT INTO prj_project (id,project_code,name,owner_id,owner_name,status,start_date,end_date,description) VALUES
 (1,'PRJ-2026-001','OA 协同平台 V1.0',3,'陈立恒','ACTIVE','2026-07-01','2026-12-31','前后端分离的企业级协同办公平台一期建设'),
 (2,'PRJ-2026-002','官网改版与品牌升级',4,'苏雨桐','ACTIVE','2026-09-01','2026-12-15','官网视觉重构、组件库落地与内容迁移'),
 (3,'PRJ-2026-003','数据中台二期',3,'陈立恒','ACTIVE','2026-10-08','2027-03-31','统一指标口径与报表中心'),
 (4,'PRJ-2026-004','Q4 市场获客专项',5,'赵启帆','ACTIVE','2026-10-01','2026-12-31','渠道投放、内容矩阵与线索转化'),
 (5,'PRJ-2026-005','财务共享与电子档案',6,'何静怡','PLANNING','2026-12-01','2027-04-30','电子发票归档与报销自动化'),
 (6,'PRJ-2026-006','移动端小程序预研',4,'苏雨桐','CLOSED','2026-05-01','2026-09-30','技术预研已结项，结论纳入 V2.0 规划');

INSERT INTO prj_task (id,project_id,title,description,assignee_id,assignee_name,priority,status,progress,due_date) VALUES
 (1,1,'完成 RBAC 菜单权限拦截器','角色-菜单-按钮三级鉴权 + 数据范围过滤',7,'张伟','HIGH','DONE',100,'2026-09-20'),
 (2,1,'审批流引擎与流程定义','支持串行节点、驳回、撤回与审批留痕',7,'张伟','HIGH','DOING',75,'2026-11-20'),
 (3,1,'考勤打卡与月报统计','迟到早退判定、缺卡补卡与部门月报',9,'王浩','MEDIUM','DOING',60,'2026-11-25'),
 (4,1,'云文档分片上传','断点续传与版本管理',9,'王浩','MEDIUM','TODO',0,'2026-12-05'),
 (5,1,'工作台数据看板','ECharts 待办、趋势与分布图',8,'李妍','MEDIUM','DOING',80,'2026-11-12'),
 (6,1,'会议室冲突检测','同一会议室时间段互斥校验',8,'李妍','LOW','REVIEW',90,'2026-11-10'),
 (7,1,'操作审计日志切面','基于 AOP 记录请求与耗时',7,'张伟','LOW','DONE',100,'2026-10-15'),
 (8,1,'登录页与Layout联调','Element Plus 主题与路由守卫',8,'李妍','MEDIUM','DONE',100,'2026-10-30'),
 (9,1,'接口压测与慢SQL优化','核心接口 P95 < 200ms',3,'陈立恒','HIGH','TODO',0,'2026-12-12'),
 (10,2,'设计系统组件库落地','统一色彩、间距与表单规范',8,'李妍','HIGH','DOING',65,'2026-11-18'),
 (11,2,'官网首页视觉重构','响应式与首屏性能优化',10,'陈曦','MEDIUM','DOING',50,'2026-11-28'),
 (12,2,'内容迁移与 SEO 检查','历史文章批量导入与死链扫描',10,'陈曦','LOW','REVIEW',85,'2026-11-08'),
 (13,3,'指标字典与口径对齐','梳理 42 个核心业务指标',3,'陈立恒','HIGH','DOING',40,'2026-12-01'),
 (14,3,'报表中心原型评审','与财务、市场确认看板字段',11,'赵敏','MEDIUM','TODO',0,'2026-12-08'),
 (15,4,'双十一投放复盘','渠道 ROI 与线索成本分析',10,'陈曦','HIGH','DONE',100,'2026-11-06'),
 (16,4,'内容矩阵排期','12 月公众号与短视频排期',5,'赵启帆','MEDIUM','DOING',30,'2026-11-22'),
 (17,5,'电子发票归档方案','票面字段抽取与归档规则',12,'孙磊','MEDIUM','TODO',0,'2026-12-20'),
 (18,6,'小程序技术选型结论','已完成，输出预研报告',4,'苏雨桐','LOW','DONE',100,'2026-09-28');

INSERT INTO prj_task_comment (id,task_id,user_id,user_name,content) VALUES
 (1,2,3,'陈立恒','驳回场景要保留上一节点意见，别丢历史。'),
 (2,2,7,'张伟','已补上 flow_history 展示，明天提测。'),
 (3,5,4,'苏雨桐','看板配色统一用主题蓝，别用渐变。'),
 (4,10,8,'李妍','组件库已发布 1.2.0，官网可直接引。');

INSERT INTO cal_meeting_room (id,name,capacity,location,equipment,status) VALUES
 (1,'3F 多功能厅',80,'3F 东侧','投影/音响/视频会议',1),
 (2,'5F 会议室 A',16,'5F 北侧','投影/白板',1),
 (3,'5F 会议室 B',10,'5F 南侧','电视投屏/白板',1),
 (4,'6F 洽谈室',6,'6F 西侧','电视投屏',1),
 (5,'2F 培训教室',40,'2F 东侧','投影/音响/录播',1);

INSERT INTO cal_event (id,title,event_type,room_id,organizer_id,organizer_name,start_time,end_time,attendees,remark) VALUES
 (1,'研发周会','MEETING',2,3,'陈立恒','2026-11-09 10:00','2026-11-09 11:00','陈立恒,苏雨桐,张伟,王浩,李妍','迭代进度与风险同步'),
 (2,'OA V1.0 需求评审','MEETING',3,4,'苏雨桐','2026-11-10 14:00','2026-11-10 16:00','苏雨桐,李妍,陈曦','审批流交互走查'),
 (3,'Q4 绩效校准会','MEETING',1,2,'林晓雯','2026-11-12 09:30','2026-11-12 11:30','周明远,林晓雯,陈立恒,赵启帆','各部门绩效结果校准'),
 (4,'全员大会暨路线图分享','MEETING',1,1,'周明远','2026-11-20 14:00','2026-11-20 16:00','全体同事','Q4 复盘与 2027 规划'),
 (5,'数据中台指标对齐','MEETING',2,3,'陈立恒','2026-11-13 15:00','2026-11-13 16:30','陈立恒,赵敏,孙磊','指标口径确认');

INSERT INTO att_leave_balance (user_id,year_no,annual_total,annual_used,comp_total,comp_used) VALUES
 (1,'2026',15,2,3,0),(2,'2026',10,1,2,0),(3,'2026',12,3,4,1),(4,'2026',10,0,1,0),
 (5,'2026',10,2,0,0),(6,'2026',10,1,2,0),(7,'2026',10,1.5,6,2),(8,'2026',10,0,3,0),
 (9,'2026',10,2,4,1),(10,'2026',10,0,1,0),(11,'2026',10,1,0,0),(12,'2026',10,0,0,0);

INSERT INTO wf_instance (id,proc_key,biz_type,biz_id,title,applicant_id,applicant_name,current_node,current_node_name,status,form_json) VALUES
 (1,'leave','LEAVE',1001,'张伟 - 年假申请 2026-11-13 至 2026-11-14（2 天）',7,'张伟','leader','直属主管审批','RUNNING','{"leaveType":"年假","startDate":"2026-11-13","endDate":"2026-11-14","days":2,"reason":"家庭事务"}'),
 (2,'expense','EXPENSE',1002,'陈曦 - 双十一物料报销 ¥2,860.00',10,'陈曦','finance','财务复核','RUNNING','{"amount":2860.00,"category":"市场物料","reason":"双十一直播物料采购"}'),
 (3,'overtime','OVERTIME',1003,'王浩 - 11/07 加班申请 3.0 小时',9,'王浩','leader','直属主管审批','RUNNING','{"date":"2026-11-07","hours":3.0,"reason":"考勤月报联调"}'),
 (4,'leave','LEAVE',1004,'李妍 - 调休申请 2026-11-16（1 天）',8,'李妍','hr','人事备案','RUNNING','{"leaveType":"调休","startDate":"2026-11-16","endDate":"2026-11-16","days":1,"reason":"调休"}'),
 (5,'business_trip','TRIP',1005,'赵启帆 - 上海客户拜访 11/18-11/19',5,'赵启帆','hr','人事备案','RUNNING','{"destination":"上海","startDate":"2026-11-18","endDate":"2026-11-19","reason":"重点客户年度复盘"}'),
 (6,'leave','LEAVE',1006,'赵敏 - 事假申请 2026-10-28（0.5 天）',11,'赵敏','','','APPROVED','{"leaveType":"事假","startDate":"2026-10-28","endDate":"2026-10-28","days":0.5,"reason":"个人事务"}'),
 (7,'expense','EXPENSE',1007,'孙磊 - 差旅报销 ¥1,240.00',12,'孙磊','','','REJECTED','{"amount":1240.00,"category":"差旅","reason":"缺少发票"}');
INSERT INTO wf_task (id,instance_id,node_key,node_name,assignee_id,assignee_name,action,comment,status) VALUES
 (1,2,'leader','部门负责人审批',5,'赵启帆','APPROVE','物料用途属实，同意。','DONE'),
 (2,1,'leader','直属主管审批',3,'陈立恒','','','PENDING'),
 (3,2,'finance','财务复核',6,'何静怡','','','PENDING'),
 (4,3,'leader','直属主管审批',3,'陈立恒','','','PENDING'),
 (5,4,'leader','直属主管审批',4,'苏雨桐','APPROVE','同意调休。','DONE'),
 (6,4,'hr','人事备案',2,'林晓雯','','','PENDING'),
 (7,5,'leader','直属主管审批',1,'周明远','APPROVE','同意出差。','DONE'),
 (8,5,'hr','人事备案',2,'林晓雯','','','PENDING'),
 (9,7,'leader','部门负责人审批',6,'何静怡','REJECT','请补充发票后重新提交。','DONE');

-- 管理员（周明远 / admin）的本人申请：覆盖 已通过 / 已驳回 / 审批中 / 已撤回 四种状态
INSERT INTO wf_instance (id,proc_key,biz_type,biz_id,title,applicant_id,applicant_name,current_node,current_node_name,status,form_json) VALUES
 (8,'leave','LEAVE',1008,'周明远 - 年假申请 2026-10-12 至 2026-10-13（2 天）',1,'周明远','','','APPROVED','{"leaveType":"年假","startDate":"2026-10-12","endDate":"2026-10-13","days":2,"reason":"家庭出行"}'),
 (9,'expense','EXPENSE',1009,'周明远 - 行业峰会差旅报销 ¥3,420.00',1,'周明远','','','APPROVED','{"amount":3420.00,"category":"差旅","reason":"参加行业峰会，票据齐全"}'),
 (10,'overtime','OVERTIME',1010,'周明远 - 10/24 加班申请 2.5 小时',1,'周明远','','','REJECTED','{"date":"2026-10-24","hours":2.5,"reason":"版本发布值守"}'),
 (11,'leave','LEAVE',1011,'周明远 - 调休申请 2026-11-26（1 天）',1,'周明远','leader','直属主管审批','RUNNING','{"leaveType":"调休","startDate":"2026-11-26","endDate":"2026-11-26","days":1,"reason":"调休"}');

-- 待管理员终审的报销单：流程定义 expense 的第三节点「总经理终审」，assignee = gm → 周明远(1)
INSERT INTO wf_instance (id,proc_key,biz_type,biz_id,title,applicant_id,applicant_name,current_node,current_node_name,status,form_json) VALUES
 (12,'expense','EXPENSE',1012,'陈曦 - 双十一投放追加预算报销 ¥8,600.00',10,'陈曦','gm','总经理终审','RUNNING','{"amount":8600.00,"category":"市场物料","reason":"双十一追加投放，已附合同与发票"}'),
 (13,'expense','EXPENSE',1013,'赵启帆 - Q4 客户招待费报销 ¥2,180.00',5,'赵启帆','gm','总经理终审','RUNNING','{"amount":2180.00,"category":"招待","reason":"重点客户季度拜访接待"}'),
 (14,'expense','EXPENSE',1014,'孙磊 - 研发测试设备采购报销 ¥4,180.00',12,'孙磊','gm','总经理终审','RUNNING','{"amount":4180.00,"category":"办公用品","reason":"压测机型采购，已走采购流程"}'),
 (15,'expense','EXPENSE',1015,'苏雨桐 - 前端团队建设费用报销 ¥3,600.00',4,'苏雨桐','gm','总经理终审','RUNNING','{"amount":3600.00,"category":"团建","reason":"Q4 团队建设，附活动审批单"}');

INSERT INTO wf_task (id,instance_id,node_key,node_name,assignee_id,assignee_name,action,comment,status) VALUES
 (10,11,'leader','直属主管审批',3,'陈立恒','','','PENDING'),
 (11,8,'leader','直属主管审批',3,'陈立恒','APPROVE','同意。','DONE'),
 (12,8,'hr','人事备案',2,'林晓雯','APPROVE','已备案。','DONE'),
 (13,10,'leader','直属主管审批',3,'陈立恒','REJECT','已计入调休池，无需加班单。','DONE'),
 (14,12,'leader','部门负责人审批',5,'赵启帆','APPROVE','预算内，同意。','DONE'),
 (15,12,'finance','财务复核',6,'何静怡','APPROVE','票据核验无误。','DONE'),
 (16,12,'gm','总经理终审',1,'周明远','','','PENDING'),
 (17,13,'leader','部门负责人审批',1,'周明远','APPROVE','同意。','DONE'),
 (18,13,'finance','财务复核',6,'何静怡','APPROVE','金额无误。','DONE'),
 (19,13,'gm','总经理终审',1,'周明远','','','PENDING'),
 (20,14,'leader','部门负责人审批',3,'陈立恒','APPROVE','设备确已入库。','DONE'),
 (21,14,'finance','财务复核',6,'何静怡','APPROVE','发票已验真。','DONE'),
 (22,14,'gm','总经理终审',1,'周明远','','','PENDING'),
 (23,15,'leader','部门负责人审批',1,'周明远','APPROVE','同意。','DONE'),
 (24,15,'finance','财务复核',6,'何静怡','APPROVE','票据齐全。','DONE'),
 (25,15,'gm','总经理终审',1,'周明远','','','PENDING');

INSERT INTO att_checkin (id,user_id,check_date,check_in,check_out,in_status,out_status,work_hours,location) VALUES
 (100,1,'2026-11-02','08:46','18:03','NORMAL','NORMAL',8.75,'总部-5F WiFi'),
 (200,2,'2026-11-02','08:47','18:04','NORMAL','NORMAL',9.00,'总部-5F WiFi'),
 (300,3,'2026-11-02','08:48','18:05','NORMAL','NORMAL',9.25,'总部-5F WiFi'),
 (400,4,'2026-11-02','08:49','18:06','NORMAL','NORMAL',8.50,'总部-5F WiFi'),
 (500,5,'2026-11-02','08:50','18:07','NORMAL','NORMAL',8.75,'总部-5F WiFi'),
 (600,6,'2026-11-02','08:51','18:08','NORMAL','NORMAL',9.00,'总部-5F WiFi'),
 (700,7,'2026-11-02','08:52','18:09','NORMAL','NORMAL',9.25,'总部-5F WiFi'),
 (800,8,'2026-11-02','08:53','18:02','NORMAL','NORMAL',8.50,'总部-5F WiFi'),
 (900,9,'2026-11-02','08:54','18:03','NORMAL','NORMAL',8.75,'总部-5F WiFi'),
 (1000,10,'2026-11-02','08:45','18:04','NORMAL','NORMAL',9.00,'总部-5F WiFi'),
 (1100,11,'2026-11-02','08:46','18:05','NORMAL','NORMAL',9.25,'总部-5F WiFi'),
 (1200,12,'2026-11-02','08:47','18:06','NORMAL','NORMAL',8.50,'总部-5F WiFi'),
 (101,1,'2026-11-03','08:46','18:03','NORMAL','NORMAL',8.75,'总部-5F WiFi'),
 (201,2,'2026-11-03','08:47','18:04','NORMAL','NORMAL',9.00,'总部-5F WiFi'),
 (301,3,'2026-11-03','08:48','18:05','NORMAL','NORMAL',9.25,'总部-5F WiFi'),
 (401,4,'2026-11-03','08:49','18:06','NORMAL','NORMAL',8.50,'总部-5F WiFi'),
 (501,5,'2026-11-03','08:50','18:07','NORMAL','NORMAL',8.75,'总部-5F WiFi'),
 (601,6,'2026-11-03','08:51','18:08','NORMAL','NORMAL',9.00,'总部-5F WiFi'),
 (701,7,'2026-11-03','08:52','18:09','NORMAL','NORMAL',9.25,'总部-5F WiFi'),
 (801,8,'2026-11-03','08:53','18:02','NORMAL','NORMAL',8.50,'总部-5F WiFi'),
 (901,9,'2026-11-03','08:54','18:03','NORMAL','NORMAL',8.75,'总部-5F WiFi'),
 (1001,10,'2026-11-03','08:45','18:04','NORMAL','NORMAL',9.00,'总部-5F WiFi'),
 (1101,11,'2026-11-03','08:46','18:05','NORMAL','NORMAL',9.25,'总部-5F WiFi'),
 (1201,12,'2026-11-03','08:47','18:06','NORMAL','NORMAL',8.50,'总部-5F WiFi'),
 (102,1,'2026-11-04','08:46','18:03','NORMAL','NORMAL',8.75,'总部-5F WiFi'),
 (202,2,'2026-11-04','08:47','18:04','NORMAL','NORMAL',9.00,'总部-5F WiFi'),
 (302,3,'2026-11-04','08:48','18:05','NORMAL','NORMAL',9.25,'总部-5F WiFi'),
 (402,4,'2026-11-04','08:49','18:06','NORMAL','NORMAL',8.50,'总部-5F WiFi'),
 (502,5,'2026-11-04','08:50','18:07','NORMAL','NORMAL',8.75,'总部-5F WiFi'),
 (602,6,'2026-11-04','08:51','18:08','NORMAL','NORMAL',9.00,'总部-5F WiFi'),
 (702,7,'2026-11-04','09:12','18:09','LATE','NORMAL',9.25,'总部-5F WiFi'),
 (802,8,'2026-11-04','08:53','18:02','NORMAL','NORMAL',8.50,'总部-5F WiFi'),
 (902,9,'2026-11-04','08:54','18:03','NORMAL','NORMAL',8.75,'总部-5F WiFi'),
 (1002,10,'2026-11-04','08:45','18:04','NORMAL','NORMAL',9.00,'总部-5F WiFi'),
 (1102,11,'2026-11-04','08:46','18:05','NORMAL','NORMAL',9.25,'总部-5F WiFi'),
 (1202,12,'2026-11-04','08:47','18:06','NORMAL','NORMAL',8.50,'总部-5F WiFi'),
 (103,1,'2026-11-05','08:46','18:03','NORMAL','NORMAL',8.75,'总部-5F WiFi'),
 (203,2,'2026-11-05','08:47','18:04','NORMAL','NORMAL',9.00,'总部-5F WiFi'),
 (303,3,'2026-11-05','08:48','18:05','NORMAL','NORMAL',9.25,'总部-5F WiFi'),
 (403,4,'2026-11-05','08:49','18:06','NORMAL','NORMAL',8.50,'总部-5F WiFi'),
 (503,5,'2026-11-05','08:50','18:07','NORMAL','NORMAL',8.75,'总部-5F WiFi'),
 (603,6,'2026-11-05','08:51','18:08','NORMAL','NORMAL',9.00,'总部-5F WiFi'),
 (703,7,'2026-11-05','08:52','18:09','NORMAL','NORMAL',9.25,'总部-5F WiFi'),
 (803,8,'2026-11-05','08:53','18:02','NORMAL','NORMAL',8.50,'总部-5F WiFi'),
 (903,9,'2026-11-05','08:54','17:32','NORMAL','EARLY',8.75,'总部-5F WiFi'),
 (1003,10,'2026-11-05','08:45','18:04','NORMAL','NORMAL',9.00,'总部-5F WiFi'),
 (1103,11,'2026-11-05','08:46','18:05','NORMAL','NORMAL',9.25,'总部-5F WiFi'),
 (1203,12,'2026-11-05','08:47','18:06','NORMAL','NORMAL',8.50,'总部-5F WiFi'),
 (104,1,'2026-11-06','08:46','18:03','NORMAL','NORMAL',8.75,'总部-5F WiFi'),
 (204,2,'2026-11-06','08:47','18:04','NORMAL','NORMAL',9.00,'总部-5F WiFi'),
 (304,3,'2026-11-06','08:48','18:05','NORMAL','NORMAL',9.25,'总部-5F WiFi'),
 (404,4,'2026-11-06','08:49','18:06','NORMAL','NORMAL',8.50,'总部-5F WiFi'),
 (504,5,'2026-11-06','08:50','18:07','NORMAL','NORMAL',8.75,'总部-5F WiFi'),
 (604,6,'2026-11-06','08:51','18:08','NORMAL','NORMAL',9.00,'总部-5F WiFi'),
 (704,7,'2026-11-06','08:52','18:09','NORMAL','NORMAL',9.25,'总部-5F WiFi'),
 (804,8,'2026-11-06','08:53','18:02','NORMAL','NORMAL',8.50,'总部-5F WiFi'),
 (904,9,'2026-11-06','08:54','18:03','NORMAL','NORMAL',8.75,'总部-5F WiFi'),
 (1004,10,'2026-11-06','08:45','18:04','NORMAL','NORMAL',9.00,'总部-5F WiFi'),
 (1104,11,'2026-11-06','08:46','18:05','NORMAL','NORMAL',9.25,'总部-5F WiFi'),
 (1204,12,'2026-11-06','08:47','18:06','NORMAL','NORMAL',8.50,'总部-5F WiFi');

INSERT INTO sys_oper_log (id,user_id,user_name,module,operation,method,request_uri,ip,cost_ms,success) VALUES
 (1,1,'周明远','Auth','login','AuthController.login','/api/auth/login','10.0.1.21',42,1),
 (2,7,'张伟','Approval','start','ApprovalController.start','/api/approvals/instances','10.0.1.36',31,1),
 (3,2,'林晓雯','Notice','publish','NoticeController.save','/api/notices','10.0.1.18',26,1),
 (4,8,'李妍','Task','move','WorkController.moveTask','/api/tasks/6/status','10.0.1.44',19,1),
 (5,3,'陈立恒','Doc','upload','DocController.upload','/api/docs/files/upload','10.0.1.12',88,1),
 (6,12,'孙磊','Approval','start','ApprovalController.start','/api/approvals/instances','10.0.1.57',27,1),
 (7,6,'何静怡','Approval','reject','ApprovalController.audit','/api/approvals/7/reject','10.0.1.29',22,1),
 (8,1,'周明远','Org','save','OrgController.saveUser','/api/org/users','10.0.1.21',35,1);
