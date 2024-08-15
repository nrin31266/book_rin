package com.rin.post.mapper;


import com.rin.post.dto.response.PostResponse;
import com.rin.post.entity.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostMapper {
    PostResponse toPostResponse(Post post);
}
