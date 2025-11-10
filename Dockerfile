# ---------------------------
# Etapa 1: Compilar o projeto
# ---------------------------
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Instala wget para baixar dependências
RUN apt-get update && apt-get install -y wget

# Baixa o JAR da Jakarta Servlet API (necessário para compilar Servlets)
RUN wget https://repo1.maven.org/maven2/jakarta/servlet/jakarta.servlet-api/6.1.0/jakarta.servlet-api-6.1.0.jar -O /app/jakarta.servlet-api.jar

# Baixa o JAR do jBCrypt (criptografia de senhas)
RUN wget https://repo1.maven.org/maven2/org/mindrot/jbcrypt/0.4/jbcrypt-0.4.jar -O /app/jbcrypt-0.4.jar

# Copia o código fonte
COPY src/main/java/ src/main/java/
COPY src/main/webapp/ src/main/webapp/

# Cria diretório para classes compiladas
RUN mkdir -p src/main/webapp/WEB-INF/classes

# Compila incluindo as duas bibliotecas no classpath
RUN find src/main/java -name "*.java" > sources.txt && \
    javac -cp "/app/jakarta.servlet-api.jar:/app/jbcrypt-0.4.jar" -d src/main/webapp/WEB-INF/classes @sources.txt

# ---------------------------
# Etapa 2: Executar no Tomcat 11
# ---------------------------
FROM tomcat:11.0.0-jdk17-temurin

# Remove o ROOT padrão
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Copia a aplicação compilada
COPY --from=build /app/src/main/webapp /usr/local/tomcat/webapps/ROOT

# Copia os jars necessários para o classpath do Tomcat
COPY --from=build /app/jbcrypt-0.4.jar /usr/local/tomcat/lib/
COPY --from=build /app/jakarta.servlet-api.jar /usr/local/tomcat/lib/

EXPOSE 8080

CMD ["catalina.sh", "run"]
