## Audit Script

通过该脚本，可以自动审核评论，计算菜品和食堂得分。

### 使用方法

1. 编译Docker

```bash
docker build -t canteen_audit-script .
```

2. 运行Docker

```bash
docker run --rm --name=canteen_audit-script_autorun canteen_audit-script
```

或者：本地运行

```bash
cd /path/to/audit-script
python3 audit.py
```

### 说明

- 该脚本会自动读取`lockfile.lock`文件，如果该文件不存在，则会自动创建一个空文件。
- `lockfile.lock`用于对脚本的运行进行加锁，防止多个脚本同时运行。