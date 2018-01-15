package top.zbeboy.isy.web.graduate.design.teacher

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignDeclareData
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignTeacher
import top.zbeboy.isy.service.cache.CacheBook
import top.zbeboy.isy.service.data.OrganizeService
import top.zbeboy.isy.service.data.ScienceService
import top.zbeboy.isy.service.data.StaffService
import top.zbeboy.isy.service.data.StudentService
import top.zbeboy.isy.service.graduate.design.GraduationDesignDeclareDataService
import top.zbeboy.isy.service.graduate.design.GraduationDesignReleaseService
import top.zbeboy.isy.service.graduate.design.GraduationDesignTeacherService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.UUIDUtils
import top.zbeboy.isy.web.bean.data.science.ScienceBean
import top.zbeboy.isy.web.bean.data.staff.StaffBean
import top.zbeboy.isy.web.bean.graduate.design.release.GraduationDesignReleaseBean
import top.zbeboy.isy.web.bean.graduate.design.teacher.GraduationDesignTeacherBean
import top.zbeboy.isy.web.common.MethodControllerCommon
import top.zbeboy.isy.web.graduate.design.common.GraduationDesignConditionCommon
import top.zbeboy.isy.web.graduate.design.common.GraduationDesignMethodControllerCommon
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import top.zbeboy.isy.web.util.PaginationUtils
import top.zbeboy.isy.web.util.SmallPropsUtils
import java.util.ArrayList
import java.util.concurrent.TimeUnit
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest

/**
 * Created by zbeboy 2018-01-15 .
 **/
@Controller
open class GraduationDesignTeacherController {

    @Resource
    open lateinit var methodControllerCommon: MethodControllerCommon

    @Resource
    open lateinit var graduationDesignReleaseService: GraduationDesignReleaseService

    @Resource
    open lateinit var graduationDesignTeacherService: GraduationDesignTeacherService

    @Resource
    open lateinit var graduationDesignDeclareDataService: GraduationDesignDeclareDataService

    @Resource
    open lateinit var organizeService: OrganizeService

    @Resource
    open lateinit var scienceService: ScienceService

    @Resource
    open lateinit var studentService: StudentService

    @Resource
    open lateinit var staffService: StaffService

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var template: StringRedisTemplate

    @Resource(name = "redisTemplate")
    open lateinit var stringListValueOperations: ValueOperations<String, List<GraduationDesignTeacherBean>>

    @Resource
    open lateinit var graduationDesignMethodControllerCommon: GraduationDesignMethodControllerCommon

    @Resource
    open lateinit var graduationDesignConditionCommon: GraduationDesignConditionCommon

    /**
     * 毕业指导教师
     *
     * @return 毕业指导教师页面
     */
    @RequestMapping(value = ["/web/menu/graduate/design/tutor"], method = [(RequestMethod.GET)])
    fun teacherData(): String {
        return "web/graduate/design/teacher/design_teacher::#page-wrapper"
    }

    /**
     * 获取毕业设计发布数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/tutor/design/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun designDatas(paginationUtils: PaginationUtils): AjaxUtils<GraduationDesignReleaseBean> {
        return graduationDesignMethodControllerCommon.graduationDesignListDatas(paginationUtils)
    }

    /**
     * 毕业指导教师列表页
     *
     * @return 毕业指导教师列表页
     */
    @RequestMapping(value = ["/web/graduate/design/tutor/look"], method = [(RequestMethod.GET)])
    fun teacherList(@RequestParam("id") graduationDesignReleaseId: String, modelMap: ModelMap): String {
        modelMap.addAttribute("graduationDesignDeclareData",
                graduationDesignDeclareDataService.findByGraduationDesignReleaseId(graduationDesignReleaseId))
        modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
        return "web/graduate/design/teacher/design_teacher_look::#page-wrapper"
    }

    /**
     * 毕业指导教师添加页
     *
     * @return 毕业指导教师添加页
     */
    @RequestMapping(value = ["/web/graduate/design/tutor/add"], method = [(RequestMethod.GET)])
    fun teacherAdd(@RequestParam("rId") graduationDesignReleaseId: String, modelMap: ModelMap): String {
        val page: String
        val errorBean = graduationDesignConditionCommon.isOkTeacherCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
            page = "web/graduate/design/teacher/design_teacher_add::#page-wrapper"
        } else {
            page = methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
        return page
    }

