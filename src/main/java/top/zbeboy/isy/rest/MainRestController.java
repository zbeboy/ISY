package top.zbeboy.isy.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * Created by zbeboy on 2017/1/19.
 */
@Slf4j
@RestController
public class MainRestController {

    @GetMapping("/user")
    public Principal user(Principal user) {
        return user;
    }
}
