package top.zbeboy.isy.service;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.InternshipApply;
import top.zbeboy.isy.web.bean.internship.apply.InternshipApplyBean;
import top.zbeboy.isy.web.bean.internship.release.InternshipReleaseBean;
import top.zbeboy.isy.web.bean.internship.review.InternshipReviewBean;
import top.zbeboy.isy.web.util.PaginationUtils;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * Created by lenovo on 2016-11-29.
 */
public interface InternshipApplyService {

    /**
     * 通过id查询
     *
     * @param id 主键
     * @return 实习申请
     */
    InternshipApply findById(String id);

    /**
     * 通过实习id与学生id查询
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @return 数据
     */
    Optional<Record> findByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId);

    /**
     * 保存
     *
     * @param internshipApply 实习申请
     */
    void save(InternshipApply internshipApply);

    /**
     * 更新
     *
     * @param internshipApply 实习申请
     */
    void update(InternshipApply internshipApply);

    /**
     * 通过实习发布id与申请状态更新状态 定时任务
     *
     * @param internshipReleaseId 实习发布id
     * @param changeState         当前状态
     * @param updateState         新状态
     */
    void updateStateWithInternshipReleaseIdAndState(String internshipReleaseId, int changeState, int updateState);

    /**
     * 更改超过信息填写时间的申请状态为申请中
     *
     * @param changeFillEndTime 填写结束时间
     * @param changeState       当前状态
     * @param updateState       新状态
     */
    void updateStateByChangeFillEndTime(Timestamp changeFillEndTime, int changeState, int updateState);

    /**
     * 通过实习id与学生id查询
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     */
    void deleteByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId);

    /**
     * 分页查询全部
     *
     * @param paginationUtils     分页工具
     * @param internshipApplyBean 额外参数
     * @return 分页数据
     */
    Result<Record> findAllByPage(PaginationUtils paginationUtils, InternshipApplyBean internshipApplyBean);

    /**
     * 处理实习返回数据
     *
     * @param paginationUtils     分页工具
     * @param records             数据
     * @param internshipApplyBean 额外参数
     * @return 处理后的数据
     */
    List<InternshipApplyBean> dealData(PaginationUtils paginationUtils, Result<Record> records, InternshipApplyBean internshipApplyBean);

    /**
     * 根据条件统计
     *
     * @param paginationUtils     分页工具
     * @param internshipApplyBean 额外参数
     * @return 统计
     */
    int countByCondition(PaginationUtils paginationUtils, InternshipApplyBean internshipApplyBean);
}
