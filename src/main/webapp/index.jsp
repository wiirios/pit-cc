<%-- Página inicial: apenas redireciona a raiz "/" para a listagem de livros --%>
<%-- session="false": esta página não usa sessão, evitando ";jsessionid" na URL --%>
<%@ page session="false" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:redirect url="/livros"/>
