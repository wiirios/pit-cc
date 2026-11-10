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

/**
 * Controle do cadastro de novos livros.
 * URL: GET /livros/novo  (mostra o formulário vazio)
 *      POST /livros/novo (recebe o formulário e grava)
 */
@WebServlet("/livros/novo")
public class CadastrarLivroServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final LivroService service = new LivroService();

    /**
     * Mostra o formulário de cadastro em branco.
     *
     * @param req  requisição HTTP
     * @param resp resposta HTTP
     * @throws ServletException se o encaminhamento para a JSP falhar
     * @throws IOException      se houver erro de entrada/saída
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        mostrarFormulario(req, resp, new Livro(), "");
    }

    /**
     * Recebe os dados do formulário, pede ao Service para validar e gravar
     * e, se der certo, redireciona para a listagem. Se houver erro, volta ao
     * formulário mantendo o que o usuário digitou.
     *
     * @param req  requisição HTTP com os campos do formulário
     * @param resp resposta HTTP
     * @throws ServletException se o encaminhamento para a JSP falhar
     * @throws IOException      se houver erro de entrada/saída
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Necessário para ler acentos corretamente (ex.: "Ficção")
        req.setCharacterEncoding("UTF-8");

        Livro livro = new Livro();
        livro.setTitulo(req.getParameter("titulo"));
        livro.setAutor(req.getParameter("autor"));
        livro.setGenero(req.getParameter("genero"));
        livro.setSinopse(req.getParameter("sinopse"));
        String anoTexto = req.getParameter("anoPublicacao");

        try {
            service.cadastrar(livro, anoTexto);
            // Padrão Post/Redirect/Get: evita cadastro duplicado ao atualizar a página
            resp.sendRedirect(req.getContextPath() + "/livros?msg=criado");
        } catch (ValidacaoException e) {
            req.setAttribute("erros", e.getErros());
            mostrarFormulario(req, resp, livro, anoTexto);
        } catch (DAOException e) {
            req.setAttribute("erro", e.getMessage());
            mostrarFormulario(req, resp, livro, anoTexto);
        }
    }

    /**
     * Prepara os atributos usados por formulario.jsp e faz o encaminhamento.
     * A mesma JSP será reaproveitada na edição; por isso título e ação variam.
     */
    private void mostrarFormulario(HttpServletRequest req, HttpServletResponse resp,
                                   Livro livro, String anoTexto)
            throws ServletException, IOException {
        req.setAttribute("livro", livro);
        req.setAttribute("anoTexto", anoTexto);
        req.setAttribute("tituloPagina", "Novo livro");
        req.setAttribute("acao", "/livros/novo");
        req.getRequestDispatcher("/WEB-INF/jsp/formulario.jsp").forward(req, resp);
    }
}
