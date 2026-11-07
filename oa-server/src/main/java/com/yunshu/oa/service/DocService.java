package com.yunshu.oa.service;

import com.yunshu.oa.common.BizException;
import com.yunshu.oa.common.PageResult;
import com.yunshu.oa.common.Params;
import com.yunshu.oa.common.UserContext;
import com.yunshu.oa.entity.DocFile;
import com.yunshu.oa.entity.DocFolder;
import com.yunshu.oa.mapper.DocMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/** 云文档：文件夹、上传落盘、版本与下载计数 */
@Service
@RequiredArgsConstructor
public class DocService {
    private final DocMapper mapper;
    @Value("${oa.upload.dir}") private String uploadDir;

    public List<DocFolder> folders() { return mapper.listFolders(); }

    public DocFolder addFolder(Map<String, Object> body) {
        DocFolder f = new DocFolder();
        f.setParentId(Params.l(body, "parentId") == null ? 0L : Params.l(body, "parentId"));
        f.setName(Params.s(body, "name"));
        f.setOwnerId(UserContext.userId());
        if (f.getName().isEmpty()) throw new BizException("文件夹名称不能为空");
        mapper.insertFolder(f);
        return f;
    }

    public void deleteFolder(Long id) {
        if (mapper.countFiles(id, "") > 0) throw new BizException("文件夹内还有文件，不能删除");
        mapper.deleteFolder(id);
    }

    public PageResult<DocFile> files(Long folderId, String kw, int page, int size) {
        int p = Math.max(page, 1), s = Math.min(Math.max(size, 1), 100);
        return PageResult.of(mapper.countFiles(folderId, kw),
                mapper.pageFiles(folderId, kw, (p - 1) * s, s), p, s);
    }

    public DocFile upload(Long folderId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) throw new BizException("请选择要上传的文件");
        String original = file.getOriginalFilename() == null ? "unnamed.bin" : file.getOriginalFilename();
        String ext = original.contains(".") ? original.substring(original.lastIndexOf('.') + 1) : "bin";
        String stored = "upload/" + UUID.randomUUID().toString().replace("-", "") + "." + ext;
        Path target = Paths.get(uploadDir, stored);
        Files.createDirectories(target.getParent());
        file.transferTo(target.toAbsolutePath().normalize());

        DocFile f = new DocFile();
        f.setFolderId(folderId == null ? 0L : folderId);
        f.setName(original); f.setExt(ext); f.setFileSize(file.getSize());
        f.setVersion(1); f.setStoragePath(stored);
        f.setUploaderId(UserContext.userId()); f.setUploaderName(UserContext.name());
        mapper.insertFile(f);
        return f;
    }

    public byte[] download(Long id) throws IOException {
        DocFile f = mapper.findFile(id);
        if (f == null) throw new BizException("文件不存在");
        Path p = Paths.get(uploadDir, f.getStoragePath());
        if (!Files.exists(p)) throw new BizException("文件已丢失：" + f.getName());
        mapper.addDownload(id);
        return Files.readAllBytes(p);
    }

    public DocFile info(Long id) { return mapper.findFile(id); }
    public void delete(Long id) { mapper.deleteFile(id); }
}
