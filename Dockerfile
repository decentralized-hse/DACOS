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

RUN pip download argon2-cffi

COPY ./requirements.txt .

RUN apk add --no-cache --virtual .pynacl_deps build-base python3-dev libffi-dev
# To fix argon2 installation
RUN apk add gcc musl-dev libffi-dev && \
    pip install -U  cffi pip setuptools && \
    pip3 install --no-cache-dir -r requirements.txt

RUN pip install -r requirements.txt

COPY ./server /app

WORKDIR /app

COPY ./entrypoint.sh /
ENTRYPOINT ["sh", "/entrypoint.sh"]