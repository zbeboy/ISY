package top.zbeboy.isy.service;

import org.jooq.Record;
import org.jooq.Result;

/**
 * Created by lenovo on 2016-12-24.
 */
public interface SystemMessageService {

    /**
     * 系统导航栏消息显示用
     *
     * @param pageNum  当前页
     * @param pageSize 多少条
     * @param username 用户账号
     * @param isSee    是否已阅
     * @return 数据
     */
    Result<Record> findAllByPageForShow(int pageNum, int pageSize, String username, boolean isSee);

    /**
     * 系统导航栏消息显示数据
     *
     * @param username 用户账号
     * @param isSee    是否已阅
     * @return 数据
     */
    int countAllForShow(String username, boolean isSee);
}
