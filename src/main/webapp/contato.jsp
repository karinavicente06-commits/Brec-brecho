<%-- /contato.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!doctype html>
<html lang="pt-br">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Contato — Brec Brechó</title>
    <link rel="stylesheet" href="css/Styles.css">
</head>
<body>

    <jsp:include page="partes/header.jsp" />

    <main>
        <form id="form-contato" class="formulario" action="contato" method="POST">
            <h2>Fale Conosco</h2>
            <p class="small">Tem dúvidas, sugestões ou problemas? Mande uma mensagem para nossa equipe.</p>
            
            <%-- Mensagens de Sucesso/Erro --%>
            <c:if test="${param.msg == 'sucesso'}">
                <p style="color:green; background:#e0ffe0; padding: 10px; border-radius: 5px;">
                    Sua mensagem foi enviada com sucesso! Responderemos em breve.
                </p>
            </c:if>
            <c:if test="${param.msg == 'erro'}">
                <p style="color:red; background:#ffe0e0; padding: 10px; border-radius: 5px;">
                    Ocorreu um erro ao enviar sua mensagem. Tente novamente.
                </p>
            </c:if>
            
            <label for="nome">Seu Nome</label>
            <input type="text" id="nome" name="nome" required>

            <label for="email">Seu Email</label>
            <input type="email" id="email" name="email" required>

            <label for="mensagem">Mensagem</label>
            <textarea id="mensagem" name="mensagem" rows="5" required></textarea>

            <button type="submit">Enviar Mensagem</button>
        </form>
    </main>

    <jsp:include page="partes/footer.jsp" />

</body>
</html>