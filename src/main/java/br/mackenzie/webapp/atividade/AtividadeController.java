package br.mackenzie.webapp.atividade;

import br.mackenzie.webapp.aluno.Aluno;
import br.mackenzie.webapp.aluno.AlunoRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

@RestController
@RequestMapping("/api")
public class AtividadeController {

    @Autowired
    private AtividadeRepo atividadeRepo;

    @Autowired
    private AlunoRepo alunoRepo;

    // CT-02: Define a lista de formatos de arquivo permitidos.
    private final List<String> allowedExtensions = Arrays.asList("pdf", "jpg", "png");

    @PostMapping(value = "/aluno/{alunoId}", consumes = "multipart/form-data")
    public ResponseEntity<Atividade> submeterAtividade(
            @PathVariable Long alunoId,
            @Valid @RequestPart("atividade") Atividade atividade, // @Valid ativa as validações (CT-03 e CT-05)
            @RequestPart("arquivo") MultipartFile arquivo) {

        if (arquivo == null || arquivo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O arquivo de comprovante é obrigatório.");
        }

        // CT-02: Lógica para validar o formato do arquivo.
        String originalFilename = arquivo.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        if (!allowedExtensions.contains(fileExtension)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de arquivo inválido. Por favor, envie apenas arquivos PDF, JPG ou PNG.");
        }

        Aluno aluno = alunoRepo.findById(alunoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno com id " + alunoId + " não encontrado."));

        // CT-06: Lógica para prevenir submissão duplicada.
        if (atividadeRepo.existsByAlunoAndTituloIgnoreCase(aluno, atividade.getTitulo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta atividade já foi submetida anteriormente. Verifique sua lista de atividades.");
        }

        // CT-01: Caminho principal de sucesso.
        atividade.setAluno(aluno);
        atividade.setStatus("Aguardando Avaliação");
        atividade.setNomeArquivo(originalFilename);
        
        // Em uma aplicação real, aqui viria o código para salvar o arquivo em um disco ou nuvem.
        // Ex: fileStorageService.save(arquivo, generatedFilename);

        Atividade novaAtividade = atividadeRepo.save(atividade);

        return new ResponseEntity<>(novaAtividade, HttpStatus.CREATED);
    }

    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    // Helper que permite teste via header x-user-role.
    // Se o header não for enviado, não bloqueia (presume que Spring Security cuidará).
    private void requireRoleHeaderIfPresent(String roleHeader, String... allowedRoles) {
        if (roleHeader == null || roleHeader.isBlank()) return;
        for (String allowed : allowedRoles) {
            if (roleHeader.equalsIgnoreCase(allowed)) return;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado para role: " + roleHeader);
    }

    @GetMapping("/apo/secretaria")
    public ResponseEntity<List<Atividade>> listarAPOsParaSecretaria(
            @RequestHeader(value = "x-user-role", required = false) String roleHeader) {

        // permite teste rápido com header x-user-role: SECRETARIA
        requireRoleHeaderIfPresent(roleHeader, "SECRETARIA");

        List<Atividade> apos = new java.util.ArrayList<>();
        atividadeRepo.findAll().forEach(apos::add);
        return ResponseEntity.ok(apos);
    }

    @GetMapping("/apo/pendentes")
    public ResponseEntity<List<Atividade>> listarAPOsPendentes(
            @RequestHeader(value = "x-user-role", required = false) String roleHeader) {

        // permitir SECRETARIA, PROFESSOR, COORDENACAO
        requireRoleHeaderIfPresent(roleHeader, "SECRETARIA", "PROFESSOR", "COORDENACAO", "COORDENAÇÃO");

        List<Atividade> apos = atividadeRepo.findAll().stream()
                .filter(a -> a.getStatus() != null && a.getStatus().toLowerCase().contains("pend"))
                .toList();
        return ResponseEntity.ok(apos);
    }

    @GetMapping("/apo/coordenacao")
    public ResponseEntity<List<Atividade>> listarAPOsCoordenacao(
            @RequestHeader(value = "x-curso-id", required = false) String cursoIdHeader,
            @RequestHeader(value = "x-user-role", required = false) String roleHeader) {

        // permite teste via header
        requireRoleHeaderIfPresent(roleHeader, "COORDENACAO", "COORDENAÇÃO", "SECRETARIA");

        if (cursoIdHeader == null || cursoIdHeader.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Long cursoId;
        try {
            cursoId = Long.valueOf(cursoIdHeader);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }

        List<Atividade> apos = new java.util.ArrayList<>();
        atividadeRepo.findAll().forEach(apos::add);

        // Filtra por curso do aluno (assume que Aluno tem getCursoId ou similar)
        List<Atividade> filtrados = apos.stream()
                .filter(a -> a.getAluno() != null && a.getAluno().getCursoId() != null)
                .filter(a -> String.valueOf(a.getAluno().getCursoId()).equals(cursoIdHeader))
                .toList();

        return ResponseEntity.ok(filtrados);
    }

    @GetMapping("/apo/meus")
    public ResponseEntity<List<Atividade>> listarAPOsAluno(
            @RequestHeader(value = "x-user-id", required = false) String alunoIdHeader,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "x-user-role", required = false) String roleHeader) {

        // permite teste via header (ALUNO) ou via token
        requireRoleHeaderIfPresent(roleHeader, "ALUNO", "SECRETARIA");

        Long alunoId = null;
        if (alunoIdHeader != null && !alunoIdHeader.isBlank()) {
            try { alunoId = Long.valueOf(alunoIdHeader); } catch (NumberFormatException ignored) {}
        }

        if (alunoId == null && authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String ra = extractRAFromToken(authHeader);
                var aluno = alunoRepo.findByRa(ra).orElse(null);
                if (aluno != null) alunoId = extractAlunoId(aluno);
            } catch (Exception e) {
                // token inválido -> responder 401 em vez de 400
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ArrayList<>());
            }
        }

        // Se ainda não conseguiu identificar o aluno, responda 401 (autenticação necessária)
        if (alunoId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ArrayList<>());
        }

        // Recupera todas as atividades e filtra pelo aluno (compatível com repositório atual)
        List<Atividade> apos = new ArrayList<>();
        atividadeRepo.findAll().forEach(apos::add);

        List<Atividade> meus = new ArrayList<>();
        for (Atividade a : apos) {
            var al = a.getAluno();
            if (al == null) continue;
            Long id = extractAlunoId(al);
            if (id != null && id.equals(alunoId)) meus.add(a);
        }

        return ResponseEntity.ok(meus);
    }

