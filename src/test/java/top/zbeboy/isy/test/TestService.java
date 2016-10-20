package top.zbeboy.isy.test;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import top.zbeboy.isy.Application;
import top.zbeboy.isy.service.ApplicationService;
import top.zbeboy.isy.web.bean.tree.TreeBean;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by lenovo on 2016-10-18.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebIntegrationTest(randomPort = true)
public class TestService {

    @Resource
    ApplicationService applicationService;

    @Test
    public void testApplicationToJsonMethod(){
        List<TreeBean> treeBeens = applicationService.getApplicationJson(0);
        System.out.println(treeBeens);
        String json= JSON.toJSONString(treeBeens, true);
        System.out.println(json);
    }
}
