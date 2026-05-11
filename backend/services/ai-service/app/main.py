from __future__ import annotations

import logging
import math
import os
import re
import unicodedata
from typing import List

import spacy
from fastapi import FastAPI
from pydantic import BaseModel, Field

logger = logging.getLogger("ai-service")
logging.basicConfig(level=logging.INFO)

MODEL_NAME = os.getenv("EMBEDDING_MODEL_NAME", "sentence-transformers/all-MiniLM-L6-v2")

try:
    from sentence_transformers import SentenceTransformer, util
except Exception:  # pragma: no cover
    SentenceTransformer = None
    util = None

_nlp = spacy.blank("xx")
_embedding_model = None

if SentenceTransformer is not None:
    try:
        _embedding_model = SentenceTransformer(MODEL_NAME)
        logger.info("Loaded embedding model: %s", MODEL_NAME)
    except Exception as exc:  # pragma: no cover
        logger.warning("Could not load embedding model %s: %s", MODEL_NAME, exc)
        _embedding_model = None


class JobPayload(BaseModel):
    title: str = ""
    description: str = ""
    candidateRequirements: str = ""
    experience: str = ""
    education: str = ""
    skills: List[str] = Field(default_factory=list)


class CvPayload(BaseModel):
    fullName: str = ""
    jobTitle: str = ""
    summary: str = ""
    skills: List[str] = Field(default_factory=list)


class MatchRequest(BaseModel):
    candidateExperienceYear: str = ""
    job: JobPayload
    cv: CvPayload


class MatchResponse(BaseModel):
    semanticScore: float
    keywordScore: float
    matchedSkills: List[str]
    missingSkills: List[str]
    recommendation: str
    model: str


app = FastAPI(title="AI Matching Service", version="1.0.0")


@app.get("/health")
def health() -> dict:
    return {
        "status": "ok",
        "model": MODEL_NAME if _embedding_model is not None else "fallback-jaccard",
    }


@app.post("/match", response_model=MatchResponse)
def match(request: MatchRequest) -> MatchResponse:
    job_skills = _normalize_skill_list(request.job.skills)
    cv_skills = _normalize_skill_list(request.cv.skills)

    matched_skills = [skill for skill in job_skills if _contains_skill(cv_skills, skill)]
    missing_skills = [skill for skill in job_skills if skill not in matched_skills]

    job_text = _compose_job_text(request.job)
    cv_text = _compose_cv_text(request.cv, request.candidateExperienceYear)

    keyword_score = _keyword_overlap_score(job_text, cv_text)
    semantic_score = _semantic_score(job_text, cv_text)
    recommendation = _build_recommendation(semantic_score, keyword_score, matched_skills, missing_skills)

    return MatchResponse(
        semanticScore=round(semantic_score, 1),
        keywordScore=round(keyword_score, 1),
        matchedSkills=matched_skills,
        missingSkills=missing_skills,
        recommendation=recommendation,
        model=MODEL_NAME if _embedding_model is not None else "fallback-jaccard",
    )


def _compose_job_text(job: JobPayload) -> str:
    return " ".join(
        part for part in [
            job.title,
            job.description,
            job.candidateRequirements,
            job.experience,
            job.education,
            " ".join(job.skills),
        ] if part
    )


def _compose_cv_text(cv: CvPayload, candidate_experience_year: str) -> str:
    return " ".join(
        part for part in [
            cv.fullName,
            cv.jobTitle,
            cv.summary,
            candidate_experience_year,
            " ".join(cv.skills),
        ] if part
    )


def _semantic_score(job_text: str, cv_text: str) -> float:
    if not job_text.strip() or not cv_text.strip():
        return 0.0

    if _embedding_model is not None and util is not None:
        try:
            embeddings = _embedding_model.encode([job_text, cv_text], convert_to_tensor=True)
            similarity = float(util.cos_sim(embeddings[0], embeddings[1]).item())
            return max(0.0, min(100.0, similarity * 100.0))
        except Exception as exc:  # pragma: no cover
            logger.warning("Semantic scoring failed, fallback to keyword mode: %s", exc)

    job_terms = set(_extract_keywords(job_text))
    cv_terms = set(_extract_keywords(cv_text))
    if not job_terms or not cv_terms:
        return 0.0

    intersection = len(job_terms.intersection(cv_terms))
    union = len(job_terms.union(cv_terms))
    return (intersection / union) * 100.0 if union else 0.0


def _keyword_overlap_score(job_text: str, cv_text: str) -> float:
    job_terms = set(_extract_keywords(job_text))
    cv_terms = set(_extract_keywords(cv_text))
    if not job_terms:
        return 0.0
    overlap = len(job_terms.intersection(cv_terms))
    return min(100.0, (overlap / len(job_terms)) * 100.0)


def _extract_keywords(text: str) -> List[str]:
    normalized = _normalize_text(text)
    if not normalized:
        return []
    doc = _nlp(normalized)
    keywords = []
    for token in doc:
        value = token.text.strip()
        if len(value) < 2:
            continue
        if value.isnumeric():
            continue
        keywords.append(value)
    return list(dict.fromkeys(keywords))


def _normalize_skill_list(skills: List[str]) -> List[str]:
    result = []
    for skill in skills or []:
        normalized = _normalize_text(skill)
        if normalized:
            result.append(normalized)
    return list(dict.fromkeys(result))


def _contains_skill(cv_skills: List[str], target: str) -> bool:
    for skill in cv_skills:
        if skill == target or skill in target or target in skill:
            return True
    return False


def _normalize_text(text: str) -> str:
    if not text:
        return ""
    text = unicodedata.normalize("NFD", text)
    text = "".join(ch for ch in text if unicodedata.category(ch) != "Mn")
    text = text.lower()
    text = re.sub(r"[^a-z0-9+#.\s]", " ", text)
    text = re.sub(r"\s+", " ", text).strip()
    return text


def _build_recommendation(semantic_score: float, keyword_score: float, matched_skills: List[str], missing_skills: List[str]) -> str:
    if semantic_score >= 80 and len(matched_skills) >= max(1, len(matched_skills) + len(missing_skills) - 2):
        return "Semantic matching rat cao. Ung vien co do tuong dong tot voi JD va nen duoc uu tien shortlist."
    if semantic_score >= 65:
        return "Muc do tuong dong kha tot. Recruiter nen review them CV goc va du an lien quan."
    if len(missing_skills) > len(matched_skills):
        return "Ung vien chua khop do thieu nhieu ky nang so voi JD, du semantic score co the co diem sang."
    if keyword_score >= 60:
        return "Co do giao keyword va mo ta cong viec o muc trung binh, phu hop de xem xet bo sung."
    return "Muc do tuong dong chua cao. Nen de ung vien o nhom du phong hoac can them du lieu de danh gia."
