package br.com.alura.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public record EscritorDTO(@JsonAlias("name") String nome,
                          @JsonAlias("birth_year") Integer nascimento,
                          @JsonAlias("death_year") Integer falecimento) {


    @Override
    public String toString() {
        return "Autor: "+ nome;
    }
}
