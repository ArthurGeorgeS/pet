package br.com.petshop.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "produtos")
public class Produto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, length = 100)
    public String nome;

    @Column(nullable = false, precision = 10, scale = 2)
    public BigDecimal preco;

    @Column(nullable = false)
    public Integer estoque;
}
