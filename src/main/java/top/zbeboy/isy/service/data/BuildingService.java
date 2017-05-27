package top.zbeboy.isy.service.data;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.web.bean.data.building.BuildingBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

/**
 * Created by zbeboy on 2017/5/27.
 */
public interface BuildingService {

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    Result<Record> findAllByPage(DataTablesUtils<BuildingBean> dataTablesUtils);

    /**
     * 总数
     *
     * @return 总数
     */
    int countAll();

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    int countByCondition(DataTablesUtils<BuildingBean> dataTablesUtils);
}
