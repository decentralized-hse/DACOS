FROM python:3.8.5-alpine

RUN pip install --upgrade pip

# Install postgres client
RUN apk add --update --no-cache postgresql-client

# To fix psycopg2 problem
RUN apk add --no-cache --virtual .build-deps \
    gcc \
    python3-dev \
    musl-dev \
    postgresql-dev \
    && pip install --no-cache-dir psycopg2 \
    && apk del --no-cache .build-deps


ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1


COPY ./requirements.txt .

RUN apk add --no-cache --virtual .pynacl_deps build-base python3-dev libffi-dev

RUN pip install -r requirements.txt

COPY ./server /app

WORKDIR /app

COPY ./entrypoint_docker.sh /
ENTRYPOINT ["sh", "/entrypoint_docker.sh"]