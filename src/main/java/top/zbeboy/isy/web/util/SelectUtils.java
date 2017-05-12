package top.zbeboy.isy.web.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Administrator on 2016/4/6.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class SelectUtils {

    private int id;

    private String value;

    private String text;

    private boolean selected;
}
