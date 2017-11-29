package top.zbeboy.isy.service.data;

import org.jooq.Record;
import top.zbeboy.isy.domain.tables.pojos.CollegeRole;
import top.zbeboy.isy.domain.tables.records.CollegeRoleRecord;

import java.util.List;
import java.util.Optional;

/**
 * Created by lenovo on 2016-10-12.
 */
public interface CollegeRoleService {

    /**
     * 通过学院Id查询
     *
     * @param collegeId 学院id
     * @return 数据
     */
    List<CollegeRoleRecord> findByCollegeId(int collegeId);

    /**
     * 通过学院Id和允许代理字段查询
     *
     * @param collegeId  学院id
     * @param allowAgent 允许代理字段
     * @return 数据
     */
    List<CollegeRoleRecord> findByCollegeIdAndAllowAgent(int collegeId, Byte allowAgent);

    /**
     * 通过角色Id查询
     *
     * @param roleId 角色id
     * @return 数据
     */
    Optional<Record> findByRoleId(String roleId);

    /**
     * 保存
     *
     * @param collegeRole 院与角色
     */
    void save(CollegeRole collegeRole);

    /**
     * 更新
     *
     * @param collegeRole 院与角色
     */
    void update(CollegeRole collegeRole);

    /**
     * 通过角色id删除
     *
     * @param roleId 角色id
     */
    void deleteByRoleId(String roleId);
}
