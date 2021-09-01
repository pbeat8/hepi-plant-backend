package com.hepiplant.backend.service.impl;

import com.hepiplant.backend.dto.PostDto;
import com.hepiplant.backend.entity.Post;
import com.hepiplant.backend.repository.CategoryRepository;
import com.hepiplant.backend.repository.PostRepository;
import com.hepiplant.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserRepository userRepository;

    @Captor
    private ArgumentCaptor<Post> postArgumentCaptor;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    public void shouldGetAllPostsOk(){
        //given
        Post post = new Post(1L, null, null, "Post 1", "Body 1", "tag1", null, null, null, null);
        given(postRepository.findAll()).willReturn(List.of(post));

        //when
        List<PostDto> result = postService.getAll();

        //then
        then(postRepository).should(times(1)).findAll();

        assertEquals(1, result.size());
        assertEquals("Post 1", result.get(0).getTitle());
        assertEquals("Body 1", result.get(0).getBody());
        assertEquals(1, result.get(0).getTags().size());
        assertEquals("tag1", result.get(0).getTags().get(0));

    }

}