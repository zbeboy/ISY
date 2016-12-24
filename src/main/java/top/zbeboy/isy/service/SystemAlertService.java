package top.zbeboy.isy.service;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.web.bean.system.alert.SystemAlertBean;
import top.zbeboy.isy.web.util.PaginationUtils;

import java.util.List;

/**
 * Created by lenovo on 2016-12-24.
 */
public interface SystemAlertService {

    /**
     * 分页查询全部
     *
     * @param paginationUtils 分页工具
     * @param systemAlertBean 额外参数
     * @return 分页数据
     */
    Result<Record> findAllByPage(PaginationUtils paginationUtils, SystemAlertBean systemAlertBean);

    /**
     * 处理返回数据
     *
     * @param paginationUtils 分页工具
     * @param records         数据
     * @param systemAlertBean 额外参数
     * @return 处理后的数据
     */
    List<SystemAlertBean> dealData(PaginationUtils paginationUtils, Result<Record> records, SystemAlertBean systemAlertBean);

    /**
     * 根据条件统计
     *
     * @param paginationUtils 分页工具
     * @param systemAlertBean 额外参数
     * @return 统计
     */
    int countByCondition(PaginationUtils paginationUtils, SystemAlertBean systemAlertBean);
}
