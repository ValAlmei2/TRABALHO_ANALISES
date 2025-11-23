package br.mackenzie.webapp.atividade;

import br.mackenzie.webapp.aluno.Aluno;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

@Entity
@Table(name = "atividades")
public class Atividade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // CT-03: Valida se o campo não está em branco.
    @NotBlank(message = "Este campo é obrigatório.")
    private String titulo;

    @NotBlank(message = "O tipo da atividade é obrigatório.")
    private String tipo;
    
    @NotNull(message = "A data de realização é obrigatória.")
    private LocalDate dataRealizacao;

    // CT-05: Valida se o número é positivo e não nulo.
    @NotNull(message = "As horas solicitadas são obrigatórias.")
    @Positive(message = "Por favor, insira um número de horas válido e maior que zero.")
    private Integer horasSolicitadas;
    
    private String nomeArquivo;
    private String status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    public Atividade() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public LocalDate getDataRealizacao() { return dataRealizacao; }
    public void setDataRealizacao(LocalDate dataRealizacao) { this.dataRealizacao = dataRealizacao; }
    public Integer getHorasSolicitadas() { return horasSolicitadas; }
    public void setHorasSolicitadas(Integer horasSolicitadas) { this.horasSolicitadas = horasSolicitadas; }
    public String getNomeArquivo() { return nomeArquivo; }
    public void setNomeArquivo(String nomeArquivo) { this.nomeArquivo = nomeArquivo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Aluno getAluno() { return aluno; }
    public void setAluno(Aluno aluno) { this.aluno = aluno; }
}