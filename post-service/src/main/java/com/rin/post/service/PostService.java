package com.rin.post.service;

import com.rin.post.dto.PageResponse;
import com.rin.post.dto.request.PostRequest;
import com.rin.post.dto.response.PostResponse;
import com.rin.post.dto.response.UserProfileResponse;
import com.rin.post.entity.Post;
import com.rin.post.mapper.PostMapper;
import com.rin.post.repository.PostRepository;
import com.rin.post.repository.httpclient.ProfileClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PostService {
    PostRepository postRepository;
    PostMapper postMapper;
    DateTimeFormatter dateTimeFormatter;
    ProfileClient profileClient;

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

    public PageResponse<PostResponse> getMyPosts(int page, int size){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();
        //Get profile by userId
        UserProfileResponse userProfile = null;

        try {
            userProfile = profileClient.getProfile(userId).getResult();
        }catch (Exception e){
            log.error("Get profile error");
        }

        Sort sort = Sort.by("createdDate").descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);
        var pageData = postRepository.findAllByUserId(userId, pageable);

        String name = userProfile !=null ? userProfile.getFirstName() +" "+ userProfile.getLastName() : "Unknown name";
        var postList = pageData.getContent().stream().map(post -> {
            var postResponse = postMapper.toPostResponse(post);
            postResponse.setCreated(dateTimeFormatter.formatDate(post.getCreatedDate()));
            postResponse.setUsername(name);
            return postResponse;
        }).toList();



        return PageResponse.<PostResponse>builder()
                .currentPage(page)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(postList)
                .build();
    }
}
