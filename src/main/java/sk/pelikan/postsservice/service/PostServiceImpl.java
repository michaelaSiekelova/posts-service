package sk.pelikan.postsservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import sk.pelikan.postsservice.errorHandling.PostNotFoundException;
import sk.pelikan.postsservice.errorHandling.UserNotFoundException;
import sk.pelikan.postsservice.model.Post;
import sk.pelikan.postsservice.mongo.SequenceGeneratorService;
import sk.pelikan.postsservice.repository.PostRepository;
import sk.pelikan.postsservice.dtos.PostDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService{
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    @Autowired
    private PostServiceExternal postServiceExternal;


    public Post addPost(PostDTO post) {
        if (postServiceExternal.validateUserById(post.getUserId())) {
            Post newPost = new Post(
                    sequenceGeneratorService.generateSequence(Post.SEQUENCE_ID),
                    post.getUserId(),
                    post.getTitle(),
                    post.getBody()
            );
            return postRepository.save(newPost);
        }else{
            throw new UserNotFoundException("User with ID " + post.getUserId() + " not found.");
        }
    }

    public Post getPostById(int id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            return optionalPost.get();
        } else {
            try {
                Post result = postServiceExternal.findPostByIdExt(id);
                if (result != null) {
                    postRepository.save(result);
                    return result;
                } else {
                    throw new PostNotFoundException("Post with ID " + id + " not found");
                }
            } catch (Exception e) {
                throw new PostNotFoundException("Error while fetching post with ID " + id);
            }
        }
    }

    public List<Post> getPostByUserId(int userId) {
        try {
            if (postServiceExternal.validateUserById(userId)) {
                List<Post> result = new ArrayList<>();
                result.addAll(postRepository.findByUserId(userId));
                List<Post> externalPosts = new ArrayList<>(postServiceExternal.findPostsByUserId(userId));

                for (Post externalPost : externalPosts) {
                    if (!result.contains(externalPost)) {
                        result.add(externalPost);
                    }
                }

                return result;
            } else {
                throw new UserNotFoundException("User with ID " + userId + " not found.");
            }
        } catch (Exception e) {
            throw new UserNotFoundException("Error while fetching posts for user with ID " + userId);
        }
    }

    public void deletePost(int id) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            postRepository.deleteById(id);
            try {
                postServiceExternal.deletePostById(id);
            }catch (PostNotFoundException e){
                System.out.println("Post was not found on external API.");
            }
        } else {
            throw new PostNotFoundException("Post with ID " + id + " not found");
        }
    }

    public Post updatePost(int id, String title, String body) {
        Optional<Post> optionalPost = postRepository.findById(id);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            post.setTitle(title);
            post.setBody(body);
            try {
                postServiceExternal.updatePostById(id, title, body);
            }catch (PostNotFoundException e){
                System.out.println("Post was not found on external API.");
            }
            return postRepository.save(post);
        } else {
            Post updatedPost = postServiceExternal.updatePostById(id, title, body);
            if (updatedPost != null) {
                return postRepository.save(updatedPost);
            }else {
                throw new PostNotFoundException("Post with ID " + id + " not found.");
            }
        }
    }


}
