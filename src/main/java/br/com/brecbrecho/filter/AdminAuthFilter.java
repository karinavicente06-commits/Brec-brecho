package br.com.brecbrecho.filter;

import java.io.IOException;

// Imports Jakarta EE (Tomcat 10+)
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter; // Importante
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Este Filtro de Segurança intercepta TODAS as requisições
 * que tentam acessar qualquer URL que comece com "/admin/"
 */
@WebFilter("/admin/*") // <-- A MÁGICA ACONTECE AQUI
public class AdminAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        // Converte os objetos genéricos para HTTP
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Pega a sessão (sem criar uma nova)
        HttpSession session = httpRequest.getSession(false);

        boolean isAdmin = false;

        // 1. Verifica se a sessão existe e se o usuário é "admin"
        if (session != null && "admin".equals(session.getAttribute("tipoUsuario"))) {
            isAdmin = true;
        }

        // 2. Lógica de Autorização
        if (isAdmin) {
            // --- USUÁRIO AUTORIZADO ---
            // Deixa a requisição continuar para o seu destino (ex: /admin/verMensagens.jsp)
            chain.doFilter(request, response);
        } else {
            // --- USUÁRIO NÃO AUTORIZADO ---
            // Redireciona para a página de login com uma mensagem de erro
            // httpRequest.getContextPath() pega a URL base (ex: /Brec_Brecho)
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp?msg=acesso_negado");
        }
    }

    // Métodos init e destroy (não precisamos deles agora, mas são obrigatórios)
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Pode ser usado para carregar configurações
    }

    @Override
    public void destroy() {
        // Pode ser usado para limpar recursos
    }
}