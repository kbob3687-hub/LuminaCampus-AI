"""工具层 — 用 @tool 装饰器定义工具（供 LangGraph ToolNode 使用）"""
from langchain_core.tools import tool
from graphrag.neo4j_client import query_entity


@tool
async def search_knowledge_graph(keyword: str) -> str:
    """搜索知识图谱，查找实体之间的关系。适合回答概念关系、原理对比等问题。"""
    result = await query_entity(keyword)

    if not result:
        return "知识图谱中未找到相关信息"

    lines = [f"- {r['source']} --[{r['relation']}]--> {r['target']}" for r in result]
    return "\n".join(lines)


@tool
def search_documents(keyword: str) -> str:
    """搜索已上传的文档内容。适合查找具体知识点、代码示例等。"""
    from agents.upload_agent import _doc_chunks
    import os

    # 从内存 chunks 中检索
    all_chunks = []
    for doc_id, chunks in _doc_chunks.items():
        for chunk in chunks:
            if keyword in chunk:
                all_chunks.append(chunk)

    # 如果内存中没有，尝试从磁盘读取
    if not all_chunks:
        uploads_dir = os.path.join(os.path.dirname(__file__), "..", "data", "uploads")
        if os.path.isdir(uploads_dir):
            for fname in os.listdir(uploads_dir):
                fpath = os.path.join(uploads_dir, fname)
                if os.path.isfile(fpath):
                    try:
                        with open(fpath, "r", encoding="utf-8", errors="ignore") as f:
                            text = f.read()
                        if keyword in text:
                            # 返回关键词周围的上下文
                            idx = text.find(keyword)
                            start = max(0, idx - 200)
                            end = min(len(text), idx + 200)
                            all_chunks.append(text[start:end])
                    except Exception:
                        continue

    if not all_chunks:
        return "未找到相关文档内容"

    return "\n---\n".join(all_chunks[:3])


@tool
def calculator(expression: str) -> str:
    """计算数学表达式。适合计算时间复杂度、数值运算等。"""
    import ast
    import operator

    # 安全的运算符映射
    safe_ops = {
        ast.Add: operator.add,
        ast.Sub: operator.sub,
        ast.Mult: operator.mul,
        ast.Div: operator.truediv,
        ast.FloorDiv: operator.floordiv,
        ast.Mod: operator.mod,
        ast.Pow: operator.pow,
        ast.USub: operator.neg,
        ast.UAdd: operator.pos,
    }

    def _eval(node):
        if isinstance(node, ast.Expression):
            return _eval(node.body)
        elif isinstance(node, ast.Constant) and isinstance(node.value, (int, float)):
            return node.value
        elif isinstance(node, ast.BinOp):
            op_type = type(node.op)
            if op_type not in safe_ops:
                raise ValueError(f"不支持的运算: {op_type.__name__}")
            return safe_ops[op_type](_eval(node.left), _eval(node.right))
        elif isinstance(node, ast.UnaryOp):
            op_type = type(node.op)
            if op_type not in safe_ops:
                raise ValueError(f"不支持的运算: {op_type.__name__}")
            return safe_ops[op_type](_eval(node.operand))
        else:
            raise ValueError(f"不支持的表达式类型: {type(node).__name__}")

    try:
        tree = ast.parse(expression, mode="eval")
        result = _eval(tree)
        return str(result)
    except Exception as e:
        return f"计算错误: {e}"
