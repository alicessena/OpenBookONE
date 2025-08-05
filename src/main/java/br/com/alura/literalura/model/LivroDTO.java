package br.com.alura.literalura.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LivroDTO(@JsonAlias("title") String titulo,
                       @JsonAlias("donwload_count") Double qtdDonwloads,
                       @JsonAlias("languages")List<String> idioma,
                       @JsonAlias("authors") List<EscritorDTO> escritores) {

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Título: ").append(titulo).append("\n");
        sb.append("Autor(es): \n");
        for (EscritorDTO escritor : escritores){
            sb.append(" - ").append(escritor.nome()).append("\n");
        }
        sb.append("Idioma(s): ").append(String.join(", ",idioma)).append("\n");
        sb.append("Donwloads: ").append(qtdDonwloads).append("\n");
        sb.append("---------------------------------------------------");
        return sb.toString();
    }
}
