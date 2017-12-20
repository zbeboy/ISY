package top.zbeboy.isy.service.common

import top.zbeboy.isy.domain.tables.pojos.Files
import top.zbeboy.isy.domain.tables.pojos.InternshipJournal
import top.zbeboy.isy.domain.tables.pojos.Users
import top.zbeboy.isy.web.bean.graduate.design.pharmtech.GraduationDesignTutorBean
import top.zbeboy.isy.web.bean.graduate.design.project.GraduationDesignPlanBean
import javax.servlet.http.HttpServletRequest

/**
 * Created by zbeboy 2017-12-20 .
 **/
interface FilesService {
    /**
     * 保存
     *
     * @param files 文件
     */
    fun save(files: Files)

    /**
     * 通过id删除
     *
     * @param fileId 文件id
     */
    fun deleteById(fileId: String)

    /**
     * 根据id查询
     *
     * @param id 主键
     * @return 文件
     */
    fun findById(id: String): Files

    /**
     * 保存实习日志
     *
     * @param internshipJournal 实习内容
     * @param users             用户信息
     * @return 是否保存成功
     */
    fun saveInternshipJournal(internshipJournal: InternshipJournal, users: Users, request: HttpServletRequest): String

    /**
     * 保存毕业设计规划
     *
     * @param users                         用户信息
     * @param request                       请求
     * @param graduationDesignTutorBeanList 学生信息
     * @param graduationDesignPlanBeanList  规划信息
     * @return 路径
     */
    fun saveGraduationDesignPlan(users: Users, request: HttpServletRequest, graduationDesignTutorBeanList: List<GraduationDesignTutorBean>, graduationDesignPlanBeanList: List<GraduationDesignPlanBean>): String
}