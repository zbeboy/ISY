package top.zbeboy.isy.service.graduate.design;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.web.bean.graduate.design.release.GraduationDesignReleaseBean;
import top.zbeboy.isy.web.util.PaginationUtils;

import java.util.List;

/**
 * Created by zbeboy on 2017/5/5.
 */
public interface GraduationDesignReleaseService {

    /**
     * 分页查询全部
     *
     * @param paginationUtils             分页工具
     * @param graduationDesignReleaseBean 额外参数
     * @return 分页数据
     */
    Result<Record> findAllByPage(PaginationUtils paginationUtils, GraduationDesignReleaseBean graduationDesignReleaseBean);

    /**
     * 处理实习返回数据
     *
     * @param paginationUtils             分页工具
     * @param records                     数据
     * @param graduationDesignReleaseBean 额外参数
     * @return 处理后的数据
     */
    List<GraduationDesignReleaseBean> dealData(PaginationUtils paginationUtils, Result<Record> records, GraduationDesignReleaseBean graduationDesignReleaseBean);
}
