package br.com.petshop.security;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/** Criptografa automaticamente o campo ao gravar e descriptografa ao ler. */
@Converter
public class CriptoConverter implements AttributeConverter<String, String> {
    @Override
    public String convertToDatabaseColumn(String atributo) {
        return CryptoUtil.criptografar(atributo);
    }

    @Override
    public String convertToEntityAttribute(String dado) {
        return CryptoUtil.descriptografar(dado);
    }
}
