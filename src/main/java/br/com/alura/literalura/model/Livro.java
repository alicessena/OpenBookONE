package br.com.alura.literalura.model;

import jakarta.persistence.*;

@Entity
@Table
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    @ManyToOne(cascade = CascadeType.ALL)
    private Escritor escritor;

    private String idioma;
    private Integer nascimentoEscritor;
    private Integer falecimentoEscritor;
    private Double qtdDownloads;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Escritor getEscritor() {
        return escritor;
    }

    public void setEscritor(Escritor escritor) {
        this.escritor = escritor;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setidioma(String idioma) {
        this.idioma = idioma;
    }

    public Integer getNascimentoEscritor() {
        return nascimentoEscritor;
    }

    public void setNascimentoEscritor(Integer nascimentoEscritor) {
        this.nascimentoEscritor = nascimentoEscritor;
    }

    public Integer getFalecimentoEscritor() {
        return falecimentoEscritor;
    }

    public void setFalecimentoEscritor(Integer falecimentoEscritor) {
        this.falecimentoEscritor = falecimentoEscritor;
    }

    public Double getQtdDownloads() {
        return qtdDownloads;
    }

    public void setQtdDownloads(Double qtdDownloads) {
        this.qtdDownloads = qtdDownloads;
    }

    public Livro (){}

    public Livro(LivroDTO livroDTO) {
        this.titulo = livroDTO.titulo();
        Escritor escritor = new Escritor(livroDTO.escritores().get(0));
        this.escritor = escritor;
        this.idioma = livroDTO.idioma().get(0);
        this.qtdDownloads = livroDTO.qtdDonwloads();
    }
    public Livro(Long idApi, String titulo, Escritor nomeEscritor, String idioma, Double qtdDownloads) {
        this.titulo = titulo;
        this.escritor = nomeEscritor;
        this.idioma = idioma;
        this.qtdDownloads = qtdDownloads;
    }


    @Override
    public String toString() {
        return "Título: " + titulo + "\n" +
                "Autor: " + escritor + "\n"+
                "Idioma: '" + idioma + "\n" +
                "Donwloads: " + qtdDownloads+ "\n" + "=====================================";
    }
}
