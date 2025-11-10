# ---------------------------
# Etapa 1: Compilar o projeto
# ---------------------------
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Instala wget para baixar o JAR da API Servlet
RUN apt-get update && apt-get install -y wget

# Baixa o JAR da Jakarta Servlet API (versão compatível com Tomcat 11)
RUN wget https://repo1.maven.org/maven2/jakarta/servlet/jakarta.servlet-api/6.1.0/jakarta.servlet-api-6.1.0.jar -O /app/jakarta.servlet-api.jar

# Copia os arquivos-fonte do projeto
COPY src/main/java/ src/main/java/
COPY src/main/webapp/ src/main/webapp/

# Cria diretório para classes compiladas
RUN mkdir -p src/main/webapp/WEB-INF/classes

# Compila os arquivos Java incluindo o jar da API Servlet no classpath
RUN find src/main/java -name "*.java" > sources.txt && \
    javac -cp /app/jakarta.servlet-api.jar -d src/main/webapp/WEB-INF/classes @sources.txt

# ---------------------------
# Etapa 2: Rodar no Tomcat 11
# ---------------------------
FROM tomcat:11.0.0-jdk17-temurin

# Remove o ROOT padrão
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Copia a aplicação compilada da etapa de build
COPY --from=build /app/src/main/webapp /usr/local/tomcat/webapps/ROOT

EXPOSE 8080

CMD ["catalina.sh", "run"]
