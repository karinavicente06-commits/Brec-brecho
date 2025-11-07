<%-- /meus-produtos.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- Configura o Locale para BRL (R$) --%>
<fmt:setLocale value="pt_BR"/>

<%-- 
  1. VERIFICAÇÃO DE SEGURANÇA
  O Servlet (/meus-produtos) já faz isso, mas é uma boa prática
  garantir que a página JSP também não possa ser acessada diretamente.
--%>
<c:if test="${empty sessionScope.usuarioLogado || sessionScope.tipoUsuario != 'fornecedor'}">
    <% response.sendRedirect("login.jsp?msg=acesso_negado"); %>
</c:if>

<!doctype html>
<html lang="pt-br">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Meus Produtos — Brec Brechó</title>
    <link rel="stylesheet" href="css/Styles.css">
    
    <%-- Estilo simples para a tabela de produtos (pode colocar no Styles.css) --%>
    <style>
        .tabela-produtos {
            width: 100%;
            border-collapse: collapse;
            margin-top: 1rem;
        }
        .tabela-produtos th, .tabela-produtos td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
            vertical-align: middle;
        }
        .tabela-produtos th {
            background-color: #f2f2f2;
        }
        .tabela-produtos img {
            width: 60px;
            height: 60px;
            object-fit: cover;
            border-radius: 4px;
        }
        .tabela-produtos .acoes a {
            margin-right: 5px;
        }
    </style>
</head>
<body>

    <%-- 2. Inclui o header --%>
    <jsp:include page="partes/header.jsp" />

    <main>
        <section class="card">
            <h2>Meus Produtos</h2>

            <%-- 3. Mensagens de Sucesso/Erro vindas do ProdutoServlet --%>
            <c:if test="${param.msg == 'salvo_sucesso'}">
                <p style="color:green; background:#e0ffe0; padding: 10px; border-radius: 5px;">Produto salvo com sucesso!</p>
            </c:if>
            <c:if test="${param.msg == 'excluido_sucesso'}">
                <p style="color:green; background:#e0ffe0; padding: 10px; border-radius: 5px;">Produto excluído com sucesso!</p>
            </c:if>
            <c:if test="${param.msg == 'erro_excluir' || param.msg == 'erro_carregar' || param.msg == 'erro_permissao'}">
                <p style="color:red; background:#ffe0e0; padding: 10px; border-radius: 5px;">Ocorreu um erro ou você não tem permissão para esta ação.</p>
            </c:if>

            <%-- 
              4. Link para CADASTRAR NOVO PRODUTO
              Aponta para o ProdutoServlet com a ação "novo".
            --%>
            <div style="margin-bottom: 1rem;">
                <a href="produto?acao=novo">
                    <button>+ Cadastrar Novo Produto</button>
                </a>
            </div>

            <%-- 
              5. Lista de Produtos
              O 'MeusProdutosServlet' enviou a lista "listaMeusProdutos"
            --%>
            <c:choose>
                <%-- CASO 1: A lista está vazia --%>
                <c:when test="${empty requestScope.listaMeusProdutos}">
                    <p class="small">Você ainda não cadastrou nenhum produto.</p>
                </c:when>
                
                <%-- CASO 2: A lista tem produtos --%>
                <c:otherwise>
                    <table class="tabela-produtos">
                        <thead>
                            <tr>
                                <th>Foto</th>
                                <th>Nome</th>
                                <th>Preço</th>
                                <th>Estoque</th>
                                <th>Ações</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="produto" items="${requestScope.listaMeusProdutos}">
                                <tr>
                                    <td><img src="${produto.fotoBase64}" alt="${produto.nome}"></td>
                                    <td>${produto.nome}</td>
                                    <td>
                                        <fmt:formatNumber value="${produto.preco}" type="currency" />
                                    </td>
                                    <td>${produto.estoque}</td>
                                    <td class="acoes">
                                        <%-- 
                                          Links de Ação (CRUD)
                                          Apontam para o ProdutoServlet
                                        --%>
                                        
                                        <%-- EDITAR: Chama o ?acao=carregar --%>
                                        <a href="produto?acao=carregar&id=${produto.idProduto}">
                                            <button>✏️ Editar</button>
                                        </a>
                                        
                                        <%-- EXCLUIR: Chama o ?acao=excluir --%>
                                        <a href="produto?acao=excluir&id=${produto.idProduto}" 
                                           onclick="return confirm('Tem certeza que deseja excluir este produto? Esta ação não pode ser desfeita.')">
                                            <button>🗑️ Excluir</button>
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>

        </section>
    </main>

    <%-- 6. Inclui o footer --%>
    <jsp:include page="partes/footer.jsp" />

</body>
</html>