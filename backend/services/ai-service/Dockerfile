FROM python:3.11-slim

WORKDIR /app

ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1
ENV HF_HOME=/models/huggingface
ENV TRANSFORMERS_CACHE=/models/huggingface

COPY services/ai-service/requirements.txt ./requirements.txt

RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    gcc \
    g++ \
    make \
    && pip install --default-timeout=1000 --no-cache-dir --upgrade pip \
    && pip install --default-timeout=1000 --no-cache-dir -r requirements.txt \
    && apt-get purge -y --auto-remove build-essential gcc g++ make \
    && rm -rf /var/lib/apt/lists/*

COPY services/ai-service/app ./app

EXPOSE 8090

CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8090"]