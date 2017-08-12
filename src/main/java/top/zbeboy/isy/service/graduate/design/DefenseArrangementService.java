package top.zbeboy.isy.service.graduate.design;

import org.jooq.Record;
import top.zbeboy.isy.domain.tables.pojos.DefenseArrangement;

import java.util.Optional;

/**
 * Created by lenovo on 2017-07-09.
 */
public interface DefenseArrangementService {

    /**
     * 根据主键查询
     *
     * @param id 主键
     * @return 数据
     */
    DefenseArrangement findById(String id);

    /**
     * 通过毕业设计发布id查询
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return 数据
     */
    Optional<Record> findByGraduationDesignReleaseId(String graduationDesignReleaseId);

    /**
     * 保存
     *
     * @param defenseArrangement 数据
     */
    void save(DefenseArrangement defenseArrangement);

    /**
     * 更新
     *
     * @param defenseArrangement 数据
     */
    void update(DefenseArrangement defenseArrangement);
}
