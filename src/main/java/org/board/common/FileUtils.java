package org.board.common;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import org.board.dto.BoardFileDto;

@Component
public class FileUtils {

    @Value("${spring.servlet.multipart.location}")
    private String uploadDir;

    public List<BoardFileDto> parseFileInfo(int boardIdx, MultipartHttpServletRequest request) throws Exception {

        if (ObjectUtils.isEmpty(request)) {
            return null;
        }

        // 파일 정보를 저장할 객체를 생성 => 해당 메서드에서 반환하는 값
        List<BoardFileDto> fileInfoList = new ArrayList<>();

        // 파일을 저장할 디렉터리를 지정 (날짜별로 저장하고 존재하지 않는 경우 생성)
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
        ZonedDateTime now = ZonedDateTime.now();
        String storedDir = uploadDir + now.format(dtf);
        File dir = new File(storedDir);
        if (!dir.exists()) {
            dir.mkdir();
        }

        Iterator<String> fileTagNames = request.getFileNames();
        while (fileTagNames.hasNext()) {
            String fileTagName = fileTagNames.next();
            List<MultipartFile> files = request.getFiles(fileTagName);
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;

                String contentType = file.getContentType();
                if (ObjectUtils.isEmpty(contentType)) continue;

                // Content-Type을 체크해서 이미지 파일인 경우에 한 해서
                // 지정된 확장자로 저장되도록 설정
                String fileExtension = "";
                if (contentType.contains("jpeg")) {
                    fileExtension = ".jpg";
                } else if (contentType.contains("png")) {
                    fileExtension = ".png";
                } else if (contentType.contains("gif")) {
                    fileExtension = ".gif";
                } else {
                    continue;
                }

                // 저장에 사용할 파일 이름을 조합 (현재 시간을 파일명으로 사용)
                String storedFileName = System.nanoTime() + fileExtension;

                // 파일 정보를 리스트에 저장
                BoardFileDto dto = new BoardFileDto();
                dto.setBoardIdx(boardIdx);
                dto.setFileSize(Long.toString(file.getSize()));
                dto.setOriginalFileName(file.getOriginalFilename());
                dto.setStoredFilePath(storedDir + "/" + storedFileName);
                fileInfoList.add(dto);

                // 파일 저장
                dir = new File(storedDir + "/" + storedFileName);
                file.transferTo(dir);
            }
        }

        return fileInfoList;
    }
}
