package br.mackenzie.webapp.Coordenacao;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
class CoordenacaoController {

	@Autowired
	private CoordenacaoRepo CoordenacaoRepo;

	public CoordenacaoController() {

	}

	@GetMapping("/api/coordenacao")
	Iterable<Coordenacao> getcoordena(@RequestParam Optional<Long> faculdadeId) {

		return CoordenacaoRepo.findAll();

	}

	@GetMapping("/api/coordenacao/{id}")
	Optional<Coordenacao> getCoordenacao(@PathVariable long id) {
		return CoordenacaoRepo.findById(id);
	}

	@PostMapping("/api/coordenacao")
	Coordenacao createCoordenacao(@RequestBody Coordenacao c) {
		Coordenacao createdCoord = CoordenacaoRepo.save(c);
		return createdCoord;
	}

	@PutMapping("/api/coordenacao/{coordenacaoId}")
	Optional<Coordenacao> updateCoordenacao(@RequestBody Coordenacao coordenacaoRequest, @PathVariable long coordenacaoId) {
		Optional<Coordenacao> opt = CoordenacaoRepo.findById(coordenacaoId);
		if (opt.isPresent()) {
			if (coordenacaoRequest.getId() == coordenacaoId) {
				CoordenacaoRepo.save(coordenacaoRequest);
				return opt;
			}
		}
		throw new ResponseStatusException(HttpStatus.NOT_FOUND,
				"Erro ao alterar dados da coordenacao com id " + coordenacaoId);
	}

	@DeleteMapping(value = "/api/coordenacao/{id}")
	void deleteCoordenacao(@PathVariable long id) {
		CoordenacaoRepo.deleteById(id);
	}
}