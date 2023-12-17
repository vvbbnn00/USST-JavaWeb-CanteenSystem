FROM python:3.11-alpine

# Install dependencies
COPY requirements.txt /tmp/requirements.txt

RUN apk add --no-cache --virtual .build-deps \
    gcc \
    musl-dev \
    libffi-dev \
    openssl-dev \
    && pip install --no-cache-dir -r /tmp/requirements.txt \
    && apk del .build-deps

# Copy source code

COPY main.py /app/main.py
COPY config_prod.py /app/config_prod.py

WORKDIR /app

# Run the application
CMD ["python", "main.py"]
