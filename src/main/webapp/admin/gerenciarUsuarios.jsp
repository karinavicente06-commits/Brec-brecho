<%-- /admin/gerenciarUsuarios.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- 
  Esta página é protegida pelo AdminAuthFilter.
  Nenhum usuário não-admin pode chegar aqui.
--%>

<!doctype html>
<html lang="pt-br">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Painel Admin — Gerenciar Usuários</title>
    
    <%-- Caminho relativo ("../") para sair da pasta /admin/ --%>
    <link rel="stylesheet" href="../css/Styles.css">
    
    <%-- Estilos locais para a tabela (pode copiar para o Styles.css se preferir) --%>
    <style>
        .tabela-admin {
            width: 100%;
            border-collapse: collapse;
            margin-top: 1rem;
            margin-bottom: 2rem; /* Espaço entre as tabelas */
            font-size: 0.9rem;
        }
        .tabela-admin th, .tabela-admin td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
            vertical-align: middle;
        }
        .tabela-admin th { background-color: #f2f2f2; }
        .tabela-admin .col-acoes { width: 100px; }
        
        /* Classes para mensagens de feedback */
        .feedback-msg {
            padding: 10px;
            border-radius: 5px;
            margin-bottom: 1rem;
        }
        .feedback-sucesso {
            color: green; 
            background: #e0ffe0;
        }
        .feedback-erro {
            color: red; 
            background: #ffe0e0;
        }
    </style>
</head>
<body>

    <%-- Caminho relativo ("../") para incluir o header --%>
    <jsp:include page="../partes/header.jsp" />

    <main>
        <section class="card">
            <h2>Gerenciar Usuários</h2>
            
            <%-- 1. Exibir Mensagem de Feedback (se houver) --%>
            <c:if test="${not empty requestScope.mensagem}">
                <c:set var="tipoMsg" value="${requestScope.mensagem.startsWith('Erro') ? 'feedback-erro' : 'feedback-sucesso'}" />
                <p class="feedback-msg ${tipoMsg}">
                    ${requestScope.mensagem}
                </p>
            </c:if>

            <%-- 2. Tabela de Clientes --%>
            <h3>Clientes</h3>
            <c:choose>
                <c:when test="${empty requestScope.listaClientes}">
                    <p class="small">Nenhum cliente cadastrado.</p>
                </c:when>
                <c:otherwise>
                    <table class="tabela-admin">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Nome</th>
                                <th>Email</th>
                                <th>CPF</th>
                                <th>Cidade/Estado</th>
                                <th class="col-acoes">Ações</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="cliente" items="${requestScope.listaClientes}">
                                <tr>
                                    <td>${cliente.idCliente}</td>
                                    <td>${cliente.nome}</td>
                                    <td>${cliente.email}</td>
                                    <td>${cliente.cpf}</td>
                                    <td>${cliente.cidade} / ${cliente.estado}</td>
                                    <td class="col-acoes">
                                        <%-- Link para o próprio servlet, com a ação de excluir --%>
                                        <a href="gerenciarUsuarios?acao=excluirCliente&id=${cliente.idCliente}"
                                           onclick="return confirm('ATENÇÃO:\\nTem certeza que deseja excluir este CLIENTE?\\n(Isso pode falhar se ele tiver pedidos registrados).')">
                                            <button>Excluir</button>
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>

            <%-- 3. Tabela de Fornecedores --%>
            <h3>Fornecedores</h3>
            <c:choose>
                <c:when test="${empty requestScope.listaFornecedores}">
                    <p class="small">Nenhum fornecedor cadastrado.</p>
                </c:when>
                <c:otherwise>
                    <table class="tabela-admin">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Nome Loja</th>
                                <th>Email</th>
                                <th>CPF/CNPJ</th>
                                <th>Telefone</th>
                                <th class="col-acoes">Ações</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="f" items="${requestScope.listaFornecedores}">
                                <tr>
                                    <td>${f.idFornecedor}</td>
                                    <td>${f.nomeLoja}</td>
                                    <td>${f.email}</td>
                                    <td>${f.cpfCnpj}</td>
                                    <td>${f.telefone}</td>
                                    <td class="col-acoes">
                                        <%-- Link para o próprio servlet, com a ação de excluir --%>
                                        <a href="gerenciarUsuarios?acao=excluirFornecedor&id=${f.idFornecedor}"
                                           onclick="return confirm('ATENÇÃO:\\nTem certeza que deseja excluir este FORNECEDOR?\\n(Todos os produtos cadastrados por ele serão excluídos JUNTOS).')">
                                            <button>Excluir</button>
                                        </a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
            
        </section>
        </section>

    <%-- COPIE ESTE MENU DE NAVEGAÇÃO --%>
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