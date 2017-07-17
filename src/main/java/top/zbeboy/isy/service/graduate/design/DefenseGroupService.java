package top.zbeboy.isy.service.graduate.design;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.DefenseGroup;
import top.zbeboy.isy.web.bean.graduate.design.replan.DefenseGroupBean;

import java.util.List;
import java.util.Optional;

/**
 * Created by zbeboy on 2017/7/11.
 */
public interface DefenseGroupService {

    /**
     * 通过主键查询
     *
     * @param id 主键
     * @return 组
     */
    DefenseGroup findById(String id);

    /**
     * 根据主键关联查询
     *
     * @param id 主键
     * @return 数据
     */
    Optional<Record> findByIdRelation(String id);

    /**
     * 通过毕业设计安排id查询
     *
     * @param defenseArrangementId 毕业设计安排id
     * @return 数据
     */
    List<DefenseGroupBean> findByDefenseArrangementId(String defenseArrangementId);

    /**
     * 通过毕业设计发布id查询
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return 数据
     */
    Result<Record> findByGraduationDesignReleaseId(String graduationDesignReleaseId);

    /**
     * 保存
     *
     * @param defenseGroup 组
     */
    void save(DefenseGroup defenseGroup);

    /**
     * 更新
     *
     * @param defenseGroup 组
     */
    void update(DefenseGroup defenseGroup);

    /**
     * 通过主键删除
     *
     * @param id 主键
     */
    void deleteById(String id);
}
