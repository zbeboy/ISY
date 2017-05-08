package top.zbeboy.isy.service.graduate.design;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.web.bean.graduate.design.teacher.GraduationDesignTeacherBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;

/**
 * Created by zbeboy on 2017/5/8.
 */
public interface GraduationDesignTeacherService {
    /**
     * 分页查询 数据
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    List<GraduationDesignTeacherBean> findAllByPage(DataTablesUtils<GraduationDesignTeacherBean> dataTablesUtils, GraduationDesignTeacherBean graduationDesignTeacherBean);

    /**
     * 数据 总数
     *
     * @return 总数
     */
    int countAll(GraduationDesignTeacherBean graduationDesignTeacherBean);

    /**
     * 根据条件查询总数 数据
     *
     * @return 条件查询总数
     */
    int countByCondition(DataTablesUtils<GraduationDesignTeacherBean> dataTablesUtils, GraduationDesignTeacherBean graduationDesignTeacherBean);
}
