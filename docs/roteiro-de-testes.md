# Roteiro de Testes Manuais — Catálogo de Livros

Testes funcionais e de integração do fluxo completo: do formulário na JSP até o banco e de
volta à tela (enunciado, p. 1/7, folha 27). Não há testes automatizados (JUnit), uma decisão
do projeto, já que o enunciado os trata como opcionais.

## Como executar

1. Desligue o servidor, se estiver rodando, e apague a pasta `dados/` para começar com o
   banco vazio.
2. Rode `mvn jetty:run` e abra http://localhost:8080.
3. Execute os casos **na ordem**: alguns usam dados criados pelos anteriores.
4. Preencha a coluna **Obtido** com OK ou com o que aconteceu de diferente. Os casos marcados
   com 📸 são bons candidatos a print para a seção Resultados do relatório.

> Casos que dizem "na barra de endereço" são feitos digitando a URL diretamente no navegador.
> Os casos T52 e T53 enviam dados sem passar pelo formulário (simulando um usuário que burla
> a validação do HTML). Para eles, use o terminal (comandos `curl` prontos) ou as
> ferramentas de desenvolvedor do navegador (F12).

---

## 1. Inicialização e listagem

| ID | Teste | Passos | Resultado esperado | Obtido |
|---|---|---|---|---|
| T01 | Servidor sobe e cria o banco | `mvn jetty:run` com a pasta `dados/` apagada | Terminal mostra `Banco de dados inicializado com sucesso.` e `Started oejs.Server`, sem linhas `ERROR`; a pasta `dados/` aparece com `catalogo.mv.db` | |
| T02 | Raiz redireciona | Abrir `http://localhost:8080/` | Endereço muda para `/livros` | |
| T03 | Lista vazia 📸 | Abrir `/livros` com o banco vazio | Mensagem "Nenhum livro cadastrado ainda." e botão "Cadastrar o primeiro livro" | |

## 2. Cadastro (Create)

| ID | Teste | Passos | Resultado esperado | Obtido |
|---|---|---|---|---|
| T10 | Formulário de cadastro 📸 | Clicar em "+ Novo livro" | Formulário com Título\*, Autor\*, Ano de publicação\*, Gênero e Sinopse | |
| T11 | Cadastro completo | Título `Dom Casmurro`, Autor `Machado de Assis`, Ano `1899`, Gênero `Romance`, Sinopse `Bentinho e Capitu.` → Salvar | Volta à lista com "Livro cadastrado com sucesso."; o livro aparece na estante | |
| T12 | Cadastro com acentos | Título `Fundação`, Autor `Isaac Asimov`, Ano `1951`, Gênero `Ficção científica` → Salvar | Acentos aparecem corretos na lista | |
| T13 | Cadastro só com obrigatórios | Título `Grande Sertão: Veredas`, Autor `João Guimarães Rosa`, Ano `1956`, sem gênero e sinopse → Salvar | Cadastra normalmente; o livro aparece sem a etiqueta de gênero | |
| T14 | Ordem alfabética 📸 | Ver a lista após T11–T13 | Ordem: Dom Casmurro, Fundação, Grande Sertão: Veredas; "Total: 3 livro(s)" | |
| T15 | Persistência | Ctrl+C no terminal, `mvn jetty:run` de novo, abrir `/livros` | Os 3 livros continuam lá | |
| T16 | Atualizar a página após salvar | Após um cadastro, apertar F5 na lista | Nenhum livro duplicado (padrão Post/Redirect/Get) | |

## 3. Validação no servidor (Situação-Problema 2)

| ID | Teste | Passos | Resultado esperado | Obtido |
|---|---|---|---|---|
| T20 | Campos obrigatórios vazios 📸 | Ver T52 (o navegador bloqueia o envio por causa do `required`) | Mensagens: "O título é obrigatório.", "O autor é obrigatório.", "O ano de publicação é obrigatório." | |
| T21 | Só espaços no título | Título com 3 espaços, demais campos válidos → Salvar | "O título é obrigatório." (os espaços são removidos antes de validar) | |
| T22 | Ano com letras | Ver T53 (o campo numérico do navegador não aceita letras) | "O ano de publicação deve conter apenas números."; a aplicação não quebra e o texto digitado continua no campo | |
| T23 | Ano fora da faixa | Ano `99999` → Salvar | "O ano de publicação deve estar entre 1 e …" (ano atual + 5) | |
| T24 | Ano zero | Ano `0` → Salvar | Mensagem de faixa (o navegador também avisa, por causa do `min="1"`) | |
| T25 | Formulário mantém os dados | Provocar qualquer erro acima | O que foi digitado nos outros campos continua preenchido | |

## 4. Busca por título ou autor (SQL LIKE)

