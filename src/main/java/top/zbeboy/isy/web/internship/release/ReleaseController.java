package top.zbeboy.isy.web.internship.release;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by lenovo on 2016-11-08.
 */
@Controller
public class ReleaseController {

    private final Logger log = LoggerFactory.getLogger(ReleaseController.class);

    /**
     * 实习发布数据
     *
     * @return 实习发布数据页面
     */
    @RequestMapping(value = "/web/menu/internship/release", method = RequestMethod.GET)
    public String releaseData() {
        return "/web/internship/release/internship_release::#page-wrapper";
    }

    /**
     * 实习发布添加页面
     *
     * @return 实习发布添加页面
     */
    @RequestMapping(value = "/web/internship/release/add", method = RequestMethod.GET)
    public String releaseAdd() {
        return "/web/internship/release/internship_release_add::#page-wrapper";
    }
}
