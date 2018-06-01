package top.zbeboy.isy.service.data

import org.jooq.Result
import top.zbeboy.isy.domain.tables.pojos.CollegeApplication
import top.zbeboy.isy.domain.tables.records.CollegeApplicationRecord

/**
 * Created by zbeboy 2017-12-02 .
 **/
interface CollegeApplicationService {
    /**
     * 通过应用id删除
     *
     * @param applicationId 应用id
     */
    fun deleteByApplicationId(applicationId: String)

    /**
     * 通过院id查询
     *
     * @param collegeId 院id
     * @return 数据
     */
    fun findByCollegeId(collegeId: Int): Result<CollegeApplicationRecord>

    /**
     * 保存
     *
     * @param collegeApplication 表数据
     */
    fun save(collegeApplication: CollegeApplication)

    /**
     * 通过院id删除
     *
     * @param collegeId 院id
     */
    fun deleteByCollegeId(collegeId: Int)
}