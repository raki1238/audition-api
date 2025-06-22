package com.audition.web.dto;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springdoc.core.annotations.ParameterObject;

/*
    This is a record for most commonly used postId param in the AuditionController
    Inorder not to repeat the same in all the APIs, it is set to a record block.
 */

@ParameterObject
public record PostIdParam(
    @Parameter(description = "Unique identifier of the post", required = true)
    @NotNull(message = "postId is required")
    @Positive(message = "postId must be greater than 0")
    Long postId
) {

}
