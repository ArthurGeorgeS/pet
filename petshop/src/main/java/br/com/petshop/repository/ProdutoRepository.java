package br.com.petshop.repository;

import br.com.petshop.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByNomeContainingIgnoreCaseOrderByNome(String nome);
    long countByEstoqueLessThan(int limite);
}
