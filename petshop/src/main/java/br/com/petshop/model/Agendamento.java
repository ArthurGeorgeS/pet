package br.com.petshop.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "agendamentos")
public class Agendamento {
    public static final String[] SERVICOS = {"BANHO", "TOSA", "BANHO_E_TOSA"};
    public static final String[] STATUS = {"AGENDADO", "EM_ANDAMENTO", "CONCLUIDO", "CANCELADO"};

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pet_id", nullable = false)
    public Pet pet;

    @Column(nullable = false, length = 20)
    public String servico;

    @Column(nullable = false)
    public LocalDateTime dataHora;

    @Column(nullable = false, length = 20)
    public String status;
}
