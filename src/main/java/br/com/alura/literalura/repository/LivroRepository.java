package br.com.alura.literalura.repository;

import br.com.alura.literalura.model.Escritor;
import br.com.alura.literalura.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Year;
import java.util.List;
import java.util.Optional; // Use Optional para quando a busca pode não encontrar nada

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {
    List<Livro> findByTitulo(String titulo);

    // Adicione este método para buscar um escritor pelo nome
    // Ou crie um EscritorRepository e mova este método para lá
    // Se o nome do escritor for único, pode retornar Optional<Escritor>
    @Query("SELECT e FROM Escritor e WHERE e.nomeEscritor = :nomeEscritor")
    Escritor findByEscritorNomeEscritor(String nomeEscritor); // Considere retornar Optional<Escritor>
    // Se retornar Optional: Escritor escritorExistente = livroRepository.findByEscritorNomeEscritor(nomeEscritorDTO).orElse(null);

    @Query("SELECT l FROM Livro l WHERE l.idioma = :idioma")
    List<Livro> findByIdioma(String idioma);

    @Query("SELECT DISTINCT e FROM Livro l JOIN l.escritor e WHERE e.nascimento <= :ano AND (e.falecimento IS NULL OR e.falecimento >= :ano)")
    List<Escritor> findEscritoresVivos(@org.springframework.data.repository.query.Param("ano") Year ano);

    @Query("SELECT DISTINCT e FROM Livro l JOIN l.escritor e WHERE e.falecimento = :anoFalecimento")
    List<Escritor> findEscritoresPorAnoFalecimento(@org.springframework.data.repository.query.Param("anoFalecimento") Year anoFalecimento);

    @Query("SELECT DISTINCT e FROM Livro l JOIN l.escritor e WHERE e.nascimento = :anoNascimento")
    List<Escritor> findEscritoresVivosFiltrado(@org.springframework.data.repository.query.Param("anoNascimento") Year anoNascimento);

}