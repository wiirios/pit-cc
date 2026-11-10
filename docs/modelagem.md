# Modelagem do Sistema — Catálogo de Livros

> Diagramas fiéis ao código em `src/main/java/br/edu/catalogo`. Estão em **Mermaid**, que o
> GitHub, o VS Code (com extensão) e o site https://mermaid.live desenham automaticamente.
> Para o relatório em PDF, gere uma imagem de cada diagrama no mermaid.live e cole no documento.

---

## 1. Diagrama de Casos de Uso

O Mermaid não tem um tipo próprio para casos de uso da UML. Por isso, o diagrama abaixo usa
a notação mais próxima: o ator à esquerda, os casos de uso em formato oval e a fronteira do
sistema como um retângulo. Se o tutor exigir a notação UML exata, redesenhe no draw.io ou
no Astah com os mesmos elementos.

```mermaid
flowchart LR
    usuario(["👤 Usuário"])

    subgraph sistema["Sistema: Catálogo de Livros"]
        uc1(["UC01 Listar livros"])
        uc2(["UC02 Buscar por título ou autor"])
        uc3(["UC03 Cadastrar livro"])
        uc4(["UC04 Ver detalhes do livro"])
        uc5(["UC05 Editar livro"])
        uc6(["UC06 Excluir livro"])
        uc7(["Validar dados do livro"])
    end

    usuario --- uc1
    usuario --- uc2
    usuario --- uc3
    usuario --- uc4
    usuario --- uc5
    usuario --- uc6

    uc3 -. "«include»" .-> uc7
    uc5 -. "«include»" .-> uc7
```

| Caso de uso | Descrição | Classe responsável |
|---|---|---|
| UC01 Listar livros | Mostra todos os livros em ordem alfabética de título | `ListarLivrosServlet` |
| UC02 Buscar | Filtra por parte do título **ou** do autor (SQL `LIKE`) | `ListarLivrosServlet` (parâmetro `busca`) |
| UC03 Cadastrar | Formulário de novo livro, validado no servidor | `CadastrarLivroServlet` |
| UC04 Ver detalhes | Mostra todos os campos de um livro | `DetalhesLivroServlet` |
| UC05 Editar | Formulário preenchido, validado no servidor | `EditarLivroServlet` |
| UC06 Excluir | Pede confirmação (GET) e exclui (POST) | `ExcluirLivroServlet` |
| Validar dados | Obrigatórios, ano numérico, tamanhos máximos | `LivroService.validar()` |

Há um único ator, o **Usuário**: o sistema não tem login, então qualquer pessoa que acessa a
aplicação pode usar todas as funções.

---

## 2. Diagrama de Classes

```mermaid
classDiagram
    direction TB

    class Livro {
        -int id
        -String titulo
        -String autor
        -int anoPublicacao
        -String genero
        -String sinopse
        +Livro()
        +Livro(titulo, autor, anoPublicacao, genero, sinopse)
        +getId() int
        +setId(int)
        +getTitulo() String
        +setTitulo(String)
        +getAutor() String
        +setAutor(String)
        +getAnoPublicacao() int
        +setAnoPublicacao(int)
        +getGenero() String
        +setGenero(String)
        +getSinopse() String
        +setSinopse(String)
    }

    class LivroDAO {
        +inserir(Livro) void
        +listarTodos() List~Livro~
        +buscarPorTituloOuAutor(String) List~Livro~
        +buscarPorId(int) Livro
        +atualizar(Livro) boolean
        +excluir(int) boolean
        -escaparLike(String) String
        -montarLivro(ResultSet) Livro
        -fechar(Connection, PreparedStatement, ResultSet) void
    }

    class LivroService {
        +int MAX_TITULO$
        +int MAX_AUTOR$
        +int MAX_GENERO$
        +int MAX_SINOPSE$
        +int MAX_BUSCA$
        -LivroDAO dao
        +listarTodos() List~Livro~
        +buscar(String) List~Livro~
        +buscarPorId(int) Livro
        +cadastrar(Livro, String) void
        +atualizar(Livro, String) boolean
        +excluir(int) boolean
        -validar(Livro, String) void
        -limpar(String) String
    }

    class DAOException {
        +DAOException(String, Throwable)
    }
    class ValidacaoException {
        -List~String~ erros
        +ValidacaoException(List~String~)
        +getErros() List~String~
    }

    class FabricaDeConexoes {
        <<utilitária>>
        +getConexao()$ Connection
    }
    class Parametros {
        <<utilitária>>
        +lerId(HttpServletRequest)$ Integer
    }
    class InicializadorBanco {
        <<WebListener>>
        +contextInitialized(ServletContextEvent) void
    }

    class HttpServlet {
        <<Jakarta>>
    }
    class ListarLivrosServlet {
        <<WebServlet /livros>>
        #doGet(req, resp)
    }
    class CadastrarLivroServlet {
        <<WebServlet /livros/novo>>
        #doGet(req, resp)
        #doPost(req, resp)
    }
    class DetalhesLivroServlet {
        <<WebServlet /livros/detalhes>>
        #doGet(req, resp)
    }
    class EditarLivroServlet {
        <<WebServlet /livros/editar>>
        #doGet(req, resp)
        #doPost(req, resp)
    }
    class ExcluirLivroServlet {
        <<WebServlet /livros/excluir>>
        #doGet(req, resp)
        #doPost(req, resp)
    }

    HttpServlet <|-- ListarLivrosServlet
    HttpServlet <|-- CadastrarLivroServlet
    HttpServlet <|-- DetalhesLivroServlet
    HttpServlet <|-- EditarLivroServlet
    HttpServlet <|-- ExcluirLivroServlet

    ListarLivrosServlet --> LivroService
    CadastrarLivroServlet --> LivroService
    DetalhesLivroServlet --> LivroService
    EditarLivroServlet --> LivroService
    ExcluirLivroServlet --> LivroService
    DetalhesLivroServlet ..> Parametros
    EditarLivroServlet ..> Parametros
    ExcluirLivroServlet ..> Parametros

    LivroService --> LivroDAO
    LivroService ..> ValidacaoException : lança
    LivroDAO ..> DAOException : lança
    LivroDAO ..> FabricaDeConexoes : usa
    InicializadorBanco ..> FabricaDeConexoes : usa
    LivroDAO ..> Livro
    LivroService ..> Livro
```

