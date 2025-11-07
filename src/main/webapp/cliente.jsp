<%-- /cliente.jsp --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!doctype html>
<html lang="pt-br">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Brec Brechó — Cadastro Cliente</title>
    <link rel="stylesheet" href="css/Styles.css"> <%-- Caminho ajustado --%>
</head>
<body>

    <%-- 1. Inclui o header --%>
    <jsp:include page="partes/header.jsp" />

    <main>
        <%-- Bloco para exibir erro, se o Servlet redirecionar com ?msg=erro --%>
        <c:if test="${param.msg == 'erro' || param.msg == 'erro_excecao'}">
            <p style="color:red; text-align:center; background:#ffe0e0; padding: 10px; border-radius: 5px; max-width: 500px; margin: 1rem auto;">
                Ocorreu um erro ao tentar seu cadastro. Verifique seus dados (email ou CPF podem já existir) e tente novamente.
            </p>
        </c:if>
        
        <%-- 
          MUDANÇA CRÍTICA:
          1. action="cadastrarCliente" (aponta para o @WebServlet do ClienteServlet)
          2. method="POST"
        --%>
        <form id="form-cliente" class="formulario" action="cadastrarCliente" method="POST">
            <h2>Cadastro de Cliente</h2>

            <label for="nome">Nome</label>
            <input type="text" id="nome" name="nome" placeholder="Digite seu nome" required>

            <label for="email">Email</label>
            <input type="email" id="email" name="email" placeholder="exemplo@email.com" required>

            <label for="cpf">CPF</label>
            <input type="text" id="cpf" name="cpf" placeholder="000.000.000-00" required>

            <label for="cep">CEP</label>
            <input type="text" id="cep" name="cep" placeholder="Ex: 89010-000" required>

            <label for="rua">Rua</label>
            <input type="text" id="rua" name="rua" required>

            <label for="numero">Número</label>
            <input type="text" id="numero" name="numero" required>

            <label for="bairro">Bairro</label>
            <input type="text" id="bairro" name="bairro" required>

            <label for="cidade">Cidade</label>
            <input type="text" id="cidade" name="cidade" required>

            <label for="estado">Estado</label>
            <input type="text" id="estado" name="estado" required>

            <label for="senha">Senha</label>
            <input type="password" id="senha" name="senha" placeholder="Mínimo 6 caracteres" required>
            <small style="color: gray;">A senha deve conter pelo menos 6 caracteres, uma letra maiúscula, uma minúscula e um caractere especial.</small>

            <button type="submit">Cadastrar</button>
        </form>
    </main>

    <%-- 2. Inclui o footer --%>
    <jsp:include page="partes/footer.jsp" />

    <%-- 
      3. SCRIPTS DE UI (VALIDAÇÃO) - MANTIDOS
      Estes scripts rodam no navegador do cliente e são boas práticas.
    --%>
    <script>
        const inputCep = document.getElementById('cep');
        const form = document.getElementById('form-cliente');

        // Auto preencher endereço via CEP
        inputCep.addEventListener('blur', () => {
            let cep = inputCep.value.replace(/\D/g, '');
            if (cep.length === 8) {
                fetch(`https://viacep.com.br/ws/${cep}/json/`)
                    .then(res => res.json())
                    .then(data => {
                        if (!data.erro) {
                            document.getElementById('rua').value = data.logradouro || "";
                            document.getElementById('bairro').value = data.bairro || "";
                            document.getElementById('cidade').value = data.localidade || "";
                            document.getElementById('estado').value = data.uf || "";
                        } else {
                            alert("CEP não encontrado!");
                        }
                    })
                    .catch(err => console.error("Erro ao buscar CEP:", err));
            }
        });

        // Validação de senha (Cliente-side)
        form.addEventListener('submit', function (event) {
            const senha = document.getElementById('senha').value;
            const regexSenha = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).{6,}$/;

            if (!regexSenha.test(senha)) {
                event.preventDefault();
                alert("A senha deve ter no mínimo 6 caracteres, incluindo letra maiúscula, letra minúscula e caractere especial.");
            }
        });
    </script>
    
    <%-- 
      4. SCRIPTS DE LÓGICA - REMOVIDOS
      <script src="../Scripts/cliente.js"> </script>
      <script src="../Scripts/usuario.js"></script>
    --%>
</body>
</html>