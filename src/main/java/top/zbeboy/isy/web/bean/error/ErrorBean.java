package top.zbeboy.isy.web.bean.error;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Created by zbeboy on 2016/11/24.
 */
@Data(staticConstructor = "of")
public class ErrorBean<T> {
    private boolean hasError;
    private String errorMsg;
    private T data;
    private Map<String, Object> mapData;
    private List<T> listData;
}
