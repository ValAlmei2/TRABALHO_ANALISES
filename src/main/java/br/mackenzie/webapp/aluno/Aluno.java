package br.mackenzie.webapp.aluno;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "aluno")
public class Aluno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ra;
    private String nome;
    private String curso;
    
    // CORREÇÃO: Adicionado o campo cursoId
    private Long cursoId; 

    public Aluno() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRa() { return ra; }
    public void setRa(String ra) { this.ra = ra; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCurso() { return curso; }
    public void setCurso(String curso) { this.curso = curso; }
    
    // CORREÇÃO: Adicionado o getter getCursoId()
    public Long getCursoId() { return cursoId; }
    public void setCursoId(Long cursoId) { this.cursoId = cursoId; }
}