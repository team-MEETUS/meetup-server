package site.mymeetup.meetupserver.board.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import site.mymeetup.meetupserver.board.entity.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findCommentByBoard_BoardIdAndStatus(Long boardId, int status, Pageable pageable);
}
