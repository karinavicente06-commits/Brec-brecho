<%-- /admin/gerenciarProdutos.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- Configura o Locale para BRL (R$) --%>
<fmt:setLocale value="pt_BR"/>

<%-- 
  Esta página é protegida pelo AdminAuthFilter.
  Nenhum usuário não-admin pode chegar aqui.
--%>

<!doctype html>
<html lang="pt-br">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Painel Admin — Gerenciar Produtos</title>
    
    <%-- Caminho relativo ("../") para sair da pasta /admin/ --%>
    <link rel="stylesheet" href="../css/Styles.css">
    
    <%-- Estilos locais (pode copiar para o Styles.css se preferir) --%>
    <style>
        .tabela-admin {
            width: 100%;
            border-collapse: collapse;
            margin-top: 1rem;
            margin-bottom: 2rem;
            font-size: 0.9rem;
        }
        .tabela-admin th, .tabela-admin td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
            vertical-align: middle;
        }
        .tabela-admin th { background-color: #f2f2f2; }
        .tabela-admin .col-acoes { width: 150px; }
        .tabela-admin .col-img { width: 80px; }
        .tabela-admin img {
            width: 60px;
            height: 60px;
            object-fit: cover;
            border-radius: 4px;
        }
        
        /* Classes para mensagens de feedback */
        .feedback-msg {
            padding: 10px;
            border-radius: 5px;
            margin-bottom: 1rem;
        }
        .feedback-sucesso { color: green; background: #e0ffe0; }
        .feedback-erro { color: red; background: #ffe0e0; }
    </style>
</head>
<body>

    <%-- Caminho relativo ("../") para incluir o header --%>
    <jsp:include page="../partes/header.jsp" />

    <main>
        <section class="card">
            <h2>Gerenciar Produtos</h2>
            <p class="small">Modere todos os produtos cadastrados no site.</p>
            
            <%-- 1. Exibir Mensagem de Feedback (se houver) --%>
            <c:if test="${not empty param.msg}">
                <c:set var="tipoMsg" value="feedback-erro" />
                <c:if test="${param.msg == 'salvo_sucesso' || param.msg == 'excluido_sucesso'}">
                    <c:set var="tipoMsg" value="feedback-sucesso" />
                </c:if>
                
                <p class="feedback-msg ${tipoMsg}">
                    <c:choose>
                        <c:when test="${param.msg == 'salvo_sucesso'}">Produto salvo com sucesso!</c:when>
                        <c:when test="${param.msg == 'excluido_sucesso'}">Produto excluído com sucesso!</c:when>
                        <c:when test="${param.msg == 'admin_nao_cria'}">Administradores não podem criar produtos, apenas editá-los.</c:when>
                        <c:when test="${param.msg == 'erro_permissao'}">Você não tem permissão para esta ação.</c:when>
                        <c:otherwise>Ocorreu um erro na operação.</c:otherwise>
                    </c:choose>
                </p>
            </c:if>

            <%-- 2. Tabela de Produtos --%>
            <c:choose>
                <c:when test="${empty requestScope.listaProdutos}">
                    <p class="small">Nenhum produto cadastrado no site.</p>
                </c:when>
                <c:otherwise>
                    <table class="tabela-admin">
                        <thead>
                            <tr>
                                <th class="col-img">Foto</th>
                                <th>Nome</th>
                                <th>Preço</th>
                                <th>Estoque</th>
                                <th>ID Fornec.</th>
                                <th class="col-acoes">Ações</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="produto" items="${requestScope.listaProdutos}">
                                <tr>
                                    <td><img src="${produto.fotoBase64}" alt="${produto.nome}"></td>
                                    <td>${produto.nome}</td>
                                    <td>
                                        <fmt:formatNumber value="${produto.preco}" type="currency" />
                                    </td>
                                    <td>${produto.estoque}</td>
                                    <td>${produto.idFornecedor}</td>
                                    <td class="col-acoes">
                                        <%-- 
                                          Links de Ação (CRUD)
                                          Apontam para o ProdutoServlet (que agora aceita o Admin)
                                        --%>
                                        
                                        <%-- EDITAR: Chama o ?acao=carregar --%>
                                        <a href="../produto?acao=carregar&id=${produto.idProduto}">
                                            <button>✏️ Editar</button>
                                        </a>
                                        
                                        <%-- EXCLUIR: Chama o ?acao=excluir --%>
                                        <a href="../produto?acao=excluir&id=${produto.idProduto}" 
                                           onclick="return confirm('Tem certeza que deseja excluir este produto? (ID: ${produto.idProduto})')">
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
        
        <%-- 3. Menu de Navegação do Admin --%>
        <section class="card" style="margin-top: 1rem; background-color: #f9f9f9;">
            <strong>Navegação do Painel:</strong>
            <div style="display:flex; gap: 10px; margin-top: 10px;">
                <a href="verMensagens"><button>Ver Mensagens SAC</button></a>
                <a href="gerenciarUsuarios"><button>Gerenciar Usuários</button></a>
                <a href="gerenciarProdutos"><button>Gerenciar Produtos</button></a>
            </div>
        </section>

    </main>

    <%-- Caminho relativo ("../") para incluir o footer --%>
    <jsp:include page="../partes/footer.jsp" />

</body>
</html>