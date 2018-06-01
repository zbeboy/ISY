package top.zbeboy.isy.web.graduate.design.common

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import top.zbeboy.isy.domain.tables.pojos.*
import top.zbeboy.isy.service.data.BuildingService
import top.zbeboy.isy.service.data.DepartmentService
import top.zbeboy.isy.service.data.OrganizeService
import top.zbeboy.isy.service.graduate.design.*
import top.zbeboy.isy.web.bean.graduate.design.teacher.GraduationDesignTeacherBean
import top.zbeboy.isy.web.util.AjaxUtils
import java.util.ArrayList
import javax.annotation.Resource

/**
 * Created by zbeboy 2018-01-14 .
 **/
@Controller
open class GraduationDesignControllerCommon {

    @Resource
    open lateinit var graduationDesignReleaseService: GraduationDesignReleaseService

    @Resource
    open lateinit var graduationDesignReleaseFileService: GraduationDesignReleaseFileService

    @Resource
    open lateinit var graduationDesignTeacherService: GraduationDesignTeacherService

    @Resource
    open lateinit var graduationDesignSubjectTypeService: GraduationDesignSubjectTypeService

    @Resource
    open lateinit var graduationDesignSubjectOriginTypeService: GraduationDesignSubjectOriginTypeService

    @Resource
    open lateinit var organizeService: OrganizeService

    @Resource
    open lateinit var departmentService: DepartmentService

    @Resource
    open lateinit var buildingService: BuildingService

    @Resource
    open lateinit var graduationDesignDatumTypeService: GraduationDesignDatumTypeService

