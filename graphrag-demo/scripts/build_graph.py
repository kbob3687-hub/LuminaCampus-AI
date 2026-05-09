"""
CLI 脚本：从文档中抽取实体关系，写入 Neo4j 知识图谱。

用法：
    python -m scripts.build_graph --subject 数据结构 --docs ./data/sample_docs/
"""
import argparse
import asyncio
import glob
import os
import sys

sys.path.insert(0, os.path.join(os.path.dirname(__file__), ".."))

from dotenv import load_dotenv
load_dotenv()

from graphrag.entity_extractor import extract_entities
from graphrag.neo4j_client import create_entity, create_relation


async def process_file(file_path: str) -> dict:
    """处理单个文件，返回抽取结果"""
    with open(file_path, "r", encoding="utf-8", errors="ignore") as f:
        text = f.read()
    if not text.strip():
        return {"entities": [], "relations": []}
    return await extract_entities(text)


async def build_graph(subject: str, docs_dir: str):
    """批量处理文档，构建知识图谱"""
    files = glob.glob(os.path.join(docs_dir, "*.txt")) + glob.glob(os.path.join(docs_dir, "*.md"))
    if not files:
        print(f"[WARN] 在 {docs_dir} 下未找到 .txt 或 .md 文件")
        return

    total_entities = 0
    total_relations = 0

    for fp in files:
        print(f"[INFO] 处理: {fp}")
        result = await process_file(fp)

        for ent in result.get("entities", []):
            await create_entity(ent["name"], ent.get("type", "Concept"))
            total_entities += 1

        for rel in result.get("relations", []):
            await create_relation(rel["source"], rel["target"], rel["type"].upper())
            total_relations += 1

    print(f"[DONE] 学科={subject}，写入实体 {total_entities} 个，关系 {total_relations} 条")


def main():
    parser = argparse.ArgumentParser(description="构建知识图谱")
    parser.add_argument("--subject", required=True, help="学科名称")
    parser.add_argument("--docs", required=True, help="文档目录路径")
    args = parser.parse_args()

    asyncio.run(build_graph(args.subject, args.docs))


if __name__ == "__main__":
    main()
