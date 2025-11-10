FROM eclipse-temurin:17-jdk AS build 
WORKDIR /app 

# Copia o código-fonte e dependências 
COPY src/main/java/ ./src/main/java/ 
COPY src/main/webapp/WEB-INF/lib ./lib 
 

# Cria diretório de classes compiladas 
RUN mkdir -p build/classes 
 

# Baixa o jakarta.servlet-api.jar para compilar (Tomcat fornece no runtime) 
RUN curl -o lib/jakarta.servlet-api.jar \ 

https://repo1.maven.org/maven2/jakarta/servlet/jakarta.servlet-api/6.0.0/jakarta.servlet-api-6.0.0.jar 
 

# Compila todos os .java, incluindo dependências 
RUN find src/main/java -name "*.java" > sources.txt && \ 
javac -cp "lib/*" -d build/classes @sources.txt 

 

# =============================== 

# Etapa 2: Rodar no Tomcat 

# =============================== 

FROM tomcat:10.1.26-jdk17 

# Remove o aplicativo padrão ROOT 
RUN rm -rf /usr/local/tomcat/webapps/ROOT 
 

# Copia arquivos da aplicação compilada 
#COPY src/main/webapp /usr/local/tomcat/webapps/Brec_Brecho/ 
#COPY --from=build /app/build/classes /usr/local/tomcat/webapps/Brec_Brecho/WEB-INF/classes 

COPY src/main/webapp /usr/local/tomcat/webapps/ROOT/ 

COPY --from=build /app/build/classes /usr/local/tomcat/webapps/ROOT/WEB-INF/classes 

 
# Expõe a porta padrão do Tomcat 
EXPOSE 8080 

# Inicia o Tomcat
CMD ["catalina.sh", "run"] 

 