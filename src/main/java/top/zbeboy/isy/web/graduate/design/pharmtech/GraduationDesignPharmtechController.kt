package top.zbeboy.isy.web.graduate.design.pharmtech

import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.math.NumberUtils
import org.springframework.data.redis.core.ListOperations
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.util.ObjectUtils
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignHopeTutor
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignRelease
import top.zbeboy.isy.domain.tables.pojos.Student
import top.zbeboy.isy.service.cache.CacheBook
import top.zbeboy.isy.service.data.StudentService
import top.zbeboy.isy.service.graduate.design.GraduationDesignHopeTutorService
import top.zbeboy.isy.service.graduate.design.GraduationDesignTeacherService
import top.zbeboy.isy.service.graduate.design.GraduationDesignTutorService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.platform.UsersTypeService
import top.zbeboy.isy.web.bean.error.ErrorBean
import top.zbeboy.isy.web.bean.graduate.design.pharmtech.GraduationDesignTutorBean
import top.zbeboy.isy.web.bean.graduate.design.release.GraduationDesignReleaseBean
import top.zbeboy.isy.web.bean.graduate.design.teacher.GraduationDesignTeacherBean
import top.zbeboy.isy.web.common.MethodControllerCommon
import top.zbeboy.isy.web.graduate.design.common.GraduationDesignConditionCommon
import top.zbeboy.isy.web.graduate.design.common.GraduationDesignMethodControllerCommon
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.PaginationUtils
import java.util.ArrayList
import java.util.HashMap
import java.util.concurrent.TimeUnit
import javax.annotation.Resource

/**
 * Created by zbeboy 2018-01-19 .
 **/
@Controller
open class GraduationDesignPharmtechController {

    @Resource
    open lateinit var graduationDesignTeacherService: GraduationDesignTeacherService

    @Resource
    open lateinit var graduationDesignHopeTutorService: GraduationDesignHopeTutorService

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var studentService: StudentService

    @Resource
    open lateinit var methodControllerCommon: MethodControllerCommon

    @Resource
    open lateinit var graduationDesignTutorService: GraduationDesignTutorService

    @Resource
    open lateinit var usersTypeService: UsersTypeService

    @Resource
    open lateinit var template: StringRedisTemplate

    @Resource(name = "redisTemplate")
    open lateinit var stringValueOperations: ValueOperations<String, String>

    @Resource(name = "redisTemplate")
    open lateinit var stringListValueOperations: ValueOperations<String, List<GraduationDesignTeacherBean>>

    @Resource(name = "redisTemplate")
    open lateinit var listOperations: ListOperations<String, String>

    @Resource
    open lateinit var graduationDesignMethodControllerCommon: GraduationDesignMethodControllerCommon

    @Resource
    open lateinit var graduationDesignConditionCommon: GraduationDesignConditionCommon


    /**
     * 填报指导教师
     *
     * @return 填报指导教师页面
     */
    @RequestMapping(value = ["/web/menu/graduate/design/pharmtech"], method = [(RequestMethod.GET)])
    fun pharmtech(): String {
        return "web/graduate/design/pharmtech/design_pharmtech::#page-wrapper"
    }

