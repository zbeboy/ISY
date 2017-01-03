package top.zbeboy.isy.service;

import top.zbeboy.isy.domain.tables.pojos.Files;
import top.zbeboy.isy.domain.tables.pojos.InternshipJournal;
import top.zbeboy.isy.domain.tables.pojos.Users;

import javax.servlet.http.HttpServletRequest;

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

}
