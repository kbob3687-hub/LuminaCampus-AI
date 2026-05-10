<p align="center">
  <img src="https://img.shields.io/badge/GraphRAG-Learning%20Assistant-4A90D9?style=for-the-badge&logo=neo4j&logoColor=white" alt="GraphRAG"/>
  <img src="https://img.shields.io/badge/LangGraph-Multi--Agent-F5A623?style=for-the-badge&logo=langchain&logoColor=white" alt="LangGraph"/>
  <img src="https://img.shields.io/badge/Phase-3%20Complete-brightgreen?style=for-the-badge" alt="Phase 3"/>
</p>

<h1 align="center">GraphRAG 学习助手</h1>

<p align="center">
  <b>基于 LangGraph 多 Agent + Neo4j 知识图谱的智能学习问答系统</b>
</p>

<p align="center">
  预置学科知识图谱 &nbsp;|&nbsp; 用户资料 RAG 问答 &nbsp;|&nbsp; 多轮对话 &nbsp;|&nbsp; 流式输出
</p>

---

## 项目简介

GraphRAG 学习助手是一个面向大学生的开源学习工具，采用 **Java + Python 双服务架构**，融合了知识图谱与大语言模型的能力。

**核心理念：** 用知识图谱回答「结构化」的学科问题，用 RAG 回答「非结构化」的个人资料问题 —— 两条路径由 LangGraph Agent 自动路由。

> 选学科提问 → GraphRAG 查 Neo4j 图谱 → 精准回答  
> 上传资料提问 → 向量检索文档片段 → RAG 回答

---

## 系统架构

```
                        ┌──────────────────────────────────────┐
                        │         Vue 3 + Element Plus         │
                        │    登录 · 学科选择 · 聊天 · 上传       │
                        └──────────────────┬───────────────────┘
                                           │ HTTP / SSE
                        ┌──────────────────▼───────────────────┐
                        │     Java 服务 (Spring Boot 3.4)       │
                        │     用户认证 · 文档管理 · 对话记录      │
                        │         MySQL  ·  Redis               │
                        └──────────────────┬───────────────────┘
                                           │ REST
                        ┌──────────────────▼───────────────────┐
                        │     Python 服务 (FastAPI + LangGraph) │
                        │  ┌─────────────────────────────────┐ │
                        │  │         LangGraph Router         │ │
                        │  │              │                    │ │
                        │  │    ┌─────────┴──────────┐       │ │
                        │  │    ▼                    ▼       │ │
                        │  │ Query Agent      Upload Agent   │ │
                        │  │ (GraphRAG)       (Vector RAG)   │ │
                        │  │    │                    │       │ │
                        │  │    ▼                    ▼       │ │
                        │  │  Neo4j            向量检索       │ │
                        │  └─────────────────────────────────┘ │
                        └──────────────────────────────────────┘
```

---

## 技术栈

<table>
  <tr>
    <th>层</th>
    <th>技术</th>
    <th>说明</th>
  </tr>
  <tr>
    <td><b>前端</b></td>
    <td>Vue 3 + Element Plus</td>
    <td>响应式 UI，流式对话展示</td>
  </tr>
  <tr>
    <td><b>Java 后端</b></td>
    <td>Spring Boot 3.4 + Java 17</td>
    <td>DDD 架构，用户 / 文档 / 对话管理</td>
  </tr>
  <tr>
    <td><b>Python 后端</b></td>
    <td>FastAPI + LangGraph</td>
    <td>Agent 编排，RAG 推理，SSE 流式输出</td>
  </tr>
  <tr>
    <td><b>知识图谱</b></td>
    <td>Neo4j 5.x</td>
    <td>存储学科实体与关系，支持 Cypher 查询</td>
  </tr>
  <tr>
    <td><b>关系数据库</b></td>
    <td>MySQL 8.x</td>
    <td>用户数据、文档元数据、对话记录</td>
  </tr>
  <tr>
    <td><b>缓存</b></td>
    <td>Redis 6.x</td>
    <td>会话缓存、热点数据加速</td>
  </tr>
  <tr>
    <td><b>LLM</b></td>
    <td>DeepSeek / GPT-4o</td>
    <td>默认 DeepSeek，可切换 GPT-4o（自备 Key）</td>
  </tr>
  <tr>
    <td><b>部署</b></td>
    <td>Docker Compose</td>
    <td>一键启动全部基础设施</td>
  </tr>
</table>

---

## 核心功能

### 用户端

| 功能 | 说明 |
|:---|:---|
| **选择学科** | 点击标签选择预置学科（数据结构 / 计算机网络 / 高等数学） |
| **上传资料** | 支持 Word / PDF / 纯文本，自动构建 RAG 索引 |
| **聊天问答** | 多轮对话 + 流式输出 + 标注来源 |
| **对话记录** | 登录后保存历史，每个账号独立空间 |

### 开发端

| 功能 | 说明 |
|:---|:---|
| **CLI 建图** | `python build_graph.py --subject 数据结构 --docs ./docs/` |
| **Neo4j Browser** | `localhost:7474` 可视化查看和调试图谱 |
| **查询日志** | 记录用户提问、Agent 耗时、Token 消耗 |

---

