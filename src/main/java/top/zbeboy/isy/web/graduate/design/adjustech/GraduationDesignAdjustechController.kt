package top.zbeboy.isy.web.graduate.design.adjustech

import org.apache.commons.lang3.math.NumberUtils
import org.joda.time.DateTime
import org.springframework.data.redis.core.ListOperations
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignTutor
import top.zbeboy.isy.service.cache.CacheBook
import top.zbeboy.isy.service.graduate.design.GraduationDesignHopeTutorService
import top.zbeboy.isy.service.graduate.design.GraduationDesignReleaseService
import top.zbeboy.isy.service.graduate.design.GraduationDesignTeacherService
import top.zbeboy.isy.service.graduate.design.GraduationDesignTutorService
import top.zbeboy.isy.service.util.DateTimeUtils
import top.zbeboy.isy.service.util.UUIDUtils
import top.zbeboy.isy.web.bean.data.student.StudentBean
import top.zbeboy.isy.web.bean.graduate.design.pharmtech.GraduationDesignHopeTutorBean
import top.zbeboy.isy.web.bean.graduate.design.pharmtech.GraduationDesignTutorBean
import top.zbeboy.isy.web.bean.graduate.design.release.GraduationDesignReleaseBean
import top.zbeboy.isy.web.bean.graduate.design.teacher.GraduationDesignTeacherBean
import top.zbeboy.isy.web.graduate.design.common.GraduationDesignConditionCommon
import top.zbeboy.isy.web.graduate.design.common.GraduationDesignMethodControllerCommon
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.DataTablesUtils
import top.zbeboy.isy.web.util.PaginationUtils
import top.zbeboy.isy.web.util.SmallPropsUtils
import top.zbeboy.isy.web.vo.graduate.design.adjustech.GraduationDesignTutorAddVo
import top.zbeboy.isy.web.vo.graduate.design.adjustech.GraduationDesignTutorUpdateVo
import java.util.*
import java.util.concurrent.TimeUnit
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

/**
 * Created by zbeboy 2018-01-20 .
 **/
@Controller
open class GraduationDesignAdjustechController {

    @Resource
    open lateinit var graduationDesignReleaseService: GraduationDesignReleaseService

    @Resource
    open lateinit var graduationDesignTutorService: GraduationDesignTutorService

    @Resource
    open lateinit var graduationDesignTeacherService: GraduationDesignTeacherService

    @Resource
    open lateinit var graduationDesignHopeTutorService: GraduationDesignHopeTutorService

    @Resource(name = "redisTemplate")
    open lateinit var stringValueOperations: ValueOperations<String, String>

    @Resource(name = "redisTemplate")
    open lateinit var stringListValueOperations: ValueOperations<String, List<GraduationDesignTeacherBean>>

    @Resource
    open lateinit var template: StringRedisTemplate

    @Resource(name = "redisTemplate")
    open lateinit var listOperations: ListOperations<String, String>

    @Resource
    open lateinit var graduationDesignConditionCommon: GraduationDesignConditionCommon

    @Resource
    open lateinit var graduationDesignMethodControllerCommon: GraduationDesignMethodControllerCommon


    /**
     * 调整填报教师
     *
     * @return 调整填报教师页面
     */
    @RequestMapping(value = ["/web/menu/graduate/design/adjustech"], method = [(RequestMethod.GET)])
    fun adjustech(): String {
        return "web/graduate/design/adjustech/design_adjustech::#page-wrapper"
    }

    /**
     * 填报情况页面
     *
     * @param graduationDesignReleaseId 发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = ["/web/graduate/design/adjustech/apply"], method = [(RequestMethod.GET)])
    fun adjustechApply(@RequestParam("id") graduationDesignReleaseId: String, modelMap: ModelMap): String {
        modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
        return "web/graduate/design/adjustech/design_adjustech_apply::#page-wrapper"
    }

    /**
     * 调整
     *
     * @return 调整页面
     */
    @RequestMapping(value = ["/web/graduate/design/adjustech/adjust"], method = [(RequestMethod.GET)])
    fun adjust(@RequestParam("id") graduationDesignReleaseId: String, modelMap: ModelMap): String {
        modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
        return "web/graduate/design/adjustech/design_adjustech_adjust::#page-wrapper"
    }

