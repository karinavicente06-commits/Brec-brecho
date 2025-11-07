<%-- /carrinho.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- Importa JSTL Core (loops, if) e Format (moeda) --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%-- Configura o Locale para BRL (R$) --%>
<fmt:setLocale value="pt_BR"/>

<!doctype html>
<html lang="pt-br">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Brec Brechó — Carrinho</title>
    <link rel="stylesheet" href="css/Styles.css"> <%-- Caminho ajustado --%>
</head>
<body>

    <%-- 1. Inclui o header --%>
    <jsp:include page="partes/header.jsp" />

    <main>
        <section id="carrinho" class="card">
            <h2>Carrinho</h2>

            <%-- Bloco de mensagens de erro/aviso vindas do Servlet --%>
            <c:if test="${param.msg == 'vazio'}">
                <p style="color:red; background:#ffe0e0; padding: 10px; border-radius: 5px;">Seu carrinho está vazio.</p>
            </c:if>
            <c:if test="${param.msg == 'erro_finalizar'}">
                <p style="color:red; background:#ffe0e0; padding: 10px; border-radius: 5px;">Erro ao finalizar a compra. Tente novamente.</p>
            </c:if>
            <c:if test="${not empty sessionScope.msg_carrinho}">
                <p style="color:red; background:#ffe0e0; padding: 10px; border-radius: 5px;">
                    ${sessionScope.msg_carrinho}
                </p>
                <%-- Limpa a mensagem após exibi-la --%>
                <c:remove var="msg_carrinho" scope="session" />
            </c:if>
            
            <%-- 
              O CarrinhoServlet salva o carrinho na sessão como um Map<Integer, ItemPedido>
              Vamos verificar se ele está vazio ou não.
            --%>
            <c:choose>
                <%-- CASO 1: Carrinho vazio ou não existe --%>
                <c:when test="${empty sessionScope.carrinho}">
                    <div id="lista-carrinho">
                        <p class="small">Seu carrinho está vazio.</p>
                    </div>
                </c:when>
                
                <%-- CASO 2: Carrinho TEM itens --%>
                <c:otherwise>
                    <%-- 
                      JSTL para calcular o Subtotal
                      (O Frete é fixo, R$ 15,00, como no seu carrinho.js)
                    --%>
                    <c:set var="subtotal" value="0" />
                    
                    <div id="lista-carrinho">
                        <%-- 
                          Loop sobre o Map do carrinho. 
                          'entry.value' é o objeto ItemPedido 
                        --%>
                        <c:forEach var="entry" items="${sessionScope.carrinho}">
                            <c:set var="item" value="${entry.value}" />
                            
                            <div class="item-carrinho">
                                <img src="${item.produto.fotoBase64}" alt="${item.produto.nome}" style="width:80px;height:80px;object-fit:cover;border-radius:8px">
                                <div style="flex:1">
                                    <h3>${item.produto.nome}</h3>
                                    <p><strong>Tamanho:</strong> ${item.produto.tamanho}</p>
                                    <p><strong>Preço:</strong> 
                                        <fmt:formatNumber value="${item.precoUnitarioVenda}" type="currency" />
                                    </p>
                                    <p><strong>Qtd:</strong> ${item.quantidade}</p>
                                </div>
                                <%-- Link para o CarrinhoServlet remover o item --%>
                                <a href="carrinho?acao=remover&id=${item.produto.idProduto}">
                                    <button>Remover</button>
                                </a>
                            </div>
                            
                            <%-- Acumula o subtotal a cada item --%>
                            <c:set var="subtotal" value="${subtotal + (item.quantidade * item.precoUnitarioVenda)}" />
                        </c:forEach>
                    </div>
                    
                    <%-- Define o frete e calcula o total --%>
                    <c:set var="frete" value="15.00" />
                    <c:set var="total" value="${subtotal + frete}" />
                    
                    <%-- 
                      FORMULÁRIO DE FINALIZAÇÃO
                      Substitui o JavaScript de finalizar compra.
                      Ele envia os dados para o CarrinhoServlet via POST.
                    --%>
                    <form action="carrinho" method="POST" id="form-finalizar">
                        <%-- Ação oculta para o Servlet saber o que fazer --%>
                        <input type="hidden" name="acao" value="finalizar">
                    
                        <div id="resumo-carrinho" style="margin-top:1rem;">
                            <p><strong>Subtotal:</strong> <fmt:formatNumber value="${subtotal}" type="currency" /></p>
                            <p><strong>Frete:</strong> <fmt:formatNumber value="${frete}" type="currency" /></p>
                            
                            <p><strong>Total:</strong> 
                                <fmt:formatNumber value="${total}" type="currency" />
                                <%-- O ID 'total-carrinho-span' é usado pelo JS do parcelamento --%>
                                <span id="total-carrinho-span" data-total-valor="${total}" style="display:none;"></span>
                            </p>
                            
                            <label for="formaPagamento">Forma de Pagamento:</label>
                            <%-- O 'name' é crucial para o Servlet receber o valor --%>
                            <select id="formaPagamento" name="formaPagamento">
                                <option value="pix">PIX (10% desconto)</option>
                                <option value="boleto">Boleto (10% desconto)</option>
                                <option value="cartao">Cartão de Crédito</option>
                            </select>
                            
                            <div id="parcelamento" style="margin-top:.5rem; display:none;"></div>
                        </div>

                        <div style="margin-top:.6rem;display:flex;gap:.5rem">
                            <%-- Botão de Submit do formulário --%>
                            <button id="btnFinalizarCompra" type="submit">Finalizar Compra</button>
                            
                            <%-- Link para o Servlet esvaziar o carrinho --%>
                            <a href="carrinho?acao=esvaziar" id="btnEsvaziarCarrinho">
                                <button type="button">Esvaziar</button>
                            </a>
                        </div>
                    </form>
                    
                </c:otherwise>
            </c:choose>
            
        </section>
    </main>

    <%-- 2. Inclui o footer --%>
    <jsp:include page="partes/footer.jsp" />

    <%-- 
      3. SCRIPTS DE UI (Parcelamento) - MANTIDOS
      Este script é o único pedaço do carrinho.js que precisamos,
      pois ele lida com a INTERFACE de parcelamento (UI).
    --%>
    <script>
        document.getElementById("formaPagamento").addEventListener("change", () => {
            const forma = document.getElementById("formaPagamento").value;
            const