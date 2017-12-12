package top.zbeboy.isy.service.data

/**
 * Created by zbeboy 2017-12-12 .
 **/
interface ElasticSyncService {
    /**
     * 清理日志
     */
    fun cleanSystemLog()

    /**
     * 清理邮件日志
     */
    fun cleanSystemMailbox()

    /**
     * 清理短信日志
     */
    fun cleanSystemSms()

    /**
     * 同步班级数据
     */
    fun syncOrganizeData()

    /**
     * 同步用户数据
     */
    fun syncUsersData()

    /**
     * 同步学生数据
     */
    fun syncStudentData()

    /**
     * 同步教职工数据
     */
    fun syncStaffData()

    /**
     * 更新院级自定义角色名
     *
     * @param collegeId 院id
     * @param roleName  角色名
     */
    fun collegeRoleNameUpdate(collegeId: Int, roleName: String)

    /**
     * 更新系统级角色名
     *
     * @param roleName 角色名
     */
    fun systemRoleNameUpdate(roleName: String)
}