package top.zbeboy.isy.web.util;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 2016/9/22.
 */
public class SmallPropsUtils {

    /**
     * 参数ids ',' 分隔转list<Integer>
     * @param ids
     * @return list<Integer>
     */
    public static List<Integer> StringIdsToList(String ids){
        String[] idArr = ids.split(",");
        List<Integer> newIds = new ArrayList<>();
        for(String id:idArr){
            newIds.add(NumberUtils.toInt(id));
        }
        return newIds;
    }

    /**
     * 参数ids ',' 分隔转list<String>
     * @param ids
     * @return list<String>
     */
    public static List<String> StringIdsToStringList(String ids){
        String[] idArr = ids.split(",");
        List<String> newIds = new ArrayList<>();
        for(String id:idArr){
            newIds.add(id);
        }
        return newIds;
    }

    /**
     * 参数ids ',' 是否为Integer类型
     * @param ids
     * @return true or false
     */
    public static boolean StringIdsIsNumber(String ids){
        String[] idArr = ids.split(",");
        boolean isNumber = true;
        for(String id:idArr){
            if(!NumberUtils.isNumber(id)){
                isNumber = false;
                break;
            }
        }
        return isNumber;
    }
}
