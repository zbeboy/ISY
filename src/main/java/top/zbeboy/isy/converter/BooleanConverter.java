package top.zbeboy.isy.converter;

import org.jooq.Converter;

/**
 * Created by lenovo on 2016-08-16.
 */
public class BooleanConverter implements Converter<Byte, Boolean> {
    @Override
    public Boolean from(Byte aByte) {
        if (null == aByte || aByte == 0) {
            return false;
        }
        return true;
    }

    @Override
    public Byte to(Boolean aBoolean) {
        if (aBoolean) {
            return 1;
        }
        return 0;
    }

    @Override
    public Class<Byte> fromType() {
        return Byte.class;
    }

    @Override
    public Class<Boolean> toType() {
        return Boolean.class;
    }
}
