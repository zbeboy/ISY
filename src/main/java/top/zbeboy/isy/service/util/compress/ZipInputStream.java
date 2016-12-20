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

    private File file;

    public ZipInputStream(File file){
        this.file = file;
    }

    @Override
    public InputStream get() {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return inputStream;
    }
}
