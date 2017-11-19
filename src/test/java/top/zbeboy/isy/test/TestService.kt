package top.zbeboy.isy.test

import com.alibaba.fastjson.JSON
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import top.zbeboy.isy.Application
import top.zbeboy.isy.elastic.repository.StudentElasticRepository
import top.zbeboy.isy.service.system.ApplicationService
import javax.annotation.Resource

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = arrayOf(Application::class))
open class TestService {

    @Resource
    open lateinit var applicationService: ApplicationService

    @Resource
    open lateinit var studentElasticRepository: StudentElasticRepository

    @Test
    fun testApplicationToJsonMethod() {
        val treeBeens = applicationService.getApplicationJson("0")
        println(treeBeens)
        val json = JSON.toJSONString(treeBeens, true)
        println(json)
    }

    @Test
    fun deleteStudentByUsername() {
        val studentElastic = studentElasticRepository.findByUsername("1006367538@qq.com")
        println(studentElastic)

        val studentElastics = studentElasticRepository.findAll()
        studentElastics?.forEach { student -> println(student) }
//        studentElasticRepository?.deleteByUsername("")
    }
}