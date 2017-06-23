package top.zbeboy.isy.service.graduate.design;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignRelease;
import top.zbeboy.isy.domain.tables.records.GraduationDesignReleaseRecord;
import top.zbeboy.isy.web.bean.error.ErrorBean;
import top.zbeboy.isy.web.bean.graduate.design.release.GraduationDesignReleaseBean;
import top.zbeboy.isy.web.util.PaginationUtils;

import java.util.List;
import java.util.Optional;

/**
 * Created by zbeboy on 2017/5/5.
 */
public interface GraduationDesignReleaseService {

    /**
     * 通过主键查询
     *
     * @param id 主键
     * @return 毕业设计发布
     */
    GraduationDesignRelease findById(String id);

    /**
     * 通过主键关联查询
     *
     * @param id 主键
     * @return 关联数据
     */
    Optional<Record> findByIdRelation(String id);

    /**
     * 通过标题查询
     *
     * @param graduationDesignTitle 毕业设计标题
     * @return 毕业设计
     */
    List<GraduationDesignRelease> findByGraduationDesignTitle(String graduationDesignTitle);

    /**
     * 通过标题查询
     *
     * @param graduationDesignTitle     毕业设计标题
     * @param graduationDesignReleaseId 毕业设计id
     * @return 毕业设计
     */
    Result<GraduationDesignReleaseRecord> findByGraduationDesignTitleNeGraduationDesignReleaseId(String graduationDesignTitle, String graduationDesignReleaseId);

    /**
     * 分页查询全部
     *
     * @param paginationUtils             分页工具
     * @param graduationDesignReleaseBean 额外参数
     * @return 分页数据
     */
    Result<Record> findAllByPage(PaginationUtils paginationUtils, GraduationDesignReleaseBean graduationDesignReleaseBean);

    /**
     * 处理实习返回数据
     *
     * @param paginationUtils             分页工具
     * @param records                     数据
     * @param graduationDesignReleaseBean 额外参数
     * @return 处理后的数据
     */
    List<GraduationDesignReleaseBean> dealData(PaginationUtils paginationUtils, Result<Record> records, GraduationDesignReleaseBean graduationDesignReleaseBean);

    /**
     * 保存
     *
     * @param graduationDesignRelease 数据
     */
    void save(GraduationDesignRelease graduationDesignRelease);

    /**
     * 更新
     *
     * @param graduationDesignRelease 数据
     */
    void update(GraduationDesignRelease graduationDesignRelease);

    /**
     * 基础条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return 条件
     */
    ErrorBean<GraduationDesignRelease> basicCondition(String graduationDesignReleaseId);
}
