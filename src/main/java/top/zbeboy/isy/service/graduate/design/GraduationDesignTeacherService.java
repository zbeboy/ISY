package top.zbeboy.isy.service.graduate.design;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignTeacher;
import top.zbeboy.isy.web.bean.graduate.design.teacher.GraduationDesignTeacherBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;

/**
 * Created by zbeboy on 2017/5/8.
 */
public interface GraduationDesignTeacherService {


    /**
     * 查询指导教师数据
     *
     * @param departmentId              系id
     * @param b                         用户状态
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return 数据
     */
    Result<Record> findByDepartmentIdAndEnabledRelationExistsAuthoritiesNotExistsDesignTeacher(int departmentId, Byte b, String graduationDesignReleaseId);

    /**
     * 根据毕业设计发布id删除
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     */
    void deleteByGraduationDesignReleaseId(String graduationDesignReleaseId);

    /**
     * 保存
     *
     * @param graduationDesignTeacher 数据
     */
    void save(GraduationDesignTeacher graduationDesignTeacher);

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
