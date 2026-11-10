<%--
  Página de erro amigável (configurada no web.xml).
  De propósito NÃO mostra a exceção nem detalhes técnicos ao usuário.
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="tituloPagina" value="Ops!"/>
<%@ include file="cabecalho.jspf" %>

<div class="vazio">
    <%-- Desenho decorativo: um livro caído na prateleira, ao lado de um lugar vazio --%>
    <div class="prateleira-vazia" aria-hidden="true">
        <span class="lugar"></span>
        <span class="lombada lombada-caida lombada-1"></span>
    </div>
    <c:choose>
        <c:when test="${requestScope['jakarta.servlet.error.status_code'] == 404}">
            <h1>Página não encontrada</h1>
            <p>O endereço acessado não existe.</p>
        </c:when>
        <c:otherwise>
            <h1>Algo deu errado</h1>
            <p>Não foi possível concluir a operação. Tente novamente em instantes.</p>
        </c:otherwise>
    </c:choose>
    <a class="botao" href="<c:url value='/livros'/>">Voltar ao catálogo</a>
</div>

<%@ include file="rodape.jspf" %>
