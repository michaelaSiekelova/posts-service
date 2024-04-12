package sk.pelikan.postsservice.service;

import sk.pelikan.postsservice.model.Post;

import java.util.List;

public interface PostServiceExternal {

    Post findPostByIdExt (int id);

    boolean validateUserById(int userId);

    List<Post> findPostsByUserId(int userId);

    void deletePostById(int id);

    Post updatePostById(int id,String title,String body);
}
