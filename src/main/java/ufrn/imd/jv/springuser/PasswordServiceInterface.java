package ufrn.imd.jv.springuser;

import feign.Body;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@FeignClient("SPRINGFUNCTION")
public interface PasswordServiceInterface {
    @RequestMapping(method = RequestMethod.POST, value = "/validatePassword")
    ResponseEntity<String> validatePassword(String password);
}