    /**
     * 已填报学生
     *
     * @return 已填报学生页面
     */
    @RequestMapping(value = ["/web/graduate/design/adjustech/student/submit"], method = [(RequestMethod.GET)])
    fun fill(@RequestParam("id") graduationDesignReleaseId: String, modelMap: ModelMap): String {
        modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
        return "web/graduate/design/adjustech/design_adjustech_submit::#page-wrapper"
    }

    /**
     * 未填报学生
     *
     * @return 已填报学生页面
     */
    @RequestMapping(value = ["/web/graduate/design/adjustech/student/unsubmit"], method = [(RequestMethod.GET)])
    fun notFill(@RequestParam("id") graduationDesignReleaseId: String, modelMap: ModelMap): String {
        modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId)
        return "web/graduate/design/adjustech/design_adjustech_unsubmit::#page-wrapper"
    }

    /**
     * 填报情况教师数据
     *
     * @param graduationDesignReleaseId 发布id
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/adjustech/apply/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun applyData(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<GraduationDesignTeacherBean> {
        val ajaxUtils = AjaxUtils.of<GraduationDesignTeacherBean>()
        val graduationDesignTeacherBeens: List<GraduationDesignTeacherBean>
        val cacheKey = CacheBook.GRADUATION_DESIGN_TEACHER_STUDENT + graduationDesignReleaseId
        // 从缓存中得到列表
        graduationDesignTeacherBeens = if (stringListValueOperations.operations.hasKey(cacheKey)!!) {
            stringListValueOperations.get(cacheKey)
        } else {
            ArrayList()
        }
        // 处理列表
        if (!graduationDesignTeacherBeens.isNotEmpty()) {
            for (designTeacherBean in graduationDesignTeacherBeens) {
                // 装填剩余人数
                val studentCountKey = CacheBook.GRADUATION_DESIGN_TEACHER_STUDENT_COUNT + designTeacherBean.graduationDesignTeacherId
                if (template.hasKey(studentCountKey)!!) {
                    val ops = this.template.opsForValue()
                    designTeacherBean.residueCount = NumberUtils.toInt(ops.get(studentCountKey))
                }
            }
        }
        ajaxUtils.success().msg("获取数据成功").listData(graduationDesignTeacherBeens)
        return ajaxUtils
    }

    /**
     * 列表数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/adjustech/design/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun adjustechDatas(paginationUtils: PaginationUtils): AjaxUtils<GraduationDesignReleaseBean> {
        val ajaxUtils = graduationDesignMethodControllerCommon.graduationDesignListDatas(paginationUtils)
        val graduationDesignReleaseBeens = ajaxUtils.listResult
        graduationDesignReleaseBeens!!.forEach { graduationDesignRelease ->
            graduationDesignRelease.studentNotFillCount = graduationDesignTutorService.countNotFillStudent(graduationDesignRelease)
            graduationDesignRelease.studentFillCount = graduationDesignTutorService.countFillStudent(graduationDesignRelease)
            val syncTimeKey = CacheBook.GRADUATION_DESIGN_ADJUSTECH_SYNC_TIME + graduationDesignRelease.graduationDesignReleaseId
            if (stringValueOperations.operations.hasKey(syncTimeKey)!!) {
                graduationDesignRelease.hasSyncDate = true
                graduationDesignRelease.syncDate = stringValueOperations.get(syncTimeKey)
            } else {
                graduationDesignRelease.hasSyncDate = false
            }
        }
        return ajaxUtils.listData(graduationDesignReleaseBeens)
    }

    /**
     * 同步数据
     *
     * @param graduationDesignReleaseId 毕业发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/adjustech/sync/data"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun syncData(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = graduationDesignConditionCommon.isOkTeacherAdjust(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val graduationDesignRelease = errorBean.data
            // 仅允许在填报时间内同步，若超出填报时间，缓存 KEY 有可能失效，同时在非填报时间内做的调整，因同步将会被清空
            if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease!!.fillTeacherStartTime, graduationDesignRelease.fillTeacherEndTime)) {
                // step 1. 检查同步时间
                val syncTimeKey = CacheBook.GRADUATION_DESIGN_ADJUSTECH_SYNC_TIME + graduationDesignReleaseId
                if (!stringValueOperations.operations.hasKey(syncTimeKey)) {
                    // 更新剩余人数到指导教师表 ， 并且删除学生指导教师表中的关联数据
                    val graduationDesignTeachers = graduationDesignTeacherService.findByGraduationDesignReleaseId(graduationDesignReleaseId)
                    val ops = this.template.opsForValue()
                    graduationDesignTeachers.forEach { graduationDesignTeacher ->
                        val studentCountKey = CacheBook.GRADUATION_DESIGN_TEACHER_STUDENT_COUNT + graduationDesignTeacher.graduationDesignTeacherId
                        if (template.hasKey(studentCountKey)!!) {
                            graduationDesignTeacher.residue = NumberUtils.toInt(ops.get(studentCountKey))
                        }
                        graduationDesignTutorService.deleteByGraduationDesignTeacherId(graduationDesignTeacher.graduationDesignTeacherId)
                    }
                    // 更新
                    graduationDesignTeacherService.update(graduationDesignTeachers)

                    // 同步关联数据
                    // step 2. 先取所有学生key
                    val listKey = CacheBook.GRADUATION_DESIGN_PHARMTECH_STUDENT_LIST + graduationDesignReleaseId
                    val keys = listOperations.range(listKey, 0, listOperations.size(listKey)!!)
                    for (key in keys) {
                        if (stringValueOperations.operations.hasKey(key)!!) {
                            val str = stringValueOperations.get(key)
                            if (StringUtils.hasLength(str)) {
                                val arr = str.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                                if (arr.size >= 2 && arr[0] != "-1") {
                                    // step 3. 存储关联信息
                                    val graduationDesignTutor = GraduationDesignTutor()
                                    graduationDesignTutor.graduationDesignTutorId = UUIDUtils.getUUID()
                                    graduationDesignTutor.graduationDesignTeacherId = arr[0]
                                    graduationDesignTutor.studentId = NumberUtils.toInt(arr[1])
                                    graduationDesignTutorService.save(graduationDesignTutor)
                                }
                            }
                        }
                    }
                    val dateTime = DateTime.now()
                    val s = dateTime.plusHours(4)
                    stringValueOperations.set(syncTimeKey, DateTimeUtils.formatDate(s.toDate()), CacheBook.EXPIRES_HOURS, TimeUnit.HOURS)
                    ajaxUtils.success().msg("同步完成")
                } else {
                    ajaxUtils.fail().msg("请等到 " + stringValueOperations.get(syncTimeKey) + " 后再进行同步")
                }
            } else {
                ajaxUtils.fail().msg("不在填报时间范围，无法进行操作")
            }
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 获取教师数据
     *
     * @param graduationDesignReleaseId 毕业发布id
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/adjustech/teacher/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun adjustTeacherData(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<GraduationDesignTeacherBean> {
        val ajaxUtils = AjaxUtils.of<GraduationDesignTeacherBean>()
        val graduationDesignTeacherBeens = graduationDesignTeacherService.findByGraduationDesignReleaseIdRelationForStaff(graduationDesignReleaseId)
        return ajaxUtils.success().msg("获取数据成功").listData(graduationDesignTeacherBeens)
    }

    /**
     * 获取学生数据
     *
     * @param graduationDesignTeacherId 毕业指导教师id
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/adjustech/student/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun adjustStudentData(@RequestParam("graduationDesignTeacherId") graduationDesignTeacherId: String): AjaxUtils<GraduationDesignTutorBean> {
        val ajaxUtils = AjaxUtils.of<GraduationDesignTutorBean>()
        var graduationDesignTutorBeens: List<GraduationDesignTutorBean> = ArrayList()
        val records = graduationDesignTutorService.findByGraduationDesignTeacherIdRelationForStudent(graduationDesignTeacherId)
        if (records.isNotEmpty) {
            graduationDesignTutorBeens = records.into(GraduationDesignTutorBean::class.java)
        }
        return ajaxUtils.success().msg("获取数据成功").listData(graduationDesignTutorBeens)
    }

    /**
     * 调整教师数据
     *
     * @param graduationDesignReleaseId 毕业发布id
     * @param graduationDesignTeacherId 毕业指导教师id
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/adjustech/teachers"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun adjustTeachers(@RequestParam("id") graduationDesignReleaseId: String,
                       @RequestParam("graduationDesignTeacherId") graduationDesignTeacherId: String): AjaxUtils<GraduationDesignTeacherBean> {
        val ajaxUtils = AjaxUtils.of<GraduationDesignTeacherBean>()
        val errorBean = graduationDesignConditionCommon.isOkTeacherAdjust(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val graduationDesignRelease = errorBean.data
            // 仅允许在填报时间之后调整
            if (DateTimeUtils.timestampAfterDecide(graduationDesignRelease!!.fillTeacherEndTime)) {
                val graduationDesignTeacherBeens = graduationDesignTeacherService.findByGraduationDesignReleaseIdRelationForStaff(graduationDesignReleaseId)
                if (graduationDesignTeacherId != "-1") {
                    graduationDesignTeacherBeens.forEach { graduationDesignTeacherBean ->
                        if (graduationDesignTeacherId == graduationDesignTeacherBean.graduationDesignTeacherId) {
                            graduationDesignTeacherBean.selected = true
                        }
                    }
                }
                ajaxUtils.success().msg("获取数据成功").listData(graduationDesignTeacherBeens)
            } else {
                ajaxUtils.fail().msg("请在填报时间结束后操作")
            }
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 获取已填报学生数据
     *
     * @param request 请求
     * @return 已填报学生数据
     */
    @RequestMapping(value = ["/web/graduate/design/adjustech/student/submit/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun fillData(request: HttpServletRequest): DataTablesUtils<GraduationDesignTutorBean> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("select")
        headers.add("student_name")
        headers.add("student_number")
        headers.add("organize_name")
        headers.add("staff_name")
        headers.add("operator")
        val dataTablesUtils = DataTablesUtils<GraduationDesignTutorBean>(request, headers)
        val condition = GraduationDesignTutorBean()
        val graduationDesignReleaseId = request.getParameter("graduationDesignReleaseId")
        if (!ObjectUtils.isEmpty(graduationDesignReleaseId)) {
            condition.graduationDesignReleaseId = request.getParameter("graduationDesignReleaseId")
            val graduationDesignTutorBeens = graduationDesignTutorService.findAllFillByPage(dataTablesUtils, condition)
            dataTablesUtils.data = graduationDesignTutorBeens
            dataTablesUtils.setiTotalRecords(graduationDesignTutorService.countAllFill(condition).toLong())
            dataTablesUtils.setiTotalDisplayRecords(graduationDesignTutorService.countFillByCondition(dataTablesUtils, condition).toLong())
        }
        return dataTablesUtils
    }

    /**
     * 获取未填报学生数据
     *
     * @param request 请求
     * @return 已填报学生数据
     */
    @RequestMapping(value = ["/web/graduate/design/adjustech/student/unsubmit/data"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun notFillData(request: HttpServletRequest): DataTablesUtils<StudentBean> {
        // 前台数据标题 注：要和前台标题顺序一致，获取order用
        val headers = ArrayList<String>()
        headers.add("select")
        headers.add("student_name")
        headers.add("student_number")
        headers.add("organize_name")
        headers.add("operator")
        val dataTablesUtils = DataTablesUtils<StudentBean>(request, headers)
        val graduationDesignReleaseId = request.getParameter("graduationDesignReleaseId")
        if (!ObjectUtils.isEmpty(graduationDesignReleaseId)) {
            val condition = graduationDesignReleaseService.findById(graduationDesignReleaseId)
            var studentBeens: List<StudentBean> = ArrayList()
            val records = graduationDesignTutorService.findAllNotFillByPage(dataTablesUtils, condition)
            if (records.isNotEmpty) {
                studentBeens = records.into(StudentBean::class.java)
            }
            dataTablesUtils.data = studentBeens
            dataTablesUtils.setiTotalRecords(graduationDesignTutorService.countAllNotFill(condition).toLong())
            dataTablesUtils.setiTotalDisplayRecords(graduationDesignTutorService.countNotFillByCondition(dataTablesUtils, condition).toLong())
        }
        return dataTablesUtils
    }

    /**
     * 查询学生志愿信息
     *
     * @param graduationDesignTutorId 毕业设计教师与学生关联id
     * @return 数据
     */
    @RequestMapping(value = ["/web/graduate/design/adjustech/student/wish"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun studentWish(@RequestParam("id") graduationDesignReleaseId: String,
                    @RequestParam("graduationDesignTutorId") graduationDesignTutorId: String): AjaxUtils<GraduationDesignHopeTutorBean> {
        val ajaxUtils = AjaxUtils.of<GraduationDesignHopeTutorBean>()
        val graduationDesignTutor = graduationDesignTutorService.findById(graduationDesignTutorId)
        if (!ObjectUtils.isEmpty(graduationDesignTutor)) {
            val records = graduationDesignHopeTutorService.findByStudentIdAndGraduationDesignReleaseIdRelationForStaff(graduationDesignTutor.studentId!!, graduationDesignReleaseId)
            var graduationDesignHopeTutorBeens: List<GraduationDesignHopeTutorBean> = ArrayList()
            if (records.isNotEmpty) {
                graduationDesignHopeTutorBeens = records.into(GraduationDesignHopeTutorBean::class.java)
            }
            ajaxUtils.success().msg("获取数据成功").listData(graduationDesignHopeTutorBeens)
        } else {
            ajaxUtils.fail().msg("未查询到相关信息")
        }
        return ajaxUtils
    }

    /**
     * 更新
     *
     * @param graduationDesignTutorUpdateVo 页面数据
     * @param bindingResult                 检验
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/adjustech/update"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun update(@Valid graduationDesignTutorUpdateVo: GraduationDesignTutorUpdateVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!bindingResult.hasErrors()) {
            val errorBean = graduationDesignConditionCommon.isOkTeacherAdjust(graduationDesignTutorUpdateVo.graduationDesignReleaseId!!)
            if (!errorBean.isHasError()) {
                val graduationDesignRelease = errorBean.data
                // 仅允许在填报时间之后调整
                if (DateTimeUtils.timestampAfterDecide(graduationDesignRelease!!.fillTeacherEndTime)) {
                    val graduationDesignTutorIds = SmallPropsUtils.StringIdsToStringList(graduationDesignTutorUpdateVo.graduationDesignTutorId!!)
                    graduationDesignTutorIds.forEach { graduationDesignTutorId ->
                        val graduationDesignTutor = graduationDesignTutorService.findById(graduationDesignTutorId)
                        graduationDesignTutor.graduationDesignTeacherId = graduationDesignTutorUpdateVo.graduationDesignTeacherId
                        graduationDesignTutorService.update(graduationDesignTutor)
                    }
                    ajaxUtils.success().msg("保存成功")
                } else {
                    ajaxUtils.fail().msg("请在填报时间结束后操作")
                }
            } else {
                ajaxUtils.fail().msg(errorBean.errorMsg!!)
            }
        } else {
            ajaxUtils.fail().msg("参数异常")
        }
        return ajaxUtils
    }

    /**
     * 保存
     *
     * @param graduationDesignTutorAddVo 页面数据
     * @param bindingResult              检验
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/adjustech/save"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun save(@Valid graduationDesignTutorAddVo: GraduationDesignTutorAddVo, bindingResult: BindingResult): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (!bindingResult.hasErrors()) {
            val errorBean = graduationDesignConditionCommon.isOkTeacherAdjust(graduationDesignTutorAddVo.graduationDesignReleaseId!!)
            if (!errorBean.isHasError()) {
                val graduationDesignRelease = errorBean.data
                // 仅允许在填报时间之后调整
                if (DateTimeUtils.timestampAfterDecide(graduationDesignRelease!!.fillTeacherEndTime)) {
                    if (SmallPropsUtils.StringIdsIsNumber(graduationDesignTutorAddVo.studentId!!)) {
                        val studentIds = SmallPropsUtils.StringIdsToList(graduationDesignTutorAddVo.studentId!!)
                        studentIds.forEach { studentId ->
                            val graduationDesignTutor = GraduationDesignTutor()
                            graduationDesignTutor.graduationDesignTutorId = UUIDUtils.getUUID()
                            graduationDesignTutor.studentId = studentId
                            graduationDesignTutor.graduationDesignTeacherId = graduationDesignTutorAddVo.graduationDesignTeacherId
                            graduationDesignTutorService.save(graduationDesignTutor)
                        }
                        ajaxUtils.success().msg("保存成功")
                    } else {
                        ajaxUtils.fail().msg("保存失败")
                    }
                } else {
                    ajaxUtils.fail().msg("请在填报时间结束后操作")
                }
            } else {
                ajaxUtils.fail().msg(errorBean.errorMsg!!)
            }
        } else {
            ajaxUtils.fail().msg("参数异常")
        }
        return ajaxUtils
    }

    /**
     * 删除
     *
     * @param graduationDesignReleaseId 毕业发布id
     * @param graduationDesignTutorIds  关联id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/adjustech/delete"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun delete(@RequestParam("id") graduationDesignReleaseId: String, graduationDesignTutorIds: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = graduationDesignConditionCommon.isOkTeacherAdjust(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val graduationDesignRelease = errorBean.data
            // 仅允许在填报时间之后调整
            if (DateTimeUtils.timestampAfterDecide(graduationDesignRelease!!.fillTeacherEndTime)) {
                graduationDesignTutorService.deleteByIds(SmallPropsUtils.StringIdsToStringList(graduationDesignTutorIds))
                ajaxUtils.success().msg("删除成功")
            } else {
                ajaxUtils.fail().msg("请在填报时间结束后操作")
            }
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }

    /**
     * 确认调整
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/adjustech/ok"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun adjustechOk(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = graduationDesignConditionCommon.isOkTeacherAdjust(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            val graduationDesignRelease = errorBean.data
            // 仅允许在填报时间之后调整
            if (DateTimeUtils.timestampAfterDecide(graduationDesignRelease!!.fillTeacherEndTime)) {
                graduationDesignRelease.isOkTeacherAdjust = 1
                graduationDesignReleaseService.update(graduationDesignRelease)
                ajaxUtils.success().msg("确认调整成功")
            } else {
                ajaxUtils.fail().msg("请在填报时间结束后操作")
            }
        }
        return ajaxUtils
    }

    /**
     * 进入页面判断条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = ["/web/graduate/design/adjustech/condition"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun canUse(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        val errorBean = graduationDesignConditionCommon.isOkTeacherAdjust(graduationDesignReleaseId)
        if (!errorBean.isHasError()) {
            ajaxUtils.success().msg("在条件范围，允许使用")
        } else {
            ajaxUtils.fail().msg(errorBean.errorMsg!!)
        }
        return ajaxUtils
    }
}