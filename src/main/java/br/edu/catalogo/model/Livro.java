package br.edu.catalogo.model;

/**
 * Entidade principal do sistema: representa um livro do catálogo.
 * Os atributos são privados (encapsulamento) e acessados
 * por getters e setters.
 */
public class Livro {

    private int id;
    private String titulo;
    private String autor;
    private int anoPublicacao;
    private String genero;
    private String sinopse;

    /** Construtor vazio, usado para montar o objeto a partir do formulário. */
    public Livro() {
    }

    /** Construtor completo (sem id), usado ao cadastrar um novo livro. */
    public Livro(String titulo, String autor, int anoPublicacao,
                 String genero, String sinopse) {
        this.titulo = titulo;
        this.autor = autor;
        this.anoPublicacao = anoPublicacao;
        this.genero = genero;
        this.sinopse = sinopse;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public int getAnoPublicacao() { return anoPublicacao; }
    public void setAnoPublicacao(int anoPublicacao) { this.anoPublicacao = anoPublicacao; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getSinopse() { return sinopse; }
    public void setSinopse(String sinopse) { this.sinopse = sinopse; }
}
