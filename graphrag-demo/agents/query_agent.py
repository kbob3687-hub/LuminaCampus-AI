from graphrag.neo4j_client import get_neo4j_driver
from langchain_openai import ChatOpenAI
from langchain_core.messages import HumanMessage, SystemMessage
import os


async def run_query_agent(question: str, subject: str) -> dict:
    """GraphRAG 查询：从 Neo4j 检索相关实体，拼接上下文后让 LLM 回答"""
    driver = get_neo4j_driver()
    # 1. 从 Neo4j 检索与问题相关的节点和关系
    cypher = """
    MATCH (n)-[r]->(m)
    WHERE n.name CONTAINS $keyword OR m.name CONTAINS $keyword
    RETURN n.name AS source, type(r) AS rel, m.name AS target
    LIMIT 20
    """
    keyword = subject or question[:4]
    records = []
    async with driver.session() as session:
        result = await session.run(cypher, keyword=keyword)
        async for record in result:
            records.append({
                "source": record["source"],
                "relation": record["rel"],
                "target": record["target"],
            })

    # 2. 拼接上下文
    context = "\n".join(
        f"- {r['source']} --[{r['relation']}]--> {r['target']}" for r in records
    ) if records else "暂无相关知识图谱数据"

    # 3. LLM 生成回答
    model = ChatOpenAI(
        model=os.getenv("OPENAI_MODEL", "deepseek-chat"),
        api_key=os.getenv("OPENAI_API_KEY"),
        base_url=os.getenv("OPENAI_BASE_URL"),
        temperature=0.3,
    )
    messages = [
        SystemMessage(content=f"你是一个学习助手，基于以下知识图谱信息回答问题。\n\n知识图谱：\n{context}"),
        HumanMessage(content=question),
    ]
    response = await model.ainvoke(messages)
    return {"answer": response.content, "sources": records}
