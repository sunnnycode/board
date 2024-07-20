package org.board.controller;

import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
import org.board.dto.BoardFileDto;
import org.board.dto.BoardInsertRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import org.board.dto.BoardDto;
import org.board.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class BoardController {

    @Autowired
    private BoardService boardService;

    @GetMapping("/board/openBoardList.do")
    public ModelAndView openBoardList(HttpServletRequest request) throws Exception {
        ModelAndView mv = new ModelAndView("/board/boardList");

        List<BoardDto> list = boardService.selectBoardList();
        mv.addObject("list", list);

        return mv;
    }

    @GetMapping("/board/openBoardWrite.do")
    public String openBoardWrite() throws Exception {
        return "/board/boardWrite";
    }

    @PostMapping("/board/insertBoard.do")
    public String insertBoard(BoardInsertRequest boardInsertRequest, MultipartHttpServletRequest request) throws Exception {
        // 서비스 메서드에 맞춰서 데이터를 변경
        // BoardDto boardDto = new BoardDto();
        // boardDto.setTitle(boardInsertRequest.getTitle());
        // boardDto.setContents(boardInsertRequest.getContents());
        BoardDto boardDto = new ModelMapper().map(boardInsertRequest, BoardDto.class);
        //                                        source              destination type
        boardService.insertBoard(boardDto, request);
        return "redirect:/board/openBoardList.do";
    }


    @GetMapping("/board/openBoardDetail.do")
    public ModelAndView openBoardDetail(@RequestParam("boardIdx") int boardIdx) throws Exception {
        ModelAndView mv = new ModelAndView("/board/boardDetail");

        BoardDto boardDto = boardService.selectBoardDetail(boardIdx);
        mv.addObject("board", boardDto);

        return mv;
    }

    @PostMapping("/board/updateBoard.do")
    public String updateBoard(BoardDto boardDto) throws Exception {
        boardService.updateBoard(boardDto);
        return "redirect:/board/openBoardList.do";
    }

    @PostMapping("/board/deleteBoard.do")
    public String deleteBoard(@RequestParam("boardIdx") int boardIdx) throws Exception {
        boardService.deleteBoard(boardIdx);
        return "redirect:/board/openBoardList.do";
    }

    @GetMapping("/board/downloadBoardFile.do")
    public void downloadBoardFile(@RequestParam("idx") int idx, @RequestParam("boardIdx") int boardIdx, HttpServletResponse response) throws Exception {
        // idx와 boardIdx가 일치하는 파일 정보를 조회
        BoardFileDto boardFileDto = boardService.selectBoardFileInfo(idx, boardIdx);
        if (ObjectUtils.isEmpty(boardFileDto)) {
            return;
        }

        // 원본 파일 저장 위치에서 파일을 읽어서 호출한 곳으로 첨부파일을 응답으로 전달
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
