package com.example.boardservice.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.boardservice.client.PointClient;
import com.example.boardservice.client.UserClient;
import com.example.boardservice.domain.Board;
import com.example.boardservice.domain.BoardRepository;
import com.example.boardservice.dto.BoardResponseDto;
import com.example.boardservice.dto.CreateBoardRequestDto;
import com.example.boardservice.dto.UserDto;
import com.example.boardservice.dto.UserResponseDto;
import com.example.boardservice.event.BoardCreatedEvent;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
public class BoardService {
    
    private final BoardRepository boardRepository;
    private final UserClient userClient;
    private final PointClient pointClient;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public BoardService(
        BoardRepository boardRepository,
        UserClient userClient,
        PointClient pointClient,
        KafkaTemplate<String, String> kafkaTemplate
    ) {
        this.boardRepository = boardRepository;
        this.userClient = userClient;
        this.pointClient = pointClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    
    public void create(CreateBoardRequestDto createBoardRequestDto, Long userId) {
        // 게시글 저장을 성공했는지 판단하는 플래그
        boolean isBoardCreated = false;
        Long savedBoardId = null;
        
        // 포인트 차감을 성공했는지 판단하는 플래그
        boolean isPointDeducted = false;
        try{
            // 포인트 차감 100 포인트
            pointClient.deductPoints(userId, 100);
            System.out.println("포인트 차감 성공");
            isPointDeducted = true;

            // 게시글 작성
            Board savedBoard = new Board(
                createBoardRequestDto.getTitle(),
                createBoardRequestDto.getContent(),
                userId
            );
            boardRepository.save(savedBoard);
            savedBoardId = savedBoard.getBoardId();
            isBoardCreated = true; //게시글 저장 성공 플래그
            System.out.println("게시글 작성 성공");

            
            // 활동점수 적립 활동점수 10점 부여 -> Kafka 이벤트로 전환
            // userClient.addActivityScore(createBoardRequestDto.getUserId(), 10);
            // System.out.println("포인트 적립 성공");
            
            BoardCreatedEvent boardCreatedEvent =
                new BoardCreatedEvent(userId);
            this.kafkaTemplate.send("board-created", this.toJsonString(boardCreatedEvent));
            System.out.println("게시글 작성 완료 이벤트 발행");

        }catch(Exception e){
            System.out.println("예외 발생");
            System.out.println("isBoardCreated :" + isBoardCreated);
            System.out.println("isPointDeducted :" + isPointDeducted);
            
            if(isBoardCreated){
                // 게시글 작성 보상 트랜잭션 => 게시글 삭제
                boardRepository.deleteById(savedBoardId);
                System.out.println("[보상 트랜잭션] 게시글 삭제");
            }

            if(isPointDeducted){
                // 포인트 차감에 대한 보상 트랜잭션
                pointClient.addPoints(userId, 100);
                System.out.println("[보상 트랜잭션] 포인트 적립");
            }

            // 실패 응답으로 처리하기 위한 예외 처리기
            throw e;
        }
    }


    public String toJsonString(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JacksonException e) {
            e.printStackTrace();
            throw new RuntimeException("Json 직렬화 실패");
        }
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
    

    // 연관관계를 활용한 특정 게시글 조회
    public BoardResponseDto getBoard2(Long boardId) {
        Board board = boardRepository.findById(boardId)
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        
        BoardResponseDto boardResponseDto = new BoardResponseDto(
            board.getBoardId(),
            board.getTitle(),
            board.getContent(),
            new UserDto(
                board.getUser().getUserId(),
                board.getUser().getName()
            )
        );
        return boardResponseDto;
    }

    //연관관계를 활용한 게시글 전체 조회
    public List<BoardResponseDto> getBoards2() {
        List<Board> boards = boardRepository.findAll();
        return boards.stream().map( board ->{
            BoardResponseDto boardResponseDto = new BoardResponseDto(
                board.getBoardId(),
                board.getTitle(),
                board.getContent(),
                new UserDto(
                    board.getUser().getUserId(),
                    board.getUser().getName()
                )
            );
            return boardResponseDto;
        }).toList();
    }
}
