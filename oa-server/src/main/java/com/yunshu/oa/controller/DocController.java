package com.yunshu.oa.controller;

import com.yunshu.oa.common.PageResult;
import com.yunshu.oa.common.R;
import com.yunshu.oa.entity.DocFile;
import com.yunshu.oa.entity.DocFolder;
import com.yunshu.oa.security.RequirePerm;
import com.yunshu.oa.service.DocService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/docs")
@RequiredArgsConstructor
public class DocController {
    private final DocService service;

    @GetMapping("/folders") public R<List<DocFolder>> folders() { return R.ok(service.folders()); }

    @PostMapping("/folders")
    @RequirePerm("doc:manage")
    public R<DocFolder> addFolder(@RequestBody Map<String, Object> body) { return R.ok(service.addFolder(body)); }

    @DeleteMapping("/folders/{id}")
    @RequirePerm("doc:manage")
    public R<?> deleteFolder(@PathVariable Long id) { service.deleteFolder(id); return R.ok(); }

    @GetMapping("/files")
    public R<PageResult<DocFile>> files(@RequestParam(required = false) Long folderId,
                                        @RequestParam(required = false) String kw,
                                        @RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "20") int size) {
        return R.ok(service.files(folderId, kw, page, size));
    }

    @PostMapping("/files/upload")
    @RequirePerm("doc:manage")
    public R<DocFile> upload(@RequestParam(required = false) Long folderId,
                             @RequestParam("file") MultipartFile file) throws IOException {
        return R.ok(service.upload(folderId, file));
    }

    @GetMapping("/files/{id}/download")
    public void download(@PathVariable Long id, HttpServletResponse resp) throws IOException {
        DocFile f = service.info(id);
        if (f == null) { resp.setStatus(404); return; }
        byte[] bytes = service.download(id);
        String encoded = URLEncoder.encode(f.getName(), StandardCharsets.UTF_8).replace("+", "%20");
        resp.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        resp.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + encoded);
        resp.setContentLength(bytes.length);
        resp.getOutputStream().write(bytes);
    }

    @DeleteMapping("/files/{id}")
    @RequirePerm("doc:manage")
    public R<?> delete(@PathVariable Long id) { service.delete(id); return R.ok(); }
}
