package top.zbeboy.isy.service.data

import org.jooq.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.domain.Tables.*
import top.zbeboy.isy.domain.tables.daos.StudentDao
import top.zbeboy.isy.domain.tables.pojos.Student
import top.zbeboy.isy.domain.tables.records.StudentRecord
import top.zbeboy.isy.elastic.config.ElasticBook
import top.zbeboy.isy.elastic.pojo.StudentElastic
import top.zbeboy.isy.elastic.repository.StudentElasticRepository
import top.zbeboy.isy.service.common.MethodServiceCommon
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.util.SQLQueryUtils
import top.zbeboy.isy.web.bean.data.organize.OrganizeBean
import top.zbeboy.isy.web.bean.data.student.StudentBean
import top.zbeboy.isy.web.util.DataTablesUtils
import java.util.*
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-12-11 .
 **/
@Service("studentService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
open class StudentServiceImpl @Autowired constructor(dslContext: DSLContext) : StudentService {

    private val create: DSLContext = dslContext

    @Resource
    open lateinit var studentDao: StudentDao

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var roleService: RoleService

    @Resource
    open lateinit var studentElasticRepository: StudentElasticRepository

    @Resource
    open lateinit var organizeService: OrganizeService

    @Resource
    open lateinit var politicalLandscapeService: PoliticalLandscapeService

    @Resource
    open lateinit var nationService: NationService

    @Resource
    open lateinit var methodServiceCommon: MethodServiceCommon


    override fun findById(id: Int): Student {
        return studentDao.findById(id)
    }

    override fun findByIdRelation(id: Int): Optional<Record> {
        return create.select()
                .from(STUDENT)
                .join(USERS)
                .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                .join(ORGANIZE)
                .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                .join(SCIENCE)
                .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                .join(DEPARTMENT)
                .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .join(COLLEGE)
                .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                .join(SCHOOL)
                .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                .where(STUDENT.STUDENT_ID.eq(id))
                .fetchOptional()
    }

    override fun findByIdRelationForUsers(id: Int): Optional<Record> {
        return create.select()
                .from(STUDENT)
                .join(USERS)
                .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                .where(STUDENT.STUDENT_ID.eq(id))
                .fetchOptional()
    }

    override fun findByStudentNumber(studentNumber: String): Student? {
        return studentDao.fetchOneByStudentNumber(studentNumber)
    }

    override fun findInOrganizeIdsAndEnabledAndVerifyMailboxExistsAuthoritiesRelation(organizeIds: List<Int>, b: Byte?, verifyMailbox: Byte?): Result<Record> {
        val authoritiesRecordSelect = create.selectFrom(AUTHORITIES)
                .where(AUTHORITIES.USERNAME.eq(USERS.USERNAME))
        return create.select()
                .from(STUDENT)
                .join(USERS)
                .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                .where(STUDENT.ORGANIZE_ID.`in`(organizeIds).and(USERS.ENABLED.eq(b)).and(USERS.VERIFY_MAILBOX.eq(verifyMailbox)).andExists(authoritiesRecordSelect))
                .fetch()
    }

    override fun findByUsernameAndDepartmentId(username: String, departmentId: Int): Optional<Record> {
        return create.select()
                .from(STUDENT)
                .join(USERS)
                .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                .join(ORGANIZE)
                .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                .join(SCIENCE)
                .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                .join(DEPARTMENT)
                .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .where(STUDENT.USERNAME.eq(username).and(DEPARTMENT.DEPARTMENT_ID.eq(departmentId)))
                .fetchOptional()
    }

    override fun findByStudentNumberAndDepartmentId(studentNumber: String, departmentId: Int): Optional<Record> {
        return create.select()
                .from(STUDENT)
                .join(USERS)
                .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                .join(ORGANIZE)
                .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                .join(SCIENCE)
                .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                .join(DEPARTMENT)
                .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .where(STUDENT.STUDENT_NUMBER.eq(studentNumber).and(DEPARTMENT.DEPARTMENT_ID.eq(departmentId)))
                .fetchOptional()
    }

    override fun findByStudentNumberNeUsername(username: String, studentNumber: String): Result<StudentRecord> {
        return create.selectFrom(STUDENT)
                .where(STUDENT.STUDENT_NUMBER.eq(studentNumber).and(STUDENT.USERNAME.ne(username))).fetch()
    }

    override fun findByIdCardNeUsername(username: String, idCard: String): Result<StudentRecord> {
        return create.selectFrom(STUDENT)
                .where(STUDENT.ID_CARD.eq(idCard).and(STUDENT.USERNAME.ne(username))).fetch()
    }

    override fun findByIdCard(idCard: String): List<Student> {
        return studentDao.fetchByIdCard(idCard)
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    override fun save(studentElastic: StudentElastic) {
        val studentRecord = create.insertInto(STUDENT)
                .set(STUDENT.STUDENT_NUMBER, studentElastic.studentNumber)
                .set(STUDENT.BIRTHDAY, studentElastic.birthday)
                .set(STUDENT.SEX, studentElastic.sex)
                .set(STUDENT.ID_CARD, studentElastic.idCard)
                .set(STUDENT.FAMILY_RESIDENCE, studentElastic.familyResidence)
                .set(STUDENT.POLITICAL_LANDSCAPE_ID, studentElastic.politicalLandscapeId)
                .set(STUDENT.NATION_ID, studentElastic.nationId)
                .set(STUDENT.DORMITORY_NUMBER, studentElastic.dormitoryNumber)
                .set(STUDENT.PARENT_NAME, studentElastic.parentName)
                .set(STUDENT.PARENT_CONTACT_PHONE, studentElastic.parentContactPhone)
                .set(STUDENT.PLACE_ORIGIN, studentElastic.placeOrigin)
                .set(STUDENT.ORGANIZE_ID, studentElastic.organizeId)
                .set(STUDENT.USERNAME, studentElastic.username)
                .returning(STUDENT.STUDENT_ID)
                .fetchOne()
        studentElastic.authorities = ElasticBook.NO_AUTHORITIES
        studentElastic.setStudentId(studentRecord.getStudentId())
        studentElasticRepository.save(studentElastic)
    }

    override fun update(student: Student) {
        studentDao.update(student)
        val studentElastic = studentElasticRepository.findOne(student.studentId!!.toString() + "")
        studentElastic.studentNumber = student.studentNumber
        studentElastic.birthday = student.birthday
        studentElastic.sex = student.sex
        studentElastic.idCard = student.idCard
        studentElastic.familyResidence = student.familyResidence
        studentElastic.dormitoryNumber = student.dormitoryNumber
        studentElastic.parentName = student.parentName
        studentElastic.parentContactPhone = student.parentContactPhone
        studentElastic.placeOrigin = student.placeOrigin
        if (student.politicalLandscapeId != studentElastic.politicalLandscapeId) {
            if (!Objects.isNull(student.politicalLandscapeId) && student.politicalLandscapeId > 0) {
                val politicalLandscape = politicalLandscapeService.findById(student.politicalLandscapeId!!)
                if (!Objects.isNull(politicalLandscape)) {
                    studentElastic.politicalLandscapeId = politicalLandscape.politicalLandscapeId
                    studentElastic.politicalLandscapeName = politicalLandscape.politicalLandscapeName
                }
            } else {
                studentElastic.politicalLandscapeId = student.politicalLandscapeId
                studentElastic.politicalLandscapeName = ""
            }
        }
        if (student.nationId != studentElastic.nationId) {
            if (!Objects.isNull(student.nationId) && student.nationId > 0) {
                val nation = nationService.findById(student.nationId!!)
                if (!Objects.isNull(nation)) {
                    studentElastic.nationId = nation.nationId
                    studentElastic.nationName = nation.nationName
                }
            } else {
                studentElastic.nationId = student.nationId
                studentElastic.nationName = ""
            }

        }
        if (!Objects.isNull(student.organizeId) && student.organizeId > 0 && student.organizeId != studentElastic.organizeId) {
            val record = organizeService.findByIdRelation(student.organizeId!!)
            if (record.isPresent) {
                val organizeBean = record.get().into(OrganizeBean::class.java)
                studentElastic.schoolId = organizeBean.schoolId
                studentElastic.schoolName = organizeBean.schoolName
                studentElastic.collegeId = organizeBean.collegeId
                studentElastic.collegeName = organizeBean.collegeName
                studentElastic.departmentId = organizeBean.departmentId
                studentElastic.departmentName = organizeBean.departmentName
                studentElastic.scienceId = organizeBean.scienceId
                studentElastic.scienceName = organizeBean.scienceName
                studentElastic.organizeId = organizeBean.organizeId
                studentElastic.organizeName = organizeBean.organizeName
                studentElastic.grade = organizeBean.grade
            }
        }
        studentElasticRepository.delete(studentElastic)
        studentElasticRepository.save(studentElastic)
    }

    override fun findByUsernameRelation(username: String): Optional<Record> {
        return create.select()
                .from(STUDENT)
                .join(ORGANIZE)
                .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                .join(SCIENCE)
                .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                .join(DEPARTMENT)
                .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                .join(COLLEGE)
                .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                .join(SCHOOL)
                .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                .join(USERS)
                .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                .leftJoin(NATION)
                .on(STUDENT.NATION_ID.eq(NATION.NATION_ID))
                .leftJoin(POLITICAL_LANDSCAPE)
                .on(STUDENT.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                .where(STUDENT.USERNAME.eq(username))
                .fetchOptional()
    }

    override fun findByUsernameAndScienceIdAndGradeRelation(username: String, scienceId: Int, grade: String): Optional<Record> {
        return create.select()
                .from(STUDENT)
                .join(ORGANIZE)
                .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                .join(SCIENCE)
                .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                .where(STUDENT.USERNAME.eq(username).and(SCIENCE.SCIENCE_ID.eq(scienceId)).and(ORGANIZE.GRADE.eq(grade)))
                .fetchOptional()
    }

    override fun findByUsername(username: String): Student {
        return studentDao.fetchOne(STUDENT.USERNAME, username)
    }

    override fun deleteByUsername(username: String) {
        create.deleteFrom(STUDENT).where(STUDENT.USERNAME.eq(username)).execute()
        studentElasticRepository.deleteByUsername(username)
    }

    override fun findAllByPageExistsAuthorities(dataTablesUtils: DataTablesUtils<StudentBean>): Result<Record> {
        val select = usersService.existsAuthoritiesSelect()
        val a = searchCondition(dataTablesUtils)
        val roleCondition = buildStudentCondition()
        return if (ObjectUtils.isEmpty(a)) {
            if (ObjectUtils.isEmpty(roleCondition)) {
                val selectConditionStep = create.select()
                        .from(STUDENT)
                        .join(ORGANIZE)
                        .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STUDENT.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STUDENT.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .whereExists(select)
                sortCondition(dataTablesUtils, selectConditionStep)
                pagination(dataTablesUtils, selectConditionStep)
                selectConditionStep.fetch()
            } else {
                val selectConditionStep = create.select()
                        .from(STUDENT)
                        .join(ORGANIZE)
                        .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STUDENT.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STUDENT.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .where(roleCondition).andExists(select)
                sortCondition(dataTablesUtils, selectConditionStep)
                pagination(dataTablesUtils, selectConditionStep)
                selectConditionStep.fetch()
            }
        } else {
            if (ObjectUtils.isEmpty(roleCondition)) {
                val selectConditionStep = create.select()
                        .from(STUDENT)
                        .join(ORGANIZE)
                        .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STUDENT.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STUDENT.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .where(a).andExists(select)
                sortCondition(dataTablesUtils, selectConditionStep)
                pagination(dataTablesUtils, selectConditionStep)
                selectConditionStep.fetch()
            } else {
                val selectConditionStep = create.select()
                        .from(STUDENT)
                        .join(ORGANIZE)
                        .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STUDENT.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STUDENT.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .where(roleCondition!!.and(a)).andExists(select)
                sortCondition(dataTablesUtils, selectConditionStep)
                pagination(dataTablesUtils, selectConditionStep)
                selectConditionStep.fetch()
            }
        }
    }

    override fun findAllByPageNotExistsAuthorities(dataTablesUtils: DataTablesUtils<StudentBean>): Result<Record> {
        val select = create.selectFrom(AUTHORITIES)
                .where(AUTHORITIES.USERNAME.eq(USERS.USERNAME))
        val a = searchCondition(dataTablesUtils)
        val roleCondition = buildStudentCondition()
        return if (ObjectUtils.isEmpty(a)) {
            if (ObjectUtils.isEmpty(roleCondition)) {
                val selectConditionStep = create.select()
                        .from(STUDENT)
                        .join(ORGANIZE)
                        .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STUDENT.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STUDENT.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .whereNotExists(select)
                sortCondition(dataTablesUtils, selectConditionStep)
                pagination(dataTablesUtils, selectConditionStep)
                selectConditionStep.fetch()
            } else {
                val selectConditionStep = create.select()
                        .from(STUDENT)
                        .join(ORGANIZE)
                        .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STUDENT.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STUDENT.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .where(roleCondition).andNotExists(select)
                sortCondition(dataTablesUtils, selectConditionStep)
                pagination(dataTablesUtils, selectConditionStep)
                selectConditionStep.fetch()
            }
        } else {
            if (ObjectUtils.isEmpty(roleCondition)) {
                val selectConditionStep = create.select()
                        .from(STUDENT)
                        .join(ORGANIZE)
                        .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STUDENT.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STUDENT.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .where(a).andNotExists(select)
                sortCondition(dataTablesUtils, selectConditionStep)
                pagination(dataTablesUtils, selectConditionStep)
                selectConditionStep.fetch()
            } else {
                val selectConditionStep = create.select()
                        .from(STUDENT)
                        .join(ORGANIZE)
                        .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STUDENT.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STUDENT.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .where(roleCondition!!.and(a)).andNotExists(select)
                sortCondition(dataTablesUtils, selectConditionStep)
                pagination(dataTablesUtils, selectConditionStep)
                selectConditionStep.fetch()
            }
        }
    }

    override fun countAllExistsAuthorities(): Int {
        val select = usersService.existsAuthoritiesSelect()
        val roleCondition = buildStudentCondition()
        return if (ObjectUtils.isEmpty(roleCondition)) {
            create.selectCount()
                    .from(STUDENT)
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .whereExists(select)
                    .fetchOne().value1()
        } else {
            create.selectCount()
                    .from(STUDENT)
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(SCIENCE)
                    .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .join(DEPARTMENT)
                    .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                    .join(COLLEGE)
                    .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                    .where(roleCondition).andExists(select)
                    .fetchOne().value1()
        }
    }

    override fun countAllNotExistsAuthorities(): Int {
        val select = create.selectFrom(AUTHORITIES)
                .where(AUTHORITIES.USERNAME.eq(USERS.USERNAME))
        val roleCondition = buildStudentCondition()
        return if (ObjectUtils.isEmpty(roleCondition)) {
            create.selectCount()
                    .from(STUDENT)
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .whereNotExists(select)
                    .fetchOne().value1()
        } else {
            create.selectCount()
                    .from(STUDENT)
                    .join(USERS)
                    .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                    .join(ORGANIZE)
                    .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                    .join(SCIENCE)
                    .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                    .join(DEPARTMENT)
                    .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                    .join(COLLEGE)
                    .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                    .where(roleCondition).andNotExists(select)
                    .fetchOne().value1()
        }
    }

    override fun countByConditionExistsAuthorities(dataTablesUtils: DataTablesUtils<StudentBean>): Int {
        val select = usersService.existsAuthoritiesSelect()
        val count: Record1<Int>
        val a = searchCondition(dataTablesUtils)
        val roleCondition = buildStudentCondition()
        count = if (ObjectUtils.isEmpty(a)) {
            if (ObjectUtils.isEmpty(roleCondition)) {
                val selectConditionStep = create.selectCount()
                        .from(STUDENT)
                        .join(USERS)
                        .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                        .whereExists(select)
                selectConditionStep.fetchOne()
            } else {
                val selectConditionStep = create.selectCount()
                        .from(STUDENT)
                        .join(USERS)
                        .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                        .join(ORGANIZE)
                        .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .where(roleCondition).andExists(select)
                selectConditionStep.fetchOne()
            }
        } else {
            if (ObjectUtils.isEmpty(roleCondition)) {
                val selectConditionStep = create.selectCount()
                        .from(STUDENT)
                        .join(ORGANIZE)
                        .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STUDENT.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STUDENT.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .where(a).andExists(select)
                selectConditionStep.fetchOne()
            } else {
                val selectConditionStep = create.selectCount()
                        .from(STUDENT)
                        .join(ORGANIZE)
                        .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STUDENT.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STUDENT.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .where(roleCondition!!.and(a)).andExists(select)
                selectConditionStep.fetchOne()
            }
        }
        return count.value1()
    }

    override fun countByConditionNotExistsAuthorities(dataTablesUtils: DataTablesUtils<StudentBean>): Int {
        val select = create.selectFrom(AUTHORITIES)
                .where(AUTHORITIES.USERNAME.eq(USERS.USERNAME))
        val count: Record1<Int>
        val a = searchCondition(dataTablesUtils)
        val roleCondition = buildStudentCondition()
        count = if (ObjectUtils.isEmpty(a)) {
            if (ObjectUtils.isEmpty(roleCondition)) {
                val selectConditionStep = create.selectCount()
                        .from(STUDENT)
                        .join(USERS)
                        .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                        .whereNotExists(select)
                selectConditionStep.fetchOne()
            } else {
                val selectConditionStep = create.selectCount()
                        .from(STUDENT)
                        .join(USERS)
                        .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                        .join(ORGANIZE)
                        .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .where(roleCondition).andNotExists(select)
                selectConditionStep.fetchOne()
            }
        } else {
            if (ObjectUtils.isEmpty(roleCondition)) {
                val selectConditionStep = create.selectCount()
                        .from(STUDENT)
                        .join(ORGANIZE)
                        .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STUDENT.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STUDENT.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .where(a).andNotExists(select)
                selectConditionStep.fetchOne()
            } else {
                val selectConditionStep = create.selectCount()
                        .from(STUDENT)
                        .join(ORGANIZE)
                        .on(STUDENT.ORGANIZE_ID.eq(ORGANIZE.ORGANIZE_ID))
                        .join(SCIENCE)
                        .on(ORGANIZE.SCIENCE_ID.eq(SCIENCE.SCIENCE_ID))
                        .join(DEPARTMENT)
                        .on(SCIENCE.DEPARTMENT_ID.eq(DEPARTMENT.DEPARTMENT_ID))
                        .join(COLLEGE)
                        .on(DEPARTMENT.COLLEGE_ID.eq(COLLEGE.COLLEGE_ID))
                        .join(SCHOOL)
                        .on(COLLEGE.SCHOOL_ID.eq(SCHOOL.SCHOOL_ID))
                        .join(USERS)
                        .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                        .leftJoin(NATION)
                        .on(STUDENT.NATION_ID.eq(NATION.NATION_ID))
                        .leftJoin(POLITICAL_LANDSCAPE)
                        .on(STUDENT.POLITICAL_LANDSCAPE_ID.eq(POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_ID))
                        .where(roleCondition!!.and(a)).andNotExists(select)
                selectConditionStep.fetchOne()
            }
        }
        return count.value1()
    }

    override fun countByOrganizeIdAndEnabledExistsAuthorities(organizeId: Int, b: Byte?): Int {
        val authoritiesRecordSelect = create.selectFrom(AUTHORITIES)
                .where(AUTHORITIES.USERNAME.eq(USERS.USERNAME))
        return create.selectCount()
                .from(STUDENT)
                .join(USERS)
                .on(STUDENT.USERNAME.eq(USERS.USERNAME))
                .where(USERS.ENABLED.eq(b).and(STUDENT.ORGANIZE_ID.eq(organizeId)).andExists(authoritiesRecordSelect))
                .fetchOne().value1()
    }

    /**
     * 全局搜索条件
     *
     * @param dataTablesUtils datatables工具类
     * @return 搜索条件
     */
    fun searchCondition(dataTablesUtils: DataTablesUtils<StudentBean>): Condition? {
        var a: Condition? = null

        val search = dataTablesUtils.search
        if (!ObjectUtils.isEmpty(search)) {
            val school = StringUtils.trimWhitespace(search!!.getString("school"))
            val college = StringUtils.trimWhitespace(search.getString("college"))
            val department = StringUtils.trimWhitespace(search.getString("department"))
            val science = StringUtils.trimWhitespace(search.getString("science"))
            val grade = StringUtils.trimWhitespace(search.getString("grade"))
            val organize = StringUtils.trimWhitespace(search.getString("organize"))
            val studentNumber = StringUtils.trimWhitespace(search.getString("studentNumber"))
            val username = StringUtils.trimWhitespace(search.getString("username"))
            val mobile = StringUtils.trimWhitespace(search.getString("mobile"))
            val idCard = StringUtils.trimWhitespace(search.getString("idCard"))
            val realName = StringUtils.trimWhitespace(search.getString("realName"))
            val sex = StringUtils.trimWhitespace(search.getString("sex"))
            if (StringUtils.hasLength(school)) {
                a = SCHOOL.SCHOOL_NAME.like(SQLQueryUtils.likeAllParam(school))
            }

            if (StringUtils.hasLength(college)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    COLLEGE.COLLEGE_NAME.like(SQLQueryUtils.likeAllParam(college))
                } else {
                    a!!.and(COLLEGE.COLLEGE_NAME.like(SQLQueryUtils.likeAllParam(college)))
                }
            }

            if (StringUtils.hasLength(department)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    DEPARTMENT.DEPARTMENT_NAME.like(SQLQueryUtils.likeAllParam(department))
                } else {
                    a!!.and(DEPARTMENT.DEPARTMENT_NAME.like(SQLQueryUtils.likeAllParam(department)))
                }
            }

            if (StringUtils.hasLength(science)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    SCIENCE.SCIENCE_NAME.like(SQLQueryUtils.likeAllParam(science))
                } else {
                    a!!.and(SCIENCE.SCIENCE_NAME.like(SQLQueryUtils.likeAllParam(science)))
                }
            }

            if (StringUtils.hasLength(grade)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    ORGANIZE.GRADE.like(SQLQueryUtils.likeAllParam(grade))
                } else {
                    a!!.and(ORGANIZE.GRADE.like(SQLQueryUtils.likeAllParam(grade)))
                }
            }

            if (StringUtils.hasLength(organize)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    ORGANIZE.ORGANIZE_NAME.like(SQLQueryUtils.likeAllParam(organize))
                } else {
                    a!!.and(ORGANIZE.ORGANIZE_NAME.like(SQLQueryUtils.likeAllParam(organize)))
                }
            }

            if (StringUtils.hasLength(studentNumber)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    STUDENT.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber))
                } else {
                    a!!.and(STUDENT.STUDENT_NUMBER.like(SQLQueryUtils.likeAllParam(studentNumber)))
                }
            }

            if (StringUtils.hasLength(username)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    STUDENT.USERNAME.like(SQLQueryUtils.likeAllParam(username))
                } else {
                    a!!.and(STUDENT.USERNAME.like(SQLQueryUtils.likeAllParam(username)))
                }
            }

            if (StringUtils.hasLength(mobile)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    USERS.MOBILE.like(SQLQueryUtils.likeAllParam(mobile))
                } else {
                    a!!.and(USERS.MOBILE.like(SQLQueryUtils.likeAllParam(mobile)))
                }
            }

            if (StringUtils.hasLength(idCard)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    STUDENT.ID_CARD.like(SQLQueryUtils.likeAllParam(idCard))
                } else {
                    a!!.and(STUDENT.ID_CARD.like(SQLQueryUtils.likeAllParam(idCard)))
                }
            }

            if (StringUtils.hasLength(realName)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    USERS.REAL_NAME.like(SQLQueryUtils.likeAllParam(realName))
                } else {
                    a!!.and(USERS.REAL_NAME.like(SQLQueryUtils.likeAllParam(realName)))
                }
            }

            if (StringUtils.hasLength(sex)) {
                a = if (ObjectUtils.isEmpty(a)) {
                    STUDENT.SEX.like(SQLQueryUtils.likeAllParam(sex))
                } else {
                    a!!.and(STUDENT.SEX.like(SQLQueryUtils.likeAllParam(sex)))
                }
            }
        }
        return a
    }

    /**
     * 数据排序
     *
     * @param dataTablesUtils     datatables工具类
     * @param selectConditionStep 条件
     */
    fun sortCondition(dataTablesUtils: DataTablesUtils<StudentBean>, selectConditionStep: SelectConditionStep<Record>) {
        val orderColumnName = dataTablesUtils.orderColumnName
        val orderDir = dataTablesUtils.orderDir
        val isAsc = "asc".equals(orderDir, ignoreCase = true)
        var sortField: Array<SortField<*>?>? = null
        if (StringUtils.hasLength(orderColumnName)) {
            if ("student_number".equals(orderColumnName!!, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = STUDENT.STUDENT_NUMBER.asc()
                } else {
                    sortField[0] = STUDENT.STUDENT_NUMBER.desc()
                }
            }

            if ("real_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = USERS.REAL_NAME.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = USERS.REAL_NAME.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

            if ("username".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = USERS.USERNAME.desc()
                }
            }

            if ("mobile".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = USERS.MOBILE.asc()
                } else {
                    sortField[0] = USERS.MOBILE.desc()
                }
            }

            if ("id_card".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(1)
                if (isAsc) {
                    sortField[0] = STUDENT.ID_CARD.asc()
                } else {
                    sortField[0] = STUDENT.ID_CARD.desc()
                }
            }

            if ("school_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = SCHOOL.SCHOOL_NAME.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = SCHOOL.SCHOOL_NAME.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

            if ("college_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = COLLEGE.COLLEGE_NAME.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = COLLEGE.COLLEGE_NAME.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

            if ("department_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = DEPARTMENT.DEPARTMENT_NAME.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = DEPARTMENT.DEPARTMENT_NAME.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

            if ("science_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = SCIENCE.SCIENCE_NAME.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = SCIENCE.SCIENCE_NAME.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

            if ("grade".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = ORGANIZE.GRADE.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = ORGANIZE.GRADE.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

            if ("organize_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = ORGANIZE.ORGANIZE_NAME.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = ORGANIZE.ORGANIZE_NAME.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

            if ("sex".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = STUDENT.SEX.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = STUDENT.SEX.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

            if ("birthday".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = STUDENT.BIRTHDAY.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = STUDENT.BIRTHDAY.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

            if ("nation_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = NATION.NATION_NAME.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = NATION.NATION_NAME.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

            if ("politicalLandscape_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_NAME.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = POLITICAL_LANDSCAPE.POLITICAL_LANDSCAPE_NAME.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

            if ("dormitory_number".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = STUDENT.DORMITORY_NUMBER.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = STUDENT.DORMITORY_NUMBER.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

            if ("place_origin".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = STUDENT.PLACE_ORIGIN.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = STUDENT.PLACE_ORIGIN.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

            if ("parent_name".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = STUDENT.PARENT_NAME.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = STUDENT.PARENT_NAME.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

            if ("parent_contact_phone".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = STUDENT.PARENT_CONTACT_PHONE.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = STUDENT.PARENT_CONTACT_PHONE.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

            if ("family_residence".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = STUDENT.FAMILY_RESIDENCE.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = STUDENT.FAMILY_RESIDENCE.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

            if ("enabled".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = USERS.ENABLED.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = USERS.ENABLED.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

            if ("lang_key".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = USERS.LANG_KEY.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = USERS.LANG_KEY.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

            if ("join_date".equals(orderColumnName, ignoreCase = true)) {
                sortField = arrayOfNulls(2)
                if (isAsc) {
                    sortField[0] = USERS.JOIN_DATE.asc()
                    sortField[1] = USERS.USERNAME.asc()
                } else {
                    sortField[0] = USERS.JOIN_DATE.desc()
                    sortField[1] = USERS.USERNAME.desc()
                }
            }

        }
        if (!ObjectUtils.isEmpty(sortField)) {
            selectConditionStep.orderBy(*sortField!!)
        }
    }

    /**
     * 分页方式
     *
     * @param dataTablesUtils
     * @param selectConditionStep
     */
    fun pagination(dataTablesUtils: DataTablesUtils<StudentBean>, selectConditionStep: SelectConditionStep<Record>) {
        val start = dataTablesUtils.start
        val length = dataTablesUtils.length
        selectConditionStep.limit(start, length)
    }

    /**
     * 构建该角色查询条件
     */
    private fun buildStudentCondition(): Condition? {
        return methodServiceCommon.buildDepartmentAndUserCondition()
    }

}