package com.yunshu.oa.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;
import com.yunshu.oa.entity.*;
@Mapper
public interface DocMapper {
    List<DocFolder> listFolders();
    int insertFolder(DocFolder f);
    int deleteFolder(@Param("id") Long id);
    long countFiles(@Param("folderId") Long folderId, @Param("kw") String kw);
    List<DocFile> pageFiles(@Param("folderId") Long folderId, @Param("kw") String kw,
                            @Param("offset") int offset, @Param("size") int size);
    DocFile findFile(@Param("id") Long id);
    int insertFile(DocFile f);
    int deleteFile(@Param("id") Long id);
    int addDownload(@Param("id") Long id);
}
