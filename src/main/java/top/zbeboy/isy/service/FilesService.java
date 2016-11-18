package top.zbeboy.isy.service;

import top.zbeboy.isy.domain.tables.pojos.Files;

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
}
