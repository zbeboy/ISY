package top.zbeboy.isy.web.common

import org.springframework.stereotype.Component
import org.springframework.ui.ModelMap
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.domain.tables.pojos.*
import top.zbeboy.isy.elastic.config.ElasticBook
import top.zbeboy.isy.elastic.repository.StaffElasticRepository
import top.zbeboy.isy.elastic.repository.StudentElasticRepository
import top.zbeboy.isy.elastic.repository.UsersElasticRepository
import top.zbeboy.isy.service.cache.CacheManageService
import top.zbeboy.isy.service.common.CommonControllerMethodService
import top.zbeboy.isy.service.data.*
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.system.AuthoritiesService
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.SmallPropsUtils
import java.util.*
import javax.annotation.Resource
import javax.servlet.http.HttpServletRequest

/**
 * Created by zbeboy 2017-12-07 .
 **/
@Component
open class MethodControllerCommon {

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var roleService: RoleService

    @Resource
    open lateinit var departmentService: DepartmentService

    @Resource
    open lateinit var buildingService: BuildingService

    /**
     * 通过毕业设计发布 生成楼数据
     *
     * @param graduationDesignRelease 毕业设计发布
     * @return 楼
     */
    fun generateBuildFromGraduationDesignRelease(graduationDesignRelease: GraduationDesignRelease): List<Building> {
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
        return buildings
    }

    /**
     * 组装提示信息
     *
     * @param modelMap 页面对象
     * @param tip      提示内容
     */
    fun showTip(modelMap: ModelMap, tip: String): String {
        modelMap.addAttribute("showTip", true)
        modelMap.addAttribute("tip", tip)
        modelMap.addAttribute("showButton", true)
        modelMap.addAttribute("buttonText", "返回上一页")
        return Workbook.TIP_PAGE
    }

    /**
     * 如果是管理员则获取院id，如果是普通学生或教职工角色则获取系id
     *
     * @return 根据角色返回相应数据
     */
    fun adminOrNormalData(): Map<String, Int> {
        val map = HashMap<String, Int>()
        if (roleService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            val users = usersService.getUserFromSession()
            val record = usersService.findUserSchoolInfo(users!!)
            val collegeId = roleService.getRoleCollegeId(record)
            map.put("collegeId", collegeId)
        } else if (!roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
            val users = usersService.getUserFromSession()
            val record = usersService.findUserSchoolInfo(users!!)
            val departmentId = roleService.getRoleDepartmentId(record)
            map.put("departmentId", departmentId)
        }
        return map
    }
}