package br.com.petshop.controller;

import br.com.petshop.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.time.LocalDate;

@Controller
public class DashboardController {
    private final TutorRepository tutores;
    private final PetRepository pets;
    private final ProdutoRepository produtos;
    private final AgendamentoRepository ags;

    public DashboardController(TutorRepository t, PetRepository p, ProdutoRepository pr, AgendamentoRepository a) {
        this.tutores = t; this.pets = p; this.produtos = pr; this.ags = a;
    }

    @GetMapping("/")
    public String painel(Model m) {
        LocalDate hoje = LocalDate.now();
        m.addAttribute("agHoje", ags.findByDataHoraGreaterThanEqualAndDataHoraLessThanOrderByDataHora(
                hoje.atStartOfDay(), hoje.plusDays(1).atStartOfDay()));
        m.addAttribute("totalTutores", tutores.count());
        m.addAttribute("totalPets", pets.count());
        m.addAttribute("estoqueBaixo", produtos.countByEstoqueLessThan(5));
        return "painel";
    }

    @GetMapping("/ping")
    @org.springframework.web.bind.annotation.ResponseBody
    public String ping() { // chamado pelo JS a cada atividade: renova a sessão no servidor
        return "ok";
    }
}
