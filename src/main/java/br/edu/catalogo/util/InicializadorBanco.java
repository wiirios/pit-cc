package br.edu.catalogo.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

/**
 * Executa o script SQL de criação da tabela ao iniciar a aplicação.
 * Como o script usa CREATE TABLE IF NOT EXISTS, é seguro rodar várias vezes.
 */
@WebListener
public class InicializadorBanco implements ServletContextListener {

    private static final Logger LOG = Logger.getLogger(InicializadorBanco.class.getName());

    /**
     * Chamado pelo servidor uma única vez, quando a aplicação sobe.
     * Lê o arquivo /sql/script.sql e executa cada comando separado por ";".
     *
     * @param evento evento de inicialização do contexto (fornecido pelo servidor)
     */
    @Override
    public void contextInitialized(ServletContextEvent evento) {
        try (InputStream in = getClass().getResourceAsStream("/sql/script.sql")) {
            if (in == null) {
                throw new IllegalStateException("Arquivo /sql/script.sql não encontrado");
            }
            String script = new String(in.readAllBytes(), StandardCharsets.UTF_8);

            try (Connection conn = FabricaDeConexoes.getConexao();
                 Statement stmt = conn.createStatement()) {
                for (String comando : script.split(";")) {
                    if (!comando.isBlank()) {
                        stmt.execute(comando.trim());
                    }
                }
            }
            LOG.info("Banco de dados inicializado com sucesso.");
        } catch (IOException | SQLException e) {
            // Falhar cedo: sem banco a aplicação não tem como funcionar
            throw new IllegalStateException("Erro ao inicializar o banco de dados", e);
        }
    }
}