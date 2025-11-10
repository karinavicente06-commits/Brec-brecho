FROM eclipse-temurin:17-jdk AS build 

 

WORKDIR /app 

 

# Copia apenas os arquivos fonte 

COPY src/main/java/ src/main/java/ 

COPY src/main/webapp/ src/main/webapp/ 

 

# Cria diretório para as classes compiladas 

RUN mkdir -p src/main/webapp/WEB-INF/classes 

 

# Compila os arquivos Java (ajuste o pacote se necessário) 

RUN find src/main/java -name "*.java" > sources.txt && \ 

javac -d src/main/webapp/WEB-INF/classes @sources.txt 

 

# Etapa 2: rodar no Tomcat 

FROM tomcat:10.1.26-jdk17 

 

# Remove o ROOT padrão do Tomcat 

RUN rm -rf /usr/local/tomcat/webapps/ROOT 

 

# Copia a aplicação compilada da etapa anterior 

COPY --from=build /app/src/main/webapp /usr/local/tomcat/webapps/ROOT 

 

EXPOSE 8080 

 

CMD ["catalina.sh", "run"] 

 