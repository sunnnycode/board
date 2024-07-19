package org.board.service;

import java.util.List;

import org.board.dto.BoardFileDto;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import org.board.dto.BoardDto;

public interface BoardService {
    List<BoardDto> selectBoardList();
    void insertBoard(BoardDto boardDto, MultipartHttpServletRequest request) throws Exception;
    BoardDto selectBoardDetail(int boardIdx);
    void updateBoard(BoardDto boardDto);
    void deleteBoard(int boardIdx);
    BoardFileDto selectBoardFileInfo(int idx, int boardIdx);
}


