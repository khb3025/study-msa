package com.example.boardservice.Service;

import org.springframework.stereotype.Service;

import com.example.boardservice.domain.Board;
import com.example.boardservice.domain.BoardRepository;
import com.example.boardservice.dto.CreateBoardRequestDto;

import jakarta.transaction.Transactional;

@Service
public class BoardService {
    private final BoardRepository boardRepository;
    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }
    @Transactional
    public void create(CreateBoardRequestDto createBoardRequestDto) {
        Board board = new Board(
            createBoardRequestDto.getTitle(),
            createBoardRequestDto.getContent(),
            createBoardRequestDto.getUserId()
        );
        boardRepository.save(board);
    }
}
