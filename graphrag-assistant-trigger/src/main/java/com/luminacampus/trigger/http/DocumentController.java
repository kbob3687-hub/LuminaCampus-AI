package com.luminacampus.trigger.http;

import com.luminacampus.domain.adapter.IPythonGateway;
import com.luminacampus.domain.document.model.entity.DocumentEntity;
import com.luminacampus.domain.document.service.DocumentService;
import com.luminacampus.types.enums.ResponseCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/document")
public class DocumentController {

    private final DocumentService documentService;
    private final IPythonGateway pythonGateway;

    public DocumentController(DocumentService documentService, IPythonGateway pythonGateway) {
        this.documentService = documentService;
        this.pythonGateway = pythonGateway;
    }

    @PostMapping("/upload")
    public Map<String, Object> upload(@RequestAttribute("userId") Long userId,
                                      @RequestParam("file") MultipartFile file,
                                      @RequestParam(defaultValue = "") String subject) throws Exception {
        String fileName = file.getOriginalFilename();

        // 1. 转发文件到 Python 服务
        String pythonResult = pythonGateway.upload(file.getBytes(), fileName, subject);

        // 2. 保存文档元数据到 MySQL
        documentService.saveDocument(userId, fileName, subject);

        Map<String, Object> result = new HashMap<>();
        result.put("code", ResponseCode.SUCCESS.getCode());
        result.put("info", "上传成功");
        result.put("pythonResult", pythonResult);
        return result;
    }

    @GetMapping("/list")
    public Map<String, Object> list(@RequestAttribute("userId") Long userId) {
        List<DocumentEntity> documents = documentService.queryByUserId(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("code", ResponseCode.SUCCESS.getCode());
        result.put("data", documents);
        return result;
    }

}
