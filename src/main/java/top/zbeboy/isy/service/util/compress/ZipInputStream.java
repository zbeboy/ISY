package top.zbeboy.isy.service.util.compress;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.parallel.InputStreamSupplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by zbeboy on 2016/12/19.
 * compress zip builder.
 */
@Slf4j
public class ZipInputStream implements InputStreamSupplier {

    private File file;

    public ZipInputStream(File file) {
        this.file = file;
    }

    @Override
    public InputStream get() {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            log.error("Read file error, is {}", e);
        }
        return inputStream;
    }
}
