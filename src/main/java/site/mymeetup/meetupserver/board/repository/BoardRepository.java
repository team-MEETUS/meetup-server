package site.mymeetup.meetupserver.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.mymeetup.meetupserver.board.entity.Board;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    // crewId로 게시글 조회
    @Query("SELECT b FROM Board b WHERE b.crew.crewId = :crewId AND b.status <> :status ORDER BY CASE WHEN b.status = 2 THEN 0 ELSE 1 END, b.createDate DESC")
    Page<Board> findBoardByCrew_CrewIdAndStatusNotWithCustomSort(@Param("crewId") Long crewId, @Param("status") int status, Pageable pageable);

    // crewId & category로 게시글 조회
    @Query("SELECT b FROM Board b WHERE b.crew.crewId = :crewId AND b.category = :category AND b.status <> :status ORDER BY CASE WHEN b.status = 2 THEN 0 ELSE 1 END, b.createDate DESC")
    Page<Board> findBoardByCrew_CrewIdAndCategoryAndStatusNotWithCustomSort(@Param("crewId") Long crewId, @Param("category") String category, @Param("status") int status, Pageable pageable);

    // boardId와 status 로 게시글 조회
    Optional<Board> findBoardByBoardIdAndStatusNotAndCrew_CrewId(Long boardId, int status, Long crewId);
}
