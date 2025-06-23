package com.audition.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import org.junit.jupiter.api.Test;

/*
 * Test Class to cover the testing of the Model and Exceptions
 */
class ModelAndExceptionCoverageTest {

    private static final String TITLE = "title";
    private static final String BODY = "body";
    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String OTHER = "other";
    private static final String MSG = "msg";
    private static final String CAUSE = "cause";
    private static final String DETAIL = "detail";

    @Test
    void testAuditionPostGettersSetters() {
        final AuditionPost post = new AuditionPost();
        post.setId(1);
        post.setUserId(2);
        post.setTitle(TITLE);
        post.setBody(BODY);
        assertEquals(1, post.getId());
        assertEquals(2, post.getUserId());
        assertEquals(TITLE, post.getTitle());
        assertEquals(BODY, post.getBody());
        post.setComments(null);
        assertNull(post.getComments());
    }

    @Test
    void testAuditionCommentGettersSetters() {
        final AuditionComment comment = new AuditionComment();
        comment.setId(1);
        comment.setPostId(2);
        comment.setName(NAME);
        comment.setEmail(EMAIL);
        comment.setBody(BODY);
        assertEquals(1, comment.getId());
        assertEquals(2, comment.getPostId());
        assertEquals(NAME, comment.getName());
        assertEquals(EMAIL, comment.getEmail());
        assertEquals(BODY, comment.getBody());
    }

    @Test
    void testSystemExceptionDefaultConstructors() {
        final SystemException e1 = new SystemException();
        final SystemException e2 = new SystemException(MSG);
        final SystemException e3 = new SystemException(MSG, 400);
        final SystemException e4 = new SystemException(MSG, new RuntimeException(CAUSE));
        assertNull(e1.getMessage());
        assertEquals(MSG, e2.getMessage());
        assertEquals(400, e3.getStatusCode());
        assertEquals(MSG, e3.getMessage());
        assertEquals(MSG, e4.getMessage());
    }

    // Divided the methods as PMD was not allowing more than 7 asserts for a test.
    //Part1
    @Test
    void testSystemExceptionDetailTitleStatusConstructorsPart1() {
        final SystemException e5 = new SystemException(DETAIL, TITLE, 400);
        final SystemException e6 = new SystemException(DETAIL, TITLE, new RuntimeException(CAUSE));
        assertEquals(DETAIL, e5.getMessage());
        assertEquals(TITLE, e5.getTitle());
        assertEquals(400, e5.getStatusCode());
        assertEquals(DETAIL, e6.getMessage());
        assertEquals(TITLE, e6.getTitle());
        assertEquals(500, e6.getStatusCode());
    }

    //Part 2
    @Test
    void testSystemExceptionDetailTitleStatusConstructorsPart2() {
        final SystemException e7 = new SystemException(DETAIL, 400, new RuntimeException(CAUSE));
        final SystemException e8 = new SystemException(DETAIL, TITLE, 400, new RuntimeException(CAUSE));
        assertEquals(DETAIL, e7.getMessage());
        assertEquals(400, e7.getStatusCode());
        assertEquals(DETAIL, e8.getMessage());
        assertEquals(TITLE, e8.getTitle());
        assertEquals(400, e8.getStatusCode());
    }

    @Test
    void testAuditionPostEqualsHashCodeToString() {
        final AuditionPost post1 = new AuditionPost();
        post1.setId(1);
        post1.setUserId(2);
        post1.setTitle(TITLE);
        post1.setBody(BODY);

        final AuditionPost post2 = new AuditionPost();
        post2.setId(1);
        post2.setUserId(2);
        post2.setTitle(TITLE);
        post2.setBody(BODY);

        final AuditionPost post3 = new AuditionPost();
        post3.setId(2);
        post3.setUserId(3);
        post3.setTitle(OTHER);
        post3.setBody(OTHER);

        // equals
        assertEquals(post1, post2);
        assertNotEquals(post1, post3);
        assertNotEquals(null, post1);
        assertNotEquals(new Object(), post1);
        assertEquals(post1, post1);
        // hashCode
        assertEquals(post1.hashCode(), post2.hashCode());

    }

    @Test
    void testAuditionCommentEqualsHashCodeToString() {
        final AuditionComment comment1 = new AuditionComment();
        comment1.setId(1);
        comment1.setPostId(2);
        comment1.setName(NAME);
        comment1.setEmail(EMAIL);
        comment1.setBody(BODY);

        final AuditionComment comment2 = new AuditionComment();
        comment2.setId(1);
        comment2.setPostId(2);
        comment2.setName(NAME);
        comment2.setEmail(EMAIL);
        comment2.setBody(BODY);

        final AuditionComment comment3 = new AuditionComment();
        comment3.setId(2);
        comment3.setPostId(3);
        comment3.setName(OTHER);
        comment3.setEmail(OTHER);
        comment3.setBody(OTHER);

        // equals
        assertEquals(comment1, comment2);
        assertNotEquals(comment1, comment3);
        assertNotEquals(null, comment1);
        assertNotEquals(new Object(), comment1);
        assertEquals(comment1, comment1);
        // hashCode
        assertEquals(comment1.hashCode(), comment2.hashCode());
        assertNotEquals(comment1.hashCode(), comment3.hashCode());

    }
} 