package top.zbeboy.isy.glue.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import java.util.Collection;

/**
 * Created by lenovo on 2017-03-28.
 */
public class SearchUtils {
    public static boolean mapValueIsNotEmpty(JSONObject search) {
        boolean isNotEmpty = false;
        if (!ObjectUtils.isEmpty(search)) {
            Collection<Object> collection = search.values();
            for (Object o : collection) {
                isNotEmpty = StringUtils.isNotBlank(o.toString());
                if (BooleanUtils.isTrue(isNotEmpty)) {
                    break;
                }
            }
        } else {
            isNotEmpty = false;
        }
        return isNotEmpty;
    }
}
