package org.xiafei.file;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * @author 夏飞
 */

@RestController
public class UploadController {
    @RequestMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) throws IOException {
        String  filePath=generateFilePath(file.getOriginalFilename());
        uploadAliOSS(file.getInputStream(), filePath,file.getContentType());
        return "https://xiafei-web.oss-cn-hangzhou.aliyuncs.com/" + filePath;
    }

    private void uploadAliOSS(InputStream inputStream, String key,String contentType) throws FileNotFoundException {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = "http://oss-cn-hangzhou.aliyuncs.com";
// 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
        String accessKeyId = System.getProperty("access.key.id");
        String accessKeySecret = System.getProperty("access.key.secret");

// 创建OSSClient实例。
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);

// 上传文件流。
        ObjectMetadata objectMetadata=new ObjectMetadata();
        objectMetadata.setHeader("Content-Disposition","inline");
        PutObjectResult putObjectResult = ossClient.putObject("xiafei-web", key, inputStream,objectMetadata);
// 关闭OSSClient。
        ossClient.shutdown();
    }

    /**
     * 生成文件目录
     * /file/Timestamp
     */
    private String generateFilePath(String originFileName) {
        return System.currentTimeMillis() + getFileNameSuffix(originFileName);
    }

    private String getFileNameSuffix(String fileName) {
        return fileName.substring(fileName.indexOf("."));
    }
}
