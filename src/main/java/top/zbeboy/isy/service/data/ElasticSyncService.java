package top.zbeboy.isy.service.data;

/**
 * Created by zhaoyin on 17-8-8.
 */
public interface ElasticSyncService {

    /**
     * 清理日志
     */
    void cleanSystemLog();

    /**
     * 清理邮件日志
     */
    void cleanSystemMailbox();

    /**
     * 清理短信日志
     */
    void cleanSystemSms();

    /**
     * 同步班级数据
     */
    void syncOrganizeData();

    /**
     * 同步用户数据
     */
    void syncUsersData();

    /**
     * 同步学生数据
     */
    void syncStudentData();

    /**
     * 同步教职工数据
     */
    void syncStaffData();

    /**
     * 更新院级自定义角色名
     *
     * @param collegeId 院id
     * @param roleName  角色名
     */
    void collegeRoleNameUpdate(int collegeId, String roleName);

    /**
     * 更新系统级角色名
     *
     * @param roleName 角色名
     */
    void systemRoleNameUpdate(String roleName);
}
