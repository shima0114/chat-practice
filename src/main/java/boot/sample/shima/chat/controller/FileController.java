package boot.sample.shima.chat.controller;

import boot.sample.shima.chat.entity.AttachmentFile;
import boot.sample.shima.chat.service.AttachmentFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
public class FileController {

    @Autowired
    AttachmentFileService fileService;

    @Autowired
    ResourceLoader resourceLoader;

    @Value("${download.base.dir}")
    String baseDir;

    @RequestMapping("/file/upload")
    public Map<String, String> fileUpload(@RequestParam MultipartFile file, @RequestParam String channelId, @RequestParam String userId) throws IOException {
//        Map<String, String> retMap = new HashMap<>();

        // ファイルが空の場合は異常終了
        if(file.isEmpty()){
            // 異常終了時の処理
            return resultMap("failure", "ファイルが空です。", "", "", "");
        }
        Resource resource =resourceLoader.getResource(baseDir);
        File upDir = new File(resource.getFile().getPath());
        if (!upDir.exists()) {
            if (!upDir.mkdirs()) {
                return resultMap("failure", "ディレクトリ作成失敗。", "", "", "");
            }
        }

        LocalDateTime timeNow = LocalDateTime.now();
        String saveTime = timeNow.format(DateTimeFormatter.ofPattern("YYYYMMddHHmmssS"));
        String saveFileName = saveTime + file.getOriginalFilename();

        // アップロードファイルを格納するディレクトリを作成する
        File uploadFile =new File(upDir, saveFileName);
        // アップロードファイルを置く
        File createFile =
                new File(uploadFile.getPath());
        try (BufferedOutputStream uploadFileStream =
                        new BufferedOutputStream(new FileOutputStream(createFile))){
            byte[] bytes = file.getBytes();
            uploadFileStream.write(bytes);
        } catch (Exception e) {
            // 異常終了時の処理
            e.printStackTrace();
        } catch (Throwable t) {
            // 異常終了時の処理
            t.printStackTrace();
        }

        // DBへ情報を登録
        AttachmentFile attachmentFile = new AttachmentFile();
        attachmentFile.setChannelId(channelId);
        attachmentFile.setSaveFileName(saveFileName);
        attachmentFile.setOriginalFileName(file.getOriginalFilename());
        attachmentFile.setUserId(userId);
        attachmentFile.setUpdateDataTime(timeNow);
        AttachmentFile saveFile = fileService.save(attachmentFile);
        return resultMap("success", "完了", file.getOriginalFilename(), saveFile.getId(), saveFile.getSaveFileName());
    }

    private Map<String, String> resultMap(String result, String msg, String fileName, String fileId, String saveFileName) {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("result", result);
        resultMap.put("message",msg);
        resultMap.put("fileName",fileName);
        resultMap.put("fileId",fileId);
        resultMap.put("saveFileName", saveFileName);
        return resultMap;
    }

    @RequestMapping("/file/download/{id}")
    public String fileDownload(HttpServletResponse response, @PathVariable("id") String id) throws IOException {
        AttachmentFile attachmentFile = fileService.getAttachmentFile(id);
        File file = new File(resourceLoader.getResource(baseDir).getFile().getPath() + File.separator + attachmentFile.getSaveFileName());
        response.addHeader("Content-Type", "application/octet-stream");
        response.addHeader("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(attachmentFile.getOriginalFileName(), StandardCharsets.UTF_8.name()));
        Files.copy(file.toPath(), response.getOutputStream());
        return null;
    }
}
