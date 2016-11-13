package top.zbeboy.isy.service;

import top.zbeboy.isy.domain.tables.pojos.InternshipFile;

/**
 * Created by lenovo on 2016-11-13.
 */
public interface InternshipFileService {

    /**
     * 保存
     *
     * @param internshipFile 实习文件
     */
    void save(InternshipFile internshipFile);
}
