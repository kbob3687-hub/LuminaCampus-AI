from typing import TypedDict
from langgraph.graph import StateGraph, START, END
from langgraph.prebuilt import ToolNode, tools_condition
from langchain_openai import ChatOpenAI
from langchain_core.messages import SystemMessage, trim_messages
import os

from tools.neo4j_tool import search_knowledge_graph, search_documents, calculator

SYSTEM_PROMPT = (
    "你是一个智能学习助手。你可以使用以下工具来帮助回答问题：\n"
    "- search_knowledge_graph: 搜索知识图谱，查找概念之间的关系\n"
    "- search_documents: 搜索已上传的文档内容\n"
    "- calculator: 计算数学表达式\n\n"
    "根据用户的问题，自主决定是否需要调用工具，以及调用哪个工具。"
    "如果不需要工具就能回答，直接回答即可。"
)

tools = [search_knowledge_graph, search_documents, calculator]


class AgentState(TypedDict):
    messages: list
    sources: list


def _build_llm():
    return ChatOpenAI(
        model=os.getenv("OPENAI_MODEL", "deepseek-chat"),
        api_key=os.getenv("OPENAI_API_KEY"),
        base_url=os.getenv("OPENAI_BASE_URL"),
        temperature=0.3,
    ).bind_tools(tools)


async def agent_node(state: AgentState):
    """Agent 节点：LLM 自主决定是否调用工具"""
    llm = _build_llm()
    messages = state["messages"]

    # 确保有 system prompt
    if not messages or not isinstance(messages[0], SystemMessage):
        messages = [SystemMessage(content=SYSTEM_PROMPT)] + messages

    # 裁剪消息历史，防止超出上下文窗口
    trimmed = trim_messages(
        messages,
        max_tokens=4000,
        strategy="last",
        token_counter=len,  # 简化：按消息条数计数
        start_on="human",
    )

    response = await llm.ainvoke(trimmed)
    return {"messages": [response]}


def build_graph():
    """构建 LangGraph：Agent ↔ ToolNode（ReAct 循环）"""
    g = StateGraph(AgentState)

    g.add_node("agent", agent_node)
    g.add_node("tools", ToolNode(tools))

    g.add_edge(START, "agent")
    g.add_conditional_edges("agent", tools_condition)
    g.add_edge("tools", "agent")

    return g.compile()
