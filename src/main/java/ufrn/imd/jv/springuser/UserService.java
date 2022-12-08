package ufrn.imd.jv.springuser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final UserResilience resilience;
    private final UserRepository repository;
    @Value("${security.check-password-strength}")
    private Boolean checkPasswordStrength;

    @Autowired
    public UserService(UserResilience resilience, UserRepository repository) {
        this.resilience = resilience;
        this.repository = repository;
    }

    public UserEntity save(UserEntity userEntity) {
        if (userEntity.getUsername() == null) {
            throw new RuntimeException("Nome do usuario não informado");
        }
        if (userEntity.getUsername().trim().equals("")) {
            throw new RuntimeException("Nome do usuario informado é inválido");
        }
        Optional<UserEntity> optValue = repository.findByUsername(userEntity.getUsername());
        if (optValue.isPresent()) {
            throw new RuntimeException("Nome do usuario já está em uso");
        }
        if (userEntity.getPassword() == null) {
            throw new RuntimeException("Senha do usuario não informada");
        }
        if (checkPasswordStrength) {
            String response = resilience.validatePassword(userEntity.getPassword());
            if (!response.equals("OK")) {
                throw new RuntimeException(response);
            }
        }
        userEntity.setPassword(encoder.encode(userEntity.getPassword()));
        return repository.save(userEntity);
    }

    public ResponseEntity<Page<UserEntity>> getPage(int page, int limit) {
        return ResponseEntity.ok(repository.findAll(PageRequest.of(page, limit)));
    }

    public ResponseEntity<UserEntity> getById(Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).build());
    }

    public ResponseEntity<UserEntity> auth(LoginDTO loginDTO) {
        Optional<UserEntity> optionalUser = repository.findByUsername(loginDTO.getUsername());
        if (optionalUser.isPresent()) {
            if (encoder.matches(loginDTO.getPassword(), optionalUser.get().getPassword())) {
                return ResponseEntity.ok(optionalUser.get());
            }
        }
        return ResponseEntity.status(401).build();
    }
}
