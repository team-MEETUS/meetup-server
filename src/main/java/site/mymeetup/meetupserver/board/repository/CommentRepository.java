package site.mymeetup.meetupserver.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.mymeetup.meetupserver.board.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
