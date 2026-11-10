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
 * Controle da tela de detalhes de um livro.
 * URL: GET /livros/detalhes?id=1
 */
@WebServlet("/livros/detalhes")
public class DetalhesLivroServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final LivroService service = new LivroService();

    /**
     * Carrega o livro pelo id e mostra todos os seus dados.
     * Se o id for inválido ou não existir, volta para a listagem com aviso.
     *
     * @param req  requisição HTTP (parâmetros: id, e msg opcional)
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
            // Mensagem exibida depois de salvar uma edição (texto fixo)
            if ("atualizado".equals(req.getParameter("msg"))) {
                req.setAttribute("sucesso", "Alterações salvas com sucesso.");
            }
            req.setAttribute("livro", livro);
            req.getRequestDispatcher("/WEB-INF/jsp/detalhes.jsp").forward(req, resp);
        } catch (DAOException e) {
            resp.sendRedirect(req.getContextPath() + "/livros?msg=falha");
        }
    }
}
