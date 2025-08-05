package br.com.alura.literalura.service;

import br.com.alura.literalura.model.Livro;
import br.com.alura.literalura.model.LivroDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ConversorDados implements IconversorDados {
    private final ObjectMapper objectMapper = new ObjectMapper();
    public ObjectMapper getObjectMapper(){
        return objectMapper;
    }

    public  <T> T obterDados(String json, Class<T> classe){
        try {
            return objectMapper.readValue(json, classe);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public List<LivroDTO> obterListaLivros(String json){
        try {
            CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, Livro.class);
            return objectMapper.readValue(json, listType);
        }catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }
}
