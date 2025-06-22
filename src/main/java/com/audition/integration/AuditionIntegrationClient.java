package com.audition.integration;

import com.audition.common.exception.SystemException;
import com.audition.configuration.AuditionApiProperties;
import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/*
 *  IntegrationClient class which has the API calls to provided URL in application.yml file
 *  Mostly, changes might only require when the dependent API url, params etc change.
 */
@Component
@RequiredArgsConstructor
public class AuditionIntegrationClient {

    final RestTemplate restTemplate;

    final AuditionApiProperties apiConfig;

    public List<AuditionPost> getPosts() {
        final String url = apiConfig.baseUrl() + apiConfig.posts().path();
        final ResponseEntity<AuditionPost[]> responseEntity = restTemplate.getForEntity(url, AuditionPost[].class);
        return Arrays.asList(Objects.requireNonNull(responseEntity.getBody()));
    }

    public AuditionPost getPostById(final long postId) {
        final String url = apiConfig.baseUrl() + apiConfig.posts().path() + "/" + postId;
        try {
            return restTemplate.getForObject(url, AuditionPost.class);
        } catch (final HttpClientErrorException exception) {
            checkResourceNotFoundErrors(exception, "Cannot find a Post with id " + postId);
            throw new SystemException(exception.getMessage(), exception.getStatusCode().value(), exception);
        }
    }


    public AuditionPost getPostWithComments(final long postId) {
        final String url = apiConfig.baseUrl() + apiConfig.posts().path() + "/" + postId + "/comments";
        try {
            final AuditionPost auditionpost = getPostById(postId);
            final ResponseEntity<AuditionComment[]> responseEntity = restTemplate.getForEntity(url,
                AuditionComment[].class);
            final List<AuditionComment> auditionComments = Arrays.asList(responseEntity.getBody());
            auditionpost.setComments(auditionComments);
            return auditionpost;
        } catch (final HttpClientErrorException exception) {
            checkResourceNotFoundErrors(exception, "Cannot find a Post and its comments with id " + postId);
            throw new SystemException(exception.getMessage(), exception.getStatusCode().value(), exception);
        }
    }


    public List<AuditionComment> getCommentsByPostId(final long postId) {
        final String url = apiConfig.baseUrl() + apiConfig.comments().path() + "?postId=" + postId;
        final ResponseEntity<AuditionComment[]> responseEntity = restTemplate.getForEntity(url,
            AuditionComment[].class);
        return Arrays.asList(Objects.requireNonNull(responseEntity.getBody()));
    }

    // The comments are a separate list that needs to be returned to the API consumers. Hint: this is not part of the AuditionPost pojo.

    private void checkResourceNotFoundErrors(final HttpClientErrorException exception, final String message) {
        if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new SystemException(message, "Resource not found", HttpStatus.NOT_FOUND.value(), exception);
        }
    }
}

