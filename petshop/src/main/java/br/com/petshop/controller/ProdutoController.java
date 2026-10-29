package br.com.petshop.controller;

import br.com.petshop.model.Produto;
import br.com.petshop.repository.ProdutoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {
    private final ProdutoRepository repo;

    public ProdutoController(ProdutoRepository repo) { this.repo = repo; }

    @GetMapping
    public String listar(@RequestParam(name = "busca", defaultValue = "") String busca, Model m) {
        m.addAttribute("produtos", repo.findByNomeContainingIgnoreCaseOrderByNome(busca.trim()));
        m.addAttribute("busca", busca);
        return "produtos/lista";
    }

    @GetMapping("/novo")
    public String novo(Model m) {
        m.addAttribute("produto", new Produto());
        m.addAttribute("erros", new HashMap<String, String>());
        return "produtos/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable("id") Long id, Model m, RedirectAttributes ra) {
        Optional<Produto> p = repo.findById(id);
        if (p.isEmpty()) { ra.addFlashAttribute("erro", "Produto não encontrado."); return "redirect:/produtos"; }
        m.addAttribute("produto", p.get());
        m.addAttribute("erros", new HashMap<String, String>());
        return "produtos/form";
    }

    @PostMapping("/salvar")
    public String salvar(@RequestParam(name = "id", required = false) Long id,
                         @RequestParam("nome") String nome, @RequestParam("preco") String preco,
                         @RequestParam("estoque") String estoque, Model m, RedirectAttributes ra) {
        Produto p = new Produto();
        p.id = id;
        p.nome = nome.trim();
        Map<String, String> erros = new HashMap<>();
        if (p.nome.isEmpty()) erros.put("nome", "Informe o nome do produto.");
        try {
            p.preco = new BigDecimal(preco.trim().replace(",", "."));
            if (p.preco.signum() < 0) erros.put("preco", "O preço não pode ser negativo.");
        } catch (NumberFormatException e) {
            erros.put("preco", "Preço inválido (ex.: 25,90).");
        }
        try {
            p.estoque = Integer.parseInt(estoque.trim());
            if (p.estoque < 0) erros.put("estoque", "O estoque não pode ser negativo.");
        } catch (NumberFormatException e) {
            erros.put("estoque", "Estoque inválido (número inteiro).");
        }
        if (!erros.isEmpty()) {
            m.addAttribute("produto", p);
            m.addAttribute("erros", erros);
            return "produtos/form";
        }
        repo.save(p);
        ra.addFlashAttribute("ok", "Produto salvo com sucesso.");
        return "redirect:/produtos";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable("id") Long id, RedirectAttributes ra) {
        repo.deleteById(id);
        ra.addFlashAttribute("ok", "Produto excluído.");
        return "redirect:/produtos";
    }
}
