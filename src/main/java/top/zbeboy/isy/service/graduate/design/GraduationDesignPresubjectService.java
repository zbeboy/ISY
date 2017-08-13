package top.zbeboy.isy.service.graduate.design;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignPresubject;
import top.zbeboy.isy.domain.tables.records.GraduationDesignPresubjectRecord;
import top.zbeboy.isy.web.bean.graduate.design.subject.GraduationDesignPresubjectBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;
import java.util.Optional;

/**
 * Created by zbeboy on 2017/6/5.
 */
public interface GraduationDesignPresubjectService {

    /**
     * 根据主键查询
     *
     * @param id 主键
     * @return 数据
     */
    GraduationDesignPresubject findById(String id);

    /**
     * 通过题目查询
     *
     * @param presubjectTitle 题目
     * @return 数据
     */
    List<GraduationDesignPresubject> findByPresubjectTitle(String presubjectTitle);

    /**
     * 根据主键关联查询
     *
     * @param id 主键
     * @return 数据
     */
    Optional<Record> findByIdRelation(String id);

    /**
     * 通过学生id与毕业设计发布id查询
     *
     * @param studentId                 学生id
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return 数据
     */
    GraduationDesignPresubjectRecord findByStudentIdAndGraduationDesignReleaseId(int studentId, String graduationDesignReleaseId);

    /**
     * 通过毕业设计发布id查询
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return 数据
     */
    Result<GraduationDesignPresubjectRecord> findByGraduationDesignReleaseId(String graduationDesignReleaseId);

    /**
     * 保存
     *
     * @param graduationDesignPresubject 数据
     */
    void save(GraduationDesignPresubject graduationDesignPresubject);

    /**
     * 更新
     *
     * @param graduationDesignPresubject 数据
     */
    void update(GraduationDesignPresubject graduationDesignPresubject);

    /**
     * 分页查询 数据
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    Result<Record> findAllByPage(DataTablesUtils<GraduationDesignPresubjectBean> dataTablesUtils, GraduationDesignPresubjectBean graduationDesignPresubjectBean);

    /**
     * 数据 总数
     *
     * @return 总数
     */
    int countAll(GraduationDesignPresubjectBean graduationDesignPresubjectBean);

    /**
     * 根据条件查询总数 数据
     *
     * @return 条件查询总数
     */
    int countByCondition(DataTablesUtils<GraduationDesignPresubjectBean> dataTablesUtils, GraduationDesignPresubjectBean graduationDesignPresubjectBean);
}
