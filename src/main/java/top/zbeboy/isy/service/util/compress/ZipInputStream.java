package top.zbeboy.isy.service.util.compress;

import org.apache.commons.compress.parallel.InputStreamSupplier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by zbeboy on 2016/12/19.
 */
public class ZipInputStream implements InputStreamSupplier {

    private String filePath;

    public ZipInputStream(String filePath){
        this.filePath = filePath;
    }

    @Override
    public InputStream get() {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return inputStream;
    }
}
