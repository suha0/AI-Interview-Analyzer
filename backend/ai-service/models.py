from pydantic import BaseModel
from typing import List

class QuestionRequest(BaseModel):
    targetRole: str
    difficulty: str
    count: int

class QuestionResponse(BaseModel):
    questions: List[str]