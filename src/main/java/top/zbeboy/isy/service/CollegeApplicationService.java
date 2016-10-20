package top.zbeboy.isy.service;

import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.CollegeApplication;
import top.zbeboy.isy.domain.tables.records.CollegeApplicationRecord;

/**
 * Created by lenovo on 2016-10-05.
 */
public interface CollegeApplicationService {

    /**
     * 通过应用id删除
     * @param applicationId 应用id
     */
    void deleteByApplicationId(int applicationId);

    /**
     * 通过院id查询
     * @param collegeId 院id
     * @return 数据
     */
    Result<CollegeApplicationRecord> findByCollegeId(int collegeId);

    /**
     * 保存
     * @param collegeApplication 表数据
     */
    void save(CollegeApplication collegeApplication);

    /**
     * 通过院id删除
     * @param collegeId 院id
     */
    void deleteByCollegeId(int collegeId);
}
