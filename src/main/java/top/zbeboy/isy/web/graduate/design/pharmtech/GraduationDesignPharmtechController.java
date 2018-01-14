package top.zbeboy.isy.web.graduate.design.pharmtech;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import top.zbeboy.isy.config.Workbook;
import top.zbeboy.isy.service.cache.CacheBook;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignHopeTutor;
import top.zbeboy.isy.domain.tables.pojos.GraduationDesignRelease;
import top.zbeboy.isy.domain.tables.pojos.Student;
import top.zbeboy.isy.domain.tables.pojos.Users;
import top.zbeboy.isy.service.data.StudentService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignHopeTutorService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignReleaseService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignTeacherService;
import top.zbeboy.isy.service.graduate.design.GraduationDesignTutorService;
import top.zbeboy.isy.service.platform.UsersService;
import top.zbeboy.isy.service.platform.UsersTypeService;
import top.zbeboy.isy.service.util.DateTimeUtils;
import top.zbeboy.isy.web.bean.error.ErrorBean;
import top.zbeboy.isy.web.bean.graduate.design.pharmtech.GraduationDesignTutorBean;
import top.zbeboy.isy.web.bean.graduate.design.release.GraduationDesignReleaseBean;
import top.zbeboy.isy.web.bean.graduate.design.teacher.GraduationDesignTeacherBean;
import top.zbeboy.isy.web.common.MethodControllerCommon;
import top.zbeboy.isy.web.graduate.design.common.GraduationDesignConditionCommon;
import top.zbeboy.isy.web.graduate.design.common.GraduationDesignMethodControllerCommon;
import top.zbeboy.isy.web.util.AjaxUtils;
import top.zbeboy.isy.web.util.PaginationUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by zbeboy on 2017/5/15.
 */
@Slf4j
@Controller
public class GraduationDesignPharmtechController {

    @Resource
    private GraduationDesignTeacherService graduationDesignTeacherService;

    @Resource
    private GraduationDesignHopeTutorService graduationDesignHopeTutorService;

    @Resource
    private UsersService usersService;

    @Resource
    private StudentService studentService;

    @Resource
    private GraduationDesignReleaseService graduationDesignReleaseService;

    @Resource
    private MethodControllerCommon methodControllerCommon;

    @Resource
    private GraduationDesignTutorService graduationDesignTutorService;

    @Resource
    private UsersTypeService usersTypeService;

    @Resource
    private StringRedisTemplate template;

    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> stringValueOperations;

    @Resource(name = "redisTemplate")
    private ValueOperations<String, List<GraduationDesignTeacherBean>> stringListValueOperations;

    @Resource(name = "redisTemplate")
    private ListOperations<String, String> listOperations;

    @Resource
    private GraduationDesignMethodControllerCommon graduationDesignMethodControllerCommon;

    @Resource
    private GraduationDesignConditionCommon graduationDesignConditionCommon;

    /**
     * 填报指导教师
     *
     * @return 填报指导教师页面
     */
    @RequestMapping(value = "/web/menu/graduate/design/pharmtech", method = RequestMethod.GET)
    public String pharmtech() {
        return "web/graduate/design/pharmtech/design_pharmtech::#page-wrapper";
    }

