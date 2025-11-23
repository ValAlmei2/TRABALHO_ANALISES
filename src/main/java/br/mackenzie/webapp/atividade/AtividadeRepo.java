package br.mackenzie.webapp.atividade;

import br.mackenzie.webapp.aluno.Aluno;
import org.springframework.data.jpa.repository.JpaRepository; // ALTERADO: JpaRepository
import org.springframework.stereotype.Repository;
import java.util.List; // Import necessário para o novo método

@Repository
// ALTERADO: Estende JpaRepository
public interface AtividadeRepo extends JpaRepository<Atividade, Long> {

    /**
     * CT-06: Verifica se já existe uma atividade com o mesmo título para um determinado aluno.
     * O 'IgnoreCase' torna a busca insensível a maiúsculas/minúsculas.
     */
    boolean existsByAlunoAndTituloIgnoreCase(Aluno aluno, String titulo);
    
    // CORREÇÃO: Adicionado para resolver o erro findByAluno_Id
    List<Atividade> findByAluno_Id(Long alunoId); 
}