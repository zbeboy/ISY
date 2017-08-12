package top.zbeboy.isy.glue.data;

import top.zbeboy.isy.glue.util.ResultUtils;
import top.zbeboy.isy.web.bean.data.organize.OrganizeBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;

/**
 * Created by lenovo on 2017-04-09.
 */
public interface OrganizeGlue {
    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    ResultUtils<List<OrganizeBean>> findAllByPage(DataTablesUtils<OrganizeBean> dataTablesUtils);

    /**
     * 班级总数
     *
     * @return 总数
     */
    long countAll();
}
