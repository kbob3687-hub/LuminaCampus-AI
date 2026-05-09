from langchain_openai import ChatOpenAI
from langchain_core.messages import HumanMessage, SystemMessage
import os


# 简易内存向量库（MVP 阶段，后续可换 FAISS / Chroma）
_doc_chunks: dict[str, list[str]] = {}


def _chunk_text(text: str, chunk_size: int = 500) -> list[str]:
    return [text[i:i + chunk_size] for i in range(0, len(text), chunk_size)]


def ingest_document(doc_id: str, text: str):
    """将文档切片存入内存"""
    _doc_chunks[doc_id] = _chunk_text(text)


async def run_upload_agent(question: str, doc_id: str) -> dict:
    """普通 RAG：从文档切片中检索相关内容，LLM 回答"""
    chunks = _doc_chunks.get(doc_id, [])
    if not chunks:
        # 尝试从磁盘读取
        file_path = os.path.join(os.path.dirname(__file__), "..", "data", "uploads", doc_id)
        if os.path.exists(file_path):
            with open(file_path, "r", encoding="utf-8", errors="ignore") as f:
                text = f.read()
            chunks = _chunk_text(text)
            _doc_chunks[doc_id] = chunks

    # 简单关键词匹配检索（MVP，后续换向量检索）
    relevant = [c for c in chunks if any(w in c for w in question.split())][:3]
    context = "\n---\n".join(relevant) if relevant else "未找到相关内容"

    model = ChatOpenAI(
        model=os.getenv("OPENAI_MODEL", "deepseek-chat"),
        api_key=os.getenv("OPENAI_API_KEY"),
        base_url=os.getenv("OPENAI_BASE_URL"),
        temperature=0.3,
    )
    messages = [
        SystemMessage(content=f"你是一个学习助手，基于以下文档内容回答问题。\n\n文档片段：\n{context}"),
        HumanMessage(content=question),
    ]
    response = await model.ainvoke(messages)
    return {"answer": response.content, "sources": relevant}
