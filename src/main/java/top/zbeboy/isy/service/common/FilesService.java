package top.zbeboy.isy.service.common;

import top.zbeboy.isy.domain.tables.pojos.Files;
import top.zbeboy.isy.domain.tables.pojos.InternshipJournal;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.web.bean.graduate.design.pharmtech.GraduationDesignTutorBean;
import top.zbeboy.isy.web.bean.graduate.design.project.GraduationDesignPlanBean;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by lenovo on 2016-11-13.
 */
public interface FilesService {

    /**
     * 保存
     *
     * @param files 文件
     */
    void save(Files files);

    /**
     * 通过id删除
     *
     * @param fileId 文件id
     */
    void deleteById(String fileId);

    /**
     * 根据id查询
     *
     * @param id 主键
     * @return 文件
     */
    Files findById(String id);

    /**
     * 保存实习日志
     *
     * @param internshipJournal 实习内容
     * @param users             用户信息
     * @return 是否保存成功
     */
    String saveInternshipJournal(InternshipJournal internshipJournal, Users users, HttpServletRequest request);

    /**
     * 保存毕业设计规划
     *
     * @param users                         用户信息
     * @param request                       请求
     * @param graduationDesignTutorBeanList 学生信息
     * @param graduationDesignPlanBeanList  规划信息
     * @return 路径
     */
    String saveGraduationDesignPlan(Users users, HttpServletRequest request, List<GraduationDesignTutorBean> graduationDesignTutorBeanList, List<GraduationDesignPlanBean> graduationDesignPlanBeanList);
}
