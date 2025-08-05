# 📚 Literalura: Catálogo de Livros e Autores

**Literalura** é uma aplicação de linha de comando desenvolvida como parte do desafio **Oracle Next Education (ONE) G8**.  
Ela permite que os usuários interajam com a **API Gutendex** para buscar informações sobre livros e autores, registrar esses dados em um banco de dados **PostgreSQL**, e realizar consultas diversas sobre o acervo.

---

## ✨ Funcionalidades

Literalura oferece um menu interativo com as seguintes opções:

- **Buscar livros pelo título**  
  Pesquisa um livro na API Gutendex utilizando o título fornecido pelo usuário.  
  A aplicação lida com acentos e espaços na busca, e filtra os resultados para exibir apenas livros que correspondem exatamente ao título.  
  Se o livro ainda não estiver no banco de dados local, ele será salvo.

- **Listar livros registrados**  
  Exibe todos os livros que foram previamente salvos no banco de dados da aplicação.

- **Listar autores registrados**  
  Lista todos os autores únicos cujos livros foram registrados na aplicação.

- **Listar autores vivos em um determinado ano (da API)**  
  Permite buscar autores que estavam vivos em um ano específico, utilizando os filtros da API Gutendex.

- **Listar autores nascidos em ou vivos a partir de um determinado ano (da API)**  
  Busca autores cujo ano de nascimento coincide com o ano fornecido ou que estavam vivos a partir daquele ano, utilizando a API.

- **Listar autores falecidos em ou vivos até um determinado ano (da API)**  
  Busca autores cujo ano de falecimento coincide com o ano fornecido ou que estavam vivos até aquele ano, utilizando a API.

- **Listar livros em um determinado idioma (da API)**  
  Filtra e exibe livros por um idioma específico, buscando diretamente na API Gutendex.

- **Sair**  
  Finaliza a execução da aplicação.

---

## 🛠️ Tecnologias Utilizadas

- **Java 17+**: Linguagem de programação principal.
- **Spring Boot**: Framework para facilitar o desenvolvimento de aplicações Java.
- **Spring Data JPA**: Para abstração e interação com o banco de dados.
- **Hibernate**: Implementação de JPA.
- **PostgreSQL**: Banco de dados relacional para persistir os livros e autores.
- **Jackson**: Biblioteca para manipulação de JSON (integrada ao Spring Boot para conversão de DTOs).
- **API Gutendex**: Fonte externa de dados sobre livros.
