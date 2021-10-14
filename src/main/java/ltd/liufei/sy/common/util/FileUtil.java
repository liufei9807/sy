package ltd.liufei.sy.common.util;

import ltd.liufei.sy.common.exception.BaseException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileUtil {

    private FileUtil() {}

    public static final String TEMPORARY_FILE = new File("")
            .getAbsolutePath() + File.separator + "temporary" + File.separator
            + "excelExport" + File.separator;

    public static MultipartFile initMultipartFile(List<Map<String, Object>> dataList, String fileName) throws IOException {
        if (dataList == null || dataList.isEmpty()) {
            return null;
        }
        File temprory = new File(FileUtil.TEMPORARY_FILE);
        if(!temprory.exists()){
            boolean flag = temprory.mkdirs();
            if(!flag) {
                throw new BaseException("创建目录失败");
            }
        }
        String fileFullName = FileUtil.TEMPORARY_FILE + fileName;
        File file = new File(fileFullName);
        if(!file.exists()){
            file.createNewFile();
        }
        List<String> head = new ArrayList<>(dataList.get(0).keySet());
        List<List<Object>> data = new ArrayList<>();
        dataList.forEach(map ->
                data.add(new ArrayList<>(map.values())) );
        ExcelUtil.writeBySimple(fileFullName, data, head);
        return getMulFileByFile(file);
    }

    public static MultipartFile getMulFileByFile(File file) throws IOException {
        FileInputStream inputStream = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile(file.getName(), file.getName(),
                "application/octet-stream", inputStream);
        return multipartFile;
    }
}
