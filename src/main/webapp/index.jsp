<%-- /index.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!doctype html>
<html lang="pt-br">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Brec Brechó — Página Inicial</title>
    
    <%-- 
      ATENÇÃO AOS CAMINHOS: 
      Como o JSP está na raiz (webapp/), os caminhos agora são "css/Styles.css", 
      e não "../css/Styles.css". 
    --%>
    <link rel="stylesheet" href="css/Styles.css">
</head>
<body>

    <%-- 1. Inclui o cabeçalho dinâmico --%>
    <jsp:include page="partes/header.jsp" />

    <main>
        <section id="home" class="card">
            <h2>Bem-vinda ao Brec Brechó</h2>
            <p class="small">Explore, cadastre-se e contribua para a moda circular.</p>

            <div class="slider-container">
                <button class="prev">&#10094;</button>
                <div class="slider">
                    <div class="slides">
                        <%-- Caminhos das imagens também ajustados --%>
                        <a href="catalogo"><img src="img/vestido floral.jpg" alt="Vestido Floral"></a>
                        <a href="catalogo"><img src="img/jaqueta.jpg.webp" alt="Jaqueta Jeans"></a>
                        <a href="catalogo"><img src="img/bolsa vintage.jpg.webp" alt="Bolsa Vintage"></a>
                        <a href="catalogo"><img src="img/shorts.webp" alt="shorts"></a>
                        <a href="catalogo"><img src="img/vestidolongo.jpg" alt="Vestido Longo"></a>
                        <a href="catalogo"><img src="img/camisetafeminina.webp" alt="camiseta feminina"></a>
                    </div>
                </div>
                <button class="next">&#10095;</button>
            </div>

            <div class="chamariscos">
                <div class="card">💳 10% de desconto no PIX</div>
                <div class="card">🚚 Frete fixo R$ 15,00 para todo Brasil</div>
            </div>

            <div style="text-align:center; margin:2rem;">
                
                <%-- 
                  Este botão agora verifica se o usuário é "fornecedor".
                  Se for, o link aponta para o servlet de CADASTRAR PRODUTO (/produto?acao=novo).
                  Se não for (ou se estiver deslogado), o link aponta para o LOGIN.
                --%>
                <c:choose>
                    <c:when test="${sessionScope.tipoUsuario == 'fornecedor'}">
                        <a href="produto?acao=novo">
                            <button id="btnVendaPecas">
                                <img src="img/venda-icon.png" alt="Venda Icon"> Venda suas peças aqui
                            </button>
                        </a>
                    </c:when>
                    <c:otherwise>
                         <a href="login.jsp">
                            <button id="btnVendaPecas">
                                <img src="img/venda-icon.png" alt="Venda Icon"> Venda suas peças aqui
                            </button>
                        </a>
                    </c:otherwise>
                </c:choose>

            </div>

            <div class="home-links">
                <a href="cliente.jsp"><button>Cadastro Cliente</button></a>
                <a href="fornecedor.jsp"><button>Cadastro Fornecedor</button></a>
                <a href="catalogo"><button>Ver Catálogo</button></a> <%-- Link para Servlet --%>
                <a href="carrinho"><button>Ver Carrinho</button></a> <%-- Link para Servlet --%>
                <a href="login.jsp"><button>Login</button></a>
            </div>
        </section>
    </main>

    <%-- 2. Inclui o rodapé --%>
    <jsp:include page="partes/footer.jsp" />

    <%-- 3. Scripts de Interface (Carrossel) --%>
    <%-- Caminho do JS ajustado --%>
    <script src="js/carrossel.js"></script>

    <%-- 
      4. Scripts de Lógica REMOVIDOS
      (O script <script src="../Scripts/usuario.js"></script> foi REMOVIDO)
      (O script local <script>...</script> de "Botão Perfil / Sair / Login" foi REMOVIDO)
      (A lógica do "Botão venda peças" foi refeita acima com JSTL)
    --%>
</body>
</html>