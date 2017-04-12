package top.zbeboy.isy.glue.platform;

import top.zbeboy.isy.glue.util.ResultUtils;
import top.zbeboy.isy.web.bean.platform.users.UsersBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;

/**
 * Created by lenovo on 2017-04-11.
 */
public interface UsersGlue {
    /**
     * 分页查询有权限的用户
     *
     * @param dataTablesUtils datatables工具类
     * @return 用户
     */
    ResultUtils<List<UsersBean>> findAllByPageExistsAuthorities(DataTablesUtils<UsersBean> dataTablesUtils);

    /**
     * 分页查询无权限的用户
     *
     * @param dataTablesUtils datatables工具类
     * @return 用户
     */
    ResultUtils<List<UsersBean>> findAllByPageNotExistsAuthorities(DataTablesUtils<UsersBean> dataTablesUtils);

    /**
     * 统计有权限的用户
     *
     * @return 总数
     */
    long countAllExistsAuthorities();

    /**
     * 统计无权限的用户
     *
     * @return 总数
     */
    long countAllNotExistsAuthorities();
}
