package top.zbeboy.isy.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

/**
 * Created by zbeboy on 2017/1/19.
 */
@RestController
public class MainRestController {

    private final Logger log = LoggerFactory.getLogger(MainRestController.class);

    @GetMapping("/rest/user")
    public Principal user(Principal user) {
        return user;
    }
}
