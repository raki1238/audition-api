package com.audition.configuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

//Configuration properties for the Audition API Expected configuration structure in application.yml
@ConfigurationProperties(prefix = "audition.api")
@Validated
public record AuditionApiProperties(
    @NotBlank(message = "Base URL audition.api.base-url  is required")
    String baseUrl,

    @NotNull(message = "Posts audition.api.posts configuration is required (missing 'posts' section or"
        + " 'posts.path' property)")
    @Valid
    Posts posts,

    @NotNull(message = "Comments audition.api.comments configuration is required (missing 'comments' section or "
        + "'comments.path' property)")
    @Valid
    Comments comments
) {

    /**
     * Posts configuration. Requires 'path' property to be set.
     */
    public record Posts(
        @NotBlank(message = "Posts path audition.api.posts.path is required")
        String path
    ) {

    }

    /**
     * Comments configuration. Requires 'path' property to be set.
     */
    public record Comments(
        @NotBlank(message = "Comments path audition.api.comments.path is required")
        String path
    ) {

    }
}
