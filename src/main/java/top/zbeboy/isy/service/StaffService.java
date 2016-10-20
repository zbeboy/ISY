package top.zbeboy.isy.service;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.Staff;
import top.zbeboy.isy.web.bean.data.staff.StaffBean;
import top.zbeboy.isy.web.bean.data.student.StudentBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;
import java.util.Optional;

/**
 * Created by lenovo on 2016-08-28.
 */
public interface StaffService {

    /**
     * 根据工号查询
     *
     * @param staffNumber 工号
     * @return 教职工们
     */
    List<Staff> findByStaffNumber(String staffNumber);

    /**
     * 保存教职工信息
     *
     * @param staff
     */
    void save(Staff staff);

    /**
     * 通过用户账号关联查询 注：信息包括学校等 建议用于验证，效率不高
     * @param username 用户账号
     * @return 关联信息
     */
    Optional<Record> findByUsernameRelation(String username);

    /**
     * 通过账号删除
     * @param username 用户账号
     */
    void deleteByUsername(String username);

    /**
     * 分页查询有权限的用户
     *
     * @param dataTablesUtils
     * @return 用户
     */
    Result<Record> findAllByPageExistsAuthorities(DataTablesUtils<StaffBean> dataTablesUtils);

    /**
     * 分页查询无权限的用户
     *
     * @param dataTablesUtils
     * @return 用户
     */
    Result<Record> findAllByPageNotExistsAuthorities(DataTablesUtils<StaffBean> dataTablesUtils);

    /**
     * 统计有权限的用户
     *
     * @return 总数
     */
    int countAllExistsAuthorities();

    /**
     * 统计无权限的用户
     *
     * @return 总数
     */
    int countAllNotExistsAuthorities();

    /**
     * 根据条件统计有权限的用户
     *
     * @param dataTablesUtils
     * @return 数量
     */
    int countByConditionExistsAuthorities(DataTablesUtils<StaffBean> dataTablesUtils);

    /**
     * 根据条件统计无权限的用户
     *
     * @param dataTablesUtils
     * @return 数量
     */
    int countByConditionNotExistsAuthorities(DataTablesUtils<StaffBean> dataTablesUtils);
}
