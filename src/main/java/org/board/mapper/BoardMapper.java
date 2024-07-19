package org.board.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import org.apache.ibatis.annotations.Param;
import org.board.dto.BoardDto;
import org.board.dto.BoardFileDto;

@Mapper
public interface BoardMapper {
    List<BoardDto> selectBoardList();
    void insertBoard(BoardDto boardDto);
    BoardDto selectBoardDetail(int boardIdx);
    void updateHitCount(int boardIdx);
    void updateBoard(BoardDto boardDto);
    void deleteBoard(BoardDto boardDto);
    void insertBoardFileList(List<BoardFileDto> fileInfoList);
    List<BoardFileDto> selectBoardFileList(int boardIdx);
    BoardFileDto selectBoardFileInfo(@Param("idx") int idx, @Param("boardIdx") int boardIdx);
}
