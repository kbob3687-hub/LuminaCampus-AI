"""工具层 — 向外暴露 Neo4j 操作（供 LangGraph ToolNode 使用）"""
from graphrag.neo4j_client import get_neo4j_driver, query_entity, create_entity, create_relation


async def search_knowledge(keyword: str) -> list[dict]:
    """搜索知识图谱 — 可作为 LangGraph Tool"""
    return await query_entity(keyword)


async def add_knowledge(name: str, label: str = "Concept"):
    """添加知识节点"""
    await create_entity(name, label)
