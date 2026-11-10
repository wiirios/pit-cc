package br.edu.catalogo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Centraliza a criação de conexões JDBC com o banco H2.
 * Os DAOs chamam {@link #getConexao()} em vez de repetir a URL em todo lugar.
 */
public final class FabricaDeConexoes {

    // Modo arquivo: os dados ficam gravados na pasta ./dados (persistência real)
    private static final String URL = "jdbc:h2:file:./dados/catalogo";
    private static final String USUARIO = "sa";
    private static final String SENHA = "";

    private FabricaDeConexoes() {
    }

    /**
     * Abre uma nova conexão com o banco.
     *
     * @return conexão JDBC (quem chamar deve fechá-la, de preferência com try-with-resources)
     * @throws SQLException se não for possível conectar
     */
    public static Connection getConexao() throws SQLException {
        try {
            Class.forName("org.h2.Driver"); // garante o registro do driver no servidor
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver do H2 não encontrado no classpath", e);
        }
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}