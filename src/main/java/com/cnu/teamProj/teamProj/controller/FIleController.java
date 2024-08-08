//package com.cnu.teamProj.teamProj.controller;
//
//import com.mongodb.client.gridfs.GridFSBucket;
////import org.apache.commons.io.IOUtils;
//import org.bson.types.ObjectId;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.mongodb.core.query.Criteria;
//import org.springframework.data.mongodb.core.query.Query;
//import org.springframework.data.mongodb.gridfs.GridFsTemplate;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//
//@RestController
//@RequestMapping("/files")
//class FileController {
//
//    @Autowired
//    private GridFsTemplate gridFsTemplate;
//
//    @Autowired
//    private GridFSBucket gridFSBucket;
//
//    @PostMapping("/upload")
//    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
//        ObjectId fileId = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType());
//        return ResponseEntity.ok(fileId.toString());
//    }
//
//    @GetMapping("/download/{id}")
//    public ResponseEntity<byte[]> downloadFile(@PathVariable String id) throws IOException {
//        var gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(id)));
//        if (gridFSFile == null) {
//            return ResponseEntity.notFound().build();
//        }
//        var gridOut = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
//        /*
//        byte[] content = IOUtils.toByteArray(gridOut);
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(gridFSFile.getMetadata().get("_contentType").toString()))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + gridFSFile.getFilename() + "\"")
//                .body(content);
//
//         */
//        return null;
//    }
//}
