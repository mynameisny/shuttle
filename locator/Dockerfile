# 该镜像需要依赖的基础镜像
FROM openjdk:8

# 指定维护者的名字
MAINTAINER Tony Joseph

# 将当前目录下的jar包复制到docker容器的/目录下
ADD ./shuttle-locator-1.0.0.jar /shuttle-locator-1.0.0.jar

# 声明服务运行的端口
EXPOSE 7777

# 指定docker容器启动时运行jar包
ENTRYPOINT ["java", "-jar", "/shuttle-locator-1.0.0.jar"]
