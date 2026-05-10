# 基础镜像
FROM openjdk:8-jre-slim

# 作者
MAINTAINER luminacampus

# 配置
ENV PARAMS=""

# 时区
ENV TZ=PRC
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 添加应用
ADD target/graphrag-assistant-app.jar /graphrag-assistant-app.jar

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /graphrag-assistant-app.jar $PARAMS"]