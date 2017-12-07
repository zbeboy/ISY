package top.zbeboy.isy.web.common

import org.springframework.stereotype.Component
import org.springframework.ui.ModelMap
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.domain.tables.pojos.Building
import top.zbeboy.isy.domain.tables.pojos.College
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignRelease
import top.zbeboy.isy.service.data.BuildingService
import top.zbeboy.isy.service.data.DepartmentService
import top.zbeboy.isy.service.platform.RoleService
import top.zbeboy.isy.service.platform.UsersService
import java.util.ArrayList
import javax.annotation.Resource

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
     * 根据角色获取院id
     *
     * @param collegeId 页面院id
     */
    fun roleCollegeId(collegeId: Int?): Int? {
        return if (!roleService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) { // 管理员或其它角色
            val users = usersService.getUserFromSession()
            val record = usersService.findUserSchoolInfo(users!!)
            roleService.getRoleCollegeId(record)
        } else {
            collegeId
        }
    }

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
}