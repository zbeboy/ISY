package top.zbeboy.isy.service;

import org.springframework.ui.ModelMap;
import top.zbeboy.isy.web.bean.internship.release.InternshipReleaseBean;

/**
 * Created by lenovo on 2016-10-15.
 */
public interface CommonControllerMethodService {

    /**
     * 当前用户的角色名与院id
     *
     * @param modelMap 页面对象
     */
    void currentUserRoleNameAndCollegeIdPageParam(ModelMap modelMap);

    /**
     * 获取实习数据 判断角色
     *
     * @param internshipReleaseBean 实习发布
     */
    void accessRoleCondition(InternshipReleaseBean internshipReleaseBean);
}
