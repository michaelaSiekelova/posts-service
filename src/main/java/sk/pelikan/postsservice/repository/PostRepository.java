package sk.pelikan.postsservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import sk.pelikan.postsservice.model.Post;

import java.util.List;

public interface PostRepository extends MongoRepository<Post, Integer> {
    List<Post> findByUserId(Integer userId);
}
