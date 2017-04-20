package top.zbeboy.isy.service.data;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.Student;
import top.zbeboy.isy.domain.tables.records.StudentRecord;
import top.zbeboy.isy.elastic.pojo.StudentElastic;
import top.zbeboy.isy.web.bean.data.student.StudentBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;
import java.util.Optional;

/**
 * Created by lenovo on 2016-08-22.
 */
public interface StudentService {

    /**
     * 通过id查询
     *
     * @param id 主键
     * @return 学生
     */
    Student findById(int id);

    /**
     * 通过id关联查询
     *
     * @param id 主键
     * @return 数据
     */
    Optional<Record> findByIdRelation(int id);

    /**
     * 通过id关联查询 (注：只关联users表)
     *
     * @param id 主键
     * @return 数据
     */
    Optional<Record> findByIdRelationForUsers(int id);

    /**
     * 根据学号查询学生
     *
     * @param studentNumber 学号
     * @return 学生
     */
    Student findByStudentNumber(String studentNumber);

    /**
     * 根据班级id集合查询有权限未注销的学生
     *
     * @param organizeIds 班级ids
     * @param b           用户状态
     * @return 学生们
     */
    Result<Record> findInOrganizeIdsAndEnabledExistsAuthorities(List<Integer> organizeIds, Byte b);

    /**
     * 通过账号与系id查询
     *
     * @param username     账号
     * @param departmentId 系id
     * @return 数据
     */
    Optional<Record> findByUsernameAndDepartmentId(String username, int departmentId);

    /**
     * 通过账号与系id查询
     *
     * @param studentNumber 学号
     * @param departmentId  系id
     * @return 数据
     */
    Optional<Record> findByStudentNumberAndDepartmentId(String studentNumber, int departmentId);

    /**
     * 根据学号查询 注：不等于用户账号
     *
     * @param username      用户账号
     * @param studentNumber 学号
     * @return 学生
     */
    Result<StudentRecord> findByStudentNumberNeUsername(String username, String studentNumber);

    /**
     * 根据身份证号号查询 注：不等于用户账号
     *
     * @param username 用户账号
     * @param idCard   身份证号
     * @return 学生
     */
    Result<StudentRecord> findByIdCardNeUsername(String username, String idCard);

    /**
     * 根据身份证号号查询
     *
     * @param idCard 身份证号
     * @return 学生
     */
    List<Student> findByIdCard(String idCard);

    /**
     * 保存学生信息
     *
     * @param studentElastic 学生
     */
    void save(StudentElastic studentElastic);

    /**
     * 更新学生信息
     *
     * @param student 学生
     */
    void update(Student student);

    /**
     * 通过用户账号关联查询 注：信息包括学校等 建议用于验证，效率不高
     *
     * @param username 用户账号
     * @return 关联信息
     */
    Optional<Record> findByUsernameRelation(String username);

    /**
     * 通过用户账号查询
     *
     * @param username 用户账号
     * @return 学生
     */
    Student findByUsername(String username);

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
    Result<Record> findAllByPageExistsAuthorities(DataTablesUtils<StudentBean> dataTablesUtils);

    /**
     * 分页查询无权限的用户
     *
     * @param dataTablesUtils datatables工具类
     * @return 用户
     */
    Result<Record> findAllByPageNotExistsAuthorities(DataTablesUtils<StudentBean> dataTablesUtils);

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
    int countByConditionExistsAuthorities(DataTablesUtils<StudentBean> dataTablesUtils);

    /**
     * 根据条件统计无权限的用户
     *
     * @param dataTablesUtils datatables工具类
     * @return 数量
     */
    int countByConditionNotExistsAuthorities(DataTablesUtils<StudentBean> dataTablesUtils);
}
