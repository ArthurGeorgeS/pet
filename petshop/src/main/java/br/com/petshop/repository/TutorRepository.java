package br.com.petshop.repository;

import br.com.petshop.model.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TutorRepository extends JpaRepository<Tutor, Long> {
    // Busca apenas por NOME (CPF/telefone sao criptografados e nao aceitam LIKE no SQL)
    List<Tutor> findByNomeContainingIgnoreCaseOrderByNome(String nome);
}
