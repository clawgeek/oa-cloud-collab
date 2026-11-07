# 云枢 OA · 企业云协同办公平台

> 基于 **SpringBoot 3 + MyBatis + Vue 3 + Vue Router 4 + Element Plus 2** 的前后端分离企业级 OA 协同系统，覆盖真实企业日常办公的核心业务闭环。

🌐 **开源地址**：https://github.com/clawgeek/oa-cloud-collab

🎬 **5 分钟讲解视频**：[demo/云枢OA_项目讲解_1080p.mp4](demo/云枢OA_项目讲解_1080p.mp4) ·
1920×1080 横屏 / 25fps / 中英双语字幕 / 真实部署与录屏操作 ·
[视频内容清单](docs/演示视频.md)

---

## 一、项目定位

「云枢 OA」面向 50–500 人规模的中小企业，把**组织、考勤、审批、公告、文档、任务、会议、审计**八类高频事务收拢到一套系统里。不是 CRUD 拼盘，每块业务都带真实规则：

| 模块 | 真实业务规则 |
| --- | --- |
| 组织权限 | 部门树 / 岗位 / 员工台账；RBAC 用户-角色-菜单(按钮)三级鉴权；数据范围 ALL / DEPT / SELF；接口层 `@RequirePerm` 二次校验 |
| 考勤打卡 | 09:00 / 18:00 上下班判定；迟到、早退自动计算工时；月度部门统计；年假 / 调休余额 |
| 审批中心 | 流程定义以 JSON 配置串行节点（请假 2 级、报销 3 级、加班 1 级、出差 2 级）；按角色解析办理人；通过推进 / 驳回 / 撤回；全程审批留痕 |
| 公告通知 | 分类、置顶、阅读量、已读回执与未读数 |
| 云文档 | 文件夹、真实文件上传落盘、版本、下载计数；可平滑替换为 MinIO / OSS |
| 任务协作 | 项目台账 + 四列 Kanban（待办/进行中/待验收/已完成）；状态流转自动改进度；任务评论 |
| 日程会议 | 会议室台账；**提交预约时做时间段互斥冲突检测** |
| 系统审计 | Spring AOP 自动记录所有写操作：操作人 / 模块 / URI / IP / 耗时 / 成功标志 |

---

## 二、技术架构

```
┌──────────────────────────────────────────────────────────────┐
│  浏览器  Vue 3.4 + Vite 5 + Vue Router 4 + Element Plus 2      │
│          Pinia(状态) · Axios(拦截器注入 JWT) · ECharts 5        │
└───────────────────────────┬──────────────────────────────────┘
                            │ HTTP / JSON  (Vite /api 代理 → :8080)
                            │ 生产：Nginx 静态托管 + /api 反向代理
┌───────────────────────────▼──────────────────────────────────┐
│  Spring Boot 3.2.5 / Java 17 / Tomcat 10                      │
│  JwtInterceptor(HS256) → RequirePerm(RBAC) → Controller       │
│        ↓                Service(@Transactional)                │
│  AuditAspect(AOP)      MyBatis(XML + 注解)                     │
└───────────────────────────┬──────────────────────────────────┘
                            │
              ┌─────────────┴─────────────┐
              │ H2 (dev，内存，零依赖)     │ MySQL 8 (prod)
              │ 19 张表 + 演示数据         │ sql/schema-mysql.sql
              └───────────────────────────┘
```

**安全设计**：口令 PBKDF2-HMAC-SHA256 / 120000 轮加盐散列（JDK 原生，无第三方依赖）；登录签发 HS256 JWT（12h）；所有 `/api/**` 经拦截器校验；写操作再由 `@RequirePerm` 做权限码校验；全局异常处理统一响应体 `{code,msg,data,timestamp}`。

**数据库**：19 张业务表，`sql/schema-mysql.sql` 为 MySQL 8 生产脚本，classpath 内 `db/schema.sql` 为 H2/MySQL 通用脚本，启动时自动建表并灌入演示数据。

---

## 三、目录结构

```
oa-cloud-collab/
├── oa-server/                 # 后端 SpringBoot 3
│   ├── mvnw / mvnw.cmd        # Maven Wrapper（无需预装 Maven）
│   ├── .mvn/settings.xml      # 可选国内镜像
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/yunshu/oa/
│       │   ├── common/        # R / PageResult / 异常 / 上下文 / 参数工具
│       │   ├── config/        # WebMvc / CORS / 审计切面 / 文件初始化
│       │   ├── security/      # JWT / 口令散列 / 鉴权拦截器 / 权限注解
│       │   ├── jsoncodec/     # 轻量 JSON 编解码
│       │   ├── entity/        # 19 个实体
│       │   ├── mapper/        # 10 个 Mapper 接口
│       │   ├── service/       # 10 个业务服务
│       │   └── controller/    # 11 个 REST 控制器
│       └── resources/
│           ├── application*.yml
│           ├── db/schema.sql · db/data.sql
│           └── mapper/*.xml   # 10 个 MyBatis 映射文件
├── oa-web/                    # 前端 Vue 3
│   ├── vite.config.js         # /api 代理 + 构建分包
│   ├── nginx.conf             # 生产 Nginx 配置
│   └── src/
│       ├── api/ router/ store/ layout/
│       └── views/             # 15 个业务页面
├── sql/                       # MySQL 生产建表 + 演示数据
├── docs/                      # 部署手册 / 接口清单 / 数据库设计 / 业务说明
├── docker-compose.yml         # 一键起 MySQL + 后端 + 前端
└── README.md
```

