<%-- /catalogo.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- 1. Importa a biblioteca Core do JSTL --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%-- 2. Importa a biblioteca Format (para formatar moeda) --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- Configura o Locale para BRL (R$) para a formatação de moeda --%>
<fmt:setLocale value="pt_BR"/>

<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Brec Brechó - Catálogo</title>
    <link rel="stylesheet" href="css/Styles.css"> <%-- Caminho ajustado --%>
</head>
<body>

    <%-- 3. Inclui o header --%>
    <jsp:include page="partes/header.jsp" />

    <main>
        <section class="card">
            <h2>Catálogo</h2>
            
            <%-- Bloco de mensagem de erro (ex: estoque indisponível) --%>
            <c:if test="${param.msg == 'erro_estoque'}">
                 <p style="color:red; text-align:center; background:#ffe0e0; padding: 10px; border-radius: 5px;">
                    Produto indisponível ou sem estoque!
                </p>
            </c:if>
            <c:if test="${param.msg == 'erro_id'}">
                 <p style="color:red; text-align:center; background:#ffe0e0; padding: 10px; border-radius: 5px;">
                    Produto não encontrado.
                </p>
            </c:if>
            
            <%-- 
              MUDANÇA CRÍTICA:
              Este <div> era preenchido pelo 'catalogo.js'.
              Agora, ele é preenchido no servidor usando JSTL.
            --%>
            <div id="catalogoLista" class="catalogo-grid"> <%-- Adicionei uma classe "catalogo-grid" para o layout --%>

                <%-- 
                  O 'CatalogoServlet' enviou uma lista chamada "listaProdutos".
                  Vamos iterar (fazer um loop) sobre ela.
                --%>
                <c:forEach var="produto" items="${requestScope.listaProdutos}">
                    <div class="produto-card">
                        
                        <%-- A imagem é lida da string Base64 do banco --%>
                        <img src="${produto.fotoBase64}" alt="${produto.nome}">
                        
                        <h3>${produto.nome}</h3>
                        
                        <%-- Só mostra a descrição se ela existir --%>
                        <c:if test="${not empty produto.descricao}">
                            <p>${produto.descricao}</p>
                        </c:if>
                        
                        <p><strong>Tamanho:</strong> ${produto.tamanho}</p>
                        
                        <%-- Formata o 'double' do produto para moeda BRL (R$) --%>
                        <p><strong>Preço:</strong> 
                            <fmt:formatNumber value="${produto.preco}" type="currency" currencyCode="BRL" />
                        </p>
                        
                        <p><strong>Estoque:</strong> ${produto.estoque}</p>

                        <%-- 
                          Lógica para o botão "Adicionar":
                          - Se estoque > 0, mostra o link/botão para o CarrinhoServlet.
                          - Se estoque == 0, mostra um botão desabilitado.
                        --%>
                        <c:choose>
                            <c:when test="${produto.estoque > 0}">
                                <p><strong>Status:</strong> Disponível</p>
                                
                                <%-- 
                                  O botão agora é um link que chama o CarrinhoServlet
                                  com a ação "adicionar" e o ID do produto.
                                --%>
                                <a href="carrinho?acao=adicionar&id=${produto.idProduto}" class="btn-link">
                                    <button>Adicionar ao Carrinho</button>
                                </a>
                            </c:when>
                            <c:otherwise>
                                <p><strong>Status:</strong> Indisponível</p>
                                <button disabled>Indisponível</button>
                            </c:otherwise>
                        </c:choose>
                        
                        <%-- 
                          NOTA: Os botões "Editar" e "Excluir" que existiam
                          no 'catalogo.js' não estão aqui. Esta é a página PÚBLICA.
                          Eles estarão na página 'meus-produtos.jsp' do fornecedor.
                        --%>
                    </div>
                </c:forEach>
                
                <%-- Mensagem caso a lista de produtos esteja vazia --%>
                <c:if test="${empty requestScope.listaProdutos}">
                    <p class="small" style="text-align:center; width:100%;">
                        Nenhum produto cadastrado no momento. Volte em breve!
                    </p>
                </c:if>
                
            </div>
        </section>
    </main>

    <%-- 4. Inclui o footer --%>
    <jsp:include page="partes/footer.jsp" />

    <%-- 
      5. SCRIPTS REMOVIDOS
      <script src="../Scripts/usuario.js"></script>
      <script src="../Scripts/carrinho.js"></script>
      <script src="../Scripts/catalogo.js"></script>
    --%>
    
    <%-- 
      NOTA DE ESTILO: 
      O 'catalogo.js' criava 'produto-card'. O 'Styles.css' deve ter esse estilo.
      Eu adicionei 'catalogo-grid' ao redor para ajudar a formatar os cards
      (ex: display: grid; grid-template-columns: repeat(3, 1fr); gap: 1rem;)
    --%>
</body>
</html>