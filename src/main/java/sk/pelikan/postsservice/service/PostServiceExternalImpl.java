package sk.pelikan.postsservice.service;


import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import sk.pelikan.postsservice.errorHandling.ExternalServiceException;
import sk.pelikan.postsservice.errorHandling.PostNotFoundException;
import sk.pelikan.postsservice.model.Post;

import java.util.List;

@Service
public class PostServiceExternalImpl implements PostServiceExternal {

    private static final String URL = "https://jsonplaceholder.typicode.com/";
    private static final String POST_EP = "posts/";
    private static final String USER_EP = "users/";
    private static final String POST_FOR_USER_EP = "posts?userId=";
    private final RestTemplate restTemplate;

    @Autowired
    public PostServiceExternalImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Post findPostByIdExt(int id) {
        String url = URL + POST_EP + id;
        try {
            ResponseEntity<Post> responseEntity = restTemplate.getForEntity(url, Post.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                return responseEntity.getBody();
            } else if (responseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new PostNotFoundException("Post with ID " + id + " not found");
            } else {
                throw new ExternalServiceException("External service error: " + responseEntity.getStatusCode());
            }
        } catch (HttpClientErrorException ex) {
            throw new PostNotFoundException("Post with ID " + id + " not found", ex);
        } catch (RestClientException ex) {
            throw new ExternalServiceException("Error calling external service: " + ex.getMessage(), ex);
        }
    }

    @Override
    public boolean validateUserById(int userId) {
        try {
            String url = URL + USER_EP + userId;
            ResponseEntity<Object> responseEntity = restTemplate.getForEntity(url, Object.class);
            return responseEntity.hasBody();
        } catch (RestClientException e) {
            throw new ExternalServiceException("Error while validating user with ID " + userId, e);
        }
    }

    @Override
    public List<Post> findPostsByUserId(int userId) {
        try {
            String url = URL + POST_FOR_USER_EP + userId;
            ResponseEntity<List<Post>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Post>>() {}
            );
            return responseEntity.getBody();
        } catch (RestClientException e) {
            throw new ExternalServiceException("Error while fetching posts for user with ID " + userId, e);
        }
    }

    @Override
    public void deletePostById(int id){
        try {
            String url = URL + POST_EP + id;
            restTemplate.delete(url);
        } catch (RestClientException e) {
            throw new ExternalServiceException("Error while deleting post with ID " + id, e);
        }
    }

    public Post updatePostById(int id,String title,String body){
        try {
            Post post = findPostByIdExt(id);
            if (post != null) {
                post.setTitle(title);
                post.setBody(body);
                String url = URL + POST_EP + id;
                restTemplate.put(url, post);
                return post;
            } else {
                throw new PostNotFoundException("Post with ID " + id + " not found.");
            }
        } catch (RestClientException e) {
            throw new ExternalServiceException("Error while updating post with ID " + id, e);
        }
    }
}