    /**
     * 获取毕业设计发布附件数据
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return 毕业设计发布附件数据
     */
    @RequestMapping(value = ["/anyone/graduate/design/files"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun graduateDesignFiles(@RequestParam("graduationDesignReleaseId") graduationDesignReleaseId: String): AjaxUtils<Files> {
        val ajaxUtils = AjaxUtils.of<Files>()
        var files: List<Files> = ArrayList()
        val records = graduationDesignReleaseFileService.findByGraduationDesignReleaseId(graduationDesignReleaseId)
        if (records.isNotEmpty) {
            files = records.into(Files::class.java)
        }
        return ajaxUtils.success().msg("获取毕业设计附件数据成功").listData(files)
    }

    /**
     * 获取毕业设计 专业，年级下所有班级数据
     *
     * @param graduationDesignReleaseId 毕业发布id
     * @return 班级数据
     */
    @RequestMapping(value = ["/anyone/graduate/design/organizes"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun organizes(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<Organize> {
        val ajaxUtils = AjaxUtils.of<Organize>()
        val organizes = ArrayList<Organize>()
        val organize = Organize(0, "请选择班级", 0, 0, "")
        organizes.add(organize)
        val graduationDesignRelease = graduationDesignReleaseService.findById(graduationDesignReleaseId)
        val organizeRecords = organizeService.findByGradeAndScienceIdNotIsDel(graduationDesignRelease.allowGrade, graduationDesignRelease.scienceId!!)
        organizeRecords.mapTo(organizes) { Organize(it.organizeId, it.organizeName, it.organizeIsDel, it.scienceId, it.grade) }
        return ajaxUtils.success().msg("获取班级数据成功！").listData(organizes)
    }

    /**
     * 获取全部楼
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return 全部楼
     */
    @RequestMapping(value = ["/anyone/graduate/design/buildings"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun buildings(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<Building> {
        val graduationDesignRelease = graduationDesignReleaseService.findById(graduationDesignReleaseId)
        val buildings = ArrayList<Building>()
        val isDel: Byte = 0
        val building = Building(0, "请选择楼", isDel, 0)
        buildings.add(building)
        val record = departmentService.findByIdRelation(graduationDesignRelease.departmentId!!)
        if (record.isPresent) {
            val college = record.get().into(College::class.java)
            val buildingRecords = buildingService.findByCollegeIdAndIsDel(college.collegeId!!, isDel)
            buildingRecords.mapTo(buildings) { Building(it.buildingId, it.buildingName, it.buildingIsDel, it.collegeId) }
        }
        return AjaxUtils.of<Building>().success().msg("获取楼数据成功！").listData(buildings)
    }

    /**
     * 毕业设计指导教师数据
     *
     * @param graduationDesignReleaseId 毕业发布id
     * @return 数据
     */
    @RequestMapping(value = ["/anyone/graduate/design/teachers"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun teachers(@RequestParam("id") graduationDesignReleaseId: String): AjaxUtils<GraduationDesignTeacherBean> {
        val ajaxUtils = AjaxUtils.of<GraduationDesignTeacherBean>()
        val graduationDesignTeacherBeens = ArrayList<GraduationDesignTeacherBean>()
        val graduationDesignTeacherBean = GraduationDesignTeacherBean()
        graduationDesignTeacherBean.staffId = 0
        graduationDesignTeacherBean.staffRealName = "全部"
        graduationDesignTeacherBean.staffMobile = ""
        graduationDesignTeacherBeens.add(graduationDesignTeacherBean)
        graduationDesignTeacherBeens.addAll(graduationDesignTeacherService.findByGraduationDesignReleaseIdRelationForStaff(graduationDesignReleaseId))
        ajaxUtils.success().msg("获取数据成功").listData(graduationDesignTeacherBeens)
        return ajaxUtils
    }

    /**
     * 获取题目类型
     *
     * @return 数据
     */
    @RequestMapping(value = ["/anyone/graduate/design/subject/types"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun subjectTypes(): AjaxUtils<GraduationDesignSubjectType> {
        val ajaxUtils = AjaxUtils.of<GraduationDesignSubjectType>()
        val graduationDesignSubjectTypes = ArrayList<GraduationDesignSubjectType>()
        val graduationDesignSubjectType = GraduationDesignSubjectType(0, "题目类型")
        graduationDesignSubjectTypes.add(graduationDesignSubjectType)
        graduationDesignSubjectTypes.addAll(graduationDesignSubjectTypeService.findAll())
        return ajaxUtils.success().msg("获取数据成功").listData(graduationDesignSubjectTypes)
    }

    /**
     * 获取课题来源
     *
     * @return 数据
     */
    @RequestMapping(value = ["/anyone/graduate/design/subject/origin_types"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun subjectOriginTypes(): AjaxUtils<GraduationDesignSubjectOriginType> {
        val ajaxUtils = AjaxUtils.of<GraduationDesignSubjectOriginType>()
        val graduationDesignSubjectOriginTypes = ArrayList<GraduationDesignSubjectOriginType>()
        val graduationDesignSubjectOriginType = GraduationDesignSubjectOriginType(0, "课题来源")
        graduationDesignSubjectOriginTypes.add(graduationDesignSubjectOriginType)
        graduationDesignSubjectOriginTypes.addAll(graduationDesignSubjectOriginTypeService.findAll())
        return ajaxUtils.success().msg("获取数据成功").listData(graduationDesignSubjectOriginTypes)
    }

    /**
     * 获取文件类型
     *
     * @return 数据
     */
    @RequestMapping(value = ["/anyone/graduate/design/proposal/datums"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun datumTypes(): AjaxUtils<GraduationDesignDatumType> {
        val ajaxUtils = AjaxUtils.of<GraduationDesignDatumType>()
        val graduationDesignDatumTypes = ArrayList<GraduationDesignDatumType>()
        val graduationDesignDatumType = GraduationDesignDatumType(0, "文件类型")
        graduationDesignDatumTypes.add(graduationDesignDatumType)
        graduationDesignDatumTypes.addAll(graduationDesignDatumTypeService.findAll())
        return ajaxUtils.success().msg("获取数据成功").listData(graduationDesignDatumTypes)
    }
}