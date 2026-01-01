# --- 第一阶段：构建 ---
FROM maven:3.9.9-jdk-17 AS build
WORKDIR /app

# 1. 优先复制依赖描述文件
COPY pom.xml .
COPY maven-settings.xml /root/.m2/settings.xml

# 2. 【关键优化】先下载依赖（利用缓存）
# 只要 pom.xml 和 settings.xml 没变，这一层就会被缓存，秒过
RUN mvn -s /root/.m2/settings.xml -B dependency:go-offline

# 3. 再复制源代码
COPY src ./src

# 4. 执行打包（跳过测试，离线模式）
# -o (offline) 并不是必须的，但如果 dependency:go-offline 成功了，理论上不需要联网
RUN mvn -s /root/.m2/settings.xml -q -DskipTests package

# --- 第二阶段：运行 ---
FROM eclipse-temurin:17-jre-alpine-3.23 AS runtime
WORKDIR /app

# 为了防止 target 下有多个 jar 包导致 COPY 失败，
# 建议确保构建出的 jar 包名称是确定的，或者使用具体通配符
# 这里假设依然使用通配符，但建议在 maven pom 中配置 finalName
COPY --from=build /app/target/*.jar app.jar

# 时区设置（可选，但国内常用）
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

EXPOSE 8080
ENV JAVA_OPTS=""

# 【关键优化】使用 exec 确保 java 是 PID 1 进程，能接收停止信号
# 使用 shell 格式的 exec 写法，保留 $JAVA_OPTS 变量扩展的能力
CMD ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]