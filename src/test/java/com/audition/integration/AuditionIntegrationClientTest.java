package com.audition.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.contains;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.audition.configuration.AuditionApiProperties;
import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/*
 * Class to Test the integration with the external/third-party API.
 * This can be also used to test when there are any changes with the API.
 */

class AuditionIntegrationClientTest {

    private transient RestTemplate restTemplate;
    private transient AuditionIntegrationClient client;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        final AuditionApiProperties.Posts postsConfig = new AuditionApiProperties.Posts("/posts");
        final AuditionApiProperties.Comments commentsConfig = new AuditionApiProperties.Comments("/comments");
        final AuditionApiProperties apiProperties = new AuditionApiProperties("http://base", postsConfig,
            commentsConfig);
        client = new AuditionIntegrationClient(restTemplate, apiProperties);
    }

    @Test
    void testGetPosts() {
        final AuditionPost[] posts = {new AuditionPost(), new AuditionPost()};
        when(restTemplate.getForEntity(anyString(), eq(AuditionPost[].class)))
            .thenReturn(new ResponseEntity<>(posts, HttpStatus.OK));
        final List<AuditionPost> result = client.getPosts();
        assertEquals(2, result.size());
    }

    @Test
    void testGetPostByIdSuccess() {
        final AuditionPost post = new AuditionPost();
        when(restTemplate.getForObject(anyString(), eq(AuditionPost.class))).thenReturn(post);
        final AuditionPost result = client.getPostById(1L);
        assertNotNull(result);
    }

    @Test
    void testGetPostByIdNotFound() {
        when(restTemplate.getForObject(anyString(), eq(AuditionPost.class)))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        final Exception ex = assertThrows(RuntimeException.class, () -> client.getPostById(1L));
        assertTrue(ex.getMessage().contains("Cannot find a Post with id"));
    }

    @Test
    void testGetPostWithComments() {
        final AuditionPost post = new AuditionPost();
        final AuditionComment[] comments = {new AuditionComment()};
        when(restTemplate.getForObject(anyString(), eq(AuditionPost.class))).thenReturn(post);
        when(restTemplate.getForEntity(contains("/comments"), eq(AuditionComment[].class)))
            .thenReturn(new ResponseEntity<>(comments, HttpStatus.OK));
        final AuditionPost result = client.getPostWithComments(1L);
        assertNotNull(result);
        assertNotNull(result.getComments());
        assertEquals(1, result.getComments().size());
    }

    @Test
    void testGetCommentsByPostId() {
        final AuditionComment[] comments = {new AuditionComment(), new AuditionComment()};
        when(restTemplate.getForEntity(anyString(), eq(AuditionComment[].class)))
            .thenReturn(new ResponseEntity<>(comments, HttpStatus.OK));
        final List<AuditionComment> result = client.getCommentsByPostId(1L);
        assertEquals(2, result.size());
    }
} 