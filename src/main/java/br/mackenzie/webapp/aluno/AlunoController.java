package br.mackenzie.webapp.aluno;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;

@RestController
public class AlunoController {

    @Autowired
    private AlunoRepo repo;

    public AlunoController(){}

    @GetMapping("/api/aluno")
    Iterable<Aluno> getAlunos() {
        return repo.findAll();
    }

    @GetMapping("/api/aluno/{id}")
    Optional<Aluno> getAluno(@PathVariable long id) {
        return repo.findById(id);
    }

    @PostMapping("/api/aluno")
    Aluno createAluno(@RequestBody Aluno a) {
        return repo.save(a);
    }

    @PutMapping("/api/aluno/{alunoId}")
    Optional<Aluno> updateAluno(@RequestBody Aluno alunoRequest, @PathVariable long alunoId) {
        Optional<Aluno> opt = repo.findById(alunoId);
        if (opt.isPresent()) {
            if (alunoRequest.getId() == alunoId) {
                repo.save(alunoRequest);
                return opt;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Erro ao alterar dados do Aluno com id " + alunoId);
    }

    @DeleteMapping(value = "/api/aluno/{id}")
    void deleteAluno(@PathVariable long id) {
        repo.deleteById(id);
    }
}
