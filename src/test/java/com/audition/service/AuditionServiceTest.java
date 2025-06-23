package com.audition.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/*
 * Test class for all business logics in AuditionService
 */

@SuppressWarnings("PMD.TooManyMethods")
class AuditionServiceTest {

    @Mock
    private transient AuditionIntegrationClient auditionIntegrationClient;

    @InjectMocks
    private transient AuditionService auditionService;

    private transient AuditionPost post1;
    private transient List<AuditionPost> posts;
    private transient List<AuditionComment> comments;
    private static final String FIRST_POST = "First Post";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        post1 = new AuditionPost();
        post1.setId(1);
        post1.setUserId(1);
        post1.setTitle(FIRST_POST);
        post1.setBody("First Post Body");

        final AuditionPost post2 = new AuditionPost();
        post2.setId(2);
        post2.setUserId(2);
        post2.setTitle(FIRST_POST);
        post2.setBody("First Post Body");

        final AuditionComment comment1 = new AuditionComment();
        comment1.setId(1);
        comment1.setPostId(1);
        comment1.setName("Commenter");
        comment1.setEmail("commenter@example.com");
        comment1.setBody(FIRST_POST);

        posts = Arrays.asList(post1, post2);
        comments = Collections.singletonList(comment1);
    }

    @Test
    void testGetPosts() {
        when(auditionIntegrationClient.getPosts()).thenReturn(posts);
        final List<AuditionPost> result = auditionService.getPosts();
        assertEquals(2, result.size());
        verify(auditionIntegrationClient, times(1)).getPosts();
    }

    @Test
    void testGetPostsWithPagination() {
        when(auditionIntegrationClient.getPosts()).thenReturn(posts);
        final List<AuditionPost> result = auditionService.getPosts(1, 1);
        assertEquals(1, result.size());
        assertEquals(FIRST_POST, result.get(0).getTitle());
    }

    @Test
    void testGetPostsWithFilter() {
        when(auditionIntegrationClient.getPosts()).thenReturn(posts);
        final List<AuditionPost> result = auditionService.getPostsWithFilter("first", 1, 10);
        assertEquals(2, result.size());
        assertEquals(FIRST_POST, result.get(0).getTitle());
        assertEquals(FIRST_POST, result.get(1).getTitle());
    }

    @Test
    void testGetPostsWithFilterReturnsAllIfFilterNull() {
        when(auditionIntegrationClient.getPosts()).thenReturn(posts);
        final List<AuditionPost> result = auditionService.getPostsWithFilter(null, 1, 10);
        assertEquals(2, result.size());
    }

    @Test
    void testGetPostsWithFilterReturnsAllIfFilterBlank() {
        when(auditionIntegrationClient.getPosts()).thenReturn(posts);
        final List<AuditionPost> result = auditionService.getPostsWithFilter(" ", 1, 10);
        assertEquals(2, result.size());
    }

    @Test
    void testGetPostById() {
        when(auditionIntegrationClient.getPostById(1L)).thenReturn(post1);
        final AuditionPost result = auditionService.getPostById(1L);
        assertEquals(FIRST_POST, result.getTitle());
    }

    @Test
    void testGetPostWithComments() {
        when(auditionIntegrationClient.getPostWithComments(1L)).thenReturn(post1);
        final AuditionPost result = auditionService.getPostWithComments(1L);
        assertEquals(post1, result);
    }

    @Test
    void testGetCommentsByPostId() {
        when(auditionIntegrationClient.getCommentsByPostId(1L)).thenReturn(comments);
        final List<AuditionComment> result = auditionService.getCommentsByPostId(1L);
        assertEquals(1, result.size());
        assertEquals(FIRST_POST, result.get(0).getBody());
    }

    @Test
    void testGetPaginatedPostsHandlesEmptyList() {
        final List<AuditionPost> result = auditionService.getPaginatedPosts(Collections.emptyList(), 1, 10);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPaginatedPostsHandlesNullList() {
        final List<AuditionPost> result = auditionService.getPaginatedPosts(null, 1, 10);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetPaginatedPostsHandlesInvalidPageOrSize() {
        final List<AuditionPost> result1 = auditionService.getPaginatedPosts(posts, 0, 10);
        final List<AuditionPost> result2 = auditionService.getPaginatedPosts(posts, 1, 0);
        assertTrue(result1.isEmpty());
        assertTrue(result2.isEmpty());
    }

    @Test
    void testGetPaginatedPostsHandlesPageTooFar() {
        final List<AuditionPost> result = auditionService.getPaginatedPosts(posts, 10, 1);
        assertTrue(result.isEmpty());
    }
} 