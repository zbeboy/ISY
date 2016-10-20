package top.zbeboy.isy.service;

import top.zbeboy.isy.domain.tables.pojos.CollegeRole;
import top.zbeboy.isy.domain.tables.records.CollegeRoleRecord;

import java.util.List;

/**
 * Created by lenovo on 2016-10-12.
 */
public interface CollegeRoleService {

    /**
     * 通过学院Id查询
     * @param collegeId 学院id
     * @return 数据
     */
    List<CollegeRoleRecord> findByCollegeId(int collegeId);

    /**
     * 保存
     * @param collegeRole
     */
    void save(CollegeRole collegeRole);

    /**
     * 通过角色id删除
     * @param roleId 角色id
     */
    void deleteByRoleId(int roleId);
}
