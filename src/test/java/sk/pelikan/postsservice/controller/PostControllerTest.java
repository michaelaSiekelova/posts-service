package sk.pelikan.postsservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import sk.pelikan.postsservice.controller.PostController;
import sk.pelikan.postsservice.errorHandling.PostNotFoundException;
import sk.pelikan.postsservice.errorHandling.UserNotFoundException;
import sk.pelikan.postsservice.model.Post;
import sk.pelikan.postsservice.dtos.PostDTO;
import sk.pelikan.postsservice.service.PostServiceImpl;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PostControllerTest {

    @Mock
    private PostServiceImpl postService;

    @InjectMocks
    private PostController postController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddPost_Success() {
        // Given
        PostDTO postDTO = new PostDTO(1,1,"Title", "Body");
        Post savedPost = new Post(1,1, "Title", "Body");
        when(postService.addPost(postDTO)).thenReturn(savedPost);

        // When
        ResponseEntity<?> response = postController.addPost(postDTO, mock(BindingResult.class));

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedPost, response.getBody());
    }

    @Test
    void testAddPost_ValidationError() {
        // Given
        PostDTO postDTO = new PostDTO(1,1,"", "Body");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        // When
        ResponseEntity<?> response = postController.addPost(postDTO, bindingResult);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testGetPostById_Success() {
        // Given
        int postId = 1;
        Post post = new Post(postId,1, "Title", "Body");
        when(postService.getPostById(postId)).thenReturn(post);

        // When
        ResponseEntity<Post> response = postController.getPostById(postId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(post, response.getBody());
    }

    @Test
    void testGetPostById_PostNotFound() {
        // Given
        int postId = 1;
        when(postService.getPostById(postId)).thenThrow(new PostNotFoundException("Post not found"));

        // When
        ResponseEntity<Post> response = postController.getPostById(postId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetPostsByUserId_Success() {
        // Given
        int userId = 1;
        List<Post> posts = Collections.singletonList(new Post(1,1, "Title", "Body"));
        when(postService.getPostByUserId(userId)).thenReturn(posts);

        // When
        ResponseEntity<List<Post>> response = postController.getPostsByUserId(userId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(posts, response.getBody());
    }

    @Test
    void testGetPostsByUserId_UserNotFound() {
        // Given
        int userId = 1;
        when(postService.getPostByUserId(userId)).thenThrow(new UserNotFoundException("User not found"));

        // When
        ResponseEntity<List<Post>> response = postController.getPostsByUserId(userId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testUpdatePost_Success() {
        // Given
        int id = 1;
        PostDTO postDTO = new PostDTO(1,null,"New Title", "New Body");
        Post updatedPost = new Post(id,1, "New Title", "New Body");
        when(postService.updatePost(id, postDTO.getTitle(), postDTO.getBody())).thenReturn(updatedPost);

        // When
        ResponseEntity<String> response = postController.updatePost(id, postDTO);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testUpdatePost_UserNotFound() {
        // Given
        int id = 1;
        PostDTO postDTO = new PostDTO(1,null,"New Title", "New Body");
        when(postService.updatePost(id, postDTO.getTitle(), postDTO.getBody())).thenThrow(new UserNotFoundException("User not found"));

        // When
        ResponseEntity<String> response = postController.updatePost(id, postDTO);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found.", response.getBody());
    }

    @Test
    void testDeletePost_Success() {
        // Given
        int id = 1;

        // When
        ResponseEntity<String> response = postController.deletePost(id);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testDeletePost_PostNotFound() {
        // Given
        int id = 1;
        doThrow(new PostNotFoundException("Post not found")).when(postService).deletePost(id);

        // When
        ResponseEntity<String> response = postController.deletePost(id);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Post not found.", response.getBody());
    }


}
