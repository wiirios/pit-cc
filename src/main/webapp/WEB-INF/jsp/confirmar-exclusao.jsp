<%--
  Confirmação de exclusão. Recebe do servlet: livro.
  A exclusão só acontece ao enviar o formulário (POST) abaixo.
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="tituloPagina" value="Excluir livro"/>
<%@ include file="cabecalho.jspf" %>

<%-- A lombada aparece inclinada, como um livro sendo tirado da estante --%>
<div class="ficha-com-lombada">
    <div class="lombada lombada-grande lombada-saindo lombada-<c:out value='${livro.id % 6}'/>" aria-hidden="true">
        <span class="lombada-titulo"><c:out value="${livro.titulo}"/></span>
    </div>

    <article class="ficha">
        <h1>Excluir livro</h1>
        <p class="pergunta">Tem certeza de que deseja excluir o livro
            <strong>"<c:out value="${livro.titulo}"/>"</strong>,
            de <c:out value="${livro.autor}"/>?</p>
        <p class="dica">Esta ação não pode ser desfeita.</p>

        <form method="post" action="<c:url value='/livros/excluir'/>" class="acoes">
            <input type="hidden" name="id" value="<c:out value='${livro.id}'/>">
            <button type="submit" class="botao botao-perigo">Sim, excluir</button>
            <a class="botao botao-secundario" href="<c:url value='/livros/detalhes'><c:param name='id' value='${livro.id}'/></c:url>">Cancelar</a>
        </form>
    </article>
</div>

<%@ include file="rodape.jspf" %>
