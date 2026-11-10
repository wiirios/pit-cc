package br.edu.catalogo.servlet;

import br.edu.catalogo.dao.DAOException;
import br.edu.catalogo.model.Livro;
import br.edu.catalogo.service.LivroService;
import br.edu.catalogo.service.ValidacaoException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Controle da tela principal: lista os livros e faz a busca.
 * URL: GET /livros              (todos os livros)
 *      GET /livros?busca=termo  (livros cujo título ou autor contém o termo)
 */
@WebServlet("/livros")
public class ListarLivrosServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final LivroService service = new LivroService();

    /**
     * Busca os livros no Service e encaminha para a JSP de listagem.
     * Também transforma o código "msg" da URL (enviado após cadastrar,
     * excluir etc.) em uma mensagem para o usuário.
     *
     * @param req  requisição HTTP (parâmetros opcionais: busca, msg)
     * @param resp resposta HTTP
     * @throws ServletException se o encaminhamento para a JSP falhar
     * @throws IOException      se houver erro de entrada/saída
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        traduzirMensagem(req);

        String busca = req.getParameter("busca");
        req.setAttribute("busca", busca == null ? "" : busca.trim());

        try {
            List<Livro> livros = service.buscar(busca);
            req.setAttribute("livros", livros);
        } catch (ValidacaoException e) {
            req.setAttribute("erro", e.getErros().get(0));
        } catch (DAOException e) {
            // Mostra a mensagem amigável na própria página de listagem
            req.setAttribute("erro", e.getMessage());
        }

        req.getRequestDispatcher("/WEB-INF/jsp/lista.jsp").forward(req, resp);
    }

    /**
     * Converte o código "msg" em texto. Os textos são fixos no código:
     * o valor que vem na URL nunca é exibido diretamente na tela.
     */
    private void traduzirMensagem(HttpServletRequest req) {
        String msg = req.getParameter("msg");
        if ("criado".equals(msg)) {
            req.setAttribute("sucesso", "Livro cadastrado com sucesso.");
        } else if ("excluido".equals(msg)) {
            req.setAttribute("sucesso", "Livro excluído com sucesso.");
        } else if ("naoencontrado".equals(msg)) {
            req.setAttribute("erro", "Livro não encontrado. Ele pode ter sido excluído.");
        } else if ("falha".equals(msg)) {
            req.setAttribute("erro", "Não foi possível concluir a operação. Tente novamente.");
        }
    }
}
