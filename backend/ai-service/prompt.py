def build_prompt(role: str, difficulty: str, count: int):
    return f"""
You are an expert interview question generator.

Generate {count} interview questions for the role: {role}
Difficulty level: {difficulty}

Rules:
- Mix conceptual and practical questions
- Avoid repetition
- Keep questions short and clear
- Focus on real interview scenarios

Return ONLY the questions as a list.
"""