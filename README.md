<div align="center">

# LuminaCampus-AI

**基于 LangGraph + Neo4j 知识图谱的智能学习问答系统**

预置学科图谱 · 文档 RAG 问答 · Function Calling · 多轮对话 · 流式输出

![GraphRAG](https://img.shields.io/badge/GraphRAG-Learning_Assistant-blue)
![LangGraph](https://img.shields.io/badge/LangGraph-Multi--Agent-purple)
![Neo4j](https://img.shields.io/badge/Neo4j-Knowledge_Graph-green)
![License](https://img.shields.io/badge/License-MIT-yellow)

</div>

---

## 这是什么

一个面向大学生的开源学习辅助工具。

你可以把它理解成一个「会查资料的 AI 助教」—— 它不靠大模型瞎猜，而是真的去知识图谱和你的文档里找答案，然后告诉你依据是什么。

> **问学科问题** → 查 Neo4j 知识图谱 → 基于实体关系精准回答
>
> **问上传资料** → 检索文档内容 → 基于原文片段回答
>
> **问数学计算** → 调用计算器工具 → 给出准确结果

以上三条路径不需要你手动选择，AI 会自己判断该走哪条路（Function Calling）。

<!-- 运行截图后续补充 -->
<!-- ![截图占位](docs/screenshots/demo.gif) -->

---

## 系统架构

```
                    ┌──────────────────────────────────┐
                    │        Vue 3 + Element Plus       │
                    │   登录 · 学科选择 · 聊天 · 上传     │
                    └────────────────┬─────────────────┘
                                     │ HTTP / SSE
                    ┌────────────────▼─────────────────┐
                    │    Java 服务 (Spring Boot 3.4)     │
                    │    用户认证 · 文档管理 · 对话记录    │
                    │           MySQL · Redis            │
                    └────────────────┬─────────────────┘
                                     │ REST
                    ┌────────────────▼─────────────────┐
                    │   Python 服务 (FastAPI + LangGraph) │
                    │                                   │
                    │   START ──▶ Agent ◀──▶ Tools ──▶ END │
                    │             (LLM 自主决策)          │
                    │                                   │
                    │   Tools: 知识图谱 · 文档检索 · 计算器 │
                    └──────────────────────────────────┘
```

---

## 技术栈

| 层 | 技术 | 说明 |
|---|---|---|
| **前端** | Vue 3 + Element Plus | 响应式 UI，流式对话展示 |
| **Java 后端** | Spring Boot 3.4 + Java 17 | DDD 架构，用户与文档管理 |
| **Python 后端** | FastAPI + LangGraph | Agent 编排，Function Calling，SSE 流式输出 |
| **知识图谱** | Neo4j 5.x | 存储学科实体与关系 |
| **数据库** | MySQL 8.x | 用户数据、文档元数据、对话记录 |
| **缓存** | Redis 6.x | 会话缓存 |
| **LLM** | DeepSeek / GPT-4o | 默认 DeepSeek，可切换（自备 Key） |

---

## 核心功能

### 学习助手

| 功能 | 说明 |
|---|---|
| 选择学科 | 预置数据结构、计算机网络、高等数学等学科图谱 |
| 上传资料 | 支持 Word / PDF / 纯文本，自动构建文档索引 |
| 智能问答 | AI 自主选择工具回答，多轮对话 + 流式输出 |
| 来源标注 | 每次回答标注数据来源（图谱关系 / 文档片段） |

### 开发者工具

| 功能 | 说明 |
|---|---|
| CLI 建图 | `python scripts/build_graph.py --subject 数据结构 --docs ./docs/` |
| 图谱可视化 | Neo4j Browser `localhost:7474` 查看实体关系 |
| Swagger 文档 | `localhost:8000/docs` 查看 Python API 接口 |

---

## 快速开始

### 环境要求

- Java 17+
- Python 3.10+
- Docker & Docker Compose
- Node.js 18+（前端开发）

### 1. 启动基础设施

```bash
cd docs/dev-ops
docker-compose -f docker-compose-environment.yml up -d
```

启动 Neo4j + MySQL + Redis。

### 2. 启动 Python Agent 服务

```bash
cd graphrag-demo

pip install -r requirements.txt

cp .env.example .env
# 编辑 .env，填入你的 LLM API Key 和 API Token

uvicorn api.main:app --reload --port 8000
```

### 3. 启动 Java 后端

```bash
mvn clean install
mvn -pl graphrag-assistant-app spring-boot:run
```

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

### 5. 构建学科知识图谱

```bash
cd graphrag-demo
python scripts/build_graph.py --subject 数据结构 --docs ./data/sample_docs/
```

---

## API 接口

Python Agent 服务启动后访问 Swagger UI：`http://localhost:8000/docs`

| 接口 | 方法 | 认证 | 说明 |
|---|---|---|---|
| `/health` | GET | 无 | 健康检查 |
| `/api/chat` | POST | Bearer Token | 聊天问答（SSE 流式） |
| `/api/upload` | POST | Bearer Token | 文档上传 |

**安全机制：**
- Token 认证：未配置 `API_TOKEN` 时为开发模式（跳过认证）
- IP 限流：默认每 IP 每分钟 10 次，可通过 `RATE_LIMIT_PER_MINUTE` 调整

---

## 项目结构

```
LuminaCampus-AI/
├── graphrag-assistant-app/            # Spring Boot 启动模块
├── graphrag-assistant-domain/         # 领域层（DDD）
├── graphrag-assistant-trigger/        # 触发器层（Controller）
├── graphrag-assistant-infrastructure/ # 基础设施层
├── graphrag-assistant-types/          # 通用类型定义
│
├── graphrag-demo/                     # Python Agent 服务
│   ├── api/main.py                    # FastAPI 入口（认证 + 限流）
│   ├── agents/graph.py                # LangGraph 图（Agent ↔ Tools）
│   ├── tools/neo4j_tool.py            # Function Calling 工具定义
│   ├── graphrag/
│   │   ├── neo4j_client.py            # Neo4j 客户端
│   │   └── entity_extractor.py        # LLM 实体关系抽取
│   └── scripts/build_graph.py         # CLI 建图脚本
│
├── frontend/                          # Vue 3 前端
├── docs/dev-ops/                      # Docker Compose 部署配置
└── pom.xml                            # Maven 父工程
```

---

## 数据流

**学科问答（GraphRAG）：**

```
管理员 CLI 脚本 → LLM 抽取实体关系 → 写入 Neo4j
用户提问 → Agent 自主调用 search_knowledge_graph → 查图谱 → LLM 生成回答
```

**文档问答（RAG）：**

```
用户上传文档 → 文件存储 + 索引构建
用户提问 → Agent 自主调用 search_documents → 检索文档 → LLM 生成回答
```

**数学计算：**

```
用户提问 → Agent 自主调用 calculator → 安全计算 → 返回结果
```

---

## 本地开发端口

| 服务 | 端口 |
|---|---|
| Java 后端 | 8091 |
| Python Agent | 8000 |
| Neo4j Browser | 7474 |
| Neo4j Bolt | 7687 |
| MySQL | 13306 |
| Redis | 16379 |

---

## 开发进度

| 状态 | 内容 |
|---|---|
| 已完成 | 基础环境搭建 — Docker Compose、Spring Boot、Python 骨架 |
| 已完成 | Python Agent 核心 — LangGraph 编排、GraphRAG、RAG、SSE 流式 |
| 已完成 | Java 业务层 — 用户认证、文档管理、对话记录 |
| 已完成 | 安全加固 & Function Calling — Token 认证、IP 限流、LLM 自主工具调用 |
| 已完成 | 前端界面 — Vue 3 聊天界面、上传入口 |

---

## 后续规划

- 知识图谱可视化前端
- 管理后台（替代 CLI 建图）
- 对话导出
- 移动端适配

> 这是一个 MVP 版本，核心链路已跑通，更多功能正在路上。欢迎 Star 关注更新。

---

## License

MIT

---

<div align="center">

**Built with LangGraph + Neo4j + Spring Boot + FastAPI**

</div>
