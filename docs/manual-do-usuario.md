# Manual do Usuário — Catálogo de Livros

## 1. O que é

Aplicação web para cadastrar e consultar livros. Para cada livro, o catálogo guarda título,
autor, ano de publicação, gênero e sinopse. Os dados ficam salvos em um banco de dados e
continuam lá depois que o servidor é desligado.

## 2. Como instalar e executar

### Requisitos

- **JDK 17 ou mais recente** (comando `java -version`)
- **Apache Maven 3.9 ou mais recente** (comando `mvn -v`)
- Navegador web (Chrome, Edge, Firefox…)

Não é preciso instalar banco de dados nem servidor: o banco H2 e o servidor Jetty são
baixados pelo Maven na primeira execução, por isso ela exige internet e demora um pouco mais.

### Passo a passo

1. Abra um terminal na pasta do projeto (a pasta que contém o arquivo `pom.xml`).
2. Execute:

   ```bash
   mvn jetty:run
   ```

3. Espere aparecer no terminal a linha `Started oejs.Server`.
4. Abra no navegador: **http://localhost:8080**
5. Para desligar o servidor, volte ao terminal e pressione **Ctrl + C**.

### Onde ficam os dados

O banco é criado sozinho no arquivo `dados/catalogo.mv.db`, dentro da pasta do projeto.

- Para **apagar todos os livros** e começar do zero: desligue o servidor e apague a pasta `dados`.
  Na próxima execução, a tabela é recriada vazia.
- Se aparecer erro de banco "em uso", verifique se não há outro `mvn jetty:run` aberto:
  o H2 em modo arquivo só aceita um servidor por vez.
- Se a porta 8080 estiver ocupada por outro programa, feche esse programa e rode de novo.

## 3. Como usar

### Tela inicial: lista de livros

Ao abrir http://localhost:8080 você é levado à lista de livros, em ordem alfabética de título.
Em cada linha há três links: **Detalhes**, **Editar** e **Excluir**. No celular, cada livro
aparece como um cartão.

### Cadastrar um livro

1. Clique em **+ Novo livro** (canto superior direito).
2. Preencha os campos. Os marcados com **\*** são obrigatórios:
   - **Título\*** — até 255 caracteres
   - **Autor\*** — até 255 caracteres
   - **Ano de publicação\*** — apenas números (de 1 até o ano atual + 5)
   - **Gênero** — opcional, até 100 caracteres (ex.: Romance, Ficção científica)
   - **Sinopse** — opcional, até 2000 caracteres (as quebras de linha são mantidas)
3. Clique em **Salvar**. Você volta à lista com a mensagem "Livro cadastrado com sucesso."

Se algum campo estiver errado, o formulário volta com a lista do que corrigir e com o que você
já tinha digitado.

### Buscar

Digite parte do **título ou do nome do autor** no campo de busca e clique em **Buscar**.

- A busca não diferencia maiúsculas de minúsculas ("machado" acha "Machado de Assis").
- Os acentos precisam ser digitados ("sertão" acha "Grande Sertão: Veredas"; "sertao" não acha).
- Clique em **Limpar** para voltar à lista completa.

### Ver detalhes

Clique no título do livro ou em **Detalhes** para ver todos os campos, inclusive a sinopse.

### Editar

Na lista ou na tela de detalhes, clique em **Editar**. O formulário abre preenchido; altere o
que quiser e clique em **Salvar**. Você vai para a tela de detalhes com a mensagem
"Alterações salvas com sucesso." Para desistir, clique em **Cancelar**.

### Excluir

Clique em **Excluir**. O sistema pergunta se você tem certeza e mostra o título e o autor do livro.

- **Sim, excluir** apaga o livro definitivamente (não é possível desfazer).
- **Cancelar** volta para os detalhes sem apagar nada.

## 4. Mensagens que podem aparecer

| Mensagem | O que significa |
|---|---|
| "Livro não encontrado. Ele pode ter sido excluído." | O endereço aponta para um livro que não existe (ou que foi excluído) |
| "Página não encontrada" | O endereço digitado não existe no sistema |
| "Algo deu errado" / "Não foi possível concluir a operação" | Erro inesperado; tente de novo e, se continuar, reinicie o servidor |
| "Nenhum livro encontrado para …" | A busca não encontrou nenhum título ou autor com esse texto |
