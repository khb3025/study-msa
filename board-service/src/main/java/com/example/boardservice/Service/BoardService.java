package com.example.boardservice.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.boardservice.client.PointClient;
import com.example.boardservice.client.UserClient;
import com.example.boardservice.domain.Board;
import com.example.boardservice.domain.BoardRepository;
import com.example.boardservice.dto.BoardResponseDto;
import com.example.boardservice.dto.CreateBoardRequestDto;
import com.example.boardservice.dto.UserDto;
import com.example.boardservice.dto.UserResponseDto;

import jakarta.transaction.Transactional;

@Service
public class BoardService {
    
    private final BoardRepository boardRepository;
    private final UserClient userClient;
    private final PointClient pointClient;

    public BoardService(
        BoardRepository boardRepository,
        UserClient userClient,
        PointClient pointClient
    ) {
        this.boardRepository = boardRepository;
        this.userClient = userClient;
        this.pointClient = pointClient;
    }

    @Transactional
    public void create(CreateBoardRequestDto createBoardRequestDto) {

        // 포인트 차감 100 포인트
        pointClient.deductPoints(createBoardRequestDto.getUserId(), 100);

        // 게시글 작성
        Board board = new Board(
            createBoardRequestDto.getTitle(),
            createBoardRequestDto.getContent(),
            createBoardRequestDto.getUserId()
        );
        boardRepository.save(board);

        // 활동점수 적립 활동점수 10점 부여
        userClient.addActivityScore(createBoardRequestDto.getUserId(), 10);
    }

    public BoardResponseDto getBoard(Long boardId) {
        Board board = boardRepository.findById(boardId)
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을수 없습니다."));
        
        // user-service로 부터 사용자 정보 불러오기
        Optional<UserResponseDto> optionalUserResponseDto = userClient.fetchUser(board.getUserId());
        
        UserDto userDto = null;
        if (optionalUserResponseDto.isPresent()) {
            UserResponseDto userResponseDto = optionalUserResponseDto.get();
            // 응답값 조합하기
            userDto = new UserDto(
                userResponseDto.getUserId(),
                userResponseDto.getName()
            );
        }

        return new BoardResponseDto(
            board.getBoardId(),
            board.getTitle(),
            board.getContent(),
            userDto
        );

    }

    public List<BoardResponseDto> getBoards() {
        List<Board> boards = boardRepository.findAll();
        List<Long> userIds = boards.stream()
            .map(Board::getUserId)
            .distinct()
            .toList();
        List<UserResponseDto> userResponseDtos = userClient.fetchUsersByIds(userIds);
        
        // userId를 Key로 하는 Map 생성
        Map<Long, UserDto> userMap = userResponseDtos.stream()
            .collect(Collectors.toMap(
                UserResponseDto::getUserId,
                userResponseDto -> new UserDto(
                    userResponseDto.getUserId(),
                    userResponseDto.getName()
                )
            ));
        
        return boards.stream()
            .map(board -> new BoardResponseDto(
                board.getBoardId(),
                board.getTitle(),
                board.getContent(),
                userMap.get(board.getUserId())
            ))
            .toList();
    }

}
