from langchain_openai import ChatOpenAI
from langchain_core.messages import HumanMessage, SystemMessage
import os
import json


EXTRACT_PROMPT = """你是一个知识图谱实体抽取专家。从以下文本中抽取实体和关系。

返回 JSON 格式：
{
  "entities": [{"name": "实体名", "type": "实体类型"}],
  "relations": [{"source": "源实体", "target": "目标实体", "type": "关系类型"}]
}

实体类型示例：Concept, Algorithm, DataStructure, Theorem, Protocol
关系类型示例：USES, CONTAINS, DEPENDS_ON, IMPLEMENTS, IS_A

只返回 JSON，不要其他内容。

文本：
{text}
"""


async def extract_entities(text: str) -> dict:
    """用 LLM 从文本中抽取实体和关系"""
    model = ChatOpenAI(
        model=os.getenv("OPENAI_MODEL", "deepseek-chat"),
        api_key=os.getenv("OPENAI_API_KEY"),
        base_url=os.getenv("OPENAI_BASE_URL"),
        temperature=0,
    )
    messages = [
        SystemMessage(content="你是知识图谱抽取专家，只输出 JSON。"),
        HumanMessage(content=EXTRACT_PROMPT.replace("{text}", text)),
    ]
    response = await model.ainvoke(messages)
    try:
        return json.loads(response.content)
    except json.JSONDecodeError:
        # 尝试提取 JSON 部分
        content = response.content
        start = content.find("{")
        end = content.rfind("}") + 1
        if start >= 0 and end > start:
            return json.loads(content[start:end])
        return {"entities": [], "relations": []}
