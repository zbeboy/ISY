package top.zbeboy.isy.service;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.Student;
import top.zbeboy.isy.web.bean.data.student.StudentBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;
import java.util.Optional;

/**
 * Created by lenovo on 2016-08-22.
 */
public interface StudentService {

    /**
     * 根据学号查询学生
     *
     * @param studentNumber 学号
     * @return 学生们
     */
    List<Student> findByStudentNumber(String studentNumber);

    /**
     * 保存学生信息
     *
     * @param student
     */
    void save(Student student);

    /**
     * 更新学生信息
     *
     * @param student
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
     * 通过账号删除
     *
     * @param username 用户账号
     */
    void deleteByUsername(String username);

    /**
     * 分页查询有权限的用户
     *
     * @param dataTablesUtils
     * @return 用户
     */
    Result<Record> findAllByPageExistsAuthorities(DataTablesUtils<StudentBean> dataTablesUtils);

    /**
     * 分页查询无权限的用户
     *
     * @param dataTablesUtils
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
     * @param dataTablesUtils
     * @return 数量
     */
    int countByConditionExistsAuthorities(DataTablesUtils<StudentBean> dataTablesUtils);

    /**
     * 根据条件统计无权限的用户
     *
     * @param dataTablesUtils
     * @return 数量
     */
    int countByConditionNotExistsAuthorities(DataTablesUtils<StudentBean> dataTablesUtils);
}
