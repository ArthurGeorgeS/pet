package br.com.petshop.repository;

import br.com.petshop.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PetRepository extends JpaRepository<Pet, Long> {
    List<Pet> findByNomeContainingIgnoreCaseOrderByNome(String nome);
    boolean existsByTutorId(Long tutorId);
}
