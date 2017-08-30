package top.zbeboy.isy.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zbeboy on 2017/1/19.
 */
@Slf4j
@RestController
public class MainRestController {

    @GetMapping("/me")
    public Principal user(Principal user) {
        return user;
    }

    @GetMapping("/rest/test")
    public List<String> test(Principal user){
        List<String> list = new ArrayList<>();
        list.add("test1");
        list.add("test2");
        System.out.println(user.toString());
        return list;
    }
}
