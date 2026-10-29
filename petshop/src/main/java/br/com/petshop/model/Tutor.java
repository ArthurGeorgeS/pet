package br.com.petshop.model;

import br.com.petshop.security.CriptoConverter;
import br.com.petshop.util.Validador;
import jakarta.persistence.*;

@Entity
@Table(name = "tutores")
public class Tutor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, length = 120)
    public String nome;

    @Convert(converter = CriptoConverter.class) // criptografado
    @Column(nullable = false, length = 500)
    public String telefone;

    @Column(nullable = false, length = 120)
    public String email;

    @Convert(converter = CriptoConverter.class) // criptografado
    @Column(nullable = false, length = 500)
    public String cpf;

    @Column(nullable = false, length = 250)
    public String endereco;

    public String cpfMascarado() {
        return (cpf == null || cpf.length() != 11) ? "" : "***.***.***-" + cpf.substring(9);
    }

    public String telefoneFormatado() {
        return Validador.formatarTelefone(telefone);
    }
}
