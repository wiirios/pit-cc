package br.edu.catalogo.servlet;

import br.edu.catalogo.dao.DAOException;
import br.edu.catalogo.model.Livro;
import br.edu.catalogo.service.LivroService;
import br.edu.catalogo.service.ValidacaoException;
import br.edu.catalogo.util.Parametros;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Controle da edição de livros.
 * URL: GET /livros/editar?id=1 (mostra o formulário preenchido)
 *      POST /livros/editar     (recebe o formulário, com o id num campo oculto)
 */
@WebServlet("/livros/editar")
public class EditarLivroServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final LivroService service = new LivroService();

    /**
     * Carrega o livro e mostra o formulário já preenchido com os dados atuais.
     *
     * @param req  requisição HTTP (parâmetro: id)
     * @param resp resposta HTTP
     * @throws ServletException se o encaminhamento para a JSP falhar
     * @throws IOException      se houver erro de entrada/saída
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer id = Parametros.lerId(req);
        if (id == null) {
            resp.sendRedirect(req.getContextPath() + "/livros?msg=naoencontrado");
            return;
        }

        try {
            Livro livro = service.buscarPorId(id);
            if (livro == null) {
                resp.sendRedirect(req.getContextPath() + "/livros?msg=naoencontrado");
                return;
            }
            mostrarFormulario(req, resp, livro, String.valueOf(livro.getAnoPublicacao()));
        } catch (DAOException e) {
            resp.sendRedirect(req.getContextPath() + "/livros?msg=falha");
        }
    }

    /**
     * Recebe as alterações, valida pelo Service e grava. Se der certo,
     * redireciona para os detalhes do livro; se houver erro, volta ao
     * formulário mantendo o que o usuário digitou.
     *
     * @param req  requisição HTTP com os campos do formulário e o id
     * @param resp resposta HTTP
     * @throws ServletException se o encaminhamento para a JSP falhar
     * @throws IOException      se houver erro de entrada/saída
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        Integer id = Parametros.lerId(req);
        if (id == null) {
            resp.sendRedirect(req.getContextPath() + "/livros?msg=naoencontrado");
            return;
        }

        Livro livro = new Livro();
        livro.setId(id);
        livro.setTitulo(req.getParameter("titulo"));
        livro.setAutor(req.getParameter("autor"));
        livro.setGenero(req.getParameter("genero"));
        livro.setSinopse(req.getParameter("sinopse"));
        String anoTexto = req.getParameter("anoPublicacao");

        try {
            boolean atualizado = service.atualizar(livro, anoTexto);
            if (atualizado) {
                resp.sendRedirect(req.getContextPath() + "/livros/detalhes?id=" + id + "&msg=atualizado");
            } else {
                // O livro foi excluído enquanto o usuário editava
                resp.sendRedirect(req.getContextPath() + "/livros?msg=naoencontrado");
            }
        } catch (ValidacaoException e) {
            req.setAttribute("erros", e.getErros());
            mostrarFormulario(req, resp, livro, anoTexto);
        } catch (DAOException e) {
            req.setAttribute("erro", e.getMessage());
            mostrarFormulario(req, resp, livro, anoTexto);
        }
    }

    /**
     * Prepara os atributos usados por formulario.jsp (o mesmo do cadastro)
     * e faz o encaminhamento.
     */
    private void mostrarFormulario(HttpServletRequest req, HttpServletResponse resp,
                                   Livro livro, String anoTexto)
            throws ServletException, IOException {
        req.setAttribute("livro", livro);
        req.setAttribute("anoTexto", anoTexto);
        req.setAttribute("tituloPagina", "Editar livro");
        req.setAttribute("acao", "/livros/editar");
        req.getRequestDispatcher("/WEB-INF/jsp/formulario.jsp").forward(req, resp);
    }
}
