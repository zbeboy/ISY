package top.zbeboy.isy.web.internship.common

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import top.zbeboy.isy.domain.tables.pojos.Files
import top.zbeboy.isy.domain.tables.pojos.InternshipType
import top.zbeboy.isy.domain.tables.pojos.Organize
import top.zbeboy.isy.domain.tables.pojos.Science
import top.zbeboy.isy.service.data.OrganizeService
import top.zbeboy.isy.service.internship.InternshipFileService
import top.zbeboy.isy.service.internship.InternshipReleaseScienceService
import top.zbeboy.isy.service.internship.InternshipTypeService
import top.zbeboy.isy.web.util.AjaxUtils
import java.util.*
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-12-18 .
 **/
@Controller
open class InternshipControllerCommon {

    @Resource
    open lateinit var internshipTypeService: InternshipTypeService

    @Resource
    open lateinit var internshipFileService: InternshipFileService

    @Resource
    open lateinit var internshipReleaseScienceService: InternshipReleaseScienceService

    @Resource
    open lateinit var organizeService: OrganizeService

    /**
     * 获取实习类型数据
     *
     * @return 实习类型数据
     */
    @RequestMapping(value = ["/anyone/internship/types"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun internshipTypes(): AjaxUtils<InternshipType> {
        val ajaxUtils = AjaxUtils.of<InternshipType>()
        val internshipTypes = ArrayList<InternshipType>()
        val internshipType = InternshipType(0, "请选择实习类型")
        internshipTypes.add(internshipType)
        internshipTypes.addAll(internshipTypeService.findAll())
        return ajaxUtils.success().msg("获取实习类型数据成功").listData(internshipTypes)
    }

    /**
     * 获取实习附件数据
     *
     * @param internshipReleaseId 实习发布id
     * @return 实习附件数据
     */
    @RequestMapping(value = ["/anyone/internship/files"], method = [(RequestMethod.GET)])
    @ResponseBody
    fun internshipFiles(@RequestParam("internshipReleaseId") internshipReleaseId: String): AjaxUtils<Files> {
        val ajaxUtils = AjaxUtils.of<Files>()
        var files: List<Files> = ArrayList()
        val records = internshipFileService.findByInternshipReleaseId(internshipReleaseId)
        if (records.isNotEmpty) {
            files = records.into(Files::class.java)
        }
        return ajaxUtils.success().msg("获取实习附件数据成功").listData(files)
    }

    /**
     * 获取专业数据
     *
     * @param internshipReleaseId 实习发布id
     * @return 专业数据
     */
    @RequestMapping(value = ["/anyone/internship/sciences"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun sciences(@RequestParam("internshipReleaseId") internshipReleaseId: String): AjaxUtils<Science> {
        val ajaxUtils = AjaxUtils.of<Science>()
        val sciences = ArrayList<Science>()
        val science = Science()
        science.scienceId = 0
        science.scienceName = "请选择专业"
        sciences.add(science)
        val records = internshipReleaseScienceService.findByInternshipReleaseIdRelation(internshipReleaseId)
        if (records.isNotEmpty) {
            sciences.addAll(records.into(Science::class.java))
        }
        return ajaxUtils.success().msg("获取专业数据成功").listData(sciences)
    }

    /**
     * 获取班级数据
     *
     * @param scienceId 专业id
     * @return 班级
     */
    @RequestMapping(value = ["/anyone/internship/organizes"], method = [(RequestMethod.POST)])
    @ResponseBody
    fun organizes(@RequestParam("scienceId") scienceId: Int): AjaxUtils<Organize> {
        val ajaxUtils = AjaxUtils.of<Organize>()
        val organizes = ArrayList<Organize>()
        val organize = Organize()
        organize.organizeId = 0
        organize.organizeName = "请选择班级"
        organizes.add(organize)
        organizes.addAll(organizeService.findByScienceId(scienceId))
        return ajaxUtils.success().msg("获取班级数据成功").listData(organizes)
    }
}