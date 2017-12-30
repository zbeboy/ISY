package top.zbeboy.isy.test

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import top.zbeboy.isy.Application
import top.zbeboy.isy.service.internship.InternshipJournalService
import top.zbeboy.isy.web.bean.internship.journal.InternshipJournalBean
import javax.annotation.Resource

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [(Application::class)])
open class TestService {

    @Resource
    open lateinit var internshipJournalService: InternshipJournalService

    @Test
    fun testCountTeamJournalNumMethod() {
        val records = internshipJournalService.countTeamJournalNum("ee3ab1e0c5bc4124804293f1754ec9aa", 1)
        if (records.isNotEmpty) {
            val internshipJournalBeans = records.into(InternshipJournalBean::class.java)
            internshipJournalBeans.forEach { i -> print(i.studentRealName + " " + i.studentNumber + " " + i.journalNum) }
        }
    }
}