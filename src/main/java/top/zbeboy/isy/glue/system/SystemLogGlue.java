package top.zbeboy.isy.glue.system;

import top.zbeboy.isy.elastic.pojo.SystemLogElastic;
import top.zbeboy.isy.glue.util.ResultUtils;
import top.zbeboy.isy.web.bean.system.log.SystemLogBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;

/**
 * Created by lenovo on 2017-03-27.
 */
public interface SystemLogGlue {

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    ResultUtils<List<SystemLogBean>> findAllByPage(DataTablesUtils<SystemLogBean> dataTablesUtils);

    /**
     * 系统日志总数
     *
     * @return 总数
     */
    long countAll();

    /**
     * 保存
     *
     * @param systemLogElastic 系统日志
     */
    void save(SystemLogElastic systemLogElastic);
}
