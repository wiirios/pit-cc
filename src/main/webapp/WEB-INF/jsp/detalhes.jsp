<%-- Tela de detalhes de um livro. Recebe do servlet: livro, sucesso --%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="tituloPagina" value="${livro.titulo}"/>
<%@ include file="cabecalho.jspf" %>

<p class="voltar"><a href="<c:url value='/livros'/>">&larr; Voltar para a lista</a></p>

<c:if test="${not empty sucesso}">
    <div class="alerta alerta-sucesso" role="status"><c:out value="${sucesso}"/></div>
</c:if>

<%-- Lombada "em pé" ao lado da ficha catalográfica do livro --%>
<div class="ficha-com-lombada">
    <div class="lombada lombada-grande lombada-<c:out value='${livro.id % 6}'/>" aria-hidden="true">
        <span class="lombada-titulo"><c:out value="${livro.titulo}"/></span>
    </div>

    <article class="ficha">
        <h1><c:out value="${livro.titulo}"/></h1>

        <dl class="detalhes">
            <dt>Autor</dt>
            <dd><c:out value="${livro.autor}"/></dd>

            <dt>Ano de publicação</dt>
            <dd><c:out value="${livro.anoPublicacao}"/></dd>

            <dt>Gênero</dt>
            <dd>
                <c:choose>
                    <c:when test="${not empty livro.genero}"><c:out value="${livro.genero}"/></c:when>
                    <c:otherwise><span class="dica">Não informado</span></c:otherwise>
                </c:choose>
            </dd>

            <%-- A sinopse ocupa a largura toda da ficha, sobre a pauta azul --%>
            <dt class="bloco-sinopse">Sinopse</dt>
            <dd class="bloco-sinopse">
                <c:choose>
                    <%-- span na mesma linha: o CSS preserva só as quebras digitadas pelo usuário --%>
                    <c:when test="${not empty livro.sinopse}"><span class="sinopse"><c:out value="${livro.sinopse}"/></span></c:when>
                    <c:otherwise><span class="dica">Não informada</span></c:otherwise>
                </c:choose>
            </dd>
        </dl>

        <%-- c:url com c:param monta o link e codifica o id corretamente --%>
        <div class="acoes">
            <a class="botao" href="<c:url value='/livros/editar'><c:param name='id' value='${livro.id}'/></c:url>">Editar</a>
            <a class="botao botao-perigo" href="<c:url value='/livros/excluir'><c:param name='id' value='${livro.id}'/></c:url>">Excluir</a>
        </div>
    </article>
</div>

<%@ include file="rodape.jspf" %>
