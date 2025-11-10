<%-- /admin/verMensagens.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- 
  Esta página é protegida pelo AdminAuthFilter.
  Não precisamos de verificação de segurança JSTL aqui, 
  pois o Filtro já barrou usuários não-autorizados.
--%>

<!doctype html>
<html lang="pt-br">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Painel Admin — Mensagens SAC</title>
    <%-- 
      ATENÇÃO AO CAMINHO DO CSS:
      Como estamos dentro da pasta /admin/, precisamos voltar um nível ("../")
      para encontrar a pasta /css/.
    --%>
    <link rel="stylesheet" href="../css/Styles.css">
    
    <%-- Estilos locais para a tabela (opcional) --%>
    <style>
        .tabela-mensagens {
            width: 100%;
            border-collapse: collapse;
            margin-top: 1rem;
            font-size: 0.9rem;
        }
        .tabela-mensagens th, .tabela-mensagens td {
            border: 1px solid #ddd;
            padding: 8px;
            text-align: left;
            vertical-align: top;
        }
        .tabela-mensagens th { background-color: #f2f2f2; }
        .tabela-mensagens tr.lida {
            background-color: #f9f9f9;
            color: #777;
        }
        .tabela-mensagens .col-data { width: 150px; }
        .tabela-mensagens .col-nome { width: 200px; }
        .tabela-mensagens .col-acoes { width: 120px; }
    </style>
</head>
<body>

    <%-- 
      ATENÇÃO AO CAMINHO DO HEADER:
      Precisamos voltar um nível ("../") para encontrar a pasta /partes/.
    --%>
    <jsp:include page="../partes/header.jsp" />

    <main>
        <section class="card">
            <h2>Painel Admin: Mensagens do SAC</h2>
            <p class="small">Mensagens enviadas pelo formulário "Fale Conosco".</p>

            <c:choose>
                <c:when test="${empty requestScope.listaMensagens}">
                    <p>Nenhuma mensagem nova encontrada.</p>
                </c:when>
                <c:otherwise>
                    <table class="tabela-mensagens">
                        <thead>
                            <tr>
                                <th class="col-data">Data</th>
                                <th class="col-nome">De</th>
                                <th>Mensagem</th>
                                <th class="col-acoes">Ações</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%-- 
                              O Servlet enviou a lista "listaMensagens"
                              Vamos iterar sobre ela.
                            --%>
                            <c:forEach var="msg" items="${requestScope.listaMensagens}">
                                <%-- Adiciona a classe 'lida' se o 'msg.lida' for true --%>
                                <tr class="${msg.lida ? 'lida' : 'nao-lida'}">
                                    <td>
                                        <fmt:formatDate value="${msg.dataEnvio}" 
                                                        pattern="dd/MM/yyyy 'às' HH:mm" />
                                    </td>
                                    <td>
                                        <strong>${msg.nome}</strong><br>
                                        <small>${msg.email}</small>
                                    </td>
                                    <td>${msg.mensagem}</td>
                                    <td>
                                        <%-- Só mostra o botão se a msg NÃO foi lida --%>
                                        <c:if test="${not msg.lida}">
                                            <a href="verMensagens?acao=marcarLida&id=${msg.idMensagem}">
                                                <button>Marcar como Lida</button>
                                            </a>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
            
        </section>
        
        
        </section>

    <%-- NOVO MENU DE NAVEGAÇÃO DO ADMIN --%>
    <section class="card" style="margin-top: 1rem; background-color: #f9f9f9;">
        <strong>Navegação do Painel:</strong>
        <div style="display:flex; gap: 10px; margin-top: 10px;">
            <a href="verMensagens"><button>Ver Mensagens SAC</button></a>
           
            <a href="gerenciarUsuarios"><button>Gerenciar Usuários</button></a>
            <a href="gerenciarProdutos"><button>Gerenciar Produtos</button></a> <%-- ADICIONE ESTE LINK --%>
        </div>
    </section>
    </main>

    <%-- ATENÇÃO AO CAMINHO DO FOOTER: --%>
    <jsp:include page="../partes/footer.jsp" />

</body>
</html>