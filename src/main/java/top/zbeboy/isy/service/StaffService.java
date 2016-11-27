package top.zbeboy.isy.service;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.Staff;
import top.zbeboy.isy.domain.tables.records.StaffRecord;
import top.zbeboy.isy.web.bean.data.staff.StaffBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;
import java.util.Optional;

/**
 * Created by lenovo on 2016-08-28.
 */
public interface StaffService {

    /**
     * 通过id关联查询
     *
     * @param id 主键
     * @return 数据
     */
    Optional<Record> findByIdRelation(int id);

    /**
     * 根据工号查询
     *
     * @param staffNumber 工号
     * @return 教职工们
     */
    Staff findByStaffNumber(String staffNumber);

    /**
     * 根据系id查询
     *
     * @param departmentId 系id
     * @return 教职工们
     */
    Result<Record> findByDepartmentIdRelation(int departmentId);

    /**
     * 根据用户账号查询
     *
     * @param username 用户账号
     * @return 教职工
     */
    Staff findByUsername(String username);

    /**
     * 根据工号查询 注：不等于用户账号
     *
     * @param username    用户账号
     * @param staffNumber 工号
     * @return 教职工
     */
    Result<StaffRecord> findByStaffNumberNeUsername(String username, String staffNumber);

    /**
     * 通过身份证号查询 注：不等于用户账号
     *
     * @param username 用户账号
     * @param idCard   身份证号
     * @return 教职工
     */
    Result<StaffRecord> findByIdCardNeUsername(String username, String idCard);

    /**
     * 通过身份证号查询
     *
     * @param idCard 身份证号
     * @return 教职工
     */
    List<Staff> findByIdCard(String idCard);

    /**
     * 保存教职工信息
     *
     * @param staff 教职工
     */
    void save(Staff staff);

    /**
     * 更新教职式信息
     *
     * @param staff 教职工
     */
    void update(Staff staff);

    /**
     * 通过用户账号关联查询 注：信息包括学校等 建议用于验证，效率不高
     *
     * @param username 用户账号
     * @return 关联信息
     */
    Optional<Record> findByUsernameRelation(String username);

    /**
     * 通过账号删除
     *
     * @param username 用户账号
     */
    void deleteByUsername(String username);

    /**
     * 分页查询有权限的用户
     *
     * @param dataTablesUtils datatables工具类
     * @return 用户
     */
    Result<Record> findAllByPageExistsAuthorities(DataTablesUtils<StaffBean> dataTablesUtils);

    /**
     * 分页查询无权限的用户
     *
     * @param dataTablesUtils datatables工具类
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
     * @param dataTablesUtils datatables工具类
     * @return 数量
     */
    int countByConditionExistsAuthorities(DataTablesUtils<StaffBean> dataTablesUtils);

    /**
     * 根据条件统计无权限的用户
     *
     * @param dataTablesUtils datatables工具类
     * @return 数量
     */
    int countByConditionNotExistsAuthorities(DataTablesUtils<StaffBean> dataTablesUtils);
}
