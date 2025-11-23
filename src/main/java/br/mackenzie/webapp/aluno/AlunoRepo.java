package br.mackenzie.webapp.aluno;

import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

public interface AlunoRepo extends CrudRepository<Aluno, Long> {
    Optional<Aluno> findByRa(String ra);
}