package boot.sample.shima.chat.service;

import boot.sample.shima.chat.entity.AttachmentFile;
import boot.sample.shima.chat.repository.AttachmentFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttachmentFileService {

    @Autowired
    AttachmentFileRepository repo;

    public List<AttachmentFile> getAttachmentFiles(String channelId) {
        return repo.findAllByChannelId(channelId);
    }

    public AttachmentFile getAttachmentFile(String id) {
        return repo.findOne(id);
    }

    public Map<String, String> saveFile(MultipartFile file, String channelId, String userId, Resource resource) {
        try {
            // ファイルが空の場合は異常終了
            if (file.isEmpty()) {
                // 異常終了時の処理
                throw new RuntimeException("ファイルが空です。");
            }
            File upDir = new File(resource.getFile().getPath());
            if (!upDir.exists()) {
                if (!upDir.mkdirs()) {
                    throw new RuntimeException("ディレクトリ作成失敗。");
                }
            }

            LocalDateTime timeNow = LocalDateTime.now();
            String saveTime = timeNow.format(DateTimeFormatter.ofPattern("YYYYMMddHHmmssS"));
            String saveFileName = saveTime + file.getOriginalFilename();

            // アップロードファイルを格納するディレクトリを作成する
            File uploadFile = new File(upDir, saveFileName);
            // アップロードファイルを置く
            File createFile =
                    new File(uploadFile.getPath());
            try (BufferedOutputStream uploadFileStream =
                         new BufferedOutputStream(new FileOutputStream(createFile))) {
                byte[] bytes = file.getBytes();
                uploadFileStream.write(bytes);
            } catch (Exception e) {
                // 異常終了時の処理
                throw e;
            } catch (Throwable t) {
                // 異常終了時の処理
                throw t;
            }

            // DBへ情報を登録
            AttachmentFile attachmentFile = new AttachmentFile();
            attachmentFile.setChannelId(channelId);
            attachmentFile.setSaveFileName(saveFileName);
            attachmentFile.setOriginalFileName(file.getOriginalFilename());
            attachmentFile.setUserId(userId);
            attachmentFile.setUpdateDataTime(timeNow);
            AttachmentFile saveFile = repo.save(attachmentFile);
            return resultMap("success", "完了", file.getOriginalFilename(), saveFile.getId(), saveFile.getSaveFileName());
        } catch (Exception e) {
            e.printStackTrace();
            return resultMap("failure", e.getMessage(), "", "", "");
        }
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

}
