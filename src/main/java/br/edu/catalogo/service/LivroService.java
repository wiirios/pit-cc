package br.edu.catalogo.service;

import br.edu.catalogo.dao.DAOException;
import br.edu.catalogo.dao.LivroDAO;
import br.edu.catalogo.model.Livro;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

/**
 * Camada de regras de negócio do catálogo de livros.
 *
 * Fica entre os Servlets e o DAO: valida os dados recebidos do formulário
 * antes de chamar o banco. A validação é feita aqui, no servidor, porque a
 * validação do navegador (HTML) pode ser facilmente burlada.
 */
public class LivroService {

    // Tamanhos máximos: os mesmos definidos nas colunas do script.sql
    public static final int MAX_TITULO = 255;
    public static final int MAX_AUTOR = 255;
    public static final int MAX_GENERO = 100;
    public static final int MAX_SINOPSE = 2000;
    public static final int MAX_BUSCA = 100;

    private final LivroDAO dao = new LivroDAO();

    /**
     * Retorna todos os livros do catálogo.
     *
     * @return lista de livros em ordem alfabética
     * @throws DAOException se ocorrer erro no banco
     */
    public List<Livro> listarTodos() throws DAOException {
        return dao.listarTodos();
    }

    /**
     * Busca livros por título ou autor. Se o termo estiver vazio,
     * devolve todos os livros (comportamento da listagem normal).
     *
     * @param termo texto digitado no campo de busca (pode ser null)
     * @return livros encontrados, em ordem alfabética
     * @throws ValidacaoException se o termo for longo demais
     * @throws DAOException       se ocorrer erro no banco
     */
    public List<Livro> buscar(String termo) throws ValidacaoException, DAOException {
        termo = limpar(termo);
        if (termo.isEmpty()) {
            return dao.listarTodos();
        }
        if (termo.length() > MAX_BUSCA) {
            throw new ValidacaoException(List.of(
                    "O termo de busca deve ter no máximo " + MAX_BUSCA + " caracteres."));
        }
        return dao.buscarPorTituloOuAutor(termo);
    }

    /**
     * Busca um livro pelo id.
     *
     * @param id identificador do livro
     * @return o livro, ou null se não existir
     * @throws DAOException se ocorrer erro no banco
     */
    public Livro buscarPorId(int id) throws DAOException {
        return dao.buscarPorId(id);
    }

    /**
     * Valida e grava as alterações de um livro existente.
     *
     * @param livro    livro com o id e os campos de texto vindos do formulário
     * @param anoTexto ano de publicação digitado pelo usuário, ainda como texto
     * @return true se atualizou; false se o livro não existe mais
     * @throws ValidacaoException se algum campo for inválido
     * @throws DAOException       se ocorrer erro no banco
     */
    public boolean atualizar(Livro livro, String anoTexto) throws ValidacaoException, DAOException {
        validar(livro, anoTexto);
        return dao.atualizar(livro);
    }

    /**
     * Exclui um livro.
     *
     * @param id identificador do livro
     * @return true se excluiu; false se o livro não existia
     * @throws DAOException se ocorrer erro no banco
     */
    public boolean excluir(int id) throws DAOException {
        return dao.excluir(id);
    }

    /**
     * Valida e grava um novo livro.
     *
     * @param livro    livro com os campos de texto preenchidos pelo formulário
     * @param anoTexto ano de publicação digitado pelo usuário, ainda como texto
     * @throws ValidacaoException se algum campo for inválido
     * @throws DAOException       se ocorrer erro no banco
     */
    public void cadastrar(Livro livro, String anoTexto) throws ValidacaoException, DAOException {
        validar(livro, anoTexto);
        dao.inserir(livro);
    }

    /**
     * Confere todos os campos e junta os problemas numa lista.
     * Também remove espaços nas pontas dos textos e converte o ano para número.
     * Se houver algum erro, lança ValidacaoException com todas as mensagens.
     */
    private void validar(Livro livro, String anoTexto) throws ValidacaoException {
        List<String> erros = new ArrayList<>();

        // Remove espaços extras (e troca null por texto vazio)
        livro.setTitulo(limpar(livro.getTitulo()));
        livro.setAutor(limpar(livro.getAutor()));
        livro.setGenero(limpar(livro.getGenero()));
        livro.setSinopse(limpar(livro.getSinopse()));
        anoTexto = limpar(anoTexto);

        // Título: obrigatório e com tamanho máximo
        if (livro.getTitulo().isEmpty()) {
            erros.add("O título é obrigatório.");
        } else if (livro.getTitulo().length() > MAX_TITULO) {
            erros.add("O título deve ter no máximo " + MAX_TITULO + " caracteres.");
        }

        // Autor: obrigatório e com tamanho máximo
        if (livro.getAutor().isEmpty()) {
            erros.add("O autor é obrigatório.");
        } else if (livro.getAutor().length() > MAX_AUTOR) {
            erros.add("O autor deve ter no máximo " + MAX_AUTOR + " caracteres.");
        }

        // Ano de publicação: obrigatório, numérico e dentro de um intervalo razoável
        int anoMaximo = Year.now().getValue() + 5;
        if (anoTexto.isEmpty()) {
            erros.add("O ano de publicação é obrigatório.");
        } else {
            try {
                int ano = Integer.parseInt(anoTexto);
                if (ano < 1 || ano > anoMaximo) {
                    erros.add("O ano de publicação deve estar entre 1 e " + anoMaximo + ".");
                } else {
                    livro.setAnoPublicacao(ano);
                }
            } catch (NumberFormatException e) {
                erros.add("O ano de publicação deve conter apenas números.");
            }
        }

        // Gênero e sinopse são opcionais, mas têm tamanho máximo
        if (livro.getGenero().length() > MAX_GENERO) {
            erros.add("O gênero deve ter no máximo " + MAX_GENERO + " caracteres.");
        }
        if (livro.getSinopse().length() > MAX_SINOPSE) {
            erros.add("A sinopse deve ter no máximo " + MAX_SINOPSE + " caracteres.");
        }

        if (!erros.isEmpty()) {
            throw new ValidacaoException(erros);
        }
    }

    /** Devolve o texto sem espaços nas pontas; null vira texto vazio. */
    private String limpar(String texto) {
        return texto == null ? "" : texto.trim();
    }
}
