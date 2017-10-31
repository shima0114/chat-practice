package boot.sample.shima.chat.controller.rest;

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
        Resource resource =resourceLoader.getResource(baseDir);
        return fileService.saveFile(file, channelId, userId, resource);
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
