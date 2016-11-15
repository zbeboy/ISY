package top.zbeboy.isy.service;

import org.springframework.stereotype.Service;

/**
 * Created by zbeboy on 2016/11/15.
 */
@Service("weixinService")
public class WeixinServiceImpl implements WeixinService {

    @Override
    public boolean checkSignature() {
        return false;
    }
}
