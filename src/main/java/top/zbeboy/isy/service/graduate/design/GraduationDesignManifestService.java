package top.zbeboy.isy.service.graduate.design;

import top.zbeboy.isy.web.bean.graduate.design.declare.GraduationDesignDeclareBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;

/**
 * Created by lenovo on 2017-08-13.
 */
public interface GraduationDesignManifestService {

    /**
     * 毕业设计清单分页查询 数据
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    List<GraduationDesignDeclareBean> findAllManifestByPage(DataTablesUtils<GraduationDesignDeclareBean> dataTablesUtils, GraduationDesignDeclareBean graduationDesignDeclareBean);

    /**
     * 毕业设计清单数据 总数
     *
     * @return 总数
     */
    int countAllManifest(GraduationDesignDeclareBean graduationDesignDeclareBean);

    /**
     * 毕业设计清单根据条件查询总数 数据
     *
     * @return 条件查询总数
     */
    int countManifestByCondition(DataTablesUtils<GraduationDesignDeclareBean> dataTablesUtils, GraduationDesignDeclareBean graduationDesignDeclareBean);

    /**
     * 导出毕业设计清单
     *
     * @param dataTablesUtils             datatables工具类
     * @param graduationDesignDeclareBean 条件
     * @return 导出数据
     */
    List<GraduationDesignDeclareBean> exportManifestData(DataTablesUtils<GraduationDesignDeclareBean> dataTablesUtils, GraduationDesignDeclareBean graduationDesignDeclareBean);

}
