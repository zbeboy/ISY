/*
 * Copyright (ISY Team) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package top.zbeboy.isy.service.rest.internship;

import org.jooq.Record;
import org.jooq.Result;
import top.zbeboy.isy.web.bean.internship.release.InternshipReleaseBean;
import top.zbeboy.isy.web.util.PaginationUtils;

import java.util.List;

public interface InternshipReleaseRestService {

    /**
     * 分页查询全部
     *
     * @param paginationUtils 分页工具
     * @return 分页数据
     */
    Result<Record> findAllByPage(PaginationUtils paginationUtils);

    /**
     * 处理实习返回数据
     *
     * @param records 数据
     * @return 处理后的数据
     */
    List<InternshipReleaseBean> dealData(Result<Record> records);

    /**
     * 处理实习中时间
     *
     * @param i      数据
     * @param format 时间格式
     * @return 格式化后的数据
     */
    InternshipReleaseBean dealDateTime(InternshipReleaseBean i, String format);

    /**
     * 分页查询全部
     *
     * @param paginationUtils 分页工具
     * @param scienceId       专业
     * @param allowGrade      允许的年级
     * @return 分页数据
     */
    Result<Record> findAllByPageForStudent(PaginationUtils paginationUtils, int scienceId, String allowGrade);

    /**
     * 分页查询全部
     *
     * @param paginationUtils 分页工具
     * @param departmentId    系id
     * @return 分页数据
     */
    Result<Record> findAllByPageForStaff(PaginationUtils paginationUtils, int departmentId);
}
