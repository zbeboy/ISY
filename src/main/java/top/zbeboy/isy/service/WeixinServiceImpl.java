package top.zbeboy.isy.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.zbeboy.isy.config.ISYProperties;
import top.zbeboy.isy.service.util.MD5Utils;
import top.zbeboy.isy.web.vo.weixin.WeixinVo;

import java.util.Arrays;

/**
 * Created by zbeboy on 2016/11/15.
 */
@Service("weixinService")
public class WeixinServiceImpl implements WeixinService {

    private final Logger log = LoggerFactory.getLogger(WeixinServiceImpl.class);

    @Autowired
    private ISYProperties isyProperties;

    @Override
    public boolean checkSignature(WeixinVo weixinVo) {
        String[] arr = new String[]{isyProperties.getWeixin().getToken(), weixinVo.getTimestamp(), weixinVo.getNonce()};
        Arrays.sort(arr);
        String newArr = MD5Utils.sha_1(arr[0] + arr[1] + arr[2]);
        return StringUtils.equals(weixinVo.getSignature(), newArr);
    }
}
