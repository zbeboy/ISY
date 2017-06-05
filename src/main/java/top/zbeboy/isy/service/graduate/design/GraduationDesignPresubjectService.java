package top.zbeboy.isy.service.graduate.design;

import top.zbeboy.isy.domain.tables.pojos.GraduationDesignPresubject;
import top.zbeboy.isy.domain.tables.records.GraduationDesignPresubjectRecord;

/**
 * Created by zbeboy on 2017/6/5.
 */
public interface GraduationDesignPresubjectService {

    /**
     * 根据主键查询
     *
     * @param id 主键
     * @return 数据
     */
    GraduationDesignPresubject findById(String id);

    /**
     * 通过学生id与毕业设计发布id查询
     *
     * @param studentId                 学生id
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return 数据
     */
    GraduationDesignPresubjectRecord findByStudentIdAndGraduationDesignReleaseId(int studentId, String graduationDesignReleaseId);

    /**
     * 保存
     *
     * @param graduationDesignPresubject 数据
     */
    void save(GraduationDesignPresubject graduationDesignPresubject);

    /**
     * 更新
     *
     * @param graduationDesignPresubject 数据
     */
    void update(GraduationDesignPresubject graduationDesignPresubject);
}
