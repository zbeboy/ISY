package top.zbeboy.isy.test;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.zbeboy.isy.Application;
import top.zbeboy.isy.elastic.pojo.StudentElastic;
import top.zbeboy.isy.elastic.repository.StudentElasticRepository;
import top.zbeboy.isy.service.system.ApplicationService;
import top.zbeboy.isy.web.bean.tree.TreeBean;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by lenovo on 2016-10-18.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = Application.class)
public class TestService {

    @Resource
    private ApplicationService applicationService;

    @Resource
    private StudentElasticRepository studentElasticRepository;

    @Test
    public void testApplicationToJsonMethod() {
        List<TreeBean> treeBeens = applicationService.getApplicationJson("0");
        System.out.println(treeBeens);
        String json = JSON.toJSONString(treeBeens, true);
        System.out.println(json);
    }

    @Test
    public void deleteStudentByUsername(){
        StudentElastic studentElastic = studentElasticRepository.findByUsername("1006367538@qq.com");
//        Iterable<StudentElastic> studentElastics = studentElasticRepository.findAll();
//        studentElastics.forEach(System.out::println);
//        studentElasticRepository.deleteByUsername("");
        System.out.println(studentElastic);
    }
}