| ID | Teste | Passos | Resultado esperado | Obtido |
|---|---|---|---|---|
| T30 | Parte do título 📸 | Buscar `casm` | Só "Dom Casmurro"; texto "1 resultado(s) para "casm"" | |
| T31 | Parte do autor | Buscar `asimov` | Só "Fundação" | |
| T32 | Maiúsculas/minúsculas | Buscar `MACHADO` | Acha "Dom Casmurro" | |
| T33 | Com acento | Buscar `sertão` | Acha "Grande Sertão: Veredas" | |
| T34 | Sem resultado | Buscar `tolkien` | "Nenhum livro encontrado para "tolkien"." e botão "Ver todos os livros" | |
| T35 | Busca vazia | Apagar o texto e buscar | Lista todos os livros | |
| T36 | Limpar | Após uma busca, clicar em "Limpar" | Volta à lista completa e o campo fica vazio | |
| T37 | Coringa `%` | Buscar `%` | "Nenhum livro encontrado" (o `%` é tratado como texto, e não como "qualquer coisa") | |

## 5. Detalhes, edição e exclusão (Read / Update / Delete)

| ID | Teste | Passos | Resultado esperado | Obtido |
|---|---|---|---|---|
| T40 | Detalhes 📸 | Clicar no título "Dom Casmurro" | Todos os campos; botões Editar e Excluir | |
| T41 | Formulário de edição preenchido | Em Detalhes, clicar em "Editar" | Título "Editar livro"; campos com os valores atuais | |
| T42 | Edição válida 📸 | Mudar o ano para `1900` e a sinopse para duas linhas → Salvar | Vai para Detalhes com "Alterações salvas com sucesso."; ano 1900; a sinopse mostra as duas linhas | |
| T43 | Edição inválida | Apagar o título → Salvar | Erro "O título é obrigatório."; em Detalhes, o livro continua com o título antigo | |
| T44 | Cancelar edição | Em "Editar", clicar em Cancelar | Volta para os Detalhes sem alterar nada | |
| T45 | Confirmação de exclusão 📸 | Clicar em "Excluir" em um livro | Página "Tem certeza de que deseja excluir o livro …?"; o livro **ainda** aparece na lista | |
| T46 | Cancelar exclusão | Na confirmação, clicar em Cancelar | Volta aos Detalhes; o livro continua no catálogo | |
| T47 | Exclusão | Na confirmação, clicar em "Sim, excluir" | Lista com "Livro excluído com sucesso."; o total diminui em 1 | |

## 6. Segurança e robustez

| ID | Teste | Passos | Resultado esperado | Obtido |
|---|---|---|---|---|
| T50 | XSS (Cross-Site Scripting) 📸 | Cadastrar com título `<script>alert(1)</script>` | Nenhum alerta aparece; o título é exibido literalmente como texto na lista e nos detalhes | |
| T51 | SQL Injection no cadastro | Cadastrar com autor `x'); DROP TABLE livro; --` | Livro salvo com esse texto como autor; a tabela continua existindo e a lista funciona | |
| T52 | Obrigatórios burlando o HTML | No terminal: `curl -s -d "titulo=&autor=&anoPublicacao=" http://localhost:8080/livros/novo` | O HTML devolvido contém as 3 mensagens de obrigatório | |
| T53 | Ano com letras burlando o HTML | `curl -s -d "titulo=X&autor=Y&anoPublicacao=abc" http://localhost:8080/livros/novo` | O HTML contém "O ano de publicação deve conter apenas números." | |
| T54 | SQL Injection na busca | Buscar `' OR '1'='1` | "Nenhum livro encontrado" (não lista tudo) | |
| T55 | id inexistente | Na barra de endereço: `/livros/detalhes?id=99999` | Volta à lista com "Livro não encontrado. Ele pode ter sido excluído." | |
| T56 | id inválido | `/livros/detalhes?id=abc`, `/livros/editar?id=-1`, `/livros/excluir?id=` | Mesma mensagem de "não encontrado", sem página de erro | |
| T57 | Página inexistente 📸 | `/qualquercoisa` | Página "Página não encontrada" no visual do sistema (não a do servidor) | |
| T58 | Método não permitido | `curl -s -X POST http://localhost:8080/livros` | Página "Algo deu errado" do sistema (não "HTTP ERROR 405 … Powered by Jetty") | |
| T59 | Excluir só por POST | Abrir `/livros/excluir?id=1` na barra de endereço | Só mostra a confirmação; nada é apagado | |
| T60 | Sem id de sessão na URL | Passar o mouse sobre os links da lista | Nenhum link contém `;jsessionid=` | |
| T61 | Texto livre na mensagem da URL | `/livros?msg=<b>oi</b>` | Nenhuma mensagem aparece (só códigos conhecidos geram mensagem) | |

## 7. Interface responsiva

| ID | Teste | Passos | Resultado esperado | Obtido |
|---|---|---|---|---|
| T70 | Celular 📸 | F12 → modo dispositivo (ícone de celular) → 375 px de largura → `/livros` | Cada livro vira um cartão; sem barra de rolagem horizontal; busca e botões cabem na tela | |
| T71 | Formulário no celular | Mesmo modo → "+ Novo livro" | Campos ocupam a largura toda; Ano e Gênero ficam um embaixo do outro | |
