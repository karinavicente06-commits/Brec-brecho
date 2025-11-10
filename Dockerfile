# ---------------------------
# Etapa 1: Compilar o projeto
# ---------------------------
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Copia os arquivos-fonte do projeto
COPY src/main/java/ src/main/java/
COPY src/main/webapp/ src/main/webapp/

# Copia o JAR da API Servlet a partir da imagem do Tomcat 11
FROM tomcat:11.0.0-M19-jdk17 AS tomcat-jar

FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY --from=tomcat-jar /usr/local/tomcat/lib/jakarta.servlet-api.jar /app/

# Cria diretório para classes compiladas
RUN mkdir -p src/main/webapp/WEB-INF/classes

# Compila os arquivos Java incluindo o jar da API Servlet no classpath
RUN find src/main/java -name "*.java" > sources.txt && \
    javac -cp /app/jakarta.servlet-api.jar -d src/main/webapp/WEB-INF/classes @sources.txt

# ---------------------------
# Etapa 2: Rodar no Tomcat
# ---------------------------
FROM tomcat:11.0.0-M19-jdk17

# Remove o ROOT padrão
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Copia a aplicação compilada da etapa de build
COPY --from=build /app/src/main/webapp /usr/local/tomcat/webapps/ROOT

EXPOSE 8080

CMD ["catalina.sh", "run"]


 