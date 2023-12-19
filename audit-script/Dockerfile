FROM python:3.12-alpine

COPY requirements.txt /tmp/requirements.txt

RUN pip install -r /tmp/requirements.txt -i https://pypi.tuna.tsinghua.edu.cn/simple \
    && rm -rf /tmp/requirements.txt

COPY . /app
WORKDIR /app

CMD ["python", "run.py"]