## 项目结构

```
claudecodedemo/
├── claudecodedemo-app/            # Spring Boot 启动模块
├── claudecodedemo-domain/         # 领域层（DDD 聚合、实体、值对象）
├── claudecodedemo-trigger/        # 触发器层（HTTP Controller）
├── claudecodedemo-infrastructure/ # 基础设施层（持久化、外部服务）
├── claudecodedemo-types/          # 通用类型定义
├── graphrag-demo/                 # Python Agent 服务
│   ├── api/
│   │   └── main.py                # FastAPI 入口
│   ├── agents/
│   │   ├── graph.py               # LangGraph 图定义（Router）
│   │   ├── query_agent.py         # GraphRAG 查询 Agent
│   │   └── upload_agent.py        # 上传资料 RAG Agent
│   ├── graphrag/
│   │   ├── entity_extractor.py    # LLM 实体关系抽取
│   │   └── neo4j_client.py        # Neo4j 连接客户端
│   ├── tools/
│   │   └── neo4j_tool.py          # Neo4j 查询工具
│   ├── scripts/
│   │   └── build_graph.py         # CLI 建图脚本
│   └── data/                      # 上传文件存储
├── docs/
│   └── dev-ops/
│       ├── docker-compose-app.yml         # 应用容器编排
│       └── docker-compose-environment.yml # 基础设施编排
└── pom.xml                        # Maven 父工程
```

---

## 快速开始

### 环境要求

- Java 17+
- Python 3.10+
- Docker & Docker Compose
- Node.js 18+（前端开发）

### 1. 启动基础设施

```bash
# 启动 Neo4j + MySQL + Redis
cd docs/dev-ops
docker-compose -f docker-compose-environment.yml up -d
```

### 2. 启动 Python Agent 服务

```bash
cd graphrag-demo

# 安装依赖
pip install -r requirements.txt

# 配置环境变量
cp .env.example .env
# 编辑 .env 填入 LLM API Key

# 启动服务
uvicorn api.main:app --reload --port 8000
```

### 3. 启动 Java 后端

```bash
# 在项目根目录
mvn clean install
mvn -pl claudecodedemo-app spring-boot:run
```

### 4. 启动前端（开发模式）

```bash
cd frontend
npm install
npm run dev
```

### 5. 构建学科知识图谱

```bash
cd graphrag-demo

# 以"数据结构"为例，从 docs 目录抽取实体关系并写入 Neo4j
python scripts/build_graph.py --subject 数据结构 --docs ./data/sample_docs/
```

### 6. 验证

- 浏览器访问前端页面，选择学科 → 提问 → 查看流式回答
- 上传一份 PDF → 提问 → 查看 RAG 回答
- 打开 `http://localhost:7474`，执行 `MATCH (n) RETURN n LIMIT 100` 验证图谱

---

## 数据流

**预置学科（GraphRAG 通道）：**

```
管理员 CLI 脚本 → LLM 抽取实体关系 → 写入 Neo4j
用户选择学科提问 → LangGraph 路由 → GraphRAG 查询 Neo4j → LLM 生成回答 + 来源标注
```

**用户上传（RAG 通道）：**

```
用户上传文档 → 文件存储 + MySQL 元数据记录
用户提问 → LangGraph 路由 → 向量检索文档片段 → LLM 生成回答 + 来源标注
```

---

## API 文档

Python Agent 服务启动后访问 Swagger UI：

```
http://localhost:8000/docs
```

| 接口 | 方法 | 说明 |
|:---|:---|:---|
| `/health` | GET | 健康检查 |
| `/api/chat` | POST | 聊天问答（SSE 流式） |
| `/api/upload` | POST | 文档上传 |

---

## 开发进度

| 阶段 | 状态 | 内容 |
|:---|:---:|:---|
| Phase 1 — 基础环境搭建 | :white_check_mark: | Docker Compose、Spring Boot 升级、Python 项目骨架 |
| Phase 2 — Python Agent 核心 | :white_check_mark: | LangGraph 编排、GraphRAG、RAG、SSE 流式 |
| Phase 3 — Java 业务层 | :white_check_mark: | 用户认证、文档管理、对话记录、REST 调用 |
| Phase 4 — 前端界面 | :construction: | Vue 3 聊天界面、上传入口、对话历史 |
| Phase 5 — 文档与发布 | :construction: | README、演示 GIF、GitHub 发布 |

---

## 后续规划

- [ ] 知识图谱可视化前端展示
- [ ] 管理后台页面（替代 CLI 建图脚本）
- [ ] 对话导出功能
- [ ] 移动端适配
- [ ] 多语言支持

---

## 本地开发备注

所有开发在本地完成，不连接任何外部服务器。各服务端口：

| 服务 | 端口 |
|:---|:---|
| Java 后端 | 8091 |
| Python Agent | 8000 |
| Neo4j Browser | 7474 |
| Neo4j Bolt | 7687 |
| MySQL | 13306 |
| Redis | 16379 |
| Redis Commander | 8081 |
| phpMyAdmin | 8899 |

---

## License

MIT

---

<p align="center">
  <sub>Built with LangGraph + Neo4j + Spring Boot + FastAPI</sub>
</p>
