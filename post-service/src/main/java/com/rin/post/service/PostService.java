package com.rin.post.service;

import com.rin.post.dto.request.PostRequest;
import com.rin.post.dto.response.PostResponse;
import com.rin.post.entity.Post;
import com.rin.post.mapper.PostMapper;
import com.rin.post.repository.PostRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostService {
    PostRepository postRepository;
    PostMapper postMapper;

    public PostResponse createPost(PostRequest postRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        Post post = Post.builder()
                .content(postRequest.getContent())
                .createdDate(Instant.now())
                .modifiedDate(Instant.now())
                .userId(authentication.getName())
                .build();

        post = postRepository.save(post);

        return postMapper.toPostResponse(post);
    }

    public List<PostResponse> getMyPosts(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        List<Post> posts = postRepository.findAllByUserId(authentication.getName());

        return posts.stream().map(postMapper::toPostResponse).toList();

    }
}
