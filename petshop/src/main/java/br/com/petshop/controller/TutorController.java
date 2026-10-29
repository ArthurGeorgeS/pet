package br.com.petshop.controller;

import br.com.petshop.model.Tutor;
import br.com.petshop.repository.PetRepository;
import br.com.petshop.repository.TutorRepository;
import br.com.petshop.util.Validador;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.*;

@Controller
@RequestMapping("/tutores")
public class TutorController {
    private final TutorRepository repo;
    private final PetRepository pets;

    public TutorController(TutorRepository repo, PetRepository pets) {
        this.repo = repo; this.pets = pets;
    }

    @GetMapping
    public String listar(@RequestParam(name = "busca", defaultValue = "") String busca, Model m) {
        m.addAttribute("tutores", repo.findByNomeContainingIgnoreCaseOrderByNome(busca.trim()));
        m.addAttribute("busca", busca);
        return "tutores/lista";
    }

    @GetMapping("/novo")
    public String novo(Model m) {
        m.addAttribute("tutor", new Tutor());
        m.addAttribute("erros", new HashMap<String, String>());
        return "tutores/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable("id") Long id, Model m, RedirectAttributes ra) {
        Optional<Tutor> t = repo.findById(id);
        if (t.isEmpty()) { ra.addFlashAttribute("erro", "Tutor não encontrado."); return "redirect:/tutores"; }
        m.addAttribute("tutor", t.get());
        m.addAttribute("erros", new HashMap<String, String>());
        return "tutores/form";
    }

    @PostMapping("/salvar")
    public String salvar(@RequestParam(name = "id", required = false) Long id,
                         @RequestParam("nome") String nome, @RequestParam("telefone") String telefone,
                         @RequestParam("email") String email, @RequestParam("cpf") String cpf,
                         @RequestParam("endereco") String endereco, Model m, RedirectAttributes ra) {
        Tutor t = new Tutor();
        t.id = id;
        t.nome = nome.trim();
        t.telefone = Validador.digitos(telefone);
        t.email = email.trim();
        t.cpf = Validador.digitos(cpf);
        t.endereco = endereco.trim();

        Map<String, String> erros = new HashMap<>();
        if (t.nome.isEmpty()) erros.put("nome", "Informe o nome.");
        if (!Validador.telefoneValido(t.telefone)) erros.put("telefone", "Telefone inválido (use DDD + número).");
        if (!Validador.emailValido(t.email)) erros.put("email", "E-mail inválido.");
        if (!Validador.cpfValido(t.cpf)) erros.put("cpf", "CPF inválido.");
        else {
            // CPF é criptografado (IV aleatório), então a unicidade é checada em memória
            boolean duplicado = repo.findAll().stream()
                    .anyMatch(o -> t.cpf.equals(o.cpf) && !Objects.equals(o.id, t.id));
            if (duplicado) erros.put("cpf", "Já existe um tutor com este CPF.");
        }
        if (t.endereco.isEmpty()) erros.put("endereco", "Informe o endereço.");

        if (!erros.isEmpty()) {
            m.addAttribute("tutor", t);
            m.addAttribute("erros", erros);
            return "tutores/form";
        }
        repo.save(t);
        ra.addFlashAttribute("ok", "Tutor salvo com sucesso.");
        return "redirect:/tutores";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable("id") Long id, RedirectAttributes ra) {
        if (pets.existsByTutorId(id)) {
            ra.addFlashAttribute("erro", "Não é possível excluir: o tutor possui pets cadastrados.");
        } else {
            repo.deleteById(id);
            ra.addFlashAttribute("ok", "Tutor excluído.");
        }
        return "redirect:/tutores";
    }
}
