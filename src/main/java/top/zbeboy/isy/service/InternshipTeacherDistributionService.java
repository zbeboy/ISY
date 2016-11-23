package top.zbeboy.isy.service;

import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.InternshipTeacherDistribution;
import top.zbeboy.isy.domain.tables.records.InternshipTeacherDistributionRecord;
import top.zbeboy.isy.web.bean.internship.distribution.InternshipTeacherDistributionBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;
import java.util.Optional;

/**
 * Created by zbeboy on 2016/11/21.
 */
public interface InternshipTeacherDistributionService {

    /**
     * 通过实习发布id distinct查询班级id
     *
     * @param internshipReleaseId 实习发布id
     * @return 数据
     */
    Result<Record1<Integer>> findByInternshipReleaseIdDistinctOrganizeId(String internshipReleaseId);

    /**
     * 通过实习发布id 和学生id查询
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @return 数据
     */
    Optional<Record> findByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId);

    /**
     * 保存
     *
     * @param internshipTeacherDistribution 教师分配
     */
    void save(InternshipTeacherDistribution internshipTeacherDistribution);

    /**
     * 更新
     *
     * @param internshipTeacherDistribution 教师分配
     */
    void updateStaffId(InternshipTeacherDistribution internshipTeacherDistribution);

    /**
     * 通过实习发布id 和 学生id删除
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     */
    void deleteByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId);

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    List<InternshipTeacherDistributionBean> findAllByPage(DataTablesUtils<InternshipTeacherDistributionBean> dataTablesUtils, String internshipReleaseId);

    /**
     * 总数
     *
     * @return 总数
     */
    int countAll(String internshipReleaseId);

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    int countByCondition(DataTablesUtils<InternshipTeacherDistributionBean> dataTablesUtils, String internshipReleaseId);
}
