package br.com.petshop.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Validação de sessão NO SERVIDOR em TODA requisição.
 * Se passaram mais de 30 s desde a última atividade, a sessão é destruída
 * e o usuário é enviado ao login — mesmo que o JavaScript do navegador
 * tenha sido manipulado ou pausado.
 */
public class SessaoInterceptor implements HandlerInterceptor {

    public static final int LIMITE_SEGUNDOS = 30;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        HttpSession s = req.getSession(false);
        if (s == null || s.getAttribute("usuario") == null) {
            res.sendRedirect("/login");
            return false;
        }
        long agora = System.currentTimeMillis();
        Long ultimo = (Long) s.getAttribute("ultimo");
        if (ultimo == null || agora - ultimo > LIMITE_SEGUNDOS * 1000L) {
            s.invalidate();
            res.sendRedirect("/login?expirada");
            return false;
        }
        s.setAttribute("ultimo", agora); // renova a contagem
        return true;
    }
}
