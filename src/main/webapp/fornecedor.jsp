<%-- /fornecedor.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!doctype html>
<html lang="pt-br">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Brec Brechó — Cadastro Fornecedor</title>
    <link rel="stylesheet" href="css/Styles.css"> <%-- Caminho ajustado --%>
</head>
<body>

    <%-- 1. Inclui o header --%>
    <jsp:include page="partes/header.jsp" />

    <main>
        <%-- Bloco para exibir erro, se o Servlet redirecionar com ?msg=erro --%>
        <c:if test="${param.msg == 'erro' || param.msg == 'erro_excecao'}">
            <p style="color:red; text-align:center; background:#ffe0e0; padding: 10px; border-radius: 5px; max-width: 500px; margin: 1rem auto;">
                Ocorreu um erro ao tentar seu cadastro. Verifique seus dados (email ou CPF/CNPJ podem já existir) e tente novamente.
            </p>
        </c:if>

        <%-- 
          MUDANÇA CRÍTICA:
          1. action="cadastrarFornecedor" (aponta para o @WebServlet do FornecedorServlet)
          2. method="POST"
        --%>
        <form id="form-fornecedor" class="formulario" action="cadastrarFornecedor" method="POST">
            <h2>Cadastro de Fornecedor</h2>

            <%-- Os 'name' dos inputs (ex: name="nomeFornecedor") devem bater
                 exatamente com o que o FornecedorServlet espera no request.getParameter() --%>
            <label for="nomeFornecedor">Nome / Loja</label>
            <input type="text" id="nomeFornecedor" name="nomeFornecedor" required>

            <label for="emailFornecedor">Email</label>
            <input type="email" id="emailFornecedor" name="emailFornecedor" required>

            <label for="cpfCnpjFornecedor">CPF/CNPJ</label>
            <input type="text" id="cpfCnpjFornecedor" name="cpfCnpjFornecedor" required>

            <label for="cepFornecedor">CEP</label>
            <input type="text" id="cepFornecedor" name="cepFornecedor" required>

            <label for="ruaFornecedor">Rua</label>
            <input type="text" id="ruaFornecedor" name="ruaFornecedor" required>

            <label for="numeroFornecedor">Número</label>
            <input type="text" id="numeroFornecedor" name="numeroFornecedor" required>

            <label for="bairroFornecedor">Bairro</label>
            <input type="text" id="bairroFornecedor" name="bairroFornecedor" required>

            <label for="cidadeFornecedor">Cidade</label>
            <input type="text" id="cidadeFornecedor" name="cidadeFornecedor" required>

            <label for="estadoFornecedor">Estado</label>
            <input type="text" id="estadoFornecedor" name="estadoFornecedor" required>

            <label for="telefoneFornecedor">Telefone</label>
            <input type="text" id="telefoneFornecedor" name="telefoneFornecedor">

            <label for="descricaoFornecedor">Descrição</label>
            <textarea id="descricaoFornecedor" name="descricaoFornecedor" rows="3"></textarea>

            <label for="senhaFornecedor">Senha</label>
            <input type="password" id="senhaFornecedor" name="senhaFornecedor" placeholder="Mínimo 6 caracteres" required>
            <small style="color: gray;">A senha deve conter pelo menos 6 caracteres, uma letra maiúscula, uma minúscula e um caractere especial.</small>

            <button type="submit">Cadastrar</button>
        </form>
    </main>

    <%-- 2. Inclui o footer --%>
    <jsp:include page="partes/footer.jsp" />

    <%-- 
      3. SCRIPTS DE UI (VALIDAÇÃO) - MANTIDOS
    --%>
    <script>
        const inputCep = document.getElementById('cepFornecedor');
        const form = document.getElementById('form-fornecedor');

        // ViaCEP
        inputCep.addEventListener('blur', () => {
            let cep = inputCep.value.replace(/\D/g, '');
            if (cep.length === 8) {
                fetch(`https://viacep.com.br/ws/${cep}/json/`)
                    .then(res => res.json())
                    .then(data => {
                        if (!data.erro) {
                            document.getElementById('ruaFornecedor').value = data.logradouro || "";
                            document.getElementById('bairroFornecedor').value = data.bairro || "";
                            document.getElementById('cidadeFornecedor').value = data.localidade || "";
                            document.getElementById('estadoFornecedor').value = data.uf || "";
                        } else {
                            alert("CEP não encontrado!");
                        }
                    })
                    .catch(err => console.error("Erro ao buscar CEP:", err));
            }
        });

        // Validação de Senha
        form.addEventListener('submit', function (event) {
            const senha = document.getElementById('senhaFornecedor').value;
            const regexSenha = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{6,}$/;
            if (!regexSenha.test(senha)) {
                event.preventDefault();
                alert("A senha deve ter no mínimo 6 caracteres, incluindo letra maiúscula, letra minúscula e caractere especial.");
            }
        });
    </script>
    
    <%-- 
      4. SCRIPTS DE LÓGICA - REMOVIDOS
      <script src="../Scripts/fornecedor.js"></script>
      <script src="../Scripts/usuario.js"></script>
    --%>
</body>
</html>