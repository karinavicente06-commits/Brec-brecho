<%-- /perfil.jsp (Versão Corrigida para Admin) --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%-- 1. VERIFICAÇÃO DE SEGURANÇA --%>
<c:if test="${empty sessionScope.usuarioLogado}">
    <% response.sendRedirect("login.jsp?msg=acesso_negado"); %>
</c:if>

<%-- 2. PREPARAÇÃO DE DADOS --%>
<c:set var="usuario" value="${sessionScope.usuarioLogado}" />
<c:set var="tipo" value="${sessionScope.tipoUsuario}" />

<!doctype html>
<html lang="pt-br">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width,initial-scale=1">
    <title>Brec Brechó — Meu Perfil</title>
    <link rel="stylesheet" href="css/Styles.css">
</head>
<body>

    <jsp:include page="partes/header.jsp" />

    <main>
        <%-- 
          Se o usuário for Admin, desabilitamos o formulário.
          Usamos <c:set> para criar uma variável 'isFormDisabled'.
        --%>
        <c:set var="isFormDisabled" value="${tipo == 'admin' ? 'disabled' : ''}" />
        
        <form id="form-perfil" class="formulario" action="salvarPerfil" method="POST">
            <h2>Meu Perfil</h2>
            
            <%-- Mensagens de Sucesso/Erro --%>
            <c:if test="${param.msg == 'sucesso'}">
                <p style="color:green; background:#e0ffe0; padding: 10px; border-radius: 5px;">Perfil atualizado com sucesso!</p>
            </c:if>
            <c:if test="${param.msg == 'erro_atualizar' || param.msg == 'erro_excecao'}">
                <p style="color:red; background:#ffe0e0; padding: 10px; border-radius: 5px;">Erro ao atualizar o perfil. Tente novamente.</p>
            </c:if>
            
            <%-- Aviso para o Admin --%>
            <c:if test="${tipo == 'admin'}">
                <p style="color:#333; background:#fffbe0; padding: 10px; border-radius: 5px; border: 1px solid #eee8aa;">
                    O perfil de Administrador não pode ser editado nesta tela.
                </p>
            </c:if>

            <%-- 
              CAMPO NOME (CORRIGIDO)
              Checa por 'fornecedor', senão usa '.nome' (que Cliente e Admin têm).
            --%>
            <label for="nome">Nome</label>
            <input type="text" id="nome" name="nome" 
                   value="${tipo == 'fornecedor' ? usuario.nomeLoja : usuario.nome}" 
                   required ${isFormDisabled}>

            <label for="email">Email</label>
            <input type="email" id="email" name="email" value="${usuario.email}" required ${isFormDisabled}>

            <%-- 
              CAMPOS DE ENDEREÇO (CORRIGIDOS)
              Estes campos agora só aparecem se o tipo NÃO for 'admin'.
            --%>
            <c:if test="${tipo != 'admin'}">
                <label for="cep">CEP</label>
                <input type="text" id="cep" name="cep" value="${usuario.cep}" required ${isFormDisabled}>

                <label for="rua">Rua</label>
                <input type="text" id="rua" name="rua" value="${usuario.rua}" required ${isFormDisabled}>

                <label for="numero">Número</label>
                <input type="text" id="numero" name="numero" value="${usuario.numero}" required ${isFormDisabled}>

                <label for="bairro">Bairro</label>
                <input type="text" id="bairro" name="bairro" value="${usuario.bairro}" required ${isFormDisabled}>

                <label for="cidade">Cidade</label>
                <input type="text" id="cidade" name="cidade" value="${usuario.cidade}" required ${isFormDisabled}>

                <label for="estado">Estado</label>
                <input type="text" id="estado" name="estado" value="${usuario.estado}" required ${isFormDisabled}>
            </c:if>

            <%-- 
              CAMPOS DE FORNECEDOR (Já estava correto)
              Só aparece se o tipo for 'fornecedor'.
            --%>
            <c:if test="${tipo == 'fornecedor'}">
                <div id="campo-telefone">
                    <label for="telefone">Telefone</label>
                    <input type="text" id="telefone" name="telefone" value="${usuario.telefone}" ${isFormDisabled}>
                </div>
                <div id="campo-descricao">
                    <label for="descricao">Descrição</label>
                    <textarea id="descricao" name="descricao" rows="3" ${isFormDisabled}>${usuario.descricao}</textarea>
                </div>
            </c:if>

            <label for="senha">Senha</label>
            <input type="password" id="senha" name="senha" value="${usuario.senha}" placeholder="Mínimo 6 caracteres" required ${isFormDisabled}>
            <small style="color: gray;">
                A senha deve conter pelo menos 6 caracteres, uma letra maiúscula, uma minúscula e um caractere especial.
            </small>

            <button type="submit" ${isFormDisabled}>Salvar Alterações</button>
        </form>
    </main>

    <jsp:include page="partes/footer.jsp" />

    <%-- 
      Scripts de UI (MANTIDOS)
      Nós só ativamos o listener de CEP se o campo de CEP existir (ou seja, se não for admin)
    --%>
    <script>
        document.addEventListener("DOMContentLoaded", () => {
            const formPerfil = document.getElementById("form-perfil");
            const inputCep = document.getElementById('cep'); // Pode ser null se for admin
            
            function validarSenha(senha) {
                const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*[\W_]).{6,}$/;
                return regex.test(senha);
            }
            
            async function buscarCEP(cep) {
                try {
                    const resposta = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
                    const dados = await resposta.json();
                    if (!dados.erro) {
                        document.getElementById("rua").value = data.logradouro || "";
                        document.getElementById("bairro").value = data.bairro || "";
                        document.getElementById("cidade").value = data.localidade || "";
                        document.getElementById("estado").value = data.uf || "";
                    }
                } catch (error) {
                    console.error("Erro ao buscar CEP:", error);
                }
            }
            
            // Só adiciona o listener de CEP se o campo existir
            if (inputCep) {
                inputCep.addEventListener("blur", (e) => {
                    const cep = e.target.value.replace(/\D/g, "");
                    if (cep.length === 8) {
                        buscarCEP(cep);
                    }
                });
            }
            
            formPerfil.addEventListener("submit", (e) => {
                const senha = document.getElementById("senha").value.trim();
                if (!validarSenha(senha)) {
                    e.preventDefault(); 
                    alert("Senha inválida! Deve ter no mínimo 6 caracteres, com maiúscula, minúscula e caractere especial.");
                    return;
                }
            });
        });
    </script>
</body>
</html>