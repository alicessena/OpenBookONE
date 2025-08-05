package br.com.alura.literalura.principal;

import br.com.alura.literalura.model.Escritor;
import br.com.alura.literalura.model.Livro;
import br.com.alura.literalura.model.LivroDTO;
import br.com.alura.literalura.model.EscritorDTO;
import br.com.alura.literalura.repository.LivroRepository;
import br.com.alura.literalura.service.ConsumoAPI;
import br.com.alura.literalura.service.ConversorDados;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class Principal {

    @Autowired
    private LivroRepository livroRepository;
    @Autowired
    private ConsumoAPI consumoAPI;

    @Autowired
    private ConversorDados conversorDados;

    private final Scanner leitura = new Scanner(System.in);

    public Principal(LivroRepository livroRepository, ConsumoAPI consumoAPI, ConversorDados conversorDados) {
        this.livroRepository = livroRepository;
        this.consumoAPI = consumoAPI;
        this.conversorDados = conversorDados;
    }

    public void rodar() {
        boolean rodando = true;
        while (rodando) {
            exibeMenu();
            var opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarLivrosPorTitulo();
                    break;
                case 2:
                    mostrarLivrosRegistrados();
                    break;
                case 3:
                    mostraEscritoresRegistrados();
                    break;
                case 4:
                    mostraEscritoresVivosNaAPI();
                    break;
                case 5:
                    mostraAutoresNascidosEmOuVivosDesdeAnoNaAPI();
                    break;
                case 6:
                    mostraAutoresFalecidosEmOuVivosAteAnoNaAPI();
                    break;
                case 7:
                    mostrarLivrosPorIdiomaNaAPI();
                    break;
                case 0:
                    System.out.println("Finalizando LiterAlura...");
                    rodando = false;
                    break;
                default:
                    System.out.println("Opção inválida, tente novamente!");
                    break;
            }
        }
    }

    private void exibeMenu() {
        System.out.println("""
                 *********************************************************************************************************
                                               Seja Bem - vindo!
                                               PROJETO LITERALURA
                              Escolha uma opção abaixo:
                 *********************************************************************************************************
                                             OPÇÕES
                              1 - Buscar livros pelo título
                              2 - Listar livros registrados
                              3 - Listar autores registrados
                              4 - Listar autores vivos em um determinado ano (da API)
                              5 - Listar autores nascidos em ou vivos a partir de um determinado ano (da API)
                              6 - Listar autores falecidos em ou vivos até um determinado ano (da API)
                              7 - Listar livros em um determinado idioma (da API)
                              0 - Sair
                =========================================================================================================
                """);
    }

    private void salvarLivros(List<Livro> livros) {
        livros.forEach(livroRepository::save);
    }

    private void buscarLivrosPorTitulo() {
        String baseURL = "https://gutendex.com/books?search=";
        try {
            System.out.println("Digite o título do livro: ");
            String tituloBusca = leitura.nextLine();
            String tituloCodificado = URLEncoder.encode(tituloBusca, StandardCharsets.UTF_8.toString());
            String tituloFinalParaURL = tituloCodificado.replace("+", "%20");

            String endereco = baseURL + tituloFinalParaURL;
            System.out.println("URL da API: " + endereco);

            String jsonResponse = consumoAPI.obterDados(endereco);
            System.out.println("Resposta da API: " + jsonResponse);

            if (jsonResponse.isEmpty() || jsonResponse.contains("\"count\":0") || !jsonResponse.contains("\"results\":[")) {
                System.out.println("Não foi possível encontrar o livro buscado :(");
                return;
            }
            JsonNode rootNode = conversorDados.getObjectMapper().readTree(jsonResponse);
            JsonNode resultsNode = rootNode.path("results");

            if (resultsNode.isEmpty()) {
                System.out.println("Não foi possível encontrar o livro buscado :(");
                return;
            }

            List<LivroDTO> livrosDTOApi = conversorDados.getObjectMapper()
                    .readerForListOf(LivroDTO.class)
                    .readValue(resultsNode);


            List<LivroDTO> livrosFiltrados = livrosDTOApi.stream()
                    .filter(l -> l.titulo().equalsIgnoreCase(tituloBusca))
                    .collect(Collectors.toList());

            if (livrosFiltrados.isEmpty()) {
                System.out.println("Nenhum livro relevante encontrado para salvar (pode ter sido filtrado pelo título exato na sua lógica).");
                return;
            }

            List<Livro> novosLivrosParaSalvar = new ArrayList<>();
            for (LivroDTO livroDTO : livrosFiltrados) {
                if (livroRepository.findByTitulo(livroDTO.titulo()).isEmpty()) {
                    Livro novoLivro = new Livro(livroDTO);

                    if (livroDTO.escritores() != null && !livroDTO.escritores().isEmpty()) {
                        String nomeEscritorDTO = livroDTO.escritores().get(0).nome();
                        Escritor escritorExistente = livroRepository.findByEscritorNomeEscritor(nomeEscritorDTO);

                        if (escritorExistente != null) {
                            novoLivro.setEscritor(escritorExistente);
                        } else {
                            Escritor novoEscritor = new Escritor(livroDTO.escritores().get(0));
                            novoLivro.setEscritor(novoEscritor);
                        }
                    } else {
                        System.out.println("Livro '" + livroDTO.titulo() + "' sem informações de autor. Não será salvo.");
                        continue;
                    }

                    novosLivrosParaSalvar.add(novoLivro);
                } else {
                    System.out.println("Livro '" + livroDTO.titulo() + "' já registrado no banco de dados.");
                }
            }

            if (!novosLivrosParaSalvar.isEmpty()) {
                System.out.println("Salvando novos livros encontrados...");
                salvarLivros(novosLivrosParaSalvar);
                System.out.println("Livros salvos com sucesso!");
            } else {
                System.out.println("Nenhum novo livro para salvar após a verificação de duplicidade.");
            }

            if (!livrosFiltrados.isEmpty()) {
                System.out.println("\n--- Livros encontrados na API para '" + tituloBusca + "' ---");
                Set<String> titulosExibidos = new HashSet<>();
                for (LivroDTO livro : livrosFiltrados) {
                    if (!titulosExibidos.contains(livro.titulo())) {
                        System.out.println(livro);
                        titulosExibidos.add(livro.titulo());
                    }
                }
                System.out.println("----------------------------------------\n");
            }

        } catch (Exception e) {
            System.out.println("Erro ao buscar livros: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarLivrosRegistrados() {
        List<Livro> livros = livroRepository.findAll();
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro registrado.");
        } else {
            System.out.println("\n--- Livros Registrados ---");
            livros.forEach(System.out::println);
            System.out.println("--------------------------\n");
        }
    }

    private void mostraEscritoresRegistrados() {
        List<Livro> livros = livroRepository.findAll();
        if (livros.isEmpty()) {
            System.out.println("Nenhum autor registrado.");
        } else {
            System.out.println("\n--- Autores Registrados ---");
            livros.stream()
                    .map(Livro::getEscritor)
                    .distinct()
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(Escritor::getNomeEscritor))
                    .forEach(System.out::println);
            System.out.println("---------------------------\n");
        }
    }

    private void mostraEscritoresVivosNaAPI() {
        System.out.println("Digite o ano para listar autores vivos: ");
        Integer ano = leitura.nextInt();
        leitura.nextLine();

        try {
            String endereco = "https://gutendex.com/books?author_year_start=" + ano + "&author_year_end=" + ano;
            String jsonResponse = consumoAPI.obterDados(endereco);
            JsonNode rootNode = conversorDados.getObjectMapper().readTree(jsonResponse);
            JsonNode resultsNode = rootNode.path("results");

            if (resultsNode.isEmpty() || jsonResponse.contains("\"count\":0") || !jsonResponse.contains("\"results\":[")) {
                System.out.println("Nenhum autor vivo encontrado na API para o ano " + ano + ".");
                return;
            }

            Set<Escritor> escritoresVivos = new HashSet<>();
            for (JsonNode node : resultsNode) {
                JsonNode authorsNode = node.path("authors");
                if (authorsNode.isArray()) {
                    for (JsonNode authorNode : authorsNode) {
                        EscritorDTO escritorDTO = conversorDados.getObjectMapper().treeToValue(authorNode, EscritorDTO.class);
                        if (escritorDTO.nascimento() != null && escritorDTO.nascimento() <= ano &&
                                (escritorDTO.falecimento() == null || escritorDTO.falecimento() >= ano)) {
                            escritoresVivos.add(new Escritor(escritorDTO));
                        }
                    }
                }
            }

            if (escritoresVivos.isEmpty()) {
                System.out.println("Nenhum autor vivo encontrado na API para o ano " + ano + ".");
            } else {
                System.out.println("\n--- Autores vivos na API no ano de " + ano + " ---");
                escritoresVivos.stream()
                        .sorted(Comparator.comparing(Escritor::getNomeEscritor))
                        .forEach(System.out::println);
                System.out.println("----------------------------------------------\n");
            }

        } catch (Exception e) {
            System.out.println("Erro ao buscar autores vivos na API: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void mostraAutoresNascidosEmOuVivosDesdeAnoNaAPI() {
        System.out.println("Digite o ano para listar autores nascidos em ou vivos a partir deste ano: ");
        Integer anoBusca = leitura.nextInt();
        leitura.nextLine();

        try {

            String endereco = "https://gutendex.com/books?author_year_start=" + anoBusca;
            String jsonResponse = consumoAPI.obterDados(endereco);
            JsonNode rootNode = conversorDados.getObjectMapper().readTree(jsonResponse);
            JsonNode resultsNode = rootNode.path("results");

            if (resultsNode.isEmpty() || jsonResponse.contains("\"count\":0") || !jsonResponse.contains("\"results\":[")) {
                System.out.println("Nenhum autor encontrado para o ano " + anoBusca + " na API.");
                return;
            }

            Set<Escritor> autoresEncontrados = new HashSet<>();
            for (JsonNode node : resultsNode) {
                JsonNode authorsNode = node.path("authors");
                if (authorsNode.isArray()) {
                    for (JsonNode authorNode : authorsNode) {
                        EscritorDTO escritorDTO = conversorDados.getObjectMapper().treeToValue(authorNode, EscritorDTO.class);

                        if (escritorDTO.nascimento() != null &&
                                (escritorDTO.nascimento().equals(anoBusca) ||
                                        (escritorDTO.nascimento() < anoBusca && (escritorDTO.falecimento() == null || escritorDTO.falecimento() >= anoBusca)))) {
                            autoresEncontrados.add(new Escritor(escritorDTO));
                        }
                    }
                }
            }

            if (autoresEncontrados.isEmpty()) {
                System.out.println("Nenhum autor que nasceu em ou vivia a partir do ano " + anoBusca + " encontrado na API.");
            } else {
                System.out.println("\n--- Autores nascidos em ou vivos a partir do ano " + anoBusca + " na API ---");
                autoresEncontrados.stream()
                        .sorted(Comparator.comparing(Escritor::getNomeEscritor))
                        .forEach(System.out::println);
                System.out.println("--------------------------------------------------\n");
            }

        } catch (Exception e) {
            System.out.println("Erro ao buscar autores: " + e.getMessage());
            e.printStackTrace();
        }
    }



    private void mostraAutoresFalecidosEmOuVivosAteAnoNaAPI() {
        System.out.println("Digite o ano para listar autores falecidos em ou vivos até este ano: ");
        Integer anoBusca = leitura.nextInt();
        leitura.nextLine();

        try {
            String endereco = "https://gutendex.com/books?author_year_end=" + anoBusca;
            String jsonResponse = consumoAPI.obterDados(endereco);
            JsonNode rootNode = conversorDados.getObjectMapper().readTree(jsonResponse);
            JsonNode resultsNode = rootNode.path("results");

            if (resultsNode.isEmpty() || jsonResponse.contains("\"count\":0") || !jsonResponse.contains("\"results\":[")) {
                System.out.println("Nenhum autor encontrado para o ano " + anoBusca + " na API.");
                return;
            }

            Set<Escritor> autoresEncontrados = new HashSet<>();
            for (JsonNode node : resultsNode) {
                JsonNode authorsNode = node.path("authors");
                if (authorsNode.isArray()) {
                    for (JsonNode authorNode : authorsNode) {
                        EscritorDTO escritorDTO = conversorDados.getObjectMapper().treeToValue(authorNode, EscritorDTO.class);

                        if (escritorDTO.falecimento() != null && escritorDTO.falecimento().equals(anoBusca) ||
                                (escritorDTO.falecimento() != null && escritorDTO.falecimento() < anoBusca &&
                                        (escritorDTO.nascimento() == null || escritorDTO.nascimento() <= anoBusca))) {
                            autoresEncontrados.add(new Escritor(escritorDTO));
                        }
                    }
                }
            }

            if (autoresEncontrados.isEmpty()) {
                System.out.println("Nenhum autor falecido em ou que vivia até o ano " + anoBusca + " encontrado na API.");
            } else {
                System.out.println("\n--- Autores falecidos em ou vivos até o ano " + anoBusca + " na API ---");
                autoresEncontrados.stream()
                        .sorted(Comparator.comparing(Escritor::getNomeEscritor))
                        .forEach(System.out::println);
                System.out.println("---------------------------------------------------\n");
            }

        } catch (Exception e) {
            System.out.println("Erro ao buscar autores: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void mostrarLivrosPorIdiomaNaAPI() {
        System.out.println("""
            \n--- Opções de Idioma ---
            Inglês (en)
            Português (pt)
            Espanhol (es)
            Francês (fr)
            Alemão (de)
            -----------------------
            Digite o idioma pretendido (código de 2 letras):
            """);
        String idiomaBusca = leitura.nextLine().toLowerCase();

        Set<String> idiomasValidos = Set.of("en", "pt", "es", "fr", "de");
        if (!idiomasValidos.contains(idiomaBusca)) {
            System.out.println("Idioma inválido. Por favor, digite um dos códigos sugeridos.");
            return;
        }

        try {
            String baseURL = "https://gutendex.com/books?";
            String endereco = baseURL + "languages=" + idiomaBusca;
            System.out.println("URL da API: " + endereco);

            String jsonResponse = consumoAPI.obterDados(endereco);
            System.out.println("Resposta da API: " + jsonResponse);

            if (jsonResponse.isEmpty() || jsonResponse.contains("\"count\":0") || !jsonResponse.contains("\"results\":[")) {
                System.out.println("Nenhum livro encontrado no idioma especificado na API.");
                return;
            }

            JsonNode rootNode = conversorDados.getObjectMapper().readTree(jsonResponse);
            JsonNode resultsNode = rootNode.path("results");

            List<LivroDTO> livrosDTOApi = conversorDados.getObjectMapper()
                    .readerForListOf(LivroDTO.class)
                    .readValue(resultsNode);

            if (livrosDTOApi.isEmpty()) {
                System.out.println("Nenhum livro encontrado no idioma especificado na API.");
            } else {
                System.out.println("\n--- Livros encontrados na API no idioma '" + idiomaBusca + "' ---");
                Set<String> titulosExibidos = new HashSet<>();
                for (LivroDTO livro : livrosDTOApi) {
                    if (!titulosExibidos.contains(livro.titulo())) {
                        System.out.println(livro);
                        titulosExibidos.add(livro.titulo());
                    }
                }
                System.out.println("-------------------------------------------------------------\n");
            }
        } catch (Exception e) {
            System.out.println("Erro ao buscar livros por idioma na API: " + e.getMessage());
            e.printStackTrace();
        }
    }
}