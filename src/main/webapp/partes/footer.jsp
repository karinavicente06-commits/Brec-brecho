<%-- /partes/footer.jsp (Versão Corrigida e Limpa) --%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<footer>
    <%-- 1. Linha de Copyright (com o ano dinâmico) --%>
    © <span id="ano"><%= java.time.Year.now().getValue() %></span> Brec Brechó — Feito com propósito
    
    <%-- 2. Links de Suporte (SAC) --%>
    <p style="margin-top: 10px; display:flex; gap: 15px; justify-content: center;">
        
        <%-- Link do WhatsApp (usei o seu número específico) --%>
        <a href="https://wa.me/5547996654584" target="_blank" style="text-decoration: none; color: #25D366; font-weight: bold;">
            Atendimento via WhatsApp
        </a>
        
        <%-- Link do Formulário de Contato --%>
        <a href="contato.jsp" style="text-decoration: none; font-weight: bold;">
            Fale Conosco
        </a>
    </p>
    
    <%-- 3. Link "Voltar ao topo" --%>
    <p><a href="#topo">Voltar ao topo</a></p>
</footer>

<%-- Script auxiliar (comentado) --%>
<script>
    // O código Java <%= java.time.Year.now().getValue() %> já preenche o ano.
    // document.getElementById("ano").textContent = new Date().getFullYear(); 
</script>