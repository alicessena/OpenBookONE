package br.com.alura.literalura.service;

public interface IconversorDados {
    <T> T obterDados(String json, Class<T> classe);
}
