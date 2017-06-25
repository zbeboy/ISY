package top.zbeboy.isy.service.graduate.design;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignDatum;
import top.zbeboy.isy.web.bean.graduate.design.proposal.GraduationDesignDatumBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.Optional;

/**
 * Created by zbeboy on 2017/6/23.
 */
public interface GraduationDesignDatumService {

    /**
     * 通过教职工与学生关联表id与文件类型id查询
     *
     * @param graduationDesignTutorId     教职工与学生关联表id
     * @param graduationDesignDatumTypeId 文件类型id
     * @return 资料
     */
    Optional<Record> findByGraduationDesignTutorIdAndGraduationDesignDatumTypeId(String graduationDesignTutorId, int graduationDesignDatumTypeId);

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

    /**
     * 更新
     *
     * @param graduationDesignDatum 数据
     */
    void update(GraduationDesignDatum graduationDesignDatum);

    /**
     * 保存
     *
     * @param graduationDesignDatum 数据
     */
    void save(GraduationDesignDatum graduationDesignDatum);
}
