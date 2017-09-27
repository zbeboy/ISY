package top.zbeboy.isy.service.graduate.design;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignDatumGroup;
import top.zbeboy.isy.web.bean.graduate.design.proposal.GraduationDesignDatumGroupBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

public interface GraduationDesignDatumGroupService {

    /**
     * 通过主键查询
     *
     * @param id 主键
     * @return 组内资料
     */
    GraduationDesignDatumGroup findById(String id);

    /**
     * 保存
     *
     * @param graduationDesignDatumGroup 数据
     */
    void save(GraduationDesignDatumGroup graduationDesignDatumGroup);

    /**
     * 删除
     *
     * @param graduationDesignDatumGroup 数据
     */
    void delete(GraduationDesignDatumGroup graduationDesignDatumGroup);

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    Result<Record> findAllByPage(DataTablesUtils<GraduationDesignDatumGroupBean> dataTablesUtils, GraduationDesignDatumGroupBean graduationDesignDatumGroupBean);

    /**
     * 总数
     *
     * @return 总数
     */
    int countAll(GraduationDesignDatumGroupBean graduationDesignDatumGroupBean);

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    int countByCondition(DataTablesUtils<GraduationDesignDatumGroupBean> dataTablesUtils, GraduationDesignDatumGroupBean graduationDesignDatumGroupBean);
}
