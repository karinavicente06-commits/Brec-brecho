<%-- /cadastrar-produto.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- 
  1. VERIFICAÇÃO DE SEGURANÇA (MUITO IMPORTANTE)
  Esta página só pode ser acessada por um Fornecedor logado.
  Se o usuário não for 'fornecedor', ele é expulso para a página de login.
--%>
<c:if test="${empty sessionScope.usuarioLogado || sessionScope.tipoUsuario != 'fornecedor'}">
    <%-- 
      Usamos um scriptlet aqui para forçar o redirecionamento imediato no lado do servidor.
    --%>
    <% response.sendRedirect("login.jsp?msg=acesso_negado"); %>
</c:if>

<!doctype html>
<html lang="pt-br">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Cadastro de Produto — Brec Brechó</title>
    <link rel="stylesheet" href="css/Styles.css"> <%-- Caminho ajustado --%>
</head>
<body>

    <%-- 2. Inclui o header --%>
    <jsp:include page="partes/header.jsp" />

    <main>
        <%-- 
          3. LÓGICA DE EDIÇÃO
          Verifica se o ProdutoServlet enviou um "produtoParaEditar".
          Se sim, estamos em "Modo Edição".
        --%>
        <c:set var="editMode" value="${not empty requestScope.produtoParaEditar}" />
        <c:set var="produto" value="${requestScope.produtoParaEditar}" />

        <%-- 
          4. MUDANÇA CRÍTICA NO FORMULÁRIO:
          1. action="produto" (aponta para o ProdutoServlet)
          2. method="POST"
          3. enctype="multipart/form-data" (ESSENCIAL para upload de arquivos)
        --%>
        <form id="form-produto" class="formulario" action="produto" method="POST" enctype="multipart/form-data">
            
            <%-- O título muda se estivermos editando --%>
            <h2>${editMode ? 'Editar Produto' : 'Cadastro de Produto'}</h2>

            <%-- Bloco de erro, se o servlet redirecionar para cá --%>
            <c:if test="${param.msg == 'erro_salvar'}">
                <p style="color:red; background:#ffe0e0; padding: 10px; border-radius: 5px;">
                    Erro ao salvar o produto. Verifique os campos e tente novamente.
                </p>
            </c:if>

            <%-- 
              5. CAMPO OCULTO (HIDDEN)
              Se estivermos em "Modo Edição", precisamos enviar o ID do produto
              para o servlet saber qual produto ele deve ATUALIZAR.
            --%>
            <c:if test="${editMode}">
                <input type="hidden" name="idProduto" value="${produto.idProduto}">
            </c:if>

            <%-- 
              6. CAMPOS PREENCHIDOS DINAMICAMENTE
              O atributo 'value' é preenchido com os dados do produto se 
              estivermos em "Modo Edição".
            --%>
            <label for="nomeProduto">Nome do Produto</label>
            <input type="text" id="nomeProduto" name="nomeProduto" value="${produto.nome}" required>

            <label for="descricaoProduto">Descrição</label>
            <textarea id="descricaoProduto" name="descricaoProduto" rows="3">${produto.descricao}</textarea>

            <label for="tamanhoProduto">Tamanho</label>
            <input type="text" id="tamanhoProduto" name="tamanhoProduto" value="${produto.tamanho}" required>

            <label for="fotoProduto">Foto</label>
            <%-- Mostra a foto atual se estivermos editando --%>
            <c:if test="${editMode && not empty produto.fotoBase64}">
                <div style="margin-bottom: 10px;">
                    <img src="${produto.fotoBase64}" alt="Foto Atual" style="width: 100px; height: 100px; object-fit: cover; border-radius: 8px;">
                    <br><small>Foto atual. Envie uma nova apenas se desejar substituí-la.</small>
                </div>
            </c:if>
            <%-- No Modo Edição, a foto não é 'required' --%>
            <input type="file" id="fotoProduto" name="fotoProduto" accept="image/*" ${editMode ? '' : 'required'}>

            <label for="quantidadeProduto">Estoque</label>
            <input type="number" id="quantidadeProduto" name="quantidadeProduto" value="${produto.estoque}" required>

            <label for="precoProduto">Preço</label>
            <input type="number" id="precoProduto" name="precoProduto" value="${produto.preco}" step="0.01" required>
            
            <div style="font-size: 0.9rem; color: #333; margin: -10px 0 15px 0; padding: 8px; background-color: #f4f4f4; border-radius: 4px;">
    Taxa da plataforma (30%): <strong><span id="taxaValor">R$ 0,00</span></strong>
    <br>
    Você receberá: <strong><span id="valorReceber">R$ 0,00</span></strong>
</div>


            <label for="prazoProduto">Prazo de locação</label>
            <select id="prazoProduto" name="prazoProduto" required>
                <option value="">Selecione...</option>
                <%-- JSTL seleciona a opção correta no Modo Edição --%>
                <option value="30" ${produto.prazoLocacaoDias == 30 ? 'selected' : ''}>30 dias</option>
                <option value="60" ${produto.prazoLocacaoDias == 60 ? 'selected' : ''}>60 dias</option>
            </select>

            <button type="submit">Salvar Produto</button>
        </form>
    </main>

    <%-- 7. Inclui o footer --%>
    <jsp:include page="partes/footer.jsp" />

    <%-- 
      8. SCRIPTS REMOVIDOS
      A lógica do 'produto.js' foi totalmente substituída pelo 
      ProdutoServlet e pela lógica JSTL desta página.
      
      <script src="../Scripts/produto.js"></script>
      <script src="../Scripts/usuario.js"></script>
    --%>
</body>
<script>
    document.addEventListener("DOMContentLoaded", () => {
        const inputPreco = document.getElementById("precoProduto");
        const spanTaxa = document.getElementById("taxaValor");
        const spanReceber = document.getElementById("valorReceber");
        const TAXA_PERCENTUAL = 0.18; // 18%

        function calcularComissao() {
            let preco = parseFloat(inputPreco.value) || 0;
            let taxa = preco * TAXA_PERCENTUAL;
            let recebido = preco - taxa;
            
            // Formata para R$ (Reais)
            spanTaxa.textContent = taxa.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
            spanReceber.textContent = recebido.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
        }

        // Calcula quando o usuário digita
        inputPreco.addEventListener("input", calcularComissao);
        
        // Calcula na carga da página (caso esteja em modo de edição)
        // Isso garante que o valor seja exibido se o formulário for pré-preenchido
        calcularComissao();
    });
</script>
</html>