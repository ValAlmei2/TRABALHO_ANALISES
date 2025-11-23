package br.mackenzie.webapp.Secretaria;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/secretaria")
public class SecretariaController {

    @Autowired
    private SecretariaRepo secretariaRepo;

    @GetMapping
    public Iterable<Secretaria> getSecretaria() {
        return secretariaRepo.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Secretaria> getSecretaria(@PathVariable long id) {
        return secretariaRepo.findById(id);
    }

    @PostMapping
    public Secretaria createSecretaria(@RequestBody Secretaria s) {
        return secretariaRepo.save(s);
    }

    @PutMapping("/{id}")
    public Secretaria updateSecretaria(@RequestBody Secretaria secretariaRequest, @PathVariable long id) {
        return secretariaRepo.findById(id).map(secretaria -> {
            secretaria.setLocal(secretariaRequest.getLocal());
            secretaria.setCriterios(secretariaRequest.getCriterios());
            secretaria.setArquivo(secretariaRequest.getArquivo());
            return secretariaRepo.save(secretaria);
        }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "Secretaria não encontrada com o id " + id));
    }

    @DeleteMapping("/{id}")
    public void deleteSecretaria(@PathVariable long id) {
        secretariaRepo.deleteById(id);
    }
}