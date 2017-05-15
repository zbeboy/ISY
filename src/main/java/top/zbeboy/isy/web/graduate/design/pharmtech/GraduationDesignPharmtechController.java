package top.zbeboy.isy.web.graduate.design.pharmtech;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by zbeboy on 2017/5/15.
 */
@Slf4j
@Controller
public class GraduationDesignPharmtechController {

    /**
     * 填报指导教师
     *
     * @return 填报指导教师页面
     */
    @RequestMapping(value = "/web/menu/graduate/design/pharmtech", method = RequestMethod.GET)
    public String pharmtechData() {
        return "web/graduate/design/pharmtech/design_pharmtech::#page-wrapper";
    }
}
