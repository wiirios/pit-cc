package br.edu.catalogo.service;

import java.util.List;

/**
 * Exceção lançada pelo Service quando os dados do formulário são inválidos.
 * Guarda a lista de mensagens de erro para que a tela mostre todas de uma vez.
 */
public class ValidacaoException extends Exception {

    private static final long serialVersionUID = 1L;

    private final List<String> erros;

    /**
     * Cria a exceção com a lista de problemas encontrados.
     *
     * @param erros mensagens de erro (uma por campo inválido)
     */
    public ValidacaoException(List<String> erros) {
        super("Dados inválidos");
        this.erros = erros;
    }

    /**
     * Retorna as mensagens de erro de validação.
     *
     * @return lista de mensagens, para exibir no formulário
     */
    public List<String> getErros() {
        return erros;
    }
}