**Como ler:** `-` é privado, `+` é público, `#` é protegido e `$` indica membro estático.
A seta cheia (`-->`) é associação ("tem um"), a tracejada (`..>`) é dependência ("usa") e o
triângulo (`<|--`) é herança. Os servlets herdam de `HttpServlet` e sobrescrevem `doGet`/`doPost`
(polimorfismo por sobrescrita).

**Camadas:**

```mermaid
flowchart LR
    nav["Navegador"] -->|"HTTP GET/POST"| sv["Servlets<br/>(controle)"]
    sv -->|"chama"| se["LivroService<br/>(regras e validação)"]
    se -->|"chama"| da["LivroDAO<br/>(JDBC + PreparedStatement)"]
    da -->|"SQL"| db[("H2<br/>tabela livro")]
    sv -->|"forward"| jsp["JSPs em WEB-INF/jsp<br/>(visão, EL + JSTL)"]
    jsp -->|"HTML"| nav
```

---

## 3. Diagrama Entidade-Relacionamento (DER)

O sistema tem uma única entidade, então não há relacionamentos entre tabelas.

```mermaid
erDiagram
    LIVRO {
        INT id PK "AUTO_INCREMENT"
        VARCHAR_255 titulo "NOT NULL"
        VARCHAR_255 autor "NOT NULL"
        INT ano_publicacao "NOT NULL"
        VARCHAR_100 genero "opcional"
        VARCHAR_2000 sinopse "opcional"
    }
```

| Coluna | Tipo | Restrição | Atributo Java | Validação no servidor (`LivroService`) |
|---|---|---|---|---|
| `id` | `INT` | `PRIMARY KEY AUTO_INCREMENT` | `id` | gerado pelo banco |
| `titulo` | `VARCHAR(255)` | `NOT NULL` | `titulo` | obrigatório, até 255 |
| `autor` | `VARCHAR(255)` | `NOT NULL` | `autor` | obrigatório, até 255 |
| `ano_publicacao` | `INT` | `NOT NULL` | `anoPublicacao` | obrigatório, numérico, de 1 até o ano atual + 5 |
| `genero` | `VARCHAR(100)` | — | `genero` | opcional, até 100 |
| `sinopse` | `VARCHAR(2000)` | — | `sinopse` | opcional, até 2000 |

Diferenças em relação ao exemplo do enunciado (tabela `item_midia`, folha 22):

- não há `tipo_midia`, porque o catálogo é só de livros;
- `autor_diretor` virou `autor` e `ano_lancamento` virou `ano_publicacao`;
- `sinopse` é `VARCHAR(2000)` em vez de `TEXT`, para o banco também impor o tamanho máximo;
- `autor` e `ano_publicacao` são `NOT NULL`, coerentes com a validação.

---

## 4. Script SQL de criação da tabela

Arquivo `src/main/resources/sql/script.sql`, executado automaticamente pela classe
`InicializadorBanco` sempre que a aplicação sobe:

```sql
-- Tabela principal do catalogo: guarda os livros
-- (executado pelo InicializadorBanco sempre que a aplicacao sobe;
--  IF NOT EXISTS evita erro quando a tabela ja existe)
CREATE TABLE IF NOT EXISTS livro (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    titulo         VARCHAR(255)  NOT NULL,
    autor          VARCHAR(255)  NOT NULL,
    ano_publicacao INT           NOT NULL,
    genero         VARCHAR(100),
    sinopse        VARCHAR(2000)
);
```

Banco: **H2 2.2.224** em modo arquivo (`jdbc:h2:file:./dados/catalogo`). O arquivo
`dados/catalogo.mv.db` é criado na pasta onde o `mvn jetty:run` é executado.
