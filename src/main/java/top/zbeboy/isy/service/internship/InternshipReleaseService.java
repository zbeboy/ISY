package top.zbeboy.isy.service.internship;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.InternshipRelease;
import top.zbeboy.isy.domain.tables.records.InternshipReleaseRecord;
import top.zbeboy.isy.web.bean.internship.release.InternshipReleaseBean;
import top.zbeboy.isy.web.util.PaginationUtils;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * Created by lenovo on 2016-11-12.
 */
public interface InternshipReleaseService {

    /**
     * 通过实习结束时间查询
     *
     * @param endTime 实习结束时间
     * @return 实习
     */
    Result<InternshipReleaseRecord> findByEndTime(Timestamp endTime);

    /**
     * 通过id查询
     *
     * @param internshipReleaseId 实习id
     * @return 实习
     */
    InternshipRelease findById(String internshipReleaseId);

    /**
     * 通过id关联查询查询
     *
     * @param internshipReleaseId 实习id
     * @return 实习
     */
    Optional<Record> findByIdRelation(String internshipReleaseId);

    /**
     * 通过标题查询
     *
     * @param releaseTitle 实习标题
     * @return 实习
     */
    List<InternshipRelease> findByReleaseTitle(String releaseTitle);

    /**
     * 通过标题查询
     *
     * @param releaseTitle        实习标题
     * @param internshipReleaseId 实习id
     * @return 实习
     */
    Result<InternshipReleaseRecord> findByReleaseTitleNeInternshipReleaseId(String releaseTitle, String internshipReleaseId);

    /**
     * 保存
     *
     * @param internshipRelease 实习
     */
    void save(InternshipRelease internshipRelease);

    /**
     * 更新
     *
     * @param internshipRelease 实习
     */
    void update(InternshipRelease internshipRelease);

    /**
     * 分页查询全部
     *
     * @param paginationUtils       分页工具
     * @param internshipReleaseBean 额外参数
     * @return 分页数据
     */
    Result<Record> findAllByPage(PaginationUtils paginationUtils, InternshipReleaseBean internshipReleaseBean);

    /**
     * 处理实习返回数据
     *
     * @param paginationUtils       分页工具
     * @param records               数据
     * @param internshipReleaseBean 额外参数
     * @return 处理后的数据
     */
    List<InternshipReleaseBean> dealData(PaginationUtils paginationUtils, Result<Record> records, InternshipReleaseBean internshipReleaseBean);

    /**
     * 根据条件统计
     *
     * @param paginationUtils       分页工具
     * @param internshipReleaseBean 额外参数
     * @return 统计
     */
    int countByCondition(PaginationUtils paginationUtils, InternshipReleaseBean internshipReleaseBean);
}
