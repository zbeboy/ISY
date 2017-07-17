package top.zbeboy.isy.service.graduate.design;

import top.zbeboy.isy.domain.tables.pojos.DefenseGroupMember;
import top.zbeboy.isy.domain.tables.records.DefenseGroupMemberRecord;
import top.zbeboy.isy.web.bean.graduate.design.teacher.GraduationDesignTeacherBean;

import java.util.List;

/**
 * Created by zbeboy on 2017/7/12.
 */
public interface DefenseGroupMemberService {

    /**
     * 查询指导教师
     *
     * @param condition 查询条件
     * @return 数据
     */
    List<GraduationDesignTeacherBean> findByGraduationDesignReleaseIdRelationForStaff(GraduationDesignTeacherBean condition);

    /**
     * 通过毕业设计指导教师id查询
     *
     * @param graduationDesignTeacherId 毕业设计指导教师id
     * @return 数据
     */
    DefenseGroupMemberRecord findByGraduationDesignTeacherId(String graduationDesignTeacherId);

    /**
     * 通过组id删除
     *
     * @param defenseGroupId 组id
     */
    void deleteByDefenseGroupId(String defenseGroupId);

    /**
     * 保存
     *
     * @param defenseGroupMember 组成员
     */
    void save(DefenseGroupMember defenseGroupMember);
}
