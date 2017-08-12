package top.zbeboy.isy.test;

import junit.framework.TestCase;
import org.joda.time.DateTime;

import java.text.SimpleDateFormat;

/**
 * Created by lenovo on 2016-11-06.
 */
public class TestTime extends TestCase {

    public void testJoda() {
        DateTime dateTime = DateTime.now();
        DateTime s = dateTime.minusDays(120);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String d = sdf.format(s.toDate());
        System.out.println(d);
        System.out.println(dateTime.getYear());
    }
}
