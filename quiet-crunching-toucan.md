# GraphRAG 学习助手

## Context

大二学生做一个开源项目放 GitHub，用于学习实践和简历加分。核心卖点：Multi-Agent（LangGraph）+ GraphRAG + Neo4j。预置学科知识图谱供学习路径推荐，用户也可上传自有资料进行 RAG 问答。

**约束：** 所有开发在本地完成，不连接公司任何服务器。

---

## 架构设计

```
用户浏览器（Vue 3 + Element Plus）
    │
    ▼
┌─────────────────────────────────────────┐
│  Java 服务 (Spring Boot 3.4)            │
│  用户登录 + 文档元数据 + 对话记录 + 日志  │
│  MySQL + Redis                          │
└──────────────────┬──────────────────────┘
                   │ HTTP
┌──────────────────▼──────────────────────┐
│  Python 服务 (FastAPI + LangGraph)       │
│  ┌──────────────────────────────────┐   │
│  │  选择学科 → Query Agent → Neo4j   │   │
│  │  上传资料 → RAG Agent → 向量检索  │   │
│  │  LangGraph 编排路由               │   │
│  └──────────────────────────────────┘   │
│  Neo4j（知识图谱）                       │
└─────────────────────────────────────────┘

开发者视角（仅内部）：
  Neo4j Browser (localhost:7474) — 查看图谱
  CLI 脚本 build_graph.py — 管理员建图
```

---

## 技术选型

| 组件 | 技术 | 版本 |
|---|---|---|
| Java 后端 | Spring Boot | 3.4 + Java 17 |
| Python 后端 | FastAPI | latest |
| Agent 框架 | LangGraph | latest |
| 图数据库 | Neo4j | 5.x |
| 关系数据库 | MySQL | 8.x |
| 缓存 | Redis | 6.x |
| 前端 | Vue 3 + Element Plus | Vue 3.4+ |
| LLM（默认） | DeepSeek | API |
| LLM（可选） | GPT-4o | 用户自备 API Key |
| 部署 | Docker Compose | - |

---

## 核心功能

### 用户端
1. **选择学科** — 点击标签选择预置学科（数据结构/计算机网络/高等数学）
2. **上传资料** — 支持 Word / PDF / 纯文本
3. **聊天问答** — 多轮对话 + 流式输出 + 标注来源
4. **对话记录** — 登录后保存历史对话，每个账号独立

### 管理端（开发者）
5. **CLI 建图** — `python build_graph.py --subject 数据结构 --docs ./docs/`
6. **Neo4j Browser** — 可视化查看和调试图谱

### 用户登录
7. **用户名密码登录** — 简单注册/登录

### 可观测性
8. **查询日志** — 记录用户提问、Agent 执行耗时、LLM token 消耗

---

## 产品交互流程

```
1. 用户打开网页 → 登录/注册
2. 主界面：
   ┌──────────────────────────────┐
   │  [数据结构] [计算机网络] [高等数学]  ← 学科标签
   │  [上传我的资料]                      ← 上传入口
   │                              │
   │  ┌──────────────────────┐   │
   │  │ 对话区域（流式输出）   │   │
   │  └──────────────────────┘   │
   │                              │
   │  ┌──────────────────────┐   │
   │  │ 输入问题...     [发送] │   │
   │  └──────────────────────┘   │
   └──────────────────────────────┘

3. 选学科 → 提问 → 基于知识图谱回答（含来源）
4. 上传资料 → 提问 → 基于普通 RAG 回答（含来源）
```

---

## 数据流

**预置学科：**
```
管理员跑 CLI 脚本 → LLM 抽取实体关系 → 写入 Neo4j
用户选学科提问 → LangGraph 路由 → GraphRAG 查询 Neo4j → LLM 生成回答
```

**用户上传：**
```
用户上传文档 → 存储文件 + 记录元数据到 MySQL
用户提问 → 普通 RAG（向量检索 + LLM 回答）
```

---

## 实施计划

### Phase 1: 基础环境 (Day 1)

1. docker-compose 添加 Neo4j 服务
2. 升级 DDD 脚手架 Java 17 + Spring Boot 3.4
3. 创建 Python 项目骨架 (`graphrag-demo/`)
4. 安装 LangGraph + FastAPI + Neo4j 依赖

### Phase 2: Python Agent 服务 — 核心 (Day 2-4)

1. FastAPI 入口 + 文档上传 API
2. LangGraph 图定义（Router → Query/Upload Agent）
3. Query Agent — GraphRAG 查 Neo4j + LLM 生成回答
4. Upload Agent — 普通 RAG（文档切片 + 向量检索 + LLM）
5. 实体抽取逻辑 — LLM 从文档抽取实体和关系
6. Neo4j 图建模 + Cypher 查询
7. CLI 脚本 `build_graph.py` — 管理员建图
8. 流式输出（SSE）

### Phase 3: Java 业务层 (Day 5-6)

1. `domain/user/` — 用户注册登录（JWT）
2. `domain/document/` — 文档上传、元数据管理
3. `domain/conversation/` — 对话记录保存
4. `infrastructure/persistent/` — PO、DAO
5. `trigger/http/` — Controller
6. Java → Python 服务调用（RestTemplate）
7. MySQL 建表

### Phase 4: 前端 (Day 7-8)

1. Vue 3 + Element Plus 项目初始化
2. 登录/注册页面
3. 主界面：学科标签 + 聊天窗口
4. 流式输出对话
5. 上传资料入口
6. 对话历史侧边栏
7. 可观测性面板（查询日志）

### Phase 5: 文档 + 发布 (Day 9-10)

1. 写 README（架构图、截图、使用说明）
2. 录制演示 GIF
3. 发布 GitHub

---

## 验证方式

1. `docker compose up -d` 启动 Neo4j + MySQL + Redis
2. 启动 Python：`uvicorn api.main:app --reload`
3. 启动 Java：`mvn spring-boot:run`
4. 启动前端：`npm run dev`
5. 测试：选学科 → 提问 → 看流式回答 + 来源
6. 测试：上传文档 → 提问 → 看 RAG 回答
7. Neo4j Browser 验证图谱：`MATCH (n) RETURN n LIMIT 100`

---

## 简历亮点

> 基于 LangGraph 构建多 Agent 知识图谱学习助手，采用双通道架构：预置学科通过 GraphRAG（Neo4j）实现结构化学习路径推荐，用户上传资料通过普通 RAG 实现文档问答。支持多轮对话 + 流式输出 + 对话持久化，Java + Python 双服务架构，Docker Compose 一键部署。

---

## 预估工作量

| Phase | 时间 | 产出 |
|---|---|---|
| Phase 1 | 1 天 | 基础环境 |
| Phase 2 | 3 天 | Agent + GraphRAG 核心 |
| Phase 3 | 2 天 | Java 业务层 |
| Phase 4 | 2 天 | 前端界面 |
| Phase 5 | 1-2 天 | 文档 + 发布 |
| **总计** | **9-10 天** | 完整 MVP |

---

## 后续可加功能（不阻塞 MVP）

- 知识图谱可视化前端展示
- 管理后台页面（替代 CLI 脚本）
- 对话导出
- 移动端适配
- 多语言支持
