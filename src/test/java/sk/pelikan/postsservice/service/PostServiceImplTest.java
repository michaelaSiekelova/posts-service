package sk.pelikan.postsservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sk.pelikan.postsservice.errorHandling.PostNotFoundException;
import sk.pelikan.postsservice.errorHandling.UserNotFoundException;
import sk.pelikan.postsservice.model.Post;
import sk.pelikan.postsservice.mongo.SequenceGeneratorService;
import sk.pelikan.postsservice.repository.PostRepository;
import sk.pelikan.postsservice.dtos.PostDTO;
import sk.pelikan.postsservice.service.PostServiceExternal;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private SequenceGeneratorService sequenceGeneratorService;

    @Mock
    private PostServiceExternal postServiceExternal;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    void testAddPost_Success() {
        // Given
        when(sequenceGeneratorService.generateSequence(anyString())).thenReturn(1);
        when(postServiceExternal.validateUserById(anyInt())).thenReturn(true);
        when(postRepository.save(any(Post.class))).thenReturn(new Post(1, 1, "Test Title", "Test Body"));

        // When
        Post result = postService.addPost(new PostDTO(1, 1, "New Title", "New Body"));

        // Then
        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(1, result.getUserId());
        assertEquals("Test Title", result.getTitle());
        assertEquals("Test Body", result.getBody());
    }

    @Test
    void testAddPost_UserNotFound() {
        // Given
        when(postServiceExternal.validateUserById(anyInt())).thenReturn(false);

        // When / Then
        assertThrows(UserNotFoundException.class,
                () -> postService.addPost(new PostDTO(1, 1, "New title", "New body")));
    }

    @Test
    void testGetPostById_Success() {
        // Given
        Post post = new Post(1, 1, "Test Title", "Test Body");
        when(postRepository.findById(anyInt())).thenReturn(Optional.of(post));

        // When
        Post result = postService.getPostById(1);

        // Then
        assertNotNull(result);
        assertEquals(post, result);
    }

    @Test
    void testGetPostById_PostNotFound() {
        // Given
        when(postRepository.findById(anyInt())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(PostNotFoundException.class, () -> postService.getPostById(1));
    }

    @Test
    void testGetPostByUserId_Success() {
        // Given
        when(postServiceExternal.validateUserById(anyInt())).thenReturn(true);
        when(postRepository.findByUserId(anyInt())).thenReturn(Arrays.asList(new Post(1, 1, "Test Title", "Test Body")));

        // When
        List<Post> result = postService.getPostByUserId(1);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testGetPostByUserId_UserNotFound() {
        // Given
        when(postServiceExternal.validateUserById(anyInt())).thenReturn(false);

        // When / Then
        assertThrows(UserNotFoundException.class, () -> postService.getPostByUserId(1));
    }

    @Test
    void testDeletePost_Success() {
        // Given
        Post post = new Post(1, 1, "Test Title", "Test Body");
        when(postRepository.findById(anyInt())).thenReturn(Optional.of(post));

        // When
        postService.deletePost(1);

        // Then
        verify(postRepository, times(1)).deleteById(1);
        verify(postServiceExternal, times(1)).deletePostById(1);
    }

    @Test
    void testDeletePost_PostNotFound() {
        // Given
        when(postRepository.findById(anyInt())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(PostNotFoundException.class, () -> postService.deletePost(1));
        verify(postServiceExternal, never()).deletePostById(anyInt());
    }

    @Test
    void testUpdatePost_Success() {
        // Given
        Post post = new Post(1, 1, "Test Title", "Test Body");
        when(postRepository.findById(anyInt())).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);

        // When
        Post updatedPost = postService.updatePost(1, "Updated Title", "Updated Body");

        // Then
        assertNotNull(updatedPost);
        assertEquals("Updated Title", updatedPost.getTitle());
        assertEquals("Updated Body", updatedPost.getBody());
    }

    @Test
    void testUpdatePost_PostNotFound() {
        // Given
        when(postRepository.findById(anyInt())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(PostNotFoundException.class, () -> postService.updatePost(1, "Updated Title", "Updated Body"));
    }
}
