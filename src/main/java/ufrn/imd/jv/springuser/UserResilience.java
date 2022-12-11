package ufrn.imd.jv.springuser;

import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserResilience {
    private final PasswordServiceInterface passwordService;

    @Autowired
    public UserResilience(PasswordServiceInterface passwordService) {
        this.passwordService = passwordService;
    }

    @CircuitBreaker(name = "validatePassword_cb")
    @Bulkhead(name = "validatePassword_bh")
    @Retry(name = "validatePassword_rt")
    public String validatePassword(String password) {
        try {
            return passwordService.validatePassword(password).getBody().get(0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
