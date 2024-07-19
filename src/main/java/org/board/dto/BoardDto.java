package org.board.dto;

import lombok.Data;

import java.util.List;

@Data
public class BoardDto {

    private int boardIdx;
    private String title;
    private String contents;
    private int hitCnt;
    private String createdDatetime;
    private String creatorId;
    private String updatedDatetime;
    private String updatorId;

    private List<BoardFileDto> fileInfoList;
}

