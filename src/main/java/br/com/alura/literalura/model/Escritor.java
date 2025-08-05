package br.com.alura.literalura.model;

import jakarta.persistence.*;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "escritores")
public class Escritor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,name = "nome_escritor")
    private String nomeEscritor;

    @Column(name = "nascimento")
    private Year nascimento;

    @Column(name = "falecimento")
    private Year falecimento;

    @OneToMany(mappedBy = "escritor",fetch = FetchType.EAGER)
    private List<Livro> livros = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeEscritor() {
        return nomeEscritor;
    }

    public void setNomeEscritor(String nomeEscritor) {
        this.nomeEscritor = nomeEscritor;
    }

    public Year getNascimento() {
        return nascimento;
    }

    public void setNascimento(Year nascimento) {
        this.nascimento = nascimento;
    }

    public Year getFalecimento() {
        return falecimento;
    }

    public void setFalecimento(Year falecimento) {
        this.falecimento = falecimento;
    }

    public List<Livro> getLivros() {
        return livros;
    }

    public void setLivros(List<Livro> livros) {
        this.livros = livros;
    }

    public Escritor(EscritorDTO escritorDTO){
        this.nomeEscritor = escritorDTO.nome();
        this.nascimento = escritorDTO.nascimento() != null ? Year.of(escritorDTO.nascimento()) : null;
        this.falecimento = escritorDTO.falecimento() != null ? Year.of(escritorDTO.falecimento()) : null;

    }

    public Escritor(String nome, Year nascimento, Year falecimento){
        this.nomeEscritor = nome;
        this.nascimento = nascimento;
        this.falecimento = falecimento;
    }


    @Override
    public String toString() {
        String nascimentoSaida = nascimento != null ? nascimento.toString() : "Desconhecido";
        String falecimentoSaida = falecimento != null ? falecimento.toString() : "Desconhecido";

        return nomeEscritor + " | nascido(a) em "+ nascimentoSaida+ ", falecimento em "+ falecimentoSaida + " |";

    }
    public  static boolean temAno (Year ano){
        return ano != null && !ano.equals(Year.of(0));
    }
    public Escritor(){}
}
