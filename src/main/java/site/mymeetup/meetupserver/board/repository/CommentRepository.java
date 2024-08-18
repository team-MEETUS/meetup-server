package site.mymeetup.meetupserver.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import site.mymeetup.meetupserver.board.entity.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findCommentByBoard_BoardIdAndStatus(Long boardId, int status, Pageable pageable);

    Optional<Comment> findByBoard_BoardIdAndCommentId(Long boardId, Long commentId);
}
