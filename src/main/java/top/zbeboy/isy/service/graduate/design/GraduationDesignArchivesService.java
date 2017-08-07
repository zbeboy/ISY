package top.zbeboy.isy.service.graduate.design;

import top.zbeboy.isy.web.bean.graduate.design.archives.GraduationDesignArchivesBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;

/**
 * Created by lenovo on 2017-08-06.
 */
public interface GraduationDesignArchivesService {

    /**
     * 毕业设计归档分页查询 数据
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    List<GraduationDesignArchivesBean> findAllByPage(DataTablesUtils<GraduationDesignArchivesBean> dataTablesUtils, GraduationDesignArchivesBean graduateArchivesBean);

    /**
     * 毕业设计归档数据 总数
     *
     * @return 总数
     */
    int countAll(GraduationDesignArchivesBean graduateArchivesBean);

    /**
     * 毕业设计归档根据条件查询总数 数据
     *
     * @return 条件查询总数
     */
    int countByCondition(DataTablesUtils<GraduationDesignArchivesBean> dataTablesUtils, GraduationDesignArchivesBean graduateArchivesBean);

    /**
     * 导出
     *
     * @param dataTablesUtils              datatables工具类
     * @param graduationDesignArchivesBean 条件
     * @return 导出数据
     */
    List<GraduationDesignArchivesBean> exportData(DataTablesUtils<GraduationDesignArchivesBean> dataTablesUtils, GraduationDesignArchivesBean graduationDesignArchivesBean);

}
