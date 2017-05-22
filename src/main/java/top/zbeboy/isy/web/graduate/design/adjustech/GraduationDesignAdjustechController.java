package top.zbeboy.isy.web.graduate.design.adjustech;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTime;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.config.CacheBook;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignRelease;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignTeacher;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignTutor;
import top.zbeboy.isy.service.common.CommonControllerMethodService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignReleaseService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignTeacherService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignTutorService;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.service.util.UUIDUtils;
import top.zbeboy.isy.web.bean.error.ErrorBean;
import top.zbeboy.isy.web.bean.graduate.design.release.GraduationDesignReleaseBean;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.PaginationUtils;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by lenovo on 2017-05-21.
 */
@Slf4j
@Controller
public class GraduationDesignAdjustechController {

    @Resource
    private CommonControllerMethodService commonControllerMethodService;

    @Resource
    private GraduationDesignReleaseService graduationDesignReleaseService;

    @Resource
    private GraduationDesignTutorService graduationDesignTutorService;

    @Resource
    private GraduationDesignTeacherService graduationDesignTeacherService;

    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> stringValueOperations;

    @Resource
    private StringRedisTemplate template;

    @Resource(name = "redisTemplate")
    private ListOperations<String, String> listOperations;

    /**
     * 调整填报教师
     *
     * @return 调整填报教师页面
     */
    @RequestMapping(value = "/web/menu/graduate/design/adjustech", method = RequestMethod.GET)
    public String releaseData() {
        return "web/graduate/design/adjustech/design_adjustech::#page-wrapper";
    }

