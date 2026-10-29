package br.com.petshop.controller;

import br.com.petshop.model.Agendamento;
import br.com.petshop.repository.AgendamentoRepository;
import br.com.petshop.repository.PetRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Controller
@RequestMapping("/agendamentos")
public class AgendamentoController {
    private final AgendamentoRepository repo;
    private final PetRepository pets;

    public AgendamentoController(AgendamentoRepository repo, PetRepository pets) {
        this.repo = repo; this.pets = pets;
    }

    @GetMapping
    public String listar(@RequestParam(name = "busca", defaultValue = "") String busca, Model m) {
        m.addAttribute("agendamentos", repo.findByPetNomeContainingIgnoreCaseOrderByDataHoraDesc(busca.trim()));
        m.addAttribute("busca", busca);
        return "agendamentos/lista";
    }

    private String form(Agendamento a, Map<String, String> erros, Model m) {
        m.addAttribute("ag", a);
        m.addAttribute("erros", erros);
        m.addAttribute("pets", pets.findAll());
        m.addAttribute("servicos", Agendamento.SERVICOS);
        m.addAttribute("statusList", Agendamento.STATUS);
        return "agendamentos/form";
    }

    @GetMapping("/novo")
    public String novo(Model m) {
        Agendamento a = new Agendamento();
        a.status = "AGENDADO";
        return form(a, new HashMap<>(), m);
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable("id") Long id, Model m, RedirectAttributes ra) {
        Optional<Agendamento> a = repo.findById(id);
        if (a.isEmpty()) { ra.addFlashAttribute("erro", "Agendamento não encontrado."); return "redirect:/agendamentos"; }
        return form(a.get(), new HashMap<>(), m);
    }

    @PostMapping("/salvar")
    public String salvar(@RequestParam(name = "id", required = false) Long id,
                         @RequestParam(name = "petId", required = false) Long petId,
                         @RequestParam("servico") String servico, @RequestParam("dataHora") String dataHora,
                         @RequestParam("status") String status, Model m, RedirectAttributes ra) {
        Agendamento a = new Agendamento();
        a.id = id;
        a.servico = servico;
        a.status = status;
        Map<String, String> erros = new HashMap<>();

        if (petId != null) a.pet = pets.findById(petId).orElse(null);
        if (a.pet == null) erros.put("pet", "Selecione o pet.");
        if (!Arrays.asList(Agendamento.SERVICOS).contains(servico)) erros.put("servico", "Serviço inválido.");
        if (!Arrays.asList(Agendamento.STATUS).contains(status)) erros.put("status", "Status inválido.");

        try {
            a.dataHora = LocalDateTime.parse(dataHora.trim()).truncatedTo(ChronoUnit.MINUTES);
            if (id == null && a.dataHora.isBefore(LocalDateTime.now())) {
                erros.put("dataHora", "Não é possível agendar no passado.");
            } else if (!"CANCELADO".equals(status) && repo.existeConflito(a.dataHora, id)) {
                erros.put("dataHora", "Já existe um agendamento neste horário.");
            }
        } catch (DateTimeParseException e) {
            erros.put("dataHora", "Informe data e horário válidos.");
        }

        if (!erros.isEmpty()) return form(a, erros, m);
        repo.save(a);
        ra.addFlashAttribute("ok", "Agendamento salvo com sucesso.");
        return "redirect:/agendamentos";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable("id") Long id, RedirectAttributes ra) {
        repo.deleteById(id);
        ra.addFlashAttribute("ok", "Agendamento excluído.");
        return "redirect:/agendamentos";
    }
}
