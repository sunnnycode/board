package org.board.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.board.dto.BoardDto;
import org.board.dto.BoardFileDto;
import org.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Controller
public class RestBoardController {

    @Autowired
    private BoardService boardService;

    // 게시판 목록 조회
    // @RequestMapping(value="/board", method=RequestMethod.GET)
    @GetMapping("/board")
    public ModelAndView openBoardList(HttpServletRequest request) throws Exception {
        ModelAndView mv = new ModelAndView("/board/restBoardList");

        List<BoardDto> list = boardService.selectBoardList();
        mv.addObject("list", list);

        return mv;
    }

    // 게시판 등록 화면
    @GetMapping("/board/write")
    public String openBoardWrite() throws Exception {
        return "/board/restBoardWrite";
    }

    // 게시판 등록
    @PostMapping("/board/write")
    public String insertBoard(BoardDto boardDto, MultipartHttpServletRequest request) throws Exception {
        boardService.insertBoard(boardDto, request);
        return "redirect:/board";
    }

    // 게시판 상세 조회
    @GetMapping("/board/{boardIdx}")
    public ModelAndView openBoardDetail(@PathVariable("boardIdx") int boardIdx) throws Exception {
        ModelAndView mv = new ModelAndView("/board/restBoardDetail");

        BoardDto boardDto = boardService.selectBoardDetail(boardIdx);
        mv.addObject("board", boardDto);

        return mv;
    }

    // 게시판 수정
    @PutMapping("/board/{boardIdx}")
    public String updateBoard(@PathVariable("boardIdx") int boardIdx, BoardDto boardDto) throws Exception {
        boardService.updateBoard(boardDto);
        return "redirect:/board";
    }

    // 게시판 삭제
    @DeleteMapping("/board/{boardIdx}")
    public String deleteBoard(@PathVariable("boardIdx") int boardIdx) throws Exception {
        boardService.deleteBoard(boardIdx);
        return "redirect:/board";
    }

    // 첨부파일 다운로드
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
