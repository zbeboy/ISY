package top.zbeboy.isy.service;

import org.jooq.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.domain.tables.pojos.Users;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * Created by lenovo on 2016-10-15.
 */
@Service("pageParamService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class PageParamServiceImpl implements PageParamService {

    private final Logger log = LoggerFactory.getLogger(PageParamServiceImpl.class);

    @Resource
    private UsersService usersService;

    @Resource
    private AuthoritiesService authoritiesService;

    /**
     * 页面参数
     * @param modelMap
     */
    public void currentUserRoleNameAndCollegeIdPageParam(ModelMap modelMap){
        if (authoritiesService.isCurrentUserInRole(Workbook.SYSTEM_AUTHORITIES)) {
            modelMap.addAttribute("currentUserRoleName", Workbook.SYSTEM_ROLE_NAME);
        } else if (authoritiesService.isCurrentUserInRole(Workbook.ADMIN_AUTHORITIES)) {
            modelMap.addAttribute("currentUserRoleName", Workbook.ADMIN_ROLE_NAME);
            Users users = usersService.getUserFromSession();
            Optional<Record> record = usersService.findUserSchoolInfo(users);
            int collegeId = authoritiesService.getRoleCollegeId(record);
            modelMap.addAttribute("collegeId",collegeId);
        }
    }
}
