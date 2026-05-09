from typing import TypedDict, Annotated, List
from langgraph.graph import StateGraph, END


class AgentState(TypedDict):
    question: str
    subject: str
    doc_id: str
    answer: str
    sources: list


def router(state: AgentState) -> str:
    """根据 subject 或 doc_id 决定走哪条路径"""
    if state.get("subject"):
        return "query_agent"
    return "upload_agent"


async def query_agent_node(state: AgentState) -> dict:
    from agents.query_agent import run_query_agent
    result = await run_query_agent(state["question"], state["subject"])
    return {"answer": result["answer"], "sources": result.get("sources", [])}


async def upload_agent_node(state: AgentState) -> dict:
    from agents.upload_agent import run_upload_agent
    result = await run_upload_agent(state["question"], state.get("doc_id", ""))
    return {"answer": result["answer"], "sources": result.get("sources", [])}


def build_graph() -> StateGraph:
    """构建 LangGraph：Router → QueryAgent / UploadAgent"""
    g = StateGraph(AgentState)
    g.add_node("query_agent", query_agent_node)
    g.add_node("upload_agent", upload_agent_node)
    g.set_conditional_entry_point(router, {"query_agent": "query_agent", "upload_agent": "upload_agent"})
    g.add_edge("query_agent", END)
    g.add_edge("upload_agent", END)
    return g.compile()
