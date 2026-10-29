package br.com.petshop.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pets")
public class Pet {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, length = 80)
    public String nome;

    @Column(nullable = false, length = 40)
    public String especie;

    @Column(length = 60)
    public String raca;

    @Column(nullable = false)
    public Integer idade;

    @ManyToOne(optional = false)
    @JoinColumn(name = "tutor_id", nullable = false)
    public Tutor tutor;
}
