package top.zbeboy.isy.service.graduate.design;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.web.bean.graduate.design.proposal.GraduationDesignDatumBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

/**
 * Created by zbeboy on 2017/6/23.
 */
public interface GraduationDesignDatumService {

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    Result<Record> findAllByPage(DataTablesUtils<GraduationDesignDatumBean> dataTablesUtils, GraduationDesignDatumBean graduationDesignDatumBean);

    /**
     * 总数
     *
     * @return 总数
     */
    int countAll(GraduationDesignDatumBean graduationDesignDatumBean);

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    int countByCondition(DataTablesUtils<GraduationDesignDatumBean> dataTablesUtils, GraduationDesignDatumBean graduationDesignDatumBean);
}
