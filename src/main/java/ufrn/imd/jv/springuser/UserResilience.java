package ufrn.imd.jv.springuser;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
public class UserResilience {
    private final PasswordServiceInterface passwordService;

    @Autowired
    public UserResilience(PasswordServiceInterface passwordService) {
        this.passwordService = passwordService;
    }

    @CircuitBreaker(name = "validatePassword_cb")
    @Bulkhead(name = "validatePassword_bh", type = Bulkhead.Type.THREADPOOL)
    @Retry(name = "validatePassword_rt")
    public String validatePassword(String password) {
        ResponseEntity<String> response = passwordService.validatePassword(password);
        return response.getBody();
    }
}