    /**
     * 获取毕业设计发布数据
     *
     * @param paginationUtils 分页工具
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/pharmtech/design/data")
    @ResponseBody
    public AjaxUtils<GraduationDesignReleaseBean> designDatas(PaginationUtils paginationUtils) {
        return graduationDesignMethodControllerCommon.graduationDesignListDatas(paginationUtils);
    }

    /**
     * 志愿页面
     *
     * @param graduationDesignReleaseId 发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/graduate/design/pharmtech/wish", method = RequestMethod.GET)
    public String pharmtechWish(@RequestParam("id") String graduationDesignReleaseId, ModelMap modelMap) {
        String page;
        if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
            modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
            page = "web/graduate/design/pharmtech/design_pharmtech_wish::#page-wrapper";
        } else {
            page = methodControllerCommon.showTip(modelMap, "仅支持学生用户使用");
        }
        return page;
    }

    /**
     * 填报页面
     *
     * @param graduationDesignReleaseId 发布id
     * @param modelMap                  页面对象
     * @return 页面
     */
    @RequestMapping(value = "/web/graduate/design/pharmtech/apply", method = RequestMethod.GET)
    public String pharmtechApply(@RequestParam("id") String graduationDesignReleaseId, ModelMap modelMap) {
        String page;
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            Student student = (Student) errorBean.getMapData().get("student");
            int count = graduationDesignHopeTutorService.countByStudentIdAndGraduationDesignReleaseId(student.getStudentId(), graduationDesignReleaseId);
            if (count > 0) {
                modelMap.addAttribute("graduationDesignReleaseId", graduationDesignReleaseId);
                page = "web/graduate/design/pharmtech/design_pharmtech_apply::#page-wrapper";
            } else {
                page = methodControllerCommon.showTip(modelMap, "请先填写志愿");
            }
        } else {
            page = methodControllerCommon.showTip(modelMap, errorBean.getErrorMsg());
        }
        return page;
    }

    /**
     * 填报指导教师志愿数据
     *
     * @param graduationDesignReleaseId 发布id
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/pharmtech/wish/data", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<GraduationDesignTeacherBean> wishData(@RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils<GraduationDesignTeacherBean> ajaxUtils = AjaxUtils.of();
        if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
            Users users = usersService.getUserFromSession();
            Student student = studentService.findByUsername(users.getUsername());
            Result<Record> designHopeTutorRecords = graduationDesignHopeTutorService.findByStudentIdAndGraduationDesignReleaseId(student.getStudentId(), graduationDesignReleaseId);
            List<GraduationDesignTeacherBean> graduationDesignTeachers = graduationDesignTeacherService.findByGraduationDesignReleaseIdRelationForStaff(graduationDesignReleaseId);
            for (GraduationDesignTeacherBean designTeacherBean : graduationDesignTeachers) {
                boolean selectedTeacher = false;
                if (designHopeTutorRecords.isNotEmpty()) {
                    List<GraduationDesignHopeTutor> graduationDesignHopeTutors = designHopeTutorRecords.into(GraduationDesignHopeTutor.class);
                    for (GraduationDesignHopeTutor r : graduationDesignHopeTutors) {
                        if (designTeacherBean.getGraduationDesignTeacherId().equals(r.getGraduationDesignTeacherId())) {
                            selectedTeacher = true;
                            break;
                        }
                    }
                }
                designTeacherBean.setSelected(selectedTeacher);
            }
            ajaxUtils.success().msg("获取数据成功").listData(graduationDesignTeachers);
        } else {
            ajaxUtils.fail().msg("仅支持学生用户使用");
        }
        return ajaxUtils;
    }

    /**
     * 填报指导教师数据
     *
     * @param graduationDesignReleaseId 发布id
     * @return 数据
     */
    @RequestMapping(value = "/web/graduate/design/pharmtech/apply/data", method = RequestMethod.GET)
    @ResponseBody
    public AjaxUtils<GraduationDesignTeacherBean> applyData(@RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils<GraduationDesignTeacherBean> ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            Student student = (Student) errorBean.getMapData().get("student");
            List<GraduationDesignTeacherBean> graduationDesignTeacherBeens;
            String cacheKey = CacheBook.GRADUATION_DESIGN_TEACHER_STUDENT + graduationDesignReleaseId;
            String studentKey = CacheBook.GRADUATION_DESIGN_PHARMTECH_STUDENT + student.getStudentId();
            // 从缓存中得到列表
            if (stringListValueOperations.getOperations().hasKey(cacheKey)) {
                graduationDesignTeacherBeens = stringListValueOperations.get(cacheKey);
            } else {
                graduationDesignTeacherBeens = new ArrayList<>();
            }
            // 处理列表
            if (!ObjectUtils.isEmpty(graduationDesignTeacherBeens) && graduationDesignTeacherBeens.size() > 0) {
                boolean selectedTeacher = false;
                for (GraduationDesignTeacherBean designTeacherBean : graduationDesignTeacherBeens) {
                    // 装填剩余人数
                    String studentCountKey = CacheBook.GRADUATION_DESIGN_TEACHER_STUDENT_COUNT + designTeacherBean.getGraduationDesignTeacherId();
                    if (template.hasKey(studentCountKey)) {
                        ValueOperations<String, String> ops = this.template.opsForValue();
                        designTeacherBean.setResidueCount(NumberUtils.toInt(ops.get(studentCountKey)));
                    }
                    // 选中当前用户已选择
                    if (!selectedTeacher && stringValueOperations.getOperations().hasKey(studentKey)) {
                        // 解除逗号分隔的字符   指导教师id , 学生id
                        String str = stringValueOperations.get(studentKey);
                        if (StringUtils.isNotBlank(str)) {
                            String[] arr = str.split(",");
                            if (arr.length >= 2) {
                                if (designTeacherBean.getGraduationDesignTeacherId().equals(arr[0])) {
                                    selectedTeacher = true;
                                    designTeacherBean.setSelected(true);
                                }
                            }
                        }
                    }
                }
            }
            ajaxUtils.success().msg("获取数据成功").listData(graduationDesignTeacherBeens);
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 选择教师
     *
     * @param graduationDesignTeacherId 指导老师id
     * @param graduationDesignReleaseId 发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/pharmtech/wish/selected", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils wishSelected(@RequestParam("graduationDesignTeacherId") String graduationDesignTeacherId,
                                  @RequestParam("graduationDesignReleaseId") String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            Student student = (Student) errorBean.getMapData().get("student");
            // 是否已达到志愿数量
            int count = graduationDesignHopeTutorService.countByStudentIdAndGraduationDesignReleaseId(student.getStudentId(), graduationDesignReleaseId);
            if (count < 3) {
                GraduationDesignHopeTutor graduationDesignHopeTutor = new GraduationDesignHopeTutor();
                graduationDesignHopeTutor.setGraduationDesignTeacherId(graduationDesignTeacherId);
                graduationDesignHopeTutor.setStudentId(student.getStudentId());
                graduationDesignHopeTutorService.save(graduationDesignHopeTutor);
                ajaxUtils.success().msg("保存成功");
            } else {
                ajaxUtils.fail().msg("最大支持志愿三位指导教师");
            }
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 填报选择教师
     *
     * @param graduationDesignTeacherId 指导老师id
     * @param graduationDesignReleaseId 发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/pharmtech/apply/selected", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils applySelected(@RequestParam("graduationDesignTeacherId") String graduationDesignTeacherId,
                                   @RequestParam("graduationDesignReleaseId") String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            Student student = (Student) errorBean.getMapData().get("student");
            // 判断是否已选择过教师
            boolean canSelect;
            // 第一次选择
            boolean isNewSelect = false;
            String studentKey = CacheBook.GRADUATION_DESIGN_PHARMTECH_STUDENT + student.getStudentId();
            if (stringValueOperations.getOperations().hasKey(studentKey)) {
                // 已选择过，不能重复选
                String str = stringValueOperations.get(studentKey);
                if (StringUtils.isNotBlank(str)) {
                    String[] arr = str.split(",");
                    canSelect = arr.length >= 2 && arr[0].equals("-1");
                } else {
                    canSelect = false;
                }
            } else {
                canSelect = true;
                isNewSelect = true;
            }
            if (canSelect) {
                // 计数器
                String countKey = CacheBook.GRADUATION_DESIGN_TEACHER_STUDENT_COUNT + graduationDesignTeacherId;
                if (template.hasKey(countKey)) {
                    ValueOperations<String, String> ops = this.template.opsForValue();
                    int count = NumberUtils.toInt(ops.get(countKey)) - 1;
                    if (count >= 0) {
                        ops.set(countKey, count + "");
                        // 存储 指导教师id , 学生id
                        if (isNewSelect) {
                            stringValueOperations.set(studentKey,
                                    graduationDesignTeacherId + "," + student.getStudentId(),
                                    CacheBook.EXPIRES_GRADUATION_DESIGN_TEACHER_STUDENT_DAYS, TimeUnit.DAYS);
                        } else {
                            stringValueOperations.set(studentKey,
                                    graduationDesignTeacherId + "," + student.getStudentId());
                        }
                        // 存储学生key
                        // 是否已经存在当前学生key
                        String listKey = CacheBook.GRADUATION_DESIGN_PHARMTECH_STUDENT_LIST + graduationDesignReleaseId;
                        List<String> keys = listOperations.range(listKey, 0, listOperations.size(listKey));
                        boolean hasKey = false;
                        for (String key : keys) {
                            // 已经存在 无需添加
                            if (key.equals(studentKey)) {
                                hasKey = true;
                                break;
                            }
                        }
                        // 不存在，需要添加
                        if (!hasKey) {
                            listOperations.rightPush(listKey, studentKey);
                        }
                        ajaxUtils.success().msg("保存成功");
                    } else {
                        ajaxUtils.fail().msg("已达当前指导教师人数上限");
                    }
                } else {
                    ajaxUtils.fail().msg("未发现确认数据");
                }
            } else {
                ajaxUtils.fail().msg("仅可选择一位指导教师");
            }
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 取消教师
     *
     * @param graduationDesignTeacherId 指导老师id
     * @param graduationDesignReleaseId 发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/pharmtech/wish/cancel", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils wishCancel(@RequestParam("graduationDesignTeacherId") String graduationDesignTeacherId,
                                @RequestParam("graduationDesignReleaseId") String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            Student student = (Student) errorBean.getMapData().get("student");
            GraduationDesignHopeTutor graduationDesignHopeTutor = new GraduationDesignHopeTutor();
            graduationDesignHopeTutor.setGraduationDesignTeacherId(graduationDesignTeacherId);
            graduationDesignHopeTutor.setStudentId(student.getStudentId());
            graduationDesignHopeTutorService.delete(graduationDesignHopeTutor);
            ajaxUtils.success().msg("取消成功");
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 填报取消教师
     *
     * @param graduationDesignTeacherId 指导老师id
     * @param graduationDesignReleaseId 发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/pharmtech/apply/cancel", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils applyCancel(@RequestParam("graduationDesignTeacherId") String graduationDesignTeacherId,
                                 @RequestParam("graduationDesignReleaseId") String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            Student student = (Student) errorBean.getMapData().get("student");
            // 计数器
            String countKey = CacheBook.GRADUATION_DESIGN_TEACHER_STUDENT_COUNT + graduationDesignTeacherId;
            if (template.hasKey(countKey)) {
                ValueOperations<String, String> ops = this.template.opsForValue();
                ops.increment(countKey, 1L);
                // 存储 指导教师id , 学生id
                String studentKey = CacheBook.GRADUATION_DESIGN_PHARMTECH_STUDENT + student.getStudentId();
                if (stringValueOperations.getOperations().hasKey(studentKey)) {
                    stringValueOperations.set(studentKey,
                            -1 + "," + student.getStudentId());
                }
                ajaxUtils.success().msg("取消成功");
            } else {
                ajaxUtils.fail().msg("未发现确认数据");
            }
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 进入志愿页面判断条件
     *
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/pharmtech/wish/condition", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils canWish() {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
            ajaxUtils.success().msg("在条件范围，允许使用");
        } else {
            ajaxUtils.fail().msg("仅支持学生用户使用");
        }
        return ajaxUtils;
    }

    /**
     * 进入填报页面判断条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/pharmtech/apply/condition", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils canApply(@RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        ErrorBean<GraduationDesignRelease> errorBean = accessCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            Student student = (Student) errorBean.getMapData().get("student");
            int count = graduationDesignHopeTutorService.countByStudentIdAndGraduationDesignReleaseId(student.getStudentId(), graduationDesignReleaseId);
            if (count > 0) {
                ajaxUtils.success().msg("在条件范围，允许使用");
            } else {
                ajaxUtils.fail().msg("请先填写志愿");
            }
        } else {
            ajaxUtils.fail().msg(errorBean.getErrorMsg());
        }
        return ajaxUtils;
    }

    /**
     * 获取我的毕业指导教师信息
     *
     * @param graduationDesignReleaseId 毕业发布id
     * @return 指导教师信息
     */
    @RequestMapping(value = "/web/graduate/design/pharmtech/my/teacher", method = RequestMethod.POST)
    @ResponseBody
    public AjaxUtils myTeacher(@RequestParam("id") String graduationDesignReleaseId) {
        AjaxUtils ajaxUtils = AjaxUtils.of();
        GraduationDesignRelease graduationDesignRelease = graduationDesignReleaseService.findById(graduationDesignReleaseId);
        if (!ObjectUtils.isEmpty(graduationDesignRelease)) {
            if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                // 查询学生
                Users users = usersService.getUserFromSession();
                Optional<Record> studentRecord = studentService.findByUsernameAndScienceIdAndGradeRelation(users.getUsername(), graduationDesignRelease.getScienceId(), graduationDesignRelease.getAllowGrade());
                if (studentRecord.isPresent()) {
                    Student student = studentRecord.get().into(Student.class);
                    // 是否已确认调整
                    if (!ObjectUtils.isEmpty(graduationDesignRelease.getIsOkTeacherAdjust()) && graduationDesignRelease.getIsOkTeacherAdjust() == 1) {
                        Optional<Record> record = graduationDesignTutorService.findByStudentIdAndGraduationDesignReleaseIdRelationForStaff(student.getStudentId(), graduationDesignReleaseId);
                        if (record.isPresent()) {
                            GraduationDesignTutorBean graduationDesignTutorBean = record.get().into(GraduationDesignTutorBean.class);
                            ajaxUtils.success().msg("获取数据成功").obj(graduationDesignTutorBean);
                        } else {
                            ajaxUtils.fail().msg("未获取到任何信息");
                        }
                    } else {
                        ajaxUtils.fail().msg("请等待调整完成后，进行查看");
                    }
                } else {
                    ajaxUtils.fail().msg("您的账号不符合此次毕业设计条件");
                }
            } else {
                ajaxUtils.fail().msg("仅支持学生用户使用");
            }
        } else {
            ajaxUtils.fail().msg("未查询到相关毕业设计信息");
        }
        return ajaxUtils;
    }

    /**
     * 进入页面判断条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    @RequestMapping(value = "/web/graduate/design/pharmtech/condition", method = RequestMethod.POST)
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
     * 进入填报教师入口条件
     *
     * @param graduationDesignReleaseId 毕业设计发布id
     * @return true or false
     */
    private ErrorBean<GraduationDesignRelease> accessCondition(String graduationDesignReleaseId) {
        ErrorBean<GraduationDesignRelease> errorBean = graduationDesignConditionCommon.basicCondition(graduationDesignReleaseId);
        if (!errorBean.isHasError()) {
            Map<String, Object> mapData = new HashMap<>();
            GraduationDesignRelease graduationDesignRelease = errorBean.getData();
            // 毕业时间范围
            if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease.getStartTime(), graduationDesignRelease.getEndTime())) {
                // 填报时间范围
                if (DateTimeUtils.timestampRangeDecide(graduationDesignRelease.getFillTeacherStartTime(), graduationDesignRelease.getFillTeacherEndTime())) {
                    if (usersTypeService.isCurrentUsersTypeName(Workbook.STUDENT_USERS_TYPE)) {
                        // 是否学生在该毕业设计专业下
                        Users users = usersService.getUserFromSession();
                        Optional<Record> studentRecord = studentService.findByUsernameAndScienceIdAndGradeRelation(users.getUsername(), graduationDesignRelease.getScienceId(), graduationDesignRelease.getAllowGrade());
                        if (studentRecord.isPresent()) {
                            Student student = studentRecord.get().into(Student.class);
                            mapData.put("student", student);
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
                            errorBean.setErrorMsg("您的账号不符合此次毕业设计条件");
                        }
                    } else {
                        errorBean.setHasError(true);
                        errorBean.setErrorMsg("仅支持学生用户使用");
                    }
                } else {
                    errorBean.setHasError(true);
                    errorBean.setErrorMsg("不在填报时间范围，无法操作");
                }
            } else {
                errorBean.setHasError(true);
                errorBean.setErrorMsg("不在毕业设计时间范围，无法操作");
            }
            errorBean.setMapData(mapData);
        }
        return errorBean;
    }
}
