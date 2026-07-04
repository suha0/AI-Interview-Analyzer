from fastapi import FastAPI
from pydantic import BaseModel
from typing import List
import requests
import re

app = FastAPI()

# =========================
# ANSWER ANALYSIS
# =========================

class AnswerAnalysisRequest(BaseModel):
    question: str
    answer: str
    targetRole: str | None = None


@app.post("/api/answers/analyze")
def analyze_answer(request: AnswerAnalysisRequest):

    answer_length = len(request.answer.strip())

    if answer_length < 10:
        return {
            "score": 5,
            "feedback": "Answer is too short to evaluate."
        }

    prompt = f"""
You are an interview evaluator.

Question:
{request.question}

Answer:
{request.answer}

Role:
{request.targetRole}

Return ONLY in this format:

Score: <0-100>

Feedback:
2-3 short sentences explaining strengths and weaknesses.

Keep response under 100 words.
"""

    try:

        response = requests.post(
            "http://127.0.0.1:11434/api/generate",
            json={
                "model": "phi3",
                "prompt": prompt,
                "stream": False,
                "options": {
                    "num_predict": 150
                }
            },
            timeout=300
        )

        response.raise_for_status()

        data = response.json()

        print("OLLAMA RESPONSE:")
        print(data)

        feedback_text = data.get("response", "").strip()

        score = 0

        match = re.search(
            r"score\s*[:\-]?\s*(\d+)",
            feedback_text,
            re.IGNORECASE
        )

        if match:
            parsed_score = int(match.group(1))

            if 0 <= parsed_score <= 100:
                score = parsed_score

        # fallback scoring if model doesn't return score
        if score == 0:

            if answer_length > 400:
                score = 90
            elif answer_length > 250:
                score = 80
            elif answer_length > 150:
                score = 70
            elif answer_length > 80:
                score = 60
            else:
                score = 40

        return {
            "score": score,
            "feedback": feedback_text
        }

    except Exception as e:

        return {
            "score": 0,
            "feedback": f"Error analyzing answer: {str(e)}"
        }


# =========================
# QUESTION GENERATION
# =========================

class QuestionGenerationRequest(BaseModel):
    targetRole: str
    difficulty: str
    count: int


class QuestionGenerationResponse(BaseModel):
    questions: List[str]


@app.post("/generate-questions")
def generate_questions(request: QuestionGenerationRequest):

    prompt = f"""
You are an expert technical interviewer.

Generate EXACTLY {request.count} interview questions for:

Role: {request.targetRole}

Difficulty: {request.difficulty}

STRICT RULES:

- Return ONLY questions.
- One question per line.
- No numbering.
- No bullet points.
- No headings.
- No introductions.
- No explanations.
- No company questions.
- No HR questions.
- No personal questions.
- Questions must be technical.
- Questions must be specific to the role.

Example:

What is dependency injection in Spring Boot?
How does Java garbage collection work?
What is the difference between HashMap and ConcurrentHashMap?
"""

    try:

        response = requests.post(
            "http://127.0.0.1:11434/api/generate",
            json={
                "model": "phi3",
                "prompt": prompt,
                "stream": False,
                "options": {
                    "num_predict": 300
                }
            },
            timeout=300
        )

        response.raise_for_status()

        data = response.json()

        text = data.get("response", "").strip()

        questions = []

        banned_phrases = [
            "interview questions",
            "job title",
            "department",
            "company",
            "tell me about yourself",
            "why should we hire you",
            "brand identity",
            "messaging",
            "role in the company"
        ]

        for line in text.split("\n"):

            line = line.strip()

            if not line:
                continue

            line = line.lstrip("0123456789.-• ")

            if len(line) < 10:
                continue

            if any(
                bad in line.lower()
                for bad in banned_phrases
            ):
                continue

            if line not in questions:
                questions.append(line)

        role = request.targetRole.lower()

        if len(questions) < request.count:

            if "java" in role:

                fallback = [
                    "What is the difference between HashMap and ConcurrentHashMap?",
                    "Explain the JVM memory model.",
                    "What is dependency injection in Spring Boot?",
                    "How does garbage collection work in Java?",
                    "What is the difference between abstract class and interface?",
                    "What are checked and unchecked exceptions?",
                    "Explain Java Streams.",
                    "How does multithreading work in Java?",
                    "What is the volatile keyword?",
                    "How do transactions work in Spring Boot?"
                ]

            elif "python" in role:

                fallback = [
                    "What is the difference between a list and a tuple?",
                    "Explain Python decorators.",
                    "What is the Global Interpreter Lock?",
                    "How does exception handling work in Python?",
                    "What are generators?",
                    "Explain list comprehensions.",
                    "What are context managers?",
                    "How does multiprocessing differ from threading?",
                    "What are virtual environments?",
                    "Explain Python memory management."
                ]

            else:

                fallback = [
                    f"What are the core responsibilities of a {request.targetRole}?",
                    f"What tools and technologies are commonly used by a {request.targetRole}?",
                    f"Describe a challenging project you completed as a {request.targetRole}.",
                    f"How do you troubleshoot technical issues in your work?",
                    f"What best practices do you follow in your profession?"
                ]

            for q in fallback:
                if q not in questions:
                    questions.append(q)

        return {
            "questions": questions[:request.count]
        }

    except Exception as e:

        return {
            "questions": [
                f"Question generation failed: {str(e)}"
            ]
        }

# =========================
# FOLLOW-UP QUESTION
# =========================

class FollowUpRequest(BaseModel):
    question: str
    answer: str
    targetRole: str | None = None


@app.post("/generate-followup")
def generate_followup(request: FollowUpRequest):

    answer_length = len(request.answer.strip())

    if answer_length < 5:
        return {
            "followUpQuestion":
                "Can you explain your answer in more detail?"
        }

    prompt = f"""
You are a senior technical interviewer.

Original Question:
{request.question}

Candidate Answer:
{request.answer}

Role:
{request.targetRole}

Generate ONE follow-up interview question.

Rules:
- Must be related to the candidate answer.
- Must test deeper technical understanding.
- Must be concise.
- Return ONLY the question.
- No numbering.
- No explanation.
- No headings.
"""

    try:

        response = requests.post(
            "http://127.0.0.1:11434/api/generate",
            json={
                "model": "phi3",
                "prompt": prompt,
                "stream": False,
                "options": {
                    "num_predict": 80
                }
            },
            timeout=300
        )

        response.raise_for_status()

        data = response.json()

        followup = data.get(
            "response",
            ""
        ).strip()

        followup = followup.replace("\n", " ")

        if (
            not followup
            or len(followup) < 10
        ):
            followup = (
                "Can you explain that concept in more detail?"
            )

        return {
            "followUpQuestion": followup
        }

    except Exception as e:

        return {
            "followUpQuestion":
                f"Failed to generate follow-up question: {str(e)}"
        }

# =========================
# HEALTH CHECK
# =========================

@app.get("/")
def root():
    return {
        "status": "AI Interview Service Running"
    }