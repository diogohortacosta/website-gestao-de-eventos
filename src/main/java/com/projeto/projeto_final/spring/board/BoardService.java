package com.projeto.projeto_final.spring.board;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BoardService {
    @Autowired
    private BoardRepository bRepository;

    public Board findById(int id) {
        return bRepository.findById(id);
    }

    public List<BoardDTO> findBoardsEventId(int eventId) {
        List<Board> boards = bRepository.findAllByEventId(eventId);
        return boards.stream().map(this::convertEntityToDto)
                .collect(Collectors.toList());
    }

    public BoardDTO convertEntityToDto(Board board) {
        BoardDTO boardDto = new BoardDTO();
        boardDto.setId(board.getId());
        boardDto.setName(board.getName());

        return boardDto;
    }
}
