package br.com.petshop.controller;

import br.com.petshop.model.Pet;
import br.com.petshop.repository.AgendamentoRepository;
import br.com.petshop.repository.PetRepository;
import br.com.petshop.repository.TutorRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.*;

@Controller
@RequestMapping("/pets")
public class PetController {
    private final PetRepository repo;
    private final TutorRepository tutores;
    private final AgendamentoRepository ags;

    public PetController(PetRepository repo, TutorRepository tutores, AgendamentoRepository ags) {
        this.repo = repo; this.tutores = tutores; this.ags = ags;
    }

    @GetMapping
    public String listar(@RequestParam(name = "busca", defaultValue = "") String busca, Model m) {
        m.addAttribute("pets", repo.findByNomeContainingIgnoreCaseOrderByNome(busca.trim()));
        m.addAttribute("busca", busca);
        return "pets/lista";
    }

    private String form(Pet p, Map<String, String> erros, Model m) {
        m.addAttribute("pet", p);
        m.addAttribute("erros", erros);
        m.addAttribute("tutores", tutores.findAll());
        return "pets/form";
    }

    @GetMapping("/novo")
    public String novo(Model m) { return form(new Pet(), new HashMap<>(), m); }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable("id") Long id, Model m, RedirectAttributes ra) {
        Optional<Pet> p = repo.findById(id);
        if (p.isEmpty()) { ra.addFlashAttribute("erro", "Pet não encontrado."); return "redirect:/pets"; }
        return form(p.get(), new HashMap<>(), m);
    }

    @PostMapping("/salvar")
    public String salvar(@RequestParam(name = "id", required = false) Long id,
                         @RequestParam("nome") String nome, @RequestParam("especie") String especie,
                         @RequestParam("raca") String raca, @RequestParam("idade") String idade,
                         @RequestParam(name = "tutorId", required = false) Long tutorId,
                         Model m, RedirectAttributes ra) {
        Pet p = new Pet();
        p.id = id;
        p.nome = nome.trim();
        p.especie = especie.trim();
        p.raca = raca.trim();
        Map<String, String> erros = new HashMap<>();
        if (p.nome.isEmpty()) erros.put("nome", "Informe o nome do pet.");
        if (p.especie.isEmpty()) erros.put("especie", "Informe a espécie.");
        try {
            p.idade = Integer.parseInt(idade.trim());
            if (p.idade < 0 || p.idade > 40) erros.put("idade", "Idade deve estar entre 0 e 40.");
        } catch (NumberFormatException e) {
            erros.put("idade", "Informe a idade em anos (número inteiro).");
        }
        if (tutorId != null) p.tutor = tutores.findById(tutorId).orElse(null);
        if (p.tutor == null) erros.put("tutor", "Selecione o tutor.");

        if (!erros.isEmpty()) return form(p, erros, m);
        repo.save(p);
        ra.addFlashAttribute("ok", "Pet salvo com sucesso.");
        return "redirect:/pets";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable("id") Long id, RedirectAttributes ra) {
        if (ags.existsByPetId(id)) {
            ra.addFlashAttribute("erro", "Não é possível excluir: o pet possui agendamentos.");
        } else {
            repo.deleteById(id);
            ra.addFlashAttribute("ok", "Pet excluído.");
        }
        return "redirect:/pets";
    }
}
