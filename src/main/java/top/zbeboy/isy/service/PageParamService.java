package top.zbeboy.isy.service;

import org.springframework.ui.ModelMap;

/**
 * Created by lenovo on 2016-10-15.
 */
public interface PageParamService {

    /**
     * 当前用户的角色名与院id
     *
     * @param modelMap 页面对象
     */
    void currentUserRoleNameAndCollegeIdPageParam(ModelMap modelMap);
}
