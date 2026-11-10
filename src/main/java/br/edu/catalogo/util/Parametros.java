package br.edu.catalogo.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Funções auxiliares para ler parâmetros das requisições com segurança.
 */
public final class Parametros {

    private Parametros() {
    }

    /**
     * Lê o parâmetro "id" da requisição e o converte para número.
     * Qualquer valor inválido (ausente, texto, zero ou negativo) resulta em null,
     * assim o servlet só precisa verificar se o retorno é null.
     *
     * @param req requisição HTTP
     * @return o id como número positivo, ou null se for inválido
     */
    public static Integer lerId(HttpServletRequest req) {
        String texto = req.getParameter("id");
        if (texto == null) {
            return null;
        }
        try {
            int id = Integer.parseInt(texto.trim());
            return id > 0 ? id : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
