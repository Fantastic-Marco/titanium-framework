# MySQL主从复制集群（两套独立环境）

此目录包含一个Docker Compose配置，用于部署两个独立的MySQL主从复制环境，每套环境包含一个主节点和一个从节点。

## 目录结构

```
mysql-replication/
├── docker-compose.yml     # Docker Compose配置文件
├── master-1/              # 第一套主节点配置
│   ├── conf/my.cnf        # 主节点1 MySQL配置文件
│   ├── data/              # 主节点1数据持久化目录
│   └── init/              # 初始化脚本目录
├── slave-1/               # 第一套从节点配置
│   ├── conf/my.cnf        # 从节点1 MySQL配置文件
│   └── data/              # 从节点1数据持久化目录
├── master-2/              # 第二套主节点配置
│   ├── conf/my.cnf        # 主节点2 MySQL配置文件
│   ├── data/              # 主节点2数据持久化目录
│   └── init/              # 初始化脚本目录
└── slave-2/               # 第二套从节点配置
    ├── conf/my.cnf        # 从节点2 MySQL配置文件
    └── data/              # 从节点2数据持久化目录
```

## 部署说明

1. 确保已安装Docker和Docker Compose
2. 在当前目录下执行以下命令启动集群：
   ```bash
   docker-compose up -d
   ```

3. 查看集群状态：
   ```bash
   docker-compose ps
   ```

## 连接信息

### 第一套主从环境
- **主节点1**: localhost:3306
- **从节点1**: localhost:3307

### 第二套主从环境
- **主节点2**: localhost:3308
- **从节点2**: localhost:3309

默认用户名: root
默认密码: rootpassword

## 配置主从复制

集群启动后，需要手动配置主从复制关系。可以通过以下步骤完成：

### 配置第一套主从复制

1. 进入主节点1容器：
   ```bash
   docker exec -it mysql-master-1 mysql -u root -p
   ```

2. 查看主节点1状态：
   ```sql
   SHOW MASTER STATUS;
   ```

3. 进入从节点1容器，配置主从复制：
   ```bash
   docker exec -it mysql-slave-1 mysql -u root -p
   ```
   
   ```sql
   CHANGE MASTER TO
   MASTER_HOST='mysql-master-1',
   MASTER_USER='repl_user',
   MASTER_PASSWORD='repl_password',
   MASTER_LOG_FILE='mysql-bin.000001',  -- 替换为实际的binlog文件名
   MASTER_LOG_POS=XXX;                  -- 替换为实际的position值
   
   START SLAVE;
   SHOW SLAVE STATUS\G
   ```

### 配置第二套主从复制

1. 进入主节点2容器：
   ```bash
   docker exec -it mysql-master-2 mysql -u root -p
   ```

2. 查看主节点2状态：
   ```sql
   SHOW MASTER STATUS;
   ```

3. 进入从节点2容器，配置主从复制：
   ```bash
   docker exec -it mysql-slave-2 mysql -u root -p
   ```
   
   ```sql
   CHANGE MASTER TO
   MASTER_HOST='mysql-master-2',
   MASTER_USER='repl_user',
   MASTER_PASSWORD='repl_password',
   MASTER_LOG_FILE='mysql-bin.000001',  -- 替换为实际的binlog文件名
   MASTER_LOG_POS=XXX;                  -- 替换为实际的position值
   
   START SLAVE;
   SHOW SLAVE STATUS\G
   ```

## 停止集群

```bash
docker-compose down
```

要删除数据卷（包括持久化数据）：
```bash
docker-compose down -v
```