package top.zbeboy.isy.service;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.School;
import top.zbeboy.isy.domain.tables.records.SchoolRecord;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;

/**
 * Created by lenovo on 2016-08-21.
 */
public interface SchoolService {

    /**
     * 查询全部学校
     *
     * @return 全部学校
     */
    Result<SchoolRecord> findAll();

    /**
     * 保存
     *
     * @param school
     */
    void save(School school);

    /**
     * 更新
     *
     * @param school
     */
    void update(School school);

    /**
     * 分页查询
     *
     * @param dataTablesUtils
     * @return 分页数据
     */
    Result<Record> findAllByPage(DataTablesUtils<School> dataTablesUtils);

    /**
     * 学校总数
     *
     * @return 总数
     */
    int countAll();

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    int countByCondition(DataTablesUtils<School> dataTablesUtils);

    /**
     * 根据学校名查询 注：等于学校名
     *
     * @param schoolName 学校名
     * @return 数据
     */
    List<School> findBySchoolName(String schoolName);

    /**
     * 查找不等于该学校id的学校名
     *
     * @param schoolName 学校名
     * @param schoolId   学校id
     * @return 数据
     */
    Result<SchoolRecord> findBySchoolNameNeSchoolId(String schoolName, int schoolId);

    /**
     * 通过id更新is_del状态
     *
     * @param ids
     * @param isDel
     */
    void updateIsDel(List<Integer> ids, Byte isDel);

    /**
     * 通过id查询学校
     *
     * @param id
     * @return 学校
     */
    School findById(int id);
}
