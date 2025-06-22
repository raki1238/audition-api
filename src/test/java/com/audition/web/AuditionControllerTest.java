package com.audition.web;


import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.audition.configuration.AuditionApiProperties;
import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import com.audition.service.AuditionService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;

@SuppressWarnings({"PMD.UnusedPrivateField"})
@WebMvcTest(AuditionController.class)
class AuditionControllerTest {

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private transient AuditionService auditionService;
    @MockBean
    private transient brave.Tracer tracer;
    @MockBean
    private transient AuditionIntegrationClient auditionIntegrationClient;
    @MockBean
    private transient RestTemplate restTemplate;
    @MockBean
    private transient com.audition.common.logging.AuditionLogger auditionLogger;

    private transient AuditionPost testPost;
    private transient List<AuditionPost> testPosts;
    private transient List<AuditionComment> testComments;

    @BeforeEach
    void setUp() {
        testPost = new AuditionPost();
        testPost.setId(1);
        testPost.setUserId(1);
        testPost.setTitle("Test Post 1 Title");
        testPost.setBody("Test Post 1 Body");

        final AuditionPost testPost2 = new AuditionPost();
        testPost2.setId(2);
        testPost2.setUserId(2);
        testPost2.setTitle("Test Post 2 Title");
        testPost2.setBody("Test Post 2 Body");

        final AuditionComment testComment = new AuditionComment();
        testComment.setId(1);
        testComment.setPostId(1);
        testComment.setName("Test Commenter Name");
        testComment.setEmail("test@example.com");
        testComment.setBody("Test Commenter Comment");

        testPosts = List.of(testPost, testPost2);
        testComments = List.of(testComment);
    }

    @Test
    void shouldGetPostsWithFilter() {
        when(auditionService.getPostsWithFilter("test", 1, 10)).thenReturn(testPosts);
        final MvcResult result = org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
            mockMvc.perform(MockMvcRequestBuilders.get("/posts")
                    .param("filter", "test")
                    .param("page", "1")
                    .param("size", "10")
                    .accept(MediaType.APPLICATION_JSON))
                .andReturn()
        );
        verify(auditionService, times(1)).getPostsWithFilter("test", 1, 10);
        org.junit.jupiter.api.Assertions.assertEquals(200, result.getResponse().getStatus());
        org.junit.jupiter.api.Assertions.assertEquals(2, testPosts.size());
    }

    @Test
    void shouldGetPostById() {
        when(auditionService.getPostById(1L)).thenReturn(testPost);
        final MvcResult result = org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
            mockMvc.perform(MockMvcRequestBuilders.get("/posts/{postId}", 1)
                    .accept(MediaType.APPLICATION_JSON))
                .andReturn()
        );
        verify(auditionService, times(1)).getPostById(1L);
        org.junit.jupiter.api.Assertions.assertEquals(200, result.getResponse().getStatus());
        org.junit.jupiter.api.Assertions.assertEquals(1, testPost.getId());
    }

    @Test
    void shouldGetPostWithComments() {
        when(auditionService.getPostWithComments(1L)).thenReturn(testPost);
        final MvcResult result = org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
            mockMvc.perform(MockMvcRequestBuilders.get("/posts/{postId}/comments", 1)
                    .accept(MediaType.APPLICATION_JSON))
                .andReturn()
        );
        verify(auditionService, times(1)).getPostWithComments(1L);
        org.junit.jupiter.api.Assertions.assertEquals(200, result.getResponse().getStatus());
        org.junit.jupiter.api.Assertions.assertEquals(1, testPost.getId());
    }

    @Test
    void shouldGetCommentsForPost() {
        when(auditionService.getCommentsByPostId(1L)).thenReturn(testComments);
        final MvcResult result = org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
            mockMvc.perform(MockMvcRequestBuilders.get("/comments")
                    .param("postId", "1")
                    .accept(MediaType.APPLICATION_JSON))
                .andReturn()
        );
        verify(auditionService, times(1)).getCommentsByPostId(1L);
        org.junit.jupiter.api.Assertions.assertEquals(200, result.getResponse().getStatus());
        org.junit.jupiter.api.Assertions.assertEquals(1, testComments.size());
    }

    @org.springframework.boot.test.context.TestConfiguration
    static class TestConfig {

        @org.springframework.context.annotation.Bean
        public AuditionApiProperties auditionApiProperties() {
            return new AuditionApiProperties(
                "http://dummy-url",
                new AuditionApiProperties.Posts("/posts"),
                new AuditionApiProperties.Comments("/comments")
            );
        }
    }
}