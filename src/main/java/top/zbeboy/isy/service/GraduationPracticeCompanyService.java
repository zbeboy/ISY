package top.zbeboy.isy.service;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.GraduationPracticeCompany;
import top.zbeboy.isy.web.util.DataTablesUtils;
import top.zbeboy.isy.web.vo.internship.apply.GraduationPracticeCompanyVo;

import java.util.Optional;

/**
 * Created by lenovo on 2016-11-27.
 */
public interface GraduationPracticeCompanyService {

    /**
     * 通过id查询
     *
     * @param id 主键
     * @return 毕业实习(校外)
     */
    GraduationPracticeCompany findById(String id);

    /**
     * 通过实习发布id与学生id查询
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     * @return 数据
     */
    Optional<Record> findByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId);

    /**
     * 保存
     *
     * @param graduationPracticeCompany 毕业实习(校外)
     */
    void save(GraduationPracticeCompany graduationPracticeCompany);

    /**
     * 开启事务保存
     *
     * @param graduationPracticeCompanyVo 毕业实习(校外)
     */
    void saveWithTransaction(GraduationPracticeCompanyVo graduationPracticeCompanyVo);

    /**
     * 更新
     *
     * @param graduationPracticeCompany 毕业实习(校外)
     */
    void update(GraduationPracticeCompany graduationPracticeCompany);

    /**
     * 通过实习发布id与学生id查询
     *
     * @param internshipReleaseId 实习发布id
     * @param studentId           学生id
     */
    void deleteByInternshipReleaseIdAndStudentId(String internshipReleaseId, int studentId);

    /**
     * 分页查询
     *
     * @param dataTablesUtils datatables工具类
     * @return 分页数据
     */
    Result<Record> findAllByPage(DataTablesUtils<GraduationPracticeCompany> dataTablesUtils, GraduationPracticeCompany graduationPracticeCompany);

    /**
     * 系总数
     *
     * @return 总数
     */
    int countAll(GraduationPracticeCompany graduationPracticeCompany);

    /**
     * 根据条件查询总数
     *
     * @return 条件查询总数
     */
    int countByCondition(DataTablesUtils<GraduationPracticeCompany> dataTablesUtils, GraduationPracticeCompany graduationPracticeCompany);

    /**
     * 查询
     *
     * @param dataTablesUtils           datatables工具类
     * @param graduationPracticeCompany 毕业实习(校外)
     * @return 导出数据
     */
    Result<Record> exportData(DataTablesUtils<GraduationPracticeCompany> dataTablesUtils, GraduationPracticeCompany graduationPracticeCompany);
}
