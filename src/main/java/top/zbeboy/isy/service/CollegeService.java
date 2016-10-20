package top.zbeboy.isy.service;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.College;
import top.zbeboy.isy.domain.tables.records.CollegeRecord;
import top.zbeboy.isy.web.bean.data.college.CollegeBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;

/**
 * Created by lenovo on 2016-08-21.
 */
public interface CollegeService {

    /**
     * 通过学校id查询全部院
     *
     * @param schoolId 学校id
     * @return 该学校下的全部院
     */
    Result<CollegeRecord> findBySchoolId(int schoolId);

    /**
     * 分页查询
     *
     * @param dataTablesUtils
     * @return 分页数据
     */
    Result<Record> findAllByPage(DataTablesUtils<CollegeBean> dataTablesUtils);

    /**
     * 院总数
     *
     * @return 总数
     */
    int countAll();

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    int countByCondition(DataTablesUtils<CollegeBean> dataTablesUtils);

    /**
     * 学校下 院名查询 注：等于院名
     *
     * @param collegeName 院名
     * @param schoolId    学校id
     * @return 数据
     */
    Result<CollegeRecord> findByCollegeNameAndSchoolId(String collegeName, int schoolId);

    /**
     * 保存
     *
     * @param college 院
     */
    void save(College college);

    /**
     * 更新
     *
     * @param college
     */
    void update(College college);

    /**
     * 通过id更新is_del状态
     *
     * @param ids
     * @param isDel
     */
    void updateIsDel(List<Integer> ids, Byte isDel);

    /**
     * 通过id查询院
     *
     * @param id 院id
     * @return 院
     */
    College findById(int id);

    /**
     * 查找学校下不等于该院id的院名
     *
     * @param collegeName 院名
     * @param collegeId   院id
     * @param schoolId    学校id
     * @return 院
     */
    Result<CollegeRecord> findByCollegeNameAndSchoolIdNeCollegeId(String collegeName, int collegeId, int schoolId);
}