    /**
     * 毕业指导教师列表数据
     *
     * @param request 请求
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/tutor/look/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun listData(request: HttpServletRequest): DataTablesUtils<GraduationDesignTeacherBean> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("real_name")
        headers.add("staff_number")
        headers.add("staff_username")
        headers.add("student_count")
        headers.add("assigner_name")
        val dataTablesUtils = DataTablesUtils<GraduationDesignTeacherBean>(request, headers)
        val otherCondition = GraduationDesignTeacherBean()
        val graduationDesignReleaseId = request.getParameter("graduationDesignReleaseId")
        if (!ObjectUtils.isEmpty(graduationDesignReleaseId)) {
            otherCondition.graduationDesignReleaseId = graduationDesignReleaseId
            val graduationDesignTeacherBeens = graduationDesignTeacherService.findAllByPage(dataTablesUtils, otherCondition)
            dataTablesUtils.data = graduationDesignTeacherBeens
            dataTablesUtils.setiTotalRecords(graduationDesignTeacherService.countAll(otherCondition).toLong())
            dataTablesUtils.setiTotalDisplayRecords(graduationDesignTeacherService.countByCondition(dataTablesUtils, otherCondition).toLong())
        }
        return dataTablesUtils
    }

    /**
     * 毕业设计指导教师 所需教师数据
     *
     * @param graduationDesignReleaseId 实习id
     * @return 页面
     */
    @RequestMapping(value = ["/web/graduate/design/tutor/teachers"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun teachers(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<StaffBean> {
        val ajaxUtils = AjaxUtils.of<StaffBean>()
        val errorBean = graduationDesignConditionCommon.isOkTeacherCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val graduationDesignRelease = errorBean.data
            var staffs: List<StaffBean> = ArrayList()
            val staffRecords = staffService.findByDepartmentIdAndEnabledAndVerifyMailboxExistsAuthoritiesRelation(graduationDesignRelease!!.departmentId!!, 1, 1)
            if (staffRecords.isNotEmpty) {
                staffs = staffRecords.into(StaffBean::class.java)
                val graduationDesignTeachers = graduationDesignTeacherService.findByGraduationDesignReleaseId(graduationDesignReleaseId)
                for (graduationDesignTeacher in graduationDesignTeachers) {
                    for (staff in staffs) {
                        if (graduationDesignTeacher.staffId == staff.staffId) {
                            staff.checked = true
                            break
                        }
                    }
                }
            }
            ajaxUtils.success().msg("获取教师数据成功").listData(staffs)
        } else {
            ajaxUtils.fail().msg("因您不满足进入条件，无法进行数据获取，请返回首页")
        }
        return ajaxUtils
    }

    /**
     * 添加指导教师
     *
     * @param staffId                   教职工id
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/tutor/save"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun add(staffId: String, graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = graduationDesignConditionCommon.isOkTeacherCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            if (StringUtils.hasLength(staffId) && SmallPropsUtils.StringIdsIsNumber(staffId)) {
                val graduationDesignRelease = errorBean.data
                // 查询出专业名，系名
                val science = scienceService.findByIdRelation(graduationDesignRelease!!.scienceId!!)
                if (science.isPresent) {
                    val scienceBean = science.get().into(ScienceBean::class.java)
                    // 查询出专业下所有班级
                    val organizeRecords = organizeService.findByScienceIdAndGradeAndIsDel(graduationDesignRelease.scienceId!!, graduationDesignRelease.allowGrade, 0)
                    // 各班级学生人数
                    val organizeNames = StringBuilder()
                    val organizePeoples = StringBuilder()
                    var totalPeoples = 0
                    val sp = "###"
                    for (organize in organizeRecords) {
                        organizeNames.append(organize.organizeName).append(sp)
                        val peoples = studentService.countByOrganizeIdAndEnabledExistsAuthorities(organize.organizeId!!, 1)
                        organizePeoples.append(peoples).append(sp)
                        totalPeoples += peoples
                    }

                    graduationDesignDeclareDataService.deleteByGraduationDesignReleaseId(graduationDesignReleaseId)
                    val graduationDesignDeclareData = GraduationDesignDeclareData()
                    graduationDesignDeclareData.graduationDesignDeclareDataId = UUIDUtils.getUUID()
                    graduationDesignDeclareData.departmentName = scienceBean.departmentName
                    graduationDesignDeclareData.scienceName = scienceBean.scienceName
                    graduationDesignDeclareData.organizeNames = organizeNames.substring(0, organizeNames.lastIndexOf(sp))
                    graduationDesignDeclareData.organizePeoples = organizePeoples.substring(0, organizePeoples.lastIndexOf(sp))
                    graduationDesignDeclareData.graduationDesignReleaseId = graduationDesignReleaseId
                    graduationDesignDeclareData.graduationDate = Workbook.graduationDate()
                    graduationDesignDeclareDataService.save(graduationDesignDeclareData)

                    // 保存指导教师数据
                    val users = usersService.getUserFromSession()
                    graduationDesignTeacherService.deleteByGraduationDesignReleaseId(graduationDesignReleaseId)
                    val staffIds = SmallPropsUtils.StringIdsToList(staffId)

                    //  计算出平均带人数
                    val average = totalPeoples / staffIds.size
                    // 余数
                    var remainder = totalPeoples % staffIds.size

                    for (id in staffIds) {
                        val graduationDesignTeacher = GraduationDesignTeacher()
                        graduationDesignTeacher.graduationDesignTeacherId = UUIDUtils.getUUID()
                        graduationDesignTeacher.graduationDesignReleaseId = graduationDesignReleaseId
                        graduationDesignTeacher.staffId = id
                        graduationDesignTeacher.username = users!!.username
                        if (remainder > 0) {
                            graduationDesignTeacher.studentCount = average + 1
                            remainder--
                        } else {
                            graduationDesignTeacher.studentCount = average
                        }
                        graduationDesignTeacherService.save(graduationDesignTeacher)
                    }
                    ajaxUtils.success().msg("添加指导教师成功")
                } else {
                    ajaxUtils.fail().msg("未查询到相关信息")
                }
            } else {
                ajaxUtils.fail().msg("参数异常")
            }
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 确认毕业设计指导教师
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/tutor/ok"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun tutorOk(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = graduationDesignConditionCommon.isOkTeacherCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val graduationDesignRelease = errorBean.data
            // 在填报时间之前
            if (DateTimeUtils.timestampBeforeDecide(graduationDesignRelease!!.fillTeacherEndTime)) {
                val graduationDesignTeachers = graduationDesignTeacherService.findByGraduationDesignReleaseIdRelationForStaff(graduationDesignReleaseId)
                val ops = this.template.opsForValue()
                // 初始化人数到缓存
                graduationDesignTeachers.forEach { graduationDesignTeacher ->
                    ops.set(
                            CacheBook.GRADUATION_DESIGN_TEACHER_STUDENT_COUNT + graduationDesignTeacher.graduationDesignTeacherId,
                            graduationDesignTeacher.studentCount!!.toString() + "",
                            CacheBook.EXPIRES_GRADUATION_DESIGN_TEACHER_STUDENT_DAYS,
                            TimeUnit.DAYS)
                }
                // 列表刷到缓存
                stringListValueOperations.set(
                        CacheBook.GRADUATION_DESIGN_TEACHER_STUDENT + graduationDesignReleaseId,
                        graduationDesignTeachers,
                        CacheBook.EXPIRES_GRADUATION_DESIGN_TEACHER_STUDENT_DAYS,
                        TimeUnit.DAYS
                )
                graduationDesignRelease.isOkTeacher = 1
                graduationDesignReleaseService.update(graduationDesignRelease)
                ajaxUtils.success().msg("确认毕业设计指导教师成功")
            } else {
                ajaxUtils.fail().msg("已过学生填报时间")
            }
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 进入页面判断条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/tutor/condition"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun canUse(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = graduationDesignConditionCommon.isOkTeacherCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            ajaxUtils.success().msg("在条件范围，允许使用")
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }
}