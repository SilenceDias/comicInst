package com.example.marvelinst.repos;

import com.example.marvelinst.entity.Comment;
import com.example.marvelinst.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPost(Post post);

    Comment findByIdAndUserId(Long id, Long userId);
}
