package top.zbeboy.isy.service.internship;

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
     * 通过实习发布id 和指导教师id查询 学生信息
     *
     * @param internshipReleaseId 实习发布id
     * @param staffId             指导教师id
     * @return 数据
     */
    Result<Record> findByInternshipReleaseIdAndStaffIdForStudent(String internshipReleaseId, int staffId);

    /**
     * 通过实习发布id 和学生id查询 指导教师
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @return 数据
     */
    Optional<Record> findByInternshipReleaseIdAndStudentIdForStaff(String internshipReleaseId, int studentId);

    /**
     * 通过实习发布id 和教职工id查询
     *
     * @param internshipReleaseId 实习发布id
     * @param staffId             教职工id
     * @return 数据
     */
    Result<InternshipTeacherDistributionRecord> findByInternshipReleaseIdAndStaffId(String internshipReleaseId, int staffId);

    /**
     * 为批量分配查询学生数据
     *
     * @param organizeIds         专业id
     * @param internshipReleaseId 实习发布id 集合
     * @param b 用户状态
     * @return 数据
     */
    Result<Record> findStudentForBatchDistributionEnabled(List<Integer> organizeIds, List<String> internshipReleaseId, Byte b);

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
     * 通过实习发布id删除
     *
     * @param internshipReleaseId 实习发布id
     */
    void deleteByInternshipReleaseId(String internshipReleaseId);

    /**
     * 通过比对其它实习学生id删除
     *
     * @param internshipReleaseId 实习发布id
     * @param excludeInternships  其它实习id
     */
    void comparisonDel(String internshipReleaseId, List<String> excludeInternships);

    /**
     * 删除未申请学生的分配
     *
     * @param internshipReleaseId 实习发布Id
     */
    void deleteNotApply(String internshipReleaseId);

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
