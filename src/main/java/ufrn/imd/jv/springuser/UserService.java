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
    private final UserRepository repository;
    @Value("${security.check-password-strength}")
    private Boolean checkPasswordStrength;

    @Autowired
    public UserService(UserRepository repository) {
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
            if (userEntity.getPassword().length() < 8) {
                throw new RuntimeException("Senha do usuario possui menos de 8 caracteres");
            }
            boolean up = false;
            boolean low = false;
            boolean digit = false;
            boolean special = false;
            for (char c : userEntity.getPassword().toCharArray()) {
                if (Character.isUpperCase(c))
                    up = true;
                else if (Character.isLowerCase(c))
                    low = true;
                else if (Character.isDigit(c))
                    digit = true;
                else
                    special = true;
            }
            if (!up) {
                throw new RuntimeException("Senha do usuario deve possuir um caractere maiusculo");
            }
            if (!low) {
                throw new RuntimeException("Senha do usuario deve possuir um caractere minusculo");
            }
            if (!digit) {
                throw new RuntimeException("Senha do usuario deve possuir um digito");
            }
            if (!special) {
                throw new RuntimeException("Senha do usuario deve possuir um caractere especial");
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
