<%-- Tela de listagem e busca. Recebe do servlet: livros, busca, sucesso, erro --%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="tituloPagina" value="Livros"/>
<%@ include file="cabecalho.jspf" %>

<section class="cabecalho-lista">
    <h1>Livros</h1>

    <%-- Busca por título ou autor (GET: o termo fica na URL e pode ser compartilhado) --%>
    <form class="busca" method="get" action="<c:url value='/livros'/>" role="search">
        <label for="busca" class="oculto">Buscar por título ou autor</label>
        <input type="search" id="busca" name="busca" maxlength="100"
               placeholder="Buscar por título ou autor..."
               value="<c:out value='${busca}'/>">
        <button type="submit" class="botao">Buscar</button>
        <c:if test="${not empty busca}">
            <a class="botao botao-secundario" href="<c:url value='/livros'/>">Limpar</a>
        </c:if>
    </form>
</section>

<%-- Mensagens de sucesso ou de erro (sempre escapadas com c:out) --%>
<c:if test="${not empty sucesso}">
    <div class="alerta alerta-sucesso" role="status"><c:out value="${sucesso}"/></div>
</c:if>
<c:if test="${not empty erro}">
    <div class="alerta alerta-erro" role="alert"><c:out value="${erro}"/></div>
</c:if>

<c:choose>
    <%-- Busca feita, mas sem resultados: prateleira vazia --%>
    <c:when test="${empty livros and not empty busca and empty erro}">
        <div class="vazio">
            <div class="prateleira-vazia" aria-hidden="true"><span class="lugar"></span></div>
            <p>Nenhum livro encontrado para "<c:out value="${busca}"/>".</p>
            <a class="botao botao-secundario" href="<c:url value='/livros'/>">Ver todos os livros</a>
        </div>
    </c:when>
    <%-- Catálogo vazio --%>
    <c:when test="${empty livros and empty erro}">
        <div class="vazio">
            <div class="prateleira-vazia" aria-hidden="true">
                <span class="lugar"></span><span class="lugar"></span><span class="lugar"></span>
            </div>
            <p>Nenhum livro cadastrado ainda.</p>
            <a class="botao" href="<c:url value='/livros/novo'/>">Cadastrar o primeiro livro</a>
        </div>
    </c:when>
    <c:when test="${not empty livros}">
        <p class="contagem">
            <c:choose>
                <c:when test="${not empty busca}">
                    <c:out value="${livros.size()}"/> resultado(s) para "<c:out value="${busca}"/>"
                </c:when>
                <c:otherwise>Total: <c:out value="${livros.size()}"/> livro(s)</c:otherwise>
            </c:choose>
        </p>

        <%--
          Estante: cada livro é um item da lista, com uma lombada colorida.
          A cor da lombada vem do resto da divisão do id por 6 (classes lombada-0 a lombada-5),
          então cada livro mantém sempre a mesma cor.
        --%>
        <ul class="estante">
            <c:forEach var="livro" items="${livros}">
                <%-- Links de cada livro, montados com c:url + c:param --%>
                <c:url var="urlDetalhes" value="/livros/detalhes"><c:param name="id" value="${livro.id}"/></c:url>
                <c:url var="urlEditar" value="/livros/editar"><c:param name="id" value="${livro.id}"/></c:url>
                <c:url var="urlExcluir" value="/livros/excluir"><c:param name="id" value="${livro.id}"/></c:url>
                <li class="livro">
                    <%-- Lombada decorativa: o título já é lido no link ao lado --%>
                    <div class="lombada lombada-<c:out value='${livro.id % 6}'/>" aria-hidden="true">
                        <span class="lombada-titulo"><c:out value="${livro.titulo}"/></span>
                    </div>

                    <div class="livro-info">
                        <h2 class="livro-titulo">
                            <a href="<c:out value='${urlDetalhes}'/>"><c:out value="${livro.titulo}"/></a>
                        </h2>
                        <p class="livro-autor"><c:out value="${livro.autor}"/></p>
                        <p class="livro-dados">
                            <span class="etiqueta"><span class="oculto">Ano de publicação: </span><c:out value="${livro.anoPublicacao}"/></span>
                            <c:if test="${not empty livro.genero}">
                                <span class="etiqueta"><span class="oculto">Gênero: </span><c:out value="${livro.genero}"/></span>
                            </c:if>
                        </p>
                    </div>

                    <div class="acoes-linha">
                        <a href="<c:out value='${urlDetalhes}'/>">Detalhes</a>
                        <a href="<c:out value='${urlEditar}'/>">Editar</a>
                        <a href="<c:out value='${urlExcluir}'/>" class="link-perigo">Excluir</a>
                    </div>
                </li>
            </c:forEach>
        </ul>
    </c:when>
</c:choose>

<%@ include file="rodape.jspf" %>
