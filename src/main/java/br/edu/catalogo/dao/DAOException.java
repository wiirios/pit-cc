package br.edu.catalogo.dao;

/**
 * Exceção lançada pelos DAOs quando algo dá errado no acesso ao banco.
 *
 * A mensagem guardada aqui é "amigável" (pode ser mostrada ao usuário).
 * O erro técnico original (SQLException) fica guardado como causa e é
 * registrado no log do servidor, nunca exibido na tela.
 */
public class DAOException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Cria a exceção com uma mensagem amigável e a causa técnica.
     *
     * @param mensagem texto que pode ser exibido ao usuário
     * @param causa    exceção original (por exemplo, uma SQLException)
     */
    public DAOException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
