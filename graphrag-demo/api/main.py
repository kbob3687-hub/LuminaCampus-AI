from fastapi import FastAPI, UploadFile, File, Form, Depends, HTTPException, Request
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from sse_starlette.sse import EventSourceResponse
from langchain_core.messages import HumanMessage, AIMessage
from collections import defaultdict
from dotenv import load_dotenv
import json
import time
import os

load_dotenv()

from agents.graph import build_graph

app = FastAPI(title="GraphRAG 学习助手", version="0.1.0")
graph = build_graph()

# ── Token 认证 ──────────────────────────────────────────────
API_TOKEN = os.getenv("API_TOKEN", "")

security = HTTPBearer()


async def verify_token(
    credentials: HTTPAuthorizationCredentials = Depends(security),
):
    if not API_TOKEN:
        return  # 未配置 Token 则跳过（开发模式）
    if credentials.credentials != API_TOKEN:
        raise HTTPException(status_code=401, detail="Invalid token")


# ── IP 限流（内存计数器）────────────────────────────────────
request_counts: dict[str, list[float]] = defaultdict(list)
RATE_LIMIT = int(os.getenv("RATE_LIMIT_PER_MINUTE", "10"))


async def check_rate_limit(request: Request):
    client_ip = request.client.host
    now = time.time()
    # 清理 1 分钟前的记录
    request_counts[client_ip] = [
        t for t in request_counts[client_ip] if now - t < 60
    ]
    if len(request_counts[client_ip]) >= RATE_LIMIT:
        raise HTTPException(status_code=429, detail="Rate limit exceeded")
    request_counts[client_ip].append(now)


# ── 路由 ────────────────────────────────────────────────────
@app.get("/health")
async def health():
    return {"status": "ok"}


@app.post(
    "/api/chat",
    dependencies=[Depends(verify_token), Depends(check_rate_limit)],
)
async def chat(
    question: str = Form(...),
    messages: str = Form(default="[]"),
):
    """聊天入口 — LLM 自主决定调用哪些工具（Function Calling）"""
    history = json.loads(messages)
    msg_list = []
    for m in history:
        if m.get("role") == "user":
            msg_list.append(HumanMessage(content=m["content"]))
        elif m.get("role") == "assistant":
            msg_list.append(AIMessage(content=m["content"]))
    msg_list.append(HumanMessage(content=question))

    async def event_generator():
        result = await graph.ainvoke({"messages": msg_list, "sources": []})

        answer = ""
        for msg in reversed(result["messages"]):
            if isinstance(msg, AIMessage) and msg.content:
                answer = msg.content
                break

        sources = result.get("sources", [])
        yield {"event": "message", "data": answer}
        if sources:
            yield {"event": "sources", "data": str(sources)}
        yield {"event": "done", "data": ""}

    return EventSourceResponse(event_generator())


@app.post(
    "/api/upload",
    dependencies=[Depends(verify_token), Depends(check_rate_limit)],
)
async def upload_doc(file: UploadFile = File(...), subject: str = Form(default="")):
    """文档上传入口 — 保存文件并触发 RAG 构建"""
    save_dir = os.path.join(os.path.dirname(__file__), "..", "data", "uploads")
    os.makedirs(save_dir, exist_ok=True)
    file_path = os.path.join(save_dir, file.filename)
    with open(file_path, "wb") as f:
        content = await file.read()
        f.write(content)
    return {"msg": "上传成功", "file": file.filename, "subject": subject}


if __name__ == "__main__":
    import uvicorn
    uvicorn.run("api.main:app", host="0.0.0.0", port=8000, reload=True)
