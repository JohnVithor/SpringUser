package ufrn.imd.jv.springuser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "issues")
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UserEntity> createUser(@RequestBody UserEntity userEntity) {
        return ResponseEntity.ok(service.save(userEntity));
    }

    @GetMapping
    public ResponseEntity<Page<UserEntity>> getPage(
            @RequestParam(name = "pg", required = false) Optional<Integer> page,
            @RequestParam(name = "lim", required = false) Optional<Integer> limit) {
        return service.getPage(page.orElse(0), limit.orElse(10));
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<UserEntity> getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping(path = "auth")
    public ResponseEntity<UserEntity> auth(@RequestBody LoginDTO loginDTO) {
        return service.auth(loginDTO);
    }
}
