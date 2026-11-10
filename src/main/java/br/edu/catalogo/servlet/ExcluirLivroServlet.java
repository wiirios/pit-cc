package br.edu.catalogo.servlet;

import br.edu.catalogo.dao.DAOException;
import br.edu.catalogo.model.Livro;
import br.edu.catalogo.service.LivroService;
import br.edu.catalogo.util.Parametros;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Controle da exclusão de livros, em dois passos:
 * URL: GET /livros/excluir?id=1 (pergunta "tem certeza?")
 *      POST /livros/excluir     (exclui de fato)
 *
 * A exclusão só acontece por POST: um simples link (GET) nunca apaga dados.
 */
@WebServlet("/livros/excluir")
public class ExcluirLivroServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final LivroService service = new LivroService();

    /**
     * Mostra a página de confirmação da exclusão.
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
            req.setAttribute("livro", livro);
            req.getRequestDispatcher("/WEB-INF/jsp/confirmar-exclusao.jsp").forward(req, resp);
        } catch (DAOException e) {
            resp.sendRedirect(req.getContextPath() + "/livros?msg=falha");
        }
    }

    /**
     * Exclui o livro confirmado e volta para a listagem com uma mensagem.
     *
     * @param req  requisição HTTP (campo oculto: id)
     * @param resp resposta HTTP
     * @throws ServletException não lançada aqui, mas faz parte da assinatura
     * @throws IOException      se houver erro de entrada/saída
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Mesmo padrão dos outros POSTs: ler o formulário sempre em UTF-8
        req.setCharacterEncoding("UTF-8");

        Integer id = Parametros.lerId(req);
        if (id == null) {
            resp.sendRedirect(req.getContextPath() + "/livros?msg=naoencontrado");
            return;
        }

        try {
            boolean excluido = service.excluir(id);
            String msg = excluido ? "excluido" : "naoencontrado";
            resp.sendRedirect(req.getContextPath() + "/livros?msg=" + msg);
        } catch (DAOException e) {
            resp.sendRedirect(req.getContextPath() + "/livros?msg=falha");
        }
    }
}
