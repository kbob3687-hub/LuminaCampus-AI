"""Neo4j 客户端 — 驱动管理 + 图操作"""
from neo4j import AsyncGraphDatabase
import os

_driver = None


def get_neo4j_driver():
    global _driver
    if _driver is None:
        uri = os.getenv("NEO4J_URI", "bolt://localhost:7687")
        user = os.getenv("NEO4J_USER", "neo4j")
        password = os.getenv("NEO4J_PASSWORD", "12345678")
        _driver = AsyncGraphDatabase.driver(uri, auth=(user, password))
    return _driver


async def close_driver():
    global _driver
    if _driver:
        await _driver.close()
        _driver = None


async def query_entity(keyword: str) -> list[dict]:
    """按关键词检索实体及关系"""
    driver = get_neo4j_driver()
    cypher = """
    MATCH (n)-[r]->(m)
    WHERE n.name CONTAINS $keyword OR m.name CONTAINS $keyword
    RETURN n.name AS source, type(r) AS rel, m.name AS target
    LIMIT 50
    """
    async with driver.session() as session:
        result = await session.run(cypher, keyword=keyword)
        records = []
        async for record in result:
            records.append({
                "source": record["source"],
                "relation": record["rel"],
                "target": record["target"],
            })
        return records


async def create_entity(name: str, label: str = "Concept"):
    """创建实体节点"""
    driver = get_neo4j_driver()
    async with driver.session() as session:
        await session.run(
            f"MERGE (n:{label} {{name: $name}})",
            name=name,
        )


async def create_relation(source: str, target: str, rel_type: str):
    """创建关系"""
    driver = get_neo4j_driver()
    async with driver.session() as session:
        await session.run(
            f"MATCH (a {{name: $src}}), (b {{name: $tgt}}) MERGE (a)-[r:{rel_type}]->(b)",
            src=source,
            tgt=target,
        )
