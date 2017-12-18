package top.zbeboy.isy.web.platform.common

import org.springframework.stereotype.Component
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import top.zbeboy.isy.config.Workbook
import top.zbeboy.isy.service.cache.CacheManageService
import top.zbeboy.isy.service.data.StaffService
import top.zbeboy.isy.service.data.StudentService
import top.zbeboy.isy.service.platform.UsersService
import top.zbeboy.isy.service.system.AuthoritiesService
import top.zbeboy.isy.web.util.AjaxUtils
import top.zbeboy.isy.web.util.SmallPropsUtils
import javax.annotation.Resource

/**
 * Created by zbeboy 2017-12-18 .
 **/
@Component
open class UsersControllerCommon {

    @Resource
    open lateinit var usersService: UsersService

    @Resource
    open lateinit var authoritiesService: AuthoritiesService

    @Resource
    open lateinit var cacheManageService: CacheManageService

    @Resource
    open lateinit var studentService: StudentService

    @Resource
    open lateinit var staffService: StaffService

    /**
     * 更新用户状态
     *
     * @param userIds ids
     * @param enabled 状态
     * @return true 成功 false 失败
     */
    fun usersUpdateEnabled(userIds: String, enabled: Byte?): AjaxUtils<*> {
        if (StringUtils.hasLength(userIds)) {
            usersService.updateEnabled(SmallPropsUtils.StringIdsToStringList(userIds), enabled)
            return AjaxUtils.of<Any>().success().msg("注销用户成功")
        }
        return AjaxUtils.of<Any>().fail().msg("注销用户失败")
    }

    /**
     * 删除无角色关联的用户
     *
     * @param userIds 用户账号
     * @return true 成功 false 失败
     */
    fun deleteUsers(userIds: String): AjaxUtils<*> {
        val ajaxUtils = AjaxUtils.of<Any>()
        if (StringUtils.hasLength(userIds)) {
            val ids = SmallPropsUtils.StringIdsToStringList(userIds)
            loop@ for (id in ids) {
                val authoritiesRecords = authoritiesService.findByUsername(id)
                if (!ObjectUtils.isEmpty(authoritiesRecords) && !authoritiesRecords.isEmpty()) {
                    ajaxUtils.fail().msg("用户'$id'存在角色关联，无法删除")
                    break
                } else {
                    val users = usersService.findByUsername(id)
                    if (!ObjectUtils.isEmpty(users)) {
                        val usersType = cacheManageService.findByUsersTypeId(users!!.usersTypeId)
                        when (usersType.usersTypeName) {
                            Workbook.STUDENT_USERS_TYPE  // 学生
                            -> {
                                studentService.deleteByUsername(id)
                                usersService.deleteById(id)
                                ajaxUtils.success().msg("删除用户成功")
                            }
                            Workbook.STAFF_USERS_TYPE  // 教职工
                            -> {
                                staffService.deleteByUsername(id)
                                usersService.deleteById(id)
                                ajaxUtils.success().msg("删除用户成功")
                            }
                            else -> {
                                ajaxUtils.fail().msg("未获取到用户'$id'类型")
                                break@loop
                            }
                        }
                    } else {
                        ajaxUtils.fail().msg("未查询到用户'$id'")
                        break
                    }
                }
            }
        } else {
            ajaxUtils.fail().msg("用户账号为空")
        }
        return ajaxUtils
    }
}