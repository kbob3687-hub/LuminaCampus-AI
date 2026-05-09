from fastapi import FastAPI, UploadFile, File, Form
from fastapi.responses import StreamingResponse
from sse_starlette.sse import EventSourceResponse
from dotenv import load_dotenv
import asyncio
import os

load_dotenv()

from agents.graph import build_graph

app = FastAPI(title="GraphRAG 学习助手", version="0.1.0")
graph = build_graph()


@app.get("/health")
async def health():
    return {"status": "ok"}


@app.post("/api/chat")
async def chat(
    question: str = Form(...),
    subject: str = Form(default=""),
    doc_id: str = Form(default=""),
):
    """聊天入口 — 根据 subject 或 doc_id 路由到不同 Agent"""
    state = {
        "question": question,
        "subject": subject,
        "doc_id": doc_id,
        "answer": "",
        "sources": [],
    }

    async def event_generator():
        result = await graph.ainvoke(state)
        answer = result.get("answer", "")
        sources = result.get("sources", [])
        # 一次性返回（后续可改为逐 token 流式）
        yield {"event": "message", "data": answer}
        if sources:
            yield {"event": "sources", "data": str(sources)}
        yield {"event": "done", "data": ""}

    return EventSourceResponse(event_generator())


@app.post("/api/upload")
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
