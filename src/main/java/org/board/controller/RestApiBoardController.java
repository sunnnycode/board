package org.board.controller;

import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import org.board.dto.BoardDto;
import org.board.dto.BoardFileDto;
import org.board.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
// @Controller와 @ResponseBody 애노테이션을 결합한 형태
// 해당 API의 응답 결과를 JSON 형식으로 변환해서 응답 본문으로 전송
@RestController
@RequestMapping("/api")
public class RestApiBoardController {

    @Autowired
    private BoardService boardService;

    @GetMapping("/board")
    public List<BoardDto> openBoardList(HttpServletRequest request) throws Exception {
        return boardService.selectBoardList();
    }

    @PostMapping("/board/write")
    public void insertBoard(@RequestBody BoardDto boardDto, MultipartHttpServletRequest request) throws Exception {
        boardService.insertBoard(boardDto, request);
    }

    @GetMapping("/board/{boardIdx}")
    public BoardDto openBoardDetail(@PathVariable("boardIdx") int boardIdx) throws Exception {
        return boardService.selectBoardDetail(boardIdx);
    }

    @PutMapping("/board/{boardIdx}")
    public void updateBoard(@PathVariable("boardIdx") int boardIdx, @RequestBody BoardDto boardDto) throws Exception {
        boardDto.setBoardIdx(boardIdx);
        boardService.updateBoard(boardDto);
    }

    @DeleteMapping("/board/{boardIdx}")
    public void deleteBoard(@PathVariable("boardIdx") int boardIdx) throws Exception {
        boardService.deleteBoard(boardIdx);
    }

    @GetMapping("/board/file/{boardIdx}/{idx}")
    public void downloadBoardFile(@PathVariable("idx") int idx, @PathVariable("boardIdx") int boardIdx, HttpServletResponse response) throws Exception {
        BoardFileDto boardFileDto = boardService.selectBoardFileInfo(idx, boardIdx);
        if (ObjectUtils.isEmpty(boardFileDto)) {
            return;
        }

        Path path = Paths.get(boardFileDto.getStoredFilePath());
        byte[] file = Files.readAllBytes(path);

        response.setContentType("application/octet-stream");
        response.setContentLength(file.length);
        response.setHeader("Content-Disposition", "attachment; fileName=\"" + URLEncoder.encode(boardFileDto.getOriginalFileName(), "UTF-8") + "\";");
        response.setHeader("Content-Transfer-Encoding", "binary");

        response.getOutputStream().write(file);
        response.getOutputStream().flush();
        response.getOutputStream().close();
    }
}
