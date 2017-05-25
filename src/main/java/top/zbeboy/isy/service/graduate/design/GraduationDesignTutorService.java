package top.zbeboy.isy.service.graduate.design;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignRelease;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignTutor;
import top.zbeboy.isy.web.bean.data.student.StudentBean;
import top.zbeboy.isy.web.bean.graduate.design.pharmtech.GraduationDesignTutorBean;
import top.zbeboy.isy.web.bean.graduate.design.release.GraduationDesignReleaseBean;
import top.zbeboy.isy.web.util.DataTablesUtils;

import java.util.List;
import java.util.Optional;

/**
 * Created by lenovo on 2017-05-20.
 */
public interface GraduationDesignTutorService {

    /**
     * 通过主键查询
     *
     * @param id 主键
     * @return 结果
     */
    GraduationDesignTutor findById(String id);

    /**
     * 通过学生id与发布id查询指导教师信息
     *
     * @param studentId                 学生id
     * @param graduationDesignReleaseId 毕业发布id
     * @return 指导教师信息
     */
    Optional<Record> findByStudentIdAndGraduationDesignReleaseIdRelationForStaff(int studentId, String graduationDesignReleaseId);

    /**
     * 通过指导教师id与发布id关联查询学生信息
     *
     * @param graduationDesignTeacherId 指导教师id
     * @param graduationDesignReleaseId 毕业发布id
     * @return 数据
     */
    Result<Record> findByGraduationDesignTeacherIdAndGraduationDesignReleaseIdRelationForStudent(String graduationDesignTeacherId, String graduationDesignReleaseId);

    /**
     * 统计未填报学生数
     *
     * @param graduationDesignReleaseBean 毕业发布
     * @return 学生数
     */
    int countNotFillStudent(GraduationDesignReleaseBean graduationDesignReleaseBean);

    /**
     * 统计填报学生数
     *
     * @param graduationDesignReleaseBean 毕业发布
     * @return 学生数
     */
    int countFillStudent(GraduationDesignReleaseBean graduationDesignReleaseBean);

    /**
     * 根据 指导教师id删除
     *
     * @param graduationDesignTeacherId 指导教师id
     */
    void deleteByGraduationDesignTeacherId(String graduationDesignTeacherId);

    /**
     * 保存
     *
     * @param graduationDesignTutor 数据
     */
    void save(GraduationDesignTutor graduationDesignTutor);

    /**
     * 更新
     *
     * @param graduationDesignTutor 数据
     */
    void update(GraduationDesignTutor graduationDesignTutor);

    /**
     * 根据主键删除
     *
     * @param ids 主键
     */
    void deleteByIds(List<String> ids);

    /**
     * 已填报学生数据
     *
     * @param dataTablesUtils datatables 工具
     * @return 数据
     */
    List<GraduationDesignTutorBean> findAllFillByPage(DataTablesUtils<GraduationDesignTutorBean> dataTablesUtils, GraduationDesignTutorBean condition);

    /**
     * 统计已填报学生
     *
     * @return 结果
     */
    int countAllFill(GraduationDesignTutorBean condition);

    /**
     * 根据条件统计已填报学生
     *
     * @param dataTablesUtils datatables 工具
     * @return 结果
     */
    int countFillByCondition(DataTablesUtils<GraduationDesignTutorBean> dataTablesUtils, GraduationDesignTutorBean condition);

    /**
     * 未填报学生数据
     *
     * @param dataTablesUtils datatables 工具
     * @return 数据
     */
    Result<Record> findAllNotFillByPage(DataTablesUtils<StudentBean> dataTablesUtils, GraduationDesignRelease condition);

    /**
     * 统计未填报学生
     *
     * @return 结果
     */
    int countAllNotFill(GraduationDesignRelease condition);

    /**
     * 根据条件统计未填报学生
     *
     * @param dataTablesUtils datatables 工具
     * @return 结果
     */
    int countNotFillByCondition(DataTablesUtils<StudentBean> dataTablesUtils, GraduationDesignRelease condition);
}
