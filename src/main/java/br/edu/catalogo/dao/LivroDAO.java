package br.edu.catalogo.dao;

import br.edu.catalogo.model.Livro;
import br.edu.catalogo.util.FabricaDeConexoes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO (Data Access Object) da tabela livro.
 *
 * É a única classe que conhece SQL. Todas as consultas usam
 * PreparedStatement, que envia os valores separados do comando SQL e
 * assim impede ataques de SQL Injection.
 */
public class LivroDAO {

    private static final Logger LOG = Logger.getLogger(LivroDAO.class.getName());

    /**
     * Grava um novo livro no banco. Depois de gravar, o id gerado pelo
     * banco é colocado no próprio objeto recebido.
     *
     * @param livro livro já validado pelo Service
     * @throws DAOException se ocorrer erro no banco
     */
    public void inserir(Livro livro) throws DAOException {
        String sql = "INSERT INTO livro (titulo, autor, ano_publicacao, genero, sinopse) "
                + "VALUES (?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet chaves = null;
        try {
            conn = FabricaDeConexoes.getConexao();
            // RETURN_GENERATED_KEYS pede ao banco o id criado pelo AUTO_INCREMENT
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, livro.getTitulo());
            stmt.setString(2, livro.getAutor());
            stmt.setInt(3, livro.getAnoPublicacao());
            stmt.setString(4, livro.getGenero());
            stmt.setString(5, livro.getSinopse());
            stmt.executeUpdate();

            chaves = stmt.getGeneratedKeys();
            if (chaves.next()) {
                livro.setId(chaves.getInt(1));
            }
        } catch (SQLException e) {
            // O erro técnico vai para o log; o usuário recebe uma mensagem simples
            LOG.log(Level.SEVERE, "Erro ao inserir livro", e);
            throw new DAOException("Não foi possível salvar o livro. Tente novamente.", e);
        } finally {
            fechar(conn, stmt, chaves);
        }
    }

    /**
     * Lista todos os livros do catálogo, em ordem alfabética de título.
     *
     * @return lista de livros (vazia se não houver nenhum)
     * @throws DAOException se ocorrer erro no banco
     */
    public List<Livro> listarTodos() throws DAOException {
        String sql = "SELECT * FROM livro ORDER BY titulo";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Livro> livros = new ArrayList<>();
        try {
            conn = FabricaDeConexoes.getConexao();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            while (rs.next()) {
                livros.add(montarLivro(rs));
            }
            return livros;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Erro ao listar livros", e);
            throw new DAOException("Não foi possível carregar o catálogo. Tente novamente.", e);
        } finally {
            fechar(conn, stmt, rs);
        }
    }

    /**
     * Procura livros cujo título OU autor contenham o termo informado.
     * A comparação ignora maiúsculas/minúsculas (LOWER nos dois lados).
     *
     * O termo é passado como parâmetro do PreparedStatement, nunca
     * concatenado no SQL. Os caracteres % e _ digitados pelo usuário são
     * "escapados" para serem tratados como texto comum, e não como coringas.
     *
     * @param termo texto a procurar (já sem espaços nas pontas)
     * @return livros encontrados, em ordem alfabética de título
     * @throws DAOException se ocorrer erro no banco
     */
    public List<Livro> buscarPorTituloOuAutor(String termo) throws DAOException {
        String sql = "SELECT * FROM livro "
                + "WHERE LOWER(titulo) LIKE ? ESCAPE '\\' "
                + "OR LOWER(autor) LIKE ? ESCAPE '\\' "
                + "ORDER BY titulo";

        // Ex.: termo "machado" vira o padrão "%machado%" (contém "machado")
        String padrao = "%" + escaparLike(termo.toLowerCase()) + "%";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        List<Livro> livros = new ArrayList<>();
        try {
            conn = FabricaDeConexoes.getConexao();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, padrao);
            stmt.setString(2, padrao);
            rs = stmt.executeQuery();
            while (rs.next()) {
                livros.add(montarLivro(rs));
            }
            return livros;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Erro ao buscar livros", e);
            throw new DAOException("Não foi possível fazer a busca. Tente novamente.", e);
        } finally {
            fechar(conn, stmt, rs);
        }
    }

    /**
     * Busca um livro pelo id.
     *
     * @param id identificador do livro
     * @return o livro encontrado, ou null se não existir
     * @throws DAOException se ocorrer erro no banco
     */
    public Livro buscarPorId(int id) throws DAOException {
        String sql = "SELECT * FROM livro WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = FabricaDeConexoes.getConexao();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return montarLivro(rs);
            }
            return null;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Erro ao buscar livro por id", e);
            throw new DAOException("Não foi possível carregar o livro. Tente novamente.", e);
        } finally {
            fechar(conn, stmt, rs);
        }
    }

    /**
     * Atualiza todos os campos de um livro existente (localizado pelo id).
     *
     * @param livro livro já validado, com o id preenchido
     * @return true se o livro foi atualizado; false se o id não existe mais
     * @throws DAOException se ocorrer erro no banco
     */
    public boolean atualizar(Livro livro) throws DAOException {
        String sql = "UPDATE livro SET titulo = ?, autor = ?, ano_publicacao = ?, "
                + "genero = ?, sinopse = ? WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = FabricaDeConexoes.getConexao();
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, livro.getTitulo());
            stmt.setString(2, livro.getAutor());
            stmt.setInt(3, livro.getAnoPublicacao());
            stmt.setString(4, livro.getGenero());
            stmt.setString(5, livro.getSinopse());
            stmt.setInt(6, livro.getId());
            // executeUpdate devolve quantas linhas foram alteradas
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Erro ao atualizar livro", e);
            throw new DAOException("Não foi possível salvar as alterações. Tente novamente.", e);
        } finally {
            fechar(conn, stmt, null);
        }
    }

    /**
     * Exclui um livro pelo id.
     *
     * @param id identificador do livro
     * @return true se o livro foi excluído; false se o id não existia
     * @throws DAOException se ocorrer erro no banco
     */
    public boolean excluir(int id) throws DAOException {
        String sql = "DELETE FROM livro WHERE id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = FabricaDeConexoes.getConexao();
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOG.log(Level.SEVERE, "Erro ao excluir livro", e);
            throw new DAOException("Não foi possível excluir o livro. Tente novamente.", e);
        } finally {
            fechar(conn, stmt, null);
        }
    }

    /**
     * Faz os caracteres especiais do LIKE (\ % _) valerem como texto comum,
     * colocando uma barra invertida antes de cada um.
     * Ex.: "100%" vira "100\%" e não "qualquer coisa que comece com 100".
     */
    private String escaparLike(String texto) {
        return texto.replace("\\", "\\\\")
                    .replace("%", "\\%")
                    .replace("_", "\\_");
    }

    /**
     * Converte a linha atual do ResultSet em um objeto Livro.
     * Fica separado para ser reaproveitado por todas as consultas.
     */
    private Livro montarLivro(ResultSet rs) throws SQLException {
        Livro livro = new Livro();
        livro.setId(rs.getInt("id"));
        livro.setTitulo(rs.getString("titulo"));
        livro.setAutor(rs.getString("autor"));
        livro.setAnoPublicacao(rs.getInt("ano_publicacao"));
        livro.setGenero(rs.getString("genero"));
        livro.setSinopse(rs.getString("sinopse"));
        return livro;
    }

    /**
     * Fecha ResultSet, PreparedStatement e Connection (nesta ordem).
     * É chamado no bloco finally, então roda sempre, com ou sem erro.
     * Qualquer um dos parâmetros pode ser null.
     */
    private void fechar(Connection conn, PreparedStatement stmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            // Falha ao fechar não deve derrubar a requisição; apenas registramos
            LOG.log(Level.WARNING, "Erro ao fechar recursos do banco", e);
        }
    }
}
