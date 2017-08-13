package top.zbeboy.isy.service.graduate.design;

import org.jooq.Record1;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignDeclare;
import top.zbeboy.isy.domain.tables.records.GraduationDesignDeclareRecord;
import top.zbeboy.isy.web.bean.graduate.design.declare.GraduationDesignDeclareBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;

/**
 * Created by zbeboy on 2017/6/8.
 */
public interface GraduationDesignDeclareService {

    /**
     * 根据毕业设计学生题目表查询
     *
     * @param graduationDesignPresubjectId 毕业设计学生题目表id
     * @return 申报数据
     */
    GraduationDesignDeclareRecord findByGraduationDesignPresubjectId(String graduationDesignPresubjectId);

    /**
     * 分页查询 数据
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    List<GraduationDesignDeclareBean> findAllByPage(DataTablesUtils<GraduationDesignDeclareBean> dataTablesUtils, GraduationDesignDeclareBean graduationDesignDeclareBean);

    /**
     * 数据 总数
     *
     * @return 总数
     */
    int countAll(GraduationDesignDeclareBean graduationDesignDeclareBean);

    /**
     * 根据条件查询总数 数据
     *
     * @return 条件查询总数
     */
    int countByCondition(DataTablesUtils<GraduationDesignDeclareBean> dataTablesUtils, GraduationDesignDeclareBean graduationDesignDeclareBean);

    /**
     * 导出
     *
     * @param dataTablesUtils             datatables工具类
     * @param graduationDesignDeclareBean 条件
     * @return 导出数据
     */
    List<GraduationDesignDeclareBean> exportData(DataTablesUtils<GraduationDesignDeclareBean> dataTablesUtils, GraduationDesignDeclareBean graduationDesignDeclareBean);

    /**
     * 保存 或 更新 根据题目id判断
     *
     * @param graduationDesignDeclare 数据
     */
    void saveOrUpdate(GraduationDesignDeclare graduationDesignDeclare);

    /**
     * 保存 或 更新 状态
     *
     * @param graduationDesignDeclare 数据
     */
    void saveOrUpdateState(GraduationDesignDeclare graduationDesignDeclare);

    /**
     * 查询该指导教师下所有未申报的题目id
     *
     * @param staffId                   教职工 id
     * @param graduationDesignReleaseId 发布id
     * @return 数据
     */
    Result<Record1<String>> findByStaffIdRelationNeIsOkApply(int staffId, String graduationDesignReleaseId);
}
