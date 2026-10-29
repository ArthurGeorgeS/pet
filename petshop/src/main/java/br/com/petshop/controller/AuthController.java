package br.com.petshop.controller;

import br.com.petshop.model.Usuario;
import br.com.petshop.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UsuarioRepository repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthController(UsuarioRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/login")
    public String loginForm(@RequestParam(name = "expirada", required = false) String expirada,
                            @RequestParam(name = "saiu", required = false) String saiu, Model m) {
        if (repo.count() == 0) return "redirect:/setup";
        m.addAttribute("expirada", expirada != null);
        m.addAttribute("saiu", saiu != null);
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username, @RequestParam("senha") String senha,
                        HttpServletRequest req, Model m) {
        Usuario u = repo.findByUsername(username.trim()).orElse(null);
        // mensagem generica: nao revela se o usuario existe
        if (u == null || !encoder.matches(senha, u.senhaHash)) {
            m.addAttribute("erro", "Usuário ou senha inválidos.");
            return "login";
        }
        HttpSession antiga = req.getSession(false);
        if (antiga != null) antiga.invalidate(); // evita fixação de sessão
        HttpSession s = req.getSession(true);
        // O interceptador aplica o limite de 30 s e preserva a mensagem de expiração.
        // O container faz apenas a limpeza de sessões abandonadas após 60 s.
        s.setMaxInactiveInterval(60);
        s.setAttribute("usuario", u.username);
        s.setAttribute("ultimo", System.currentTimeMillis());
        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(@RequestParam(name = "motivo", required = false) String motivo, HttpServletRequest req) {
        HttpSession s = req.getSession(false);
        if (s != null) s.invalidate();
        return "inatividade".equals(motivo) ? "redirect:/login?expirada" : "redirect:/login?saiu";
    }

    // Cadastro inicial do administrador: só funciona enquanto NÃO existe nenhum usuário
    @GetMapping("/setup")
    public String setupForm() {
        return repo.count() > 0 ? "redirect:/login" : "setup";
    }

    @PostMapping("/setup")
    public String setup(@RequestParam("username") String username, @RequestParam("senha") String senha,
                        @RequestParam("confirma") String confirma, Model m) {
        if (repo.count() > 0) return "redirect:/login";
        String u = username.trim();
        if (u.length() < 3) {
            m.addAttribute("erro", "O usuário precisa ter ao menos 3 caracteres.");
        } else if (senha.length() < 6) {
            m.addAttribute("erro", "A senha precisa ter ao menos 6 caracteres.");
        } else if (!senha.equals(confirma)) {
            m.addAttribute("erro", "A confirmação não confere com a senha.");
        } else {
            Usuario novo = new Usuario();
            novo.username = u;
            novo.senhaHash = encoder.encode(senha);
            repo.save(novo);
            return "redirect:/login";
        }
        return "setup";
    }
}
