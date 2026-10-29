package br.com.petshop.repository;

import br.com.petshop.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    List<Agendamento> findByPetNomeContainingIgnoreCaseOrderByDataHoraDesc(String nome);
    List<Agendamento> findByDataHoraGreaterThanEqualAndDataHoraLessThanOrderByDataHora(LocalDateTime ini, LocalDateTime fim);
    boolean existsByPetId(Long petId);

    /** Conflito: mesmo horario, ignorando cancelados e o proprio registro (em edicao). */
    @Query("select count(a) > 0 from Agendamento a where a.dataHora = :dt and a.status <> 'CANCELADO' "
         + "and (:id is null or a.id <> :id)")
    boolean existeConflito(@Param("dt") LocalDateTime dt, @Param("id") Long id);
}
