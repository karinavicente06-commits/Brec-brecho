<%-- /partes/header.jsp (Versão Final com Links Absolutos) --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- 
  Definimos uma variável 'baseURL' para facilitar a leitura.
  Ela conterá "/Brec_Brecho" (ou o nome do seu projeto).
--%>
<c:set var="baseURL" value="${pageContext.request.contextPath}" />

<div id="topo"></div>
<header>
    <div class="brand">
        <div class="logo">BB</div>
        <div>
            <div style="font-weight:800">Brec Brechó</div>
            <div style="font-size:.85rem;opacity:.9">Achados sustentáveis • reviva peças</div>
        </div>
    </div>
    <nav>
        <%-- Links normais (agora com a baseURL) --%>
        <a href="${baseURL}/index.jsp">Início</a>
        <a href="${baseURL}/catalogo">Catálogo</a>
        <a href="${baseURL}/carrinho">Carrinho</a>

        <%-- Bloco de Login/Usuário --%>
        <span id="area-login">
            <c:choose>
                <%-- CASO 1: Se existe um usuário na sessão --%>
                <c:when test="${not empty sessionScope.usuarioLogado}">
                    
                    Olá, <strong>
                        <%-- Lógica de Nome CORRIGIDA para 3 tipos --%>
                        <c:choose>
                            <c:when test="${sessionScope.tipoUsuario == 'cliente'}">
                                ${sessionScope.usuarioLogado.nome}
                            </c:when>
                            <c:when test="${sessionScope.tipoUsuario == 'admin'}">
                                ${sessionScope.usuarioLogado.nome}
                            </c:when>
                            <c:when test="${sessionScope.tipoUsuario == 'fornecedor'}">
                                ${sessionScope.usuarioLogado.nomeLoja}
                            </c:when>
                        </c:choose>
                    </strong>!
                    
                    <%-- Link de Perfil (agora com a baseURL) --%>
                    <a href="${baseURL}/perfil.jsp"><button>👤 Perfil</button></a>
                    
                    <%-- Link de Sair (agora com a baseURL) --%>
                    <a href="${baseURL}/logout"><button id="btnSair">Sair</button></a>
                    
                    <%-- Links Condicionais (agora com a baseURL) --%>
                    <c:if test="${sessionScope.tipoUsuario == 'fornecedor'}">
                        <a href="${baseURL}/meus-produtos"><button>Meus Produtos</button></a>
                    </c:if>
                    
                    <c:if test="${sessionScope.tipoUsuario == 'admin'}">
                        <a href="${baseURL}/admin/verMensagens"><button>Painel Admin</button></a>
                    </c:if>

                </c:when>
                
                <%-- CASO 2: Se não há usuário na sessão (usuário deslogado) --%>
                <c:otherwise>
                    <a href="${baseURL}/login.jsp"><button>Login</button></a>
                </c:otherwise>
            </c:choose>
        </span>
    </nav>
</header>