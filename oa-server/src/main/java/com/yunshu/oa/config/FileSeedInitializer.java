package com.yunshu.oa.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/** 为演示库中的种子文档生成真实可读的占位文件，保证「下载」按钮确实能拿到文件 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileSeedInitializer {
    private final JdbcTemplate jdbc;
    @Value("${oa.upload.dir}") private String uploadDir;

    @PostConstruct
    public void init() {
        try {
            List<Map<String, Object>> rows = jdbc.queryForList(
                    "SELECT name, storage_path, uploader_name, file_size FROM doc_file WHERE storage_path LIKE 'seed/%'");
            for (Map<String, Object> r : rows) {
                Path p = Paths.get(uploadDir, String.valueOf(r.get("storage_path")));
                if (Files.exists(p)) continue;
                Files.createDirectories(p.getParent());
                String content = "云枢 OA · 云文档演示文件\n\n"
                        + "文件名：" + r.get("name") + "\n"
                        + "上传人：" + r.get("uploader_name") + "\n"
                        + "原始大小（字节）：" + r.get("file_size") + "\n\n"
                        + "本文件为演示占位件。真实部署时，上传接口会把用户文件写入 oa.upload.dir，\n"
                        + "下载接口按 doc_file.storage_path 回读，生产环境可替换为 MinIO / OSS 对象存储。\n";
                Files.write(p, content.getBytes(StandardCharsets.UTF_8));
            }
            log.info("云文档演示文件初始化完成，共 {} 个", rows.size());
        } catch (Exception e) {
            log.warn("云文档演示文件初始化跳过：{}", e.getMessage());
        }
    }
}
