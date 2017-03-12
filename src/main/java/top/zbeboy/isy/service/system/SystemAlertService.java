package top.zbeboy.isy.service.system;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.domain.tables.pojos.SystemAlert;
import top.zbeboy.isy.web.bean.system.alert.SystemAlertBean;
import top.zbeboy.isy.web.util.PaginationUtils;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

/**
 * Created by lenovo on 2016-12-24.
 */
public interface SystemAlertService {

    /**
     * 通过用户账号和链接id查询
     *
     * @param username 用户账号
     * @param linkId   链接id
     * @return 提醒内容
     */
    Optional<Record> findByUsernameAndLinkId(String username, String linkId);

    /**
     * 系统导航栏提醒显示用
     *
     * @param pageNum  当前页
     * @param pageSize 多少条
     * @param username 用户账号
     * @param isSee    是否已阅
     * @return 数据
     */
    Result<Record> findAllByPageForShow(int pageNum, int pageSize, String username, boolean isSee);

    /**
     * 系统导航栏提醒显示数据
     *
     * @param username 用户账号
     * @param isSee    是否已阅
     * @return 数据
     */
    int countAllForShow(String username, boolean isSee);

    /**
     * 分页查询全部
     *
     * @param paginationUtils 分页工具
     * @param systemAlertBean 额外参数
     * @return 分页数据
     */
    Result<Record> findAllByPage(PaginationUtils paginationUtils, SystemAlertBean systemAlertBean);

    /**
     * 处理返回数据
     *
     * @param paginationUtils 分页工具
     * @param records         数据
     * @param systemAlertBean 额外参数
     * @return 处理后的数据
     */
    List<SystemAlertBean> dealData(PaginationUtils paginationUtils, Result<Record> records, SystemAlertBean systemAlertBean);

    /**
     * 根据条件统计
     *
     * @param paginationUtils 分页工具
     * @param systemAlertBean 额外参数
     * @return 统计
     */
    int countByCondition(PaginationUtils paginationUtils, SystemAlertBean systemAlertBean);

    /**
     * 保存提醒
     *
     * @param systemAlert 提醒
     */
    void save(SystemAlert systemAlert);

    /**
     * 通过时间删除
     *
     * @param timestamp 时间
     */
    void deleteByAlertDate(Timestamp timestamp);

    /**
     * 更新提醒
     *
     * @param systemAlert 提醒
     */
    void update(SystemAlert systemAlert);
}