    /**
     * 填报教师数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/adjustech/data", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<GraduationDesignReleaseBean> adjustechDatas(PaginationUtils paginationUtils) {
        AjaxUtils<GraduationDesignReleaseBean> ajaxUtils = AjaxUtils.of();
        Byte isDel = 0;
        GraduationDesignReleaseBean graduationDesignReleaseBean = new GraduationDesignReleaseBean();
        graduationDesignReleaseBean.setGraduationDesignIsDel(isDel);
        Map<String, Integer> commonData = commonControllerMethodService.accessRoleCondition();
        graduationDesignReleaseBean.setDepartmentId(StringUtils.isEmpty(commonData.get("departmentId")) ? -1 : commonData.get("departmentId"));
        graduationDesignReleaseBean.setCollegeId(StringUtils.isEmpty(commonData.get("collegeId")) ? -1 : commonData.get("collegeId"));
        Result<Record> records = graduationDesignReleaseService.findAllByPage(paginationUtils, graduationDesignReleaseBean);
        List<GraduationDesignReleaseBean> graduationDesignReleaseBeens = graduationDesignReleaseService.dealData(paginationUtils, records, graduationDesignReleaseBean);
        graduationDesignReleaseBeens.forEach(graduationDesignRelease -> {
                    graduationDesignRelease.setStudentNotFillCount(graduationDesignTutorService.countNotFillStudent(graduationDesignRelease));
                    graduationDesignRelease.setStudentFillCount(graduationDesignTutorService.countFillStudent(graduationDesignRelease));
                    String syncTimeKey = CacheBook.GRADUATION_DESIGN_ADJUSTECH_SYNC_TIME + graduationDesignRelease.getGraduationDesignReleaseId();
                    if (stringValueOperations.getOperations().hasKey(syncTimeKey)) {
                        graduationDesignRelease.setHasSyncDate(true);
                        graduationDesignRelease.setSyncDate(stringValueOperations.get(syncTimeKey));
                    } else {
                        graduationDesignRelease.setHasSyncDate(false);
                    }
                }
        );
        return ajaxUtils.success().msg("获取数据成功").listData(graduationDesignReleaseBeens).paginationUtils(paginationUtils);
    }

    /**
     * 同步数据
     *
     * @param graduationDesignReleaseId 毕业发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/adjustech/sync/data", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils syncData(@RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            // step 1. 检查同步时间
            String syncTimeKey = CacheBook.GRADUATION_DESIGN_ADJUSTECH_SYNC_TIME + graduationDesignReleaseId;
            if (!stringValueOperations.getOperations().hasKey(syncTimeKey)) {
                // 更新剩余人数到指导教师表 ， 并且删除学生指导教师表中的关联数据
                List<GraduationDesignTeacher> graduationDesignTeachers = graduationDesignTeacherService.findByGraduationDesignReleaseId(graduationDesignReleaseId);
                ValueOperations<String, String> ops = this.template.opsForValue();
                graduationDesignTeachers.forEach(graduationDesignTeacher -> {
                            String studentCountKey = CacheBook.GRADUATION_DESIGN_TEACHER_STUDENT_COUNT + graduationDesignTeacher.getGraduationDesignTeacherId();
                            if (template.hasKey(studentCountKey)) {
                                graduationDesignTeacher.setResidue(NumberUtils.toInt(ops.get(studentCountKey)));
                            }
                            graduationDesignTutorService.deleteByGraduationDesignTeacherId(graduationDesignTeacher.getGraduationDesignTeacherId());
                        }
                );
                // 更新
                graduationDesignTeacherService.update(graduationDesignTeachers);

                // 同步关联数据
                // step 2. 先取所有学生key
                String listKey = CacheBook.GRADUATION_DESIGN_PHARMTECH_STUDENT_LIST + graduationDesignReleaseId;
                List<String> keys = listOperations.range(listKey, 0, listOperations.size(listKey));
                for (String key : keys) {
                    if (stringValueOperations.getOperations().hasKey(key)) {
                        String str = stringValueOperations.get(key);
                        if (StringUtils.hasLength(str)) {
                            String[] arr = str.split(",");
                            if (arr.length >= 2 && !arr[0].equals("-1")) {
                                // step 3. 存储关联信息
                                GraduationDesignTutor graduationDesignTutor = new GraduationDesignTutor();
                                graduationDesignTutor.setGraduationDesignTutorId(UUIDUtils.getUUID());
                                graduationDesignTutor.setGraduationDesignTeacherId(arr[0]);
                                graduationDesignTutor.setStudentId(NumberUtils.toInt(arr[1]));
                                graduationDesignTutorService.save(graduationDesignTutor);
                            }
                        }
                    }
                }
                DateTime dateTime = DateTime.now();
                DateTime s = dateTime.plusHours(4);
                stringValueOperations.set(syncTimeKey, DateTimeUtils.formatDate(s.toDate()), CacheBook.EXPIRES_HOURS, TimeUnit.HOURS);
                ajaxUtils.success().msg("同步完成");
            } else {
                ajaxUtils.fail().msg("请等到 " + stringValueOperations.get(syncTimeKey) + " 后再进行同步");
            }
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 进入页面判断条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/adjustech/condition", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils canUse(@RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            ajaxUtils.success().msg("在条件范围，允许使用");
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 进入调整填报教师入口条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    private ErrorBean<GraduationDesignRelease> accessCondition(String graduationDesignReleaseId) {
        ErrorBean<GraduationDesignRelease> errorBean = ErrorBean.of();
        GraduationDesignRelease graduationDesignRelease = graduationDesignReleaseService.findById(graduationDesignReleaseId);
        if (!ObjectUtils.isEmpty(graduationDesignRelease)) {
            errorBean.setData(graduationDesignRelease);
            if (graduationDesignRelease.getGraduationDesignIsDel() == 1) {
                errorBean.setHasError(true);
                errorBean.setErrorMsg("该毕业设计已被注销");
            } else {
                // 毕业时间范围
                if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease.getStartTime(), graduationDesignRelease.getEndTime())) {
                    // 是否已确认
                    if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacher()) && graduationDesignRelease.getIsOkTeacher() == 1) {
                        // 是否已确认调整
                        if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                            errorBean.setHasError(true);
                            errorBean.setErrorMsg("已确认毕业设计指导教师调整，无法进行操作");
                        } else {
                            errorBean.setHasError(false);
                        }
                    } else {
                        errorBean.setHasError(true);
                        errorBean.setErrorMsg("未确认毕业设计指导教师，无法进行操作");
                    }
                } else {
                    errorBean.setHasError(true);
                    errorBean.setErrorMsg("不在毕业设计时间范围，无法操作");
                }
            }
        } else {
            errorBean.setHasError(true);
            errorBean.setErrorMsg("未查询到相关毕业设计信息");
        }
        return errorBean;
    }
}
