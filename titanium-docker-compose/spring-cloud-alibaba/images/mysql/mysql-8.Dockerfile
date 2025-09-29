FROM mysql:8.0.30

#增加nacos 文件到容器 /docker-entrypoint-initdb.d 文件夹内
ADD ../init/msyql/nacos-V2.3.2.sql /docker-entrypoint-initdb.d/nacos-mysql.sql
#修改文件权限为mysql账号可读
RUN chown -R mysql:mysql /docker-entrypoint-initdb.d/nacos-mysql.sql
#暴露3306端口
EXPOSE 3306
#执行全局命令，修改字符集
CMD ["mysqld", "--character-set-server=utf8mb4", "--collation-server=utf8mb4_unicode_ci"]
