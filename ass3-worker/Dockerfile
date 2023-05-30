FROM python:3-slim

WORKDIR /app

COPY worker.py worker.py

RUN pip install redis haversine pika

ENTRYPOINT ["python","-u", "worker.py"]


