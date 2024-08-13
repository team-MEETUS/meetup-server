package site.mymeetup.meetupserver.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import site.mymeetup.meetupserver.board.entity.Board;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    // crewId로 게시글 조회
    Page<Board> findBoardByCrew_CrewIdAndStatusNot(Long crewId, int status, Pageable pageable);

    // crewId & category 로 게시글 조회
    Page<Board> findBoardByCrew_CrewIdAndCategoryAndStatusNot(Long crewId, String category, int status, Pageable pageable);
}
