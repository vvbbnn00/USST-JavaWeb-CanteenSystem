import json
import os
import traceback
from minio import Minio
from PIL import Image
import io
import paho.mqtt.client as mqtt
import logging

from minio.commonconfig import CopySource

logging.basicConfig(level=logging.INFO)

if os.path.exists("./config_prod.py"):
    logging.info("Using production config")
    from config_prod import *
else:
    logging.info("Using development config")
    from config import *

# MinIO 配置
client = Minio(
    minio_server,
    access_key=minio_access_key,
    secret_key=minio_secret_key,
    secure=minio_secure
)

bucket_name = minio_bucket_name


def process_image(file_obj, file_name):
    try:
        # 使用 Pillow 读取图片
        image = Image.open(file_obj)
        base_name = os.path.splitext(file_name)[0]

        # 转换为 webp 和生成缩略图
        sizes = [(384, 384), (256, 256), (128, 128)]
        for size in sizes:
            thumb = image.copy()
            thumb.thumbnail(size)
            thumb_io = io.BytesIO()
            thumb.save(thumb_io, format="WEBP")
            thumb_name = f"{base_name}_{size[0]}.webp"
            length = thumb_io.tell()
            thumb_io.seek(0)
            client.put_object(bucket_name, thumb_name, thumb_io, length=length, content_type='image/webp')

        # 保存原图为 webp
        original_io = io.BytesIO()
        image.save(original_io, format="WEBP")
        length = original_io.tell()
        original_io.seek(0)
        client.put_object(bucket_name, f"{base_name}.webp", original_io, length=length,
                          content_type='image/webp')

    except IOError:
        traceback.print_exc()
        logging.error("Error in processing image")


def on_connect(client_, userdata, flags, rc):
    logging.info("Connected with result code " + str(rc))
    client_.subscribe(mqtt_topic)


def on_message(_, userdata, msg):
    logging.info(f"Received message from topic {msg.topic}")
    message = json.loads(msg.payload.decode())
    for record in message['Records']:
        file_name = record['s3']['object']['key']
        if file_name.endswith(".webp"):
            continue
        logging.info("Received file: %s" % file_name)
        size = record['s3']['object']['size']
        if size is not None and int(size) < 1024 * 1024 * 10:
            response = client.get_object(bucket_name, file_name)
            process_image(response, file_name)
            logging.info("Processed file: %s" % file_name)
        else:
            logging.warning("File too large: %s" % file_name)
        client.remove_object(bucket_name, file_name)
        logging.info("Removed file: %s" % file_name)


def process_image_from_mqtt(file_name):
    try:
        response = client.get_object(bucket_name, file_name)
        process_image(response, file_name)
        logging.info("Processed file: %s" % file_name)
        client.remove_object(bucket_name, file_name)
        logging.info("Removed file: %s" % file_name)
    except Exception as e:
        logging.error(f"Error processing file {file_name}: {e}")


def main():
    logging.info("Starting OSS AutoScript")
    mqtt_client = mqtt.Client()
    mqtt_client.username_pw_set(mqtt_username, mqtt_password)  # 设置用户名和密码
    mqtt_client.on_connect = on_connect
    mqtt_client.on_message = on_message

    mqtt_client.connect(mqtt_broker, mqtt_port, 60)

    # 阻塞等待消息
    mqtt_client.loop_forever()


if __name__ == "__main__":
    main()
