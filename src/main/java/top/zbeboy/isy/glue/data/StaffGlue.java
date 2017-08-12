package top.zbeboy.isy.glue.data;

import top.zbeboy.isy.glue.util.ResultUtils;
import top.zbeboy.isy.web.bean.data.staff.StaffBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;

/**
 * Created by lenovo on 2017-04-12.
 */
public interface StaffGlue {
    /**
     * 分页查询有权限的用户
     *
     * @param dataTablesUtils datatables工具类
     * @return 用户
     */
    ResultUtils<List<StaffBean>> findAllByPageExistsAuthorities(DataTablesUtils<StaffBean> dataTablesUtils);

    /**
     * 分页查询无权限的用户
     *
     * @param dataTablesUtils datatables工具类
     * @return 用户
     */
    ResultUtils<List<StaffBean>> findAllByPageNotExistsAuthorities(DataTablesUtils<StaffBean> dataTablesUtils);

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
