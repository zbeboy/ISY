package top.zbeboy.isy.glue.util;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by lenovo on 2017-03-28.
 */
@Slf4j
@Data(staticConstructor = "of")
public class ResultUtils<T> {

    private T data;
    private long totalElements;

    public ResultUtils<T> data(T data) {
        this.data = data;
        return this;
    }

    public ResultUtils<T> totalElements(long totalElements) {
        this.totalElements = totalElements;
        return this;
    }
}
