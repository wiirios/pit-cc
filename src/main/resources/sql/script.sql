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
