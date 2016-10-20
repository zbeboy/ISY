package top.zbeboy.isy.converter;

import org.jooq.Converter;

import java.util.Date;

/**
 * Created by lenovo on 2016-08-18.
 */
public class DateConverter implements Converter<java.sql.Date, Date> {
    @Override
    public Date from(java.sql.Date date) {
        return new Date(date.getTime());
    }

    @Override
    public java.sql.Date to(Date date) {
        return new java.sql.Date(date.getTime());
    }

    @Override
    public Class<java.sql.Date> fromType() {
        return java.sql.Date.class;
    }

    @Override
    public Class<Date> toType() {
        return Date.class;
    }
}
