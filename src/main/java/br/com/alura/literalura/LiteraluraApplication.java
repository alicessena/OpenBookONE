package br.com.alura.literalura;

import br.com.alura.literalura.principal.Principal; // Importe a sua classe Principal
import org.springframework.beans.factory.annotation.Autowired; // Necessário para a injeção
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

public class LiteraluraApplication implements CommandLineRunner {


	@Autowired
	private Principal principal;

	public static void main(String[] args) {
		SpringApplication.run(LiteraluraApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {

		principal.rodar();
	}
}