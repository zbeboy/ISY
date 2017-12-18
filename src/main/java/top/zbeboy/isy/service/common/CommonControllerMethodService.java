package top.zbeboy.isy.service.common;

/**
 * Created by lenovo on 2016-10-15.
 */
public interface CommonControllerMethodService {

    /**
     * 限制当前学生用户操作行为
     *
     * @param studentId 学生id
     * @return 是否可操作
     */
    boolean limitCurrentStudent(int studentId);
}