---

## 四、快速启动（开发环境，零外部依赖）

后端使用 **H2 内存库**，`dev` profile 启动即自动建库并灌入演示数据，**不需要安装 MySQL**。

```bash
# ① 启动后端（:8080）
cd oa-server
./mvnw spring-boot:run                 # Windows: mvnw.cmd spring-boot:run
# 或者打包后运行
./mvnw -DskipTests clean package
java -jar target/oa-server-1.0.0.jar

# ② 启动前端（:5173）
cd oa-web
npm install
npm run dev
```

打开 http://localhost:5173 ，健康检查 http://localhost:8080/api/health 。

### 演示账号（密码统一 `123456`）

| 账号 | 角色 | 说明 |
| --- | --- | --- |
| `admin` | 系统管理员 | 全部菜单与数据权限 |
| `hr` | 人事专员 | 考勤、公告、人事备案 |
| `devlead` | 研发总监 | 部门经理，审批本部门单据 |
| `zhangwei` | 普通员工 | 仅本人数据 |

> 用 `admin` 登录可在「待我审批」看到 4 条待终审报销单，在「我的申请」看到 4 种状态的申请单，便于完整演示审批流。

---

## 五、生产部署

详见 [docs/部署手册.md](docs/部署手册.md)，提供三种方式：

1. **Docker Compose 一键部署**（MySQL 8 + 后端 + Nginx 前端）
2. **手动部署**（MySQL 初始化 → `java -jar` → `npm run build` → Nginx）
3. **前后端分离独立部署**（前端 CDN / 后端多实例 + 统一 `/api` 网关）

```bash
# 方式一：docker compose up -d --build
# 方式二：
mysql -uroot -p -e "CREATE DATABASE yunshu_oa DEFAULT CHARACTER SET utf8mb4;"
mysql -uroot -p yunshu_oa < sql/schema-mysql.sql
mysql -uroot -p yunshu_oa < sql/data.sql
SPRING_PROFILES_ACTIVE=prod MYSQL_PASSWORD=xxx java -jar oa-server/target/oa-server-1.0.0.jar
cd oa-web && npm ci && npm run build   # dist/ 交给 Nginx
```

---

## 六、接口概览

全部接口统一前缀 `/api`，除 `/api/health` 与 `/api/auth/login` 外均需 `Authorization: Bearer <token>`。完整清单见 [docs/接口清单.md](docs/接口清单.md)。

| 分组 | 主要端点 |
| --- | --- |
| 认证 | `POST /auth/login` · `GET /auth/info` · `PUT /auth/password` |
| 工作台 | `GET /dashboard/overview` |
| 组织 | `GET /org/depts|posts|roles|menus|users` · `POST /org/users|depts` · `PUT /org/role-menus` |
| 考勤 | `GET /attendance/today|records|summary` · `POST /attendance/checkin` |
| 审批 | `GET /approvals/defs|instances|todo|instances/{id}` · `POST /approvals/instances|{id}/approve|{id}/reject|{id}/withdraw` |
| 公告 | `GET /notices|notices/unread|notices/{id}` · `POST /notices` |
| 云文档 | `GET /docs/folders|files|files/{id}/download` · `POST /docs/files/upload` |
| 任务 | `GET /work/projects|board|tasks` · `POST /work/tasks` · `PUT /work/tasks/{id}/status` |
| 日程 | `GET /calendar/rooms|events` · `POST /calendar/events` |
| 审计 | `GET /logs` |

---

## 七、适合作为毕业设计的点

- **真实业务闭环**：审批流引擎（节点配置 / 角色解析 / 推进 / 驳回 / 撤回 / 留痕）而非简单状态字段
- **完整权限模型**：菜单 + 按钮 + 数据范围三层，前端路由与后端接口双重控制
- **工程化**：Maven Wrapper、多 profile、MyBatis XML、AOP 审计、统一响应/异常、分页封装、Docker Compose、Nginx 反向代理
- **可演示**：内置 H2 演示库与多角色账号，无需外部中间件即可完整走通全部功能

---

## License

MIT License — 详见 [LICENSE](LICENSE)。
