## 说明

此处的代码为OSS服务的辅助脚本，针对`minio-javaweb-autoscript`存储桶监听`ObjectCreated:PutObject`事件，当有新的文件上传时，会触发此事件，
中间件判断文件是否为图片，并进行自动转码等操作。

## 依赖

- Python 3.6+
- RabbitMQ
- MinIO

## 部署

需要创建外部网络`oss-network`，命令如下：

```bash
docker network create oss-network
```

