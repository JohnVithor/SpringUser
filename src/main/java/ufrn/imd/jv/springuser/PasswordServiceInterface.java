package ufrn.imd.jv.springuser;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;

@FeignClient("SPRINGFUNCTION")
public interface PasswordServiceInterface {
    @RequestMapping(method = RequestMethod.POST, value = "/validatePassword")
    ResponseEntity<ArrayList<String>> validatePassword(String password);
}


