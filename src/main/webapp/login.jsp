<%-- /login.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!doctype html>
<html lang="pt-br">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Brec Brechó — Login</title>
    <link rel="stylesheet" href="css/Styles.css"> <%-- Caminho ajustado --%>
</head>
<body>

    <%-- 1. Inclui o header --%>
    <jsp:include page="partes/header.jsp" />

    <main>
        <section id="login" class="card">
            
            <%-- 
              Lógica para exibir mensagens de erro vindas dos Servlets
              (Ex: /login.jsp?msg=erro_login) 
            --%>
            <c:if test="${param.msg == 'erro_login'}">
                <p style="color:red; text-align:center; background:#ffe0e0; padding: 10px; border-radius: 5px;">
                    Email ou senha incorretos!
                </p>
            </c:if>
             <c:if test="${param.msg == 'erro_excecao'}">
                <p style="color:red; text-align:center; background:#ffe0e0; padding: 10px; border-radius: 5px;">
                    Ocorreu um erro inesperado. Tente novamente.
                </p>
            </c:if>
             <c:if test="${param.msg == 'sucesso'}">
                <p style="color:green; text-align:center; background:#e0ffe0; padding: 10px; border-radius: 5px;">
                    Cadastro realizado com sucesso! Faça seu login.
                </p>
            </c:if>
            <c:if test="${param.msg == 'acesso_negado'}">
                <p style="color:red; text-align:center; background:#ffe0e0; padding: 10px; border-radius: 5px;">
                    Você precisa estar logado para acessar esta página.
                </p>
            </c:if>


            <h2>Entrar</h2>
            
            <%-- 
              MUDANÇA CRÍTICA:
              1. Removemos o 'onsubmit="event.preventDefault(); fazerLogin();"'
              2. Adicionamos method="POST"
              3. O 'action' agora aponta para "login", que é a URL do @WebServlet("/login") 
                 do nosso LoginServlet.
            --%>
            <form action="login" method="POST">
                
                <label for="loginEmail">Email</label>
                <%-- O 'name' (loginEmail) deve ser o mesmo que o Servlet espera --%>
                <input id="loginEmail" name="loginEmail" type="email" required>

                <label for="loginSenha">Senha</label>
                <%-- O 'name' (loginSenha) deve ser o mesmo que o Servlet espera --%>
                <input id="loginSenha" name="loginSenha" type="password" required>

                <button class="btn" type="submit">Entrar</button>
            </form>

            <div style="margin-top:1rem; text-align:center;">
                <a href="perfil.jsp"><button id="btnEsqueciSenha" class="btn" style="margin-bottom:0.5rem;">Esqueci minha senha</button></a>
                <br>
                <a href="cliente.jsp"><button id="btnCriarConta" class="btn">Criar conta</button></a>
            </div>
        </section>
    </main>

    <%-- 2. Inclui o footer --%>
    <jsp:include page="partes/footer.jsp" />

    <%-- 
      3. Scripts de Lógica REMOVIDOS
      (O <script src="../Scripts/login.js"></script> foi REMOVIDO)
      (O <script src="../Scripts/usuario.js"></script> foi REMOVIDO)
      (O script <script>...</script> local foi simplificado para links diretos)
    --%>
</body>
</html>