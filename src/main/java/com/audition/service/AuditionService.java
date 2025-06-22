package com.audition.service;

import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/*
 * Service class to fetch the details from different resources/APIs and
 * apply the business logic to filter, update fields, or pass by
 */

@Service
@RequiredArgsConstructor
public class AuditionService {

    final AuditionIntegrationClient auditionIntegrationClient;

    // Method to fetch all the POSTs without any conditions
    public List<AuditionPost> getPosts() {
        return auditionIntegrationClient.getPosts();
    }

    // Method to fetch all the POSTs with page and no.of records for each page.
    public List<AuditionPost> getPosts(final int page, final int size) {
        return getPaginatedPosts(getPosts(), page, size);
    }

    // Method to fetch all the POSTs with a filter string which will be checked with respective
    // of title or body of all the posts.
    public List<AuditionPost> getPostsWithFilter(final String filter, final int page, final int size) {
        final List<AuditionPost> allPosts = auditionIntegrationClient.getPosts();

        if (filter != null && !filter.isBlank()) {
            return getPaginatedPosts(allPosts.stream()
                .filter(post -> post.getTitle().toLowerCase(Locale.ROOT)
                    .contains(filter.toLowerCase(Locale.ROOT))
                    ||
                    post.getBody().toLowerCase(Locale.ROOT).contains(filter.toLowerCase(Locale.ROOT)))
                .collect(Collectors.toList()), page, size);
        }

        return getPaginatedPosts(allPosts, page, size);
    }

    // Method to fetch the post details without comments for a particular postId
    public AuditionPost getPostById(final long postId) {
        return auditionIntegrationClient.getPostById(postId);
    }

    // Method to fetch the post details with comments for a particular postId
    public AuditionPost getPostWithComments(final long postId) {
        return auditionIntegrationClient.getPostWithComments(postId);
    }

    // Method to fetch the all comments(only) for a particular postId
    public List<AuditionComment> getCommentsByPostId(final long postId) {
        return auditionIntegrationClient.getCommentsByPostId(postId);
    }

    // Method to slice down the list based on given size.
    public List<AuditionPost> getPaginatedPosts(final List<AuditionPost> posts, final int page, final int size) {
        if (posts == null || posts.isEmpty() || size <= 0 || page <= 0) {
            return Collections.emptyList();
        }

        final int total = posts.size();
        final int startIndex = (page - 1) * size;

        // page too far so sending an empty list.
        if (startIndex >= total) {
            return Collections.emptyList();
        }

        final int endIndex = Math.min(startIndex + size, total);
        return posts.subList(startIndex, endIndex);
    }
}