    @PostMapping("/api/aluno/submit")
    public ResponseEntity<Atividade> submeterAtividadeSimples(
            @RequestBody Atividade atividade,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        if (authHeader == null || authHeader.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header obrigatório");
        }

        String ra = extractRAFromToken(authHeader);

        Aluno aluno = alunoRepo.findByRa(ra)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Aluno não encontrado para o RA: " + ra));

        atividade.setAluno(aluno);
        atividade.setStatus("PENDENTE");

        Atividade novaAtividade = atividadeRepo.save(atividade);
        return new ResponseEntity<>(novaAtividade, HttpStatus.CREATED);
    }

    private String extractRAFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization header inválido");
        }

        String token = authHeader.substring(7);
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) throw new IllegalArgumentException("Token JWT inválido");
            String payloadJson = new String(java.util.Base64.getUrlDecoder().decode(parts[1]));
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> payload = mapper.readValue(payloadJson, Map.class);

            Object sub = payload.get("sub");
            if (sub != null) return sub.toString();

            Object preferred = payload.get("preferred_username");
            if (preferred != null) return preferred.toString();

            throw new IllegalArgumentException("Campo 'sub' não encontrado no token");
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Erro ao decodificar token: " + e.getMessage());
        }
    }

    // Reflection helpers para tolerar diferentes modelos de Aluno/CURSO no workspace:
    private Long extractAlunoId(Object aluno) {
        if (aluno == null) return null;
        try {
            Method m = aluno.getClass().getMethod("getId");
            Object v = m.invoke(aluno);
            if (v instanceof Number) return ((Number) v).longValue();
            if (v != null) return Long.valueOf(v.toString());
        } catch (Exception ignored) {}
        try {
            Field f = aluno.getClass().getDeclaredField("id");
            f.setAccessible(true);
            Object v = f.get(aluno);
            if (v instanceof Number) return ((Number) v).longValue();
            if (v != null) return Long.valueOf(v.toString());
        } catch (Exception ignored) {}
        return null;
    }

    private Long extractCursoIdFromAluno(Object aluno) {
        if (aluno == null) return null;
        try {
            Method m = aluno.getClass().getMethod("getCursoId");
            Object v = m.invoke(aluno);
            if (v instanceof Number) return ((Number) v).longValue();
            if (v != null) return Long.valueOf(v.toString());
        } catch (Exception ignored) {}
        try {
            Method m2 = aluno.getClass().getMethod("getCurso");
            Object curso = m2.invoke(aluno);
            if (curso == null) return null;
            try {
                Method m3 = curso.getClass().getMethod("getId");
                Object vid = m3.invoke(curso);
                if (vid instanceof Number) return ((Number) vid).longValue();
                if (vid != null) return Long.valueOf(vid.toString());
            } catch (Exception ignored) {}
        } catch (Exception ignored) {}
        return null;
    }
}