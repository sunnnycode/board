package org.board.dto;

import lombok.Data;

@Data
public class BoardFileDto {
    private int idx;
    private int boardIdx;
    private String originalFileName;
    private String storedFilePath;
    private String fileSize;		// 천단위 콤마를 쿼리에서 적용한 결과를 저장해야 하므로
    private String createdDatetime;
    private String creatorId;
    private String updatedDatetime;
    private String updatorId;
}

