package top.zbeboy.isy.service;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.SystemMessage;
import top.zbeboy.isy.web.bean.system.message.SystemMessageBean;
import top.zbeboy.isy.web.util.PaginationUtils;

import java.util.List;
import java.util.Optional;

/**
 * Created by lenovo on 2016-12-24.
 */
public interface SystemMessageService {

    /**
     * 通过id关联查询
     *
     * @param id 主键
     * @return 消息
     */
    Optional<Record> findByIdRelation(String id);

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

    /**
     * 分页查询全部
     *
     * @param paginationUtils   分页工具
     * @param systemMessageBean 额外参数
     * @return 分页数据
     */
    Result<Record> findAllByPage(PaginationUtils paginationUtils, SystemMessageBean systemMessageBean);

    /**
     * 处理返回数据
     *
     * @param paginationUtils   分页工具
     * @param records           数据
     * @param systemMessageBean 额外参数
     * @return 处理后的数据
     */
    List<SystemMessageBean> dealData(PaginationUtils paginationUtils, Result<Record> records, SystemMessageBean systemMessageBean);

    /**
     * 根据条件统计
     *
     * @param paginationUtils   分页工具
     * @param systemMessageBean 额外参数
     * @return 统计
     */
    int countByCondition(PaginationUtils paginationUtils, SystemMessageBean systemMessageBean);

    /**
     * 保存
     *
     * @param systemMessage 消息
     */
    void save(SystemMessage systemMessage);
}
