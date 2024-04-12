package sk.pelikan.postsservice.service;

import sk.pelikan.postsservice.model.Post;
import sk.pelikan.postsservice.dtos.PostDTO;

import java.util.List;

public interface PostService {

    // Metóda na pridanie príspevku
    Post addPost(PostDTO post);

    // Metóda na získanie príspevku podľa ID
    Post getPostById(int id);

    // Metóda na získanie príspevku podľa ID používateľa
    List<Post> getPostByUserId(int userId);

    // Metóda na aktualizáciu príspevku
    Post updatePost(int id, String title, String body);

    // Metóda na odstránenie príspevku
    void deletePost(int id);
}
