package com.audition.configuration;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class AuditionApiPropertiesTest {

    private static Validator validator;
    private static final String POSTS_PATH = "/posts";
    private static final String COMMENTS_PATH = "/comments";
    private static final String BASE_URL = "http://base";

    @BeforeAll
    static void setUpValidator() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void testValidProperties() {
        final AuditionApiProperties.Posts posts = new AuditionApiProperties.Posts(POSTS_PATH);
        final AuditionApiProperties.Comments comments = new AuditionApiProperties.Comments(COMMENTS_PATH);
        final AuditionApiProperties props = new AuditionApiProperties(BASE_URL, posts, comments);
        final Set<ConstraintViolation<AuditionApiProperties>> violations = validator.validate(props);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testMissingBaseUrl() {
        final AuditionApiProperties.Posts posts = new AuditionApiProperties.Posts(POSTS_PATH);
        final AuditionApiProperties.Comments comments = new AuditionApiProperties.Comments(COMMENTS_PATH);
        final AuditionApiProperties props = new AuditionApiProperties(null, posts, comments);
        final Set<ConstraintViolation<AuditionApiProperties>> violations = validator.validate(props);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> "baseUrl".equals(v.getPropertyPath().toString())));
    }

    @Test
    void testMissingPosts() {
        final AuditionApiProperties.Comments comments = new AuditionApiProperties.Comments(COMMENTS_PATH);
        final AuditionApiProperties props = new AuditionApiProperties(BASE_URL, null, comments);
        final Set<ConstraintViolation<AuditionApiProperties>> violations = validator.validate(props);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> "posts".equals(v.getPropertyPath().toString())));
    }

    @Test
    void testMissingComments() {
        final AuditionApiProperties.Posts posts = new AuditionApiProperties.Posts(POSTS_PATH);
        final AuditionApiProperties props = new AuditionApiProperties(BASE_URL, posts, null);
        final Set<ConstraintViolation<AuditionApiProperties>> violations = validator.validate(props);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> "comments".equals(v.getPropertyPath().toString())));
    }

    @Test
    void testMissingPostsPath() {
        final AuditionApiProperties.Posts posts = new AuditionApiProperties.Posts("");
        final AuditionApiProperties.Comments comments = new AuditionApiProperties.Comments(COMMENTS_PATH);
        final AuditionApiProperties props = new AuditionApiProperties(BASE_URL, posts, comments);
        final Set<ConstraintViolation<AuditionApiProperties>> violations = validator.validate(props);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().contains("posts.path")));
    }

    @Test
    void testMissingCommentsPath() {
        final AuditionApiProperties.Posts posts = new AuditionApiProperties.Posts(POSTS_PATH);
        final AuditionApiProperties.Comments comments = new AuditionApiProperties.Comments("");
        final AuditionApiProperties props = new AuditionApiProperties(BASE_URL, posts, comments);
        final Set<ConstraintViolation<AuditionApiProperties>> violations = validator.validate(props);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().contains("comments.path")));
    }
} 