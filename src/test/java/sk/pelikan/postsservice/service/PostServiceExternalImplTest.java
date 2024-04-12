package sk.pelikan.postsservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import sk.pelikan.postsservice.errorHandling.ExternalServiceException;
import sk.pelikan.postsservice.errorHandling.PostNotFoundException;
import sk.pelikan.postsservice.model.Post;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceExternalImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PostServiceExternalImpl postServiceExternal;

    private static final String URL = "https://jsonplaceholder.typicode.com/";
    private static final String POST_EP = "posts/";
    private static final String USER_EP = "users/";
    private static final String POST_FOR_USER_EP = "posts?userId=";

    @BeforeEach
    void setUp() {
        postServiceExternal = new PostServiceExternalImpl(restTemplate);
    }

    @Test
    void testFindPostByIdExt_Success() {
        // Given
        int postId = 1;
        Post post = new Post(postId, 1, "Test Title", "Test Body");
        when(restTemplate.getForEntity(any(String.class), any(Class.class))).thenReturn(new ResponseEntity<>(post, HttpStatus.OK));

        // When
        Post result = postServiceExternal.findPostByIdExt(1);

        // Then
        assertNotNull(result);
        assertEquals(postId, result.getId());
        assertEquals("Test Title", result.getTitle());
        assertEquals("Test Body", result.getBody());
    }

    @Test
    void testFindPostByIdExt_PostNotFound() {
        // Given
        when(restTemplate.getForEntity(any(String.class), any(Class.class))).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // When / Then
        assertThrows(PostNotFoundException.class, () -> postServiceExternal.findPostByIdExt(1));
    }

    @Test
    void testFindPostByIdExt_ExternalServiceError() {
        // Given
        when(restTemplate.getForEntity(any(String.class), any(Class.class))).thenThrow(new RestClientException("Not found"));

        // When / Then
        assertThrows(ExternalServiceException.class, () -> postServiceExternal.findPostByIdExt(1));
    }

    @Test
    void testValidateUserById_Success() {
        // Given
        when(restTemplate.getForEntity(any(String.class), any(Class.class))).thenReturn(new ResponseEntity<>("Test Body", HttpStatus.OK));

        // When
        boolean result = postServiceExternal.validateUserById(1);

        // Then
        assertTrue(result);
    }

    @Test
    void testValidateUserById_Error() {
        // Given
        when(restTemplate.getForEntity(any(String.class), any(Class.class))).thenThrow(new RestClientException("Internal Server Error"));

        // When / Then
        assertThrows(ExternalServiceException.class, () -> postServiceExternal.validateUserById(1));
    }

    @Test
    void testFindPostsByUserId_Success() {
        // Given
        List<Post> posts = Collections.singletonList(new Post(1, 1, "Test Title", "Test Body"));
        when(restTemplate.exchange(any(String.class), any(), any(), any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<>(posts, HttpStatus.OK));

        // When
        List<Post> result = postServiceExternal.findPostsByUserId(1);

        // Then
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void testFindPostsByUserId_Error() {
        // Given
        when(restTemplate.exchange(any(String.class), any(), any(), any(ParameterizedTypeReference.class)))
                .thenThrow(new RestClientException("Internal Server Error"));

        // When / Then
        assertThrows(ExternalServiceException.class, () -> postServiceExternal.findPostsByUserId(1));
    }

    @Test
    void testDeletePostById_Success() {
        // Given

        // When / Then
        assertDoesNotThrow(() -> postServiceExternal.deletePostById(1));
    }

    @Test
    void testDeletePostById_Error() {
        // Given
        doThrow(new RestClientException("Internal Server Error")).when(restTemplate).delete(any(String.class));

        // When / Then
        assertThrows(ExternalServiceException.class, () -> postServiceExternal.deletePostById(1));
    }


    @Test
    void testUpdatePostById_Success() {
        // Given
        int postId = 1;
        Post post = new Post(postId, 1, "Test Title", "Test Body");
        when(restTemplate.getForEntity(any(String.class), any(Class.class))).thenReturn(new ResponseEntity<>(post, HttpStatus.OK));
        doNothing().when(restTemplate).put(any(String.class), any());

        // When
        Post result = postServiceExternal.updatePostById(1, "Updated Title", "Updated Body");

        // Then
        assertNotNull(result);
        assertEquals(postId, result.getId());
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Body", result.getBody());
    }


    @Test
    void testUpdatePostById_PostNotFound() {
        // Given
        when(restTemplate.getForEntity(any(String.class), any(Class.class))).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // When / Then
        assertThrows(PostNotFoundException.class, () -> postServiceExternal.updatePostById(1, "Updated Title", "Updated Body"));
    }

    @Test
    void testUpdatePostById_Error() {
        // Given
        when(restTemplate.getForEntity(any(String.class), any(Class.class))).thenThrow(new RestClientException("Not Found"));

        // When / Then
        assertThrows(ExternalServiceException.class, () -> postServiceExternal.updatePostById(1, "Updated Title", "Updated Body"));
    }
}