    /**
     * 获取毕业设计发布数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/pharmtech/design/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun designDatas(paginationUtils: PaginationUtils): AjaxUtils<GraduationDesignReleaseBean> {
        return graduationDesignMethodControllerCommon.graduationDesignListDatas(paginationUtils)
    }

    /**
     * 志愿页面
     *
     * @param graduationDesignReleaseId 发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/graduate/design/pharmtech/wish"], method = [(RequestMethod.GET)])
    fun pharmtechWish(@RequestParam("id") graduationDesignReleaseId: String, modelMap: ModelMap): String {
        return if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
            "web/graduate/design/pharmtech/design_pharmtech_wish::#page-wrapper"
        } else {
            methodControllerCommon.showTip(modelMap, "仅支持学生用户使用")
        }
    }

    /**
     * 填报页面
     *
     * @param graduationDesignReleaseId 发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/graduate/design/pharmtech/apply"], method = [(RequestMethod.GET)])
    fun pharmtechApply(@RequestParam("id") graduationDesignReleaseId: String, modelMap: ModelMap): String {
        val errorBean = accessCondition(graduationDesignReleaseId)
        return if (!errorBean.isHasError()) {
            val student = errorBean.mapData!!["student"] as Student
            val count = graduationDesignHopeTutorService.countByStudentIdAndGraduationDesignReleaseId(student.studentId!!, graduationDesignReleaseId)
            if (count > 0) {
                modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
                "web/graduate/design/pharmtech/design_pharmtech_apply::#page-wrapper"
            } else {
                methodControllerCommon.showTip(modelMap, "请先填写志愿")
            }
        } else {
            methodControllerCommon.showTip(modelMap, errorBean.errorMsg!!)
        }
    }

    /**
     * 填报指导教师志愿数据
     *
     * @param graduationDesignReleaseId 发布id
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/pharmtech/wish/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun wishData(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<GraduationDesignTeacherBean> {
        val ajaxUtils = AjaxUtils.of<GraduationDesignTeacherBean>()
        if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
            val users = usersService.getUserFromSession()
            val student = studentService.findByUsername(users!!.username)
            val designHopeTutorRecords = graduationDesignHopeTutorService.findByStudentIdAndGraduationDesignReleaseId(student.studentId!!, graduationDesignReleaseId)
            val graduationDesignTeachers = graduationDesignTeacherService.findByGraduationDesignReleaseIdRelationForStaff(graduationDesignReleaseId)
            for (designTeacherBean in graduationDesignTeachers) {
                var selectedTeacher = false
                if (designHopeTutorRecords.isNotEmpty) {
                    val graduationDesignHopeTutors = designHopeTutorRecords.into(GraduationDesignHopeTutor::class.java)
                    for (r in graduationDesignHopeTutors) {
                        if (designTeacherBean.graduationDesignTeacherId == r.graduationDesignTeacherId) {
                            selectedTeacher = true
                            break
                        }
                    }
                }
                designTeacherBean.selected = selectedTeacher
            }
            ajaxUtils.success().msg("获取数据成功").listData(graduationDesignTeachers)
        } else {
            ajaxUtils.fail().msg("仅支持学生用户使用")
        }
        return ajaxUtils
    }

    /**
     * 填报指导教师数据
     *
     * @param graduationDesignReleaseId 发布id
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/pharmtech/apply/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun applyData(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<GraduationDesignTeacherBean> {
        val ajaxUtils = AjaxUtils.of<GraduationDesignTeacherBean>()
        val errorBean = accessCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val student = errorBean.mapData!!["student"] as Student
            val graduationDesignTeacherBeens: List<GraduationDesignTeacherBean>
            val cacheKey = CacheBook.GRADUATION_DESIGN_TEACHER_STUDENT + graduationDesignReleaseId
            val studentKey = CacheBook.GRADUATION_DESIGN_PHARMTECH_STUDENT + student.studentId!!
            // 从缓存中得到列表
            if (stringListValueOperations.operations.hasKey(cacheKey)!!) {
                graduationDesignTeacherBeens = stringListValueOperations.get(cacheKey)!!
            } else {
                graduationDesignTeacherBeens = ArrayList()
            }
            // 处理列表
            if (!ObjectUtils.isEmpty(graduationDesignTeacherBeens) && graduationDesignTeacherBeens.size > 0) {
                var selectedTeacher = false
                for (designTeacherBean in graduationDesignTeacherBeens) {
                    // 装填剩余人数
                    val studentCountKey = CacheBook.GRADUATION_DESIGN_TEACHER_STUDENT_COUNT + designTeacherBean.graduationDesignTeacherId
                    if (template.hasKey(studentCountKey)) {
                        val ops = this.template.opsForValue()
                        designTeacherBean.residueCount = NumberUtils.toInt(ops.get(studentCountKey))
                    }
                    // 选中当前用户已选择
                    if (!selectedTeacher && stringValueOperations.operations.hasKey(studentKey)!!) {
                        // 解除逗号分隔的字符   指导教师id , 学生id
                        val str = stringValueOperations.get(studentKey)
                        if (StringUtils.isNotBlank(str)) {
                            val arr = str!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                            if (arr.size >= 2) {
                                if (designTeacherBean.graduationDesignTeacherId == arr[0]) {
                                    selectedTeacher = true
                                    designTeacherBean.selected = true
                                }
                            }
                        }
                    }
                }
            }
            ajaxUtils.success().msg("获取数据成功").listData(graduationDesignTeacherBeens)
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 选择教师
     *
     * @param graduationDesignTeacherId 指导老师id
     * @param graduationDesignReleaseId 发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/pharmtech/wish/selected"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun wishSelected(@RequestParam("graduationDesignTeacherId") graduationDesignTeacherId: String,
                     @RequestParam("graduationDesignReleaseId") graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = accessCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val student = errorBean.mapData!!["student"] as Student
            // 是否已达到志愿数量
            val count = graduationDesignHopeTutorService.countByStudentIdAndGraduationDesignReleaseId(student.studentId!!, graduationDesignReleaseId)
            if (count < 3) {
                val graduationDesignHopeTutor = GraduationDesignHopeTutor()
                graduationDesignHopeTutor.graduationDesignTeacherId = graduationDesignTeacherId
                graduationDesignHopeTutor.studentId = student.studentId
                graduationDesignHopeTutorService.save(graduationDesignHopeTutor)
                ajaxUtils.success().msg("保存成功")
            } else {
                ajaxUtils.fail().msg("最大支持志愿三位指导教师")
            }
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 填报选择教师
     *
     * @param graduationDesignTeacherId 指导老师id
     * @param graduationDesignReleaseId 发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/pharmtech/apply/selected"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun applySelected(@RequestParam("graduationDesignTeacherId") graduationDesignTeacherId: String,
                      @RequestParam("graduationDesignReleaseId") graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = accessCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val student = errorBean.mapData!!["student"] as Student
            // 判断是否已选择过教师
            val canSelect: Boolean
            // 第一次选择
            var isNewSelect = false
            val studentKey = CacheBook.GRADUATION_DESIGN_PHARMTECH_STUDENT + student.studentId!!
            if (stringValueOperations.operations.hasKey(studentKey)!!) {
                // 已选择过，不能重复选
                val str = stringValueOperations.get(studentKey)
                if (StringUtils.isNotBlank(str)) {
                    val arr = str!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    canSelect = arr.size >= 2 && arr[0] == "-1"
                } else {
                    canSelect = false
                }
            } else {
                canSelect = true
                isNewSelect = true
            }
            if (canSelect) {
                // 计数器
                val countKey = CacheBook.GRADUATION_DESIGN_TEACHER_STUDENT_COUNT + graduationDesignTeacherId
                if (template.hasKey(countKey)) {
                    val ops = this.template.opsForValue()
                    val count = NumberUtils.toInt(ops.get(countKey)) - 1
                    if (count >= 0) {
                        ops.set(countKey, count.toString() + "")
                        // 存储 指导教师id , 学生id
                        if (isNewSelect) {
                            stringValueOperations.set(studentKey,
                                    graduationDesignTeacherId + "," + student.studentId,
                                    CacheBook.EXPIRES_GRADUATION_DESIGN_TEACHER_STUDENT_DAYS, TimeUnit.DAYS)
                        } else {
                            stringValueOperations.set(studentKey,
                                    graduationDesignTeacherId + "," + student.studentId)
                        }
                        // 存储学生key
                        // 是否已经存在当前学生key
                        val listKey = CacheBook.GRADUATION_DESIGN_PHARMTECH_STUDENT_LIST + graduationDesignReleaseId
                        val keys = listOperations.range(listKey, 0, listOperations.size(listKey)!!)
                        val hasKey = !ObjectUtils.isEmpty(keys) && keys!!.contains(studentKey)

                        // 不存在，需要添加
                        if (!hasKey) {
                            listOperations.rightPush(listKey, studentKey)
                        }
                        ajaxUtils.success().msg("保存成功")
                    } else {
                        ajaxUtils.fail().msg("已达当前指导教师人数上限")
                    }
                } else {
                    ajaxUtils.fail().msg("未发现确认数据")
                }
            } else {
                ajaxUtils.fail().msg("仅可选择一位指导教师")
            }
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 取消教师
     *
     * @param graduationDesignTeacherId 指导老师id
     * @param graduationDesignReleaseId 发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/pharmtech/wish/cancel"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun wishCancel(@RequestParam("graduationDesignTeacherId") graduationDesignTeacherId: String,
                   @RequestParam("graduationDesignReleaseId") graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = accessCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val student = errorBean.mapData!!["student"] as Student
            val graduationDesignHopeTutor = GraduationDesignHopeTutor()
            graduationDesignHopeTutor.graduationDesignTeacherId = graduationDesignTeacherId
            graduationDesignHopeTutor.studentId = student.studentId
            graduationDesignHopeTutorService.delete(graduationDesignHopeTutor)
            ajaxUtils.success().msg("取消成功")
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 填报取消教师
     *
     * @param graduationDesignTeacherId 指导老师id
     * @param graduationDesignReleaseId 发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/pharmtech/apply/cancel"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun applyCancel(@RequestParam("graduationDesignTeacherId") graduationDesignTeacherId: String,
                    @RequestParam("graduationDesignReleaseId") graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = accessCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val student = errorBean.mapData!!["student"] as Student
            // 计数器
            val countKey = CacheBook.GRADUATION_DESIGN_TEACHER_STUDENT_COUNT + graduationDesignTeacherId
            if (template.hasKey(countKey)) {
                val ops = this.template.opsForValue()
                ops.increment(countKey, 1L)
                // 存储 指导教师id , 学生id
                val studentKey = CacheBook.GRADUATION_DESIGN_PHARMTECH_STUDENT + student.studentId!!
                if (stringValueOperations.operations.hasKey(studentKey)!!) {
                    stringValueOperations.set(studentKey,
                            (-1).toString() + "," + student.studentId)
                }
                ajaxUtils.success().msg("取消成功")
            } else {
                ajaxUtils.fail().msg("未发现确认数据")
            }
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 进入志愿页面判断条件
     *
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/pharmtech/wish/condition"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun canWish(): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
            ajaxUtils.success().msg("在条件范围，允许使用")
        } else {
            ajaxUtils.fail().msg("仅支持学生用户使用")
        }
        return ajaxUtils
    }

    /**
     * 进入填报页面判断条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/pharmtech/apply/condition"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun canApply(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = accessCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val student = errorBean.mapData!!["student"] as Student
            val count = graduationDesignHopeTutorService.countByStudentIdAndGraduationDesignReleaseId(student.studentId!!, graduationDesignReleaseId)
            if (count > 0) {
                ajaxUtils.success().msg("在条件范围，允许使用")
            } else {
                ajaxUtils.fail().msg("请先填写志愿")
            }
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 获取我的毕业指导教师信息
     *
     * @param graduationDesignReleaseId 毕业发布id
     * @return 指导教师信息
     */
    @RequestMapping(value = ["/web/graduate/design/pharmtech/my/teacher"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun myTeacher(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = graduationDesignConditionCommon.isNotOkTeacherAdjust(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val graduationDesignRelease = errorBean.data
            if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                // 查询学生
                val users = usersService.getUserFromSession()
                val studentRecord = studentService.findByUsernameAndScienceIdAndGradeRelation(users!!.username, graduationDesignRelease!!.scienceId!!, graduationDesignRelease.allowGrade)
                if (studentRecord.isPresent) {
                    val student = studentRecord.get().into(Student::class.java)
                    val record = graduationDesignTutorService.findByStudentIdAndGraduationDesignReleaseIdRelationForStaff(student.studentId!!, graduationDesignReleaseId)
                    if (record.isPresent) {
                        val graduationDesignTutorBean = record.get().into(GraduationDesignTutorBean::class.java)
                        ajaxUtils.success().msg("获取数据成功").obj(graduationDesignTutorBean)
                    } else {
                        ajaxUtils.fail().msg("未获取到任何信息")
                    }
                } else {
                    ajaxUtils.fail().msg("您的账号不符合此次毕业设计条件")
                }
            } else {
                ajaxUtils.fail().msg("仅支持学生用户使用")
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
    @RequestMapping(value = ["/web/graduate/design/pharmtech/condition"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun canUse(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = accessCondition(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            ajaxUtils.success().msg("在条件范围，允许使用")
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 进入填报教师入口条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    private fun accessCondition(graduationDesignReleaseId: String): ErrorBean<GraduationDesignRelease> {
        val errorBean = graduationDesignConditionCommon.isRangeFillTeacherDate(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val mapData = HashMap<String, Any>()
            val graduationDesignRelease = errorBean.data
            if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                // 是否学生在该毕业设计专业下
                val users = usersService.getUserFromSession()
                val studentRecord = studentService.findByUsernameAndScienceIdAndGradeRelation(users!!.username, graduationDesignRelease!!.scienceId!!, graduationDesignRelease.allowGrade)
                if (studentRecord.isPresent) {
                    val student = studentRecord.get().into(Student::class.java)
                    mapData.put("student", student)
                    // 是否已确认
                    if (!ObjectUtils.isEmpty(graduationDesignRelease.isOkTeacher) && graduationDesignRelease.isOkTeacher == 1.toByte()) {
                        // 是否已确认调整
                        if (!ObjectUtils.isEmpty(graduationDesignRelease.isOkTeacherAdjust) && graduationDesignRelease.isOkTeacherAdjust == 1.toByte()) {
                            errorBean.hasError = true
                            errorBean.errorMsg = "已确认毕业设计指导教师调整，无法进行操作"
                        } else {
                            errorBean.hasError = false
                        }
                    } else {
                        errorBean.hasError = true
                        errorBean.errorMsg = "未确认毕业设计指导教师，无法进行操作"
                    }
                } else {
                    errorBean.hasError = true
                    errorBean.errorMsg = "您的账号不符合此次毕业设计条件"
                }
            } else {
                errorBean.hasError = true
                errorBean.errorMsg = "仅支持学生用户使用"
            }
            errorBean.mapData = mapData
        }
        return errorBean
    }
}