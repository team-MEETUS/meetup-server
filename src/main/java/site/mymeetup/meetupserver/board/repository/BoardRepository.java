package site.mymeetup.meetupserver.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mymeetup.meetupserver.board.entity.Board;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    // crewId로 게시글 조회
    List<Board> findBoardByCrewCrewId(Long crewId);

    // crewId & category 로 게시글 조회
    List<Board> findBoardByCrew_CrewIdAndCategory(Long crewId, String category);
}
