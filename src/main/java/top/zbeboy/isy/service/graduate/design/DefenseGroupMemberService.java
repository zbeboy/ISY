package top.zbeboy.isy.service.graduate.design;

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
     * 通过组id删除
     *
     * @param defenseGroupId 组id
     */
    void deleteByDefenseGroupId(String defenseGroupId);
}
