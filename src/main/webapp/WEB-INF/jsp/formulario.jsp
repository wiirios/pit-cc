<%--
  Formulário de livro (usado no cadastro e, depois, na edição).
  Recebe do servlet: livro, anoTexto, tituloPagina, acao, erros, erro.
--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ include file="cabecalho.jspf" %>

<%--
  O Service devolve os erros como uma lista de textos. Para mostrar cada erro
  junto do seu campo, a JSP separa as mensagens pelo início do texto
  (ex.: "O título ..." vai para o campo Título). Nada muda no Java.
--%>
<c:forEach var="mensagem" items="${erros}">
    <c:choose>
        <c:when test="${fn:startsWith(mensagem, 'O título')}"><c:set var="erroTitulo" value="${mensagem}"/></c:when>
        <c:when test="${fn:startsWith(mensagem, 'O autor')}"><c:set var="erroAutor" value="${mensagem}"/></c:when>
        <c:when test="${fn:startsWith(mensagem, 'O ano')}"><c:set var="erroAno" value="${mensagem}"/></c:when>
        <c:when test="${fn:startsWith(mensagem, 'O gênero')}"><c:set var="erroGenero" value="${mensagem}"/></c:when>
        <c:when test="${fn:startsWith(mensagem, 'A sinopse')}"><c:set var="erroSinopse" value="${mensagem}"/></c:when>
    </c:choose>
</c:forEach>

<%--
  Os atributos required/maxlength ajudam o usuário, mas não garantem nada:
  a validação de verdade acontece no servidor (LivroService).
--%>
<form class="ficha formulario" method="post" action="<c:url value='${acao}'/>">
    <h1><c:out value="${tituloPagina}"/></h1>

    <%-- Resumo dos erros de validação vindos do Service --%>
    <c:if test="${not empty erros}">
        <div class="alerta alerta-erro" role="alert">
            <p>Corrija os campos abaixo:</p>
            <ul>
                <c:forEach var="mensagem" items="${erros}">
                    <li><c:out value="${mensagem}"/></li>
                </c:forEach>
            </ul>
        </div>
    </c:if>
    <c:if test="${not empty erro}">
        <div class="alerta alerta-erro" role="alert"><c:out value="${erro}"/></div>
    </c:if>

    <%-- Na edição, o id vai num campo oculto para o servlet saber qual livro alterar --%>
    <c:if test="${livro.id > 0}">
        <input type="hidden" name="id" value="<c:out value='${livro.id}'/>">
    </c:if>

    <%-- Cada campo com erro recebe a classe "campo-com-erro" e a mensagem logo abaixo,
         ligada ao campo por aria-describedby (leitores de tela leem o erro junto) --%>
    <div class="campo ${not empty erroTitulo ? 'campo-com-erro' : ''}">
        <label for="titulo">Título *</label>
        <input type="text" id="titulo" name="titulo" maxlength="255" required
               value="<c:out value='${livro.titulo}'/>"
               <c:if test="${not empty erroTitulo}">aria-invalid="true" aria-describedby="erro-titulo"</c:if>>
        <c:if test="${not empty erroTitulo}"><p class="erro-campo" id="erro-titulo"><c:out value="${erroTitulo}"/></p></c:if>
    </div>

    <div class="campo ${not empty erroAutor ? 'campo-com-erro' : ''}">
        <label for="autor">Autor *</label>
        <input type="text" id="autor" name="autor" maxlength="255" required
               value="<c:out value='${livro.autor}'/>"
               <c:if test="${not empty erroAutor}">aria-invalid="true" aria-describedby="erro-autor"</c:if>>
        <c:if test="${not empty erroAutor}"><p class="erro-campo" id="erro-autor"><c:out value="${erroAutor}"/></p></c:if>
    </div>

    <div class="linha">
        <div class="campo ${not empty erroAno ? 'campo-com-erro' : ''}">
            <label for="anoPublicacao">Ano de publicação *</label>
            <input type="number" id="anoPublicacao" name="anoPublicacao" min="1" required
                   value="<c:out value='${anoTexto}'/>"
                   <c:if test="${not empty erroAno}">aria-invalid="true" aria-describedby="erro-ano"</c:if>>
            <c:if test="${not empty erroAno}"><p class="erro-campo" id="erro-ano"><c:out value="${erroAno}"/></p></c:if>
        </div>
        <div class="campo ${not empty erroGenero ? 'campo-com-erro' : ''}">
            <label for="genero">Gênero</label>
            <input type="text" id="genero" name="genero" maxlength="100"
                   placeholder="Ex.: Romance, Ficção científica"
                   value="<c:out value='${livro.genero}'/>"
                   <c:if test="${not empty erroGenero}">aria-invalid="true" aria-describedby="erro-genero"</c:if>>
            <c:if test="${not empty erroGenero}"><p class="erro-campo" id="erro-genero"><c:out value="${erroGenero}"/></p></c:if>
        </div>
    </div>

    <div class="campo ${not empty erroSinopse ? 'campo-com-erro' : ''}">
        <label for="sinopse">Sinopse</label>
        <textarea id="sinopse" name="sinopse" rows="5" maxlength="2000"
                  aria-describedby="contador-sinopse<c:if test='${not empty erroSinopse}'> erro-sinopse</c:if>"
                  <c:if test="${not empty erroSinopse}">aria-invalid="true"</c:if>><c:out value="${livro.sinopse}"/></textarea>
        <c:if test="${not empty erroSinopse}"><p class="erro-campo" id="erro-sinopse"><c:out value="${erroSinopse}"/></p></c:if>
        <%-- Sem JavaScript fica só o limite; com JavaScript vira um contador --%>
        <p class="dica contador" id="contador-sinopse">Até 2000 caracteres</p>
    </div>

    <p class="dica">* Campos obrigatórios</p>

    <div class="acoes">
        <button type="submit" class="botao">Salvar</button>
        <%-- Cancelar: na edição volta aos detalhes; no cadastro, à lista --%>
        <c:choose>
            <c:when test="${livro.id > 0}">
                <a class="botao botao-secundario" href="<c:url value='/livros/detalhes'><c:param name='id' value='${livro.id}'/></c:url>">Cancelar</a>
            </c:when>
            <c:otherwise>
                <a class="botao botao-secundario" href="<c:url value='/livros'/>">Cancelar</a>
            </c:otherwise>
        </c:choose>
    </div>
</form>

<%--
  JavaScript opcional: contador de caracteres da sinopse.
  Se o navegador não rodar o script, o formulário funciona do mesmo jeito.
--%>
<script>
    (function () {
        var campo = document.getElementById('sinopse');
        var contador = document.getElementById('contador-sinopse');
        if (!campo || !contador) {
            return;
        }
        var limite = campo.maxLength;
        function atualizar() {
            contador.textContent = campo.value.length + ' de ' + limite + ' caracteres';
        }
        campo.addEventListener('input', atualizar);
        atualizar();
    })();
</script>

<%@ include file="rodape.jspf" %>
