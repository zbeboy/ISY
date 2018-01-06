package top.zbeboy.isy.test

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.util.ObjectUtils
import top.zbeboy.isy.Application
import top.zbeboy.isy.config.ISYProperties
import top.zbeboy.isy.domain.tables.daos.StaffDao
import top.zbeboy.isy.domain.tables.daos.StudentDao
import top.zbeboy.isy.domain.tables.daos.UsersDao
import top.zbeboy.isy.domain.tables.pojos.UsersKey
import top.zbeboy.isy.domain.tables.pojos.UsersUniqueInfo
import top.zbeboy.isy.service.common.DesService
import top.zbeboy.isy.service.internship.InternshipJournalService
import top.zbeboy.isy.service.platform.UsersKeyService
import top.zbeboy.isy.service.platform.UsersUniqueInfoService
import top.zbeboy.isy.service.util.RandomUtils
import top.zbeboy.isy.service.util.UUIDUtils
import top.zbeboy.isy.web.bean.internship.journal.InternshipJournalBean
import javax.annotation.Resource

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [(Application::class)])
open class TestService {

    @Resource
    open lateinit var internshipJournalService: InternshipJournalService

    @Resource
    open lateinit var desService: DesService

    @Resource
    open lateinit var usersDao: UsersDao

    @Resource
    open lateinit var studentDao: StudentDao

    @Resource
    open lateinit var staffDao: StaffDao

    @Resource
    open lateinit var usersKeyService: UsersKeyService

    @Resource
    open lateinit var usersUniqueInfoService: UsersUniqueInfoService

    @Resource
    open lateinit var iSYProperties: ISYProperties

    @Test
    fun testCountTeamJournalNumMethod() {
        val records = internshipJournalService.countTeamJournalNum("ee3ab1e0c5bc4124804293f1754ec9aa", 1)
        if (records.isNotEmpty) {
            val internshipJournalBeans = records.into(InternshipJournalBean::class.java)
            internshipJournalBeans.forEach { i -> print(i.studentRealName + " " + i.studentNumber + " " + i.journalNum) }
        }
    }

    @Test
    fun testDesService() {
        val key = UUIDUtils.getUUID()/*"kYP=[]ql_iN2lu<S7UYX"*/
        println("key : $key")
        val before = desService.encrypt("123456845646", key)
        println("before : $before")
        println("before length : ${before.length}")
        val after = desService.decrypt(before, key)
        println("after : $after")

        /**
         * 账号长度：32 * 2 = 64
         * 身份证号长度：32 * 2 = 64
         * 生日长度：24 * 2 = 48
         * 性别：12 * 2 = 24
         * 家庭地址：96 * 2 = 192
         * 宿舍号：12 * 2 = 24
         * 家长姓名：24 * 2 = 48
         * 家长联系电话：24 * 2 = 48
         * 生源地：56 * 2 = 112
         */
    }

    @Test
    fun generateUsersKey() {
        val users = usersDao.findAll()
        users.forEach { user ->
            val key = iSYProperties.getSecurity().desDefaultKey
            val usersKey = UsersKey()
            usersKey.username = desService.encrypt(user.username, key!!)
            usersKey.userKey = UUIDUtils.getUUID()
            usersKeyService.save(usersKey)
        }
    }

    @Test
    fun syncIdCard() {
        val students = studentDao.findAll()
        students.forEach { student ->
            if (!ObjectUtils.isEmpty(student.idCard)) {
                val key = iSYProperties.getSecurity().desDefaultKey
                val usersUniqueInfo = UsersUniqueInfo()
                usersUniqueInfo.username = desService.encrypt(student.username, key!!)
                usersUniqueInfo.idCard = desService.encrypt(student.idCard, key)
                usersUniqueInfoService.save(usersUniqueInfo)
            }
        }

        val staffs = staffDao.findAll()
        staffs.forEach { staff ->
            if (!ObjectUtils.isEmpty(staff.idCard)) {
                val key = iSYProperties.getSecurity().desDefaultKey
                val usersUniqueInfo = UsersUniqueInfo()
                usersUniqueInfo.username = desService.encrypt(staff.username, key!!)
                usersUniqueInfo.idCard = desService.encrypt(staff.idCard, key)
                usersUniqueInfoService.save(usersUniqueInfo)
            }
        }
    }

    @Test
    fun encryptUserInfo() {
        val students = studentDao.findAll()
        students.forEach { student ->
            val usersKey = usersKeyService.findByUsername(student.username)
            if (!ObjectUtils.isEmpty(student.birthday)) {
                student.birthday = desService.encrypt(student.birthday, usersKey.userKey)
            }

            if (!ObjectUtils.isEmpty(student.sex)) {
                student.sex = desService.encrypt(student.sex, usersKey.userKey)
            }

            if (!ObjectUtils.isEmpty(student.familyResidence)) {
                student.familyResidence = desService.encrypt(student.familyResidence, usersKey.userKey)
            }

            if (!ObjectUtils.isEmpty(student.dormitoryNumber)) {
                student.dormitoryNumber = desService.encrypt(student.dormitoryNumber, usersKey.userKey)
            }

            if (!ObjectUtils.isEmpty(student.parentName)) {
                student.parentName = desService.encrypt(student.parentName, usersKey.userKey)
            }

            if (!ObjectUtils.isEmpty(student.parentContactPhone)) {
                student.parentContactPhone = desService.encrypt(student.parentContactPhone, usersKey.userKey)
            }

            if (!ObjectUtils.isEmpty(student.placeOrigin)) {
                student.placeOrigin = desService.encrypt(student.placeOrigin, usersKey.userKey)
            }

            studentDao.update(student)
        }

        val staffs = staffDao.findAll()
        staffs.forEach { staff ->
            val usersKey = usersKeyService.findByUsername(staff.username)
            if (!ObjectUtils.isEmpty(staff.birthday)) {
                staff.birthday = desService.encrypt(staff.birthday, usersKey.userKey)
            }

            if (!ObjectUtils.isEmpty(staff.sex)) {
                staff.sex = desService.encrypt(staff.sex, usersKey.userKey)
            }

            if (!ObjectUtils.isEmpty(staff.familyResidence)) {
                staff.familyResidence = desService.encrypt(staff.familyResidence, usersKey.userKey)
            }

            staffDao.update(staff)
        }
    }

}