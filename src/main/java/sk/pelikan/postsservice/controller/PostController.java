package sk.pelikan.postsservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import sk.pelikan.postsservice.errorHandling.PostNotFoundException;
import sk.pelikan.postsservice.errorHandling.UserNotFoundException;
import sk.pelikan.postsservice.model.Post;
import sk.pelikan.postsservice.dtos.PostDTO;
import sk.pelikan.postsservice.service.PostServiceImpl;

import jakarta.validation.Valid;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts")
@Tag(name = "Post API")
public class PostController {

    @Autowired
    private PostServiceImpl postServiceImpl;

    @PostMapping
    @Operation(summary = "Create post", description = "End point for creating new post.")
    @ApiResponses(value = {
            @ApiResponse(responseCode  = "201", description = "Post created successfully"),
            @ApiResponse(responseCode = "400", description = "Post parrameters are not valid"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<?> addPost(@Valid @RequestBody @Parameter(description = "New post object",
            required = true) PostDTO post, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(getErrorsMap(result));
        } else {
            try {
                Post addedPost = postServiceImpl.addPost(post);
                return ResponseEntity.status(HttpStatus.CREATED).body(addedPost);
            } catch (UserNotFoundException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find post by ID", description = "End point for finding a post by ID. " +
            "If a post is not found in the database, the system searches for it in an external API and then saves it.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Post created successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Post> getPostById(@PathVariable("id") @Parameter(description = "ID of requested post.") int id) {
        try {
            Post post = postServiceImpl.getPostById(id);
            return ResponseEntity.ok(post);
        } catch (PostNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Find all posts for user")
    public ResponseEntity<List<Post>> getPostsByUserId(@PathVariable("userId") @Parameter(description = "ID of user associated with the post.") int userId) {
        try {
            List<Post> post = postServiceImpl.getPostByUserId(userId);
            if (post != null) {
                return new ResponseEntity<>(post, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update post")
    public ResponseEntity<String> updatePost(@PathVariable("id") int id, @RequestBody PostDTO post) {
        if (post.getTitle() == null || post.getBody() == null || post.getTitle().isEmpty() || post.getBody().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Title and body must be provided.");
        }

        try {
            Post updatedPost = postServiceImpl.updatePost(id, post.getTitle(), post.getBody());
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete post")
    public ResponseEntity<String> deletePost(@PathVariable("id") int id) {
        try {
            postServiceImpl.deletePost(id);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        } catch (PostNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found.");
        }
    }

    private Map<String, String> getErrorsMap(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : result.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return errors;
    }
}
