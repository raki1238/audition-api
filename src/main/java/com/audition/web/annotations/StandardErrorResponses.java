package com.audition.web.annotations;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;

/*
 * Interface which has the common APIResponses swagger documentation
 * for all the APIs in the app.
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ApiResponses({
    @ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ProblemDetail.class)
        )
    ),
    @ApiResponse(
        responseCode = "400", description = "Bad Request - postId provided is invalid",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)
        )
    ),
    @ApiResponse(
        responseCode = "404", description = "Resource Not Found Error, post/comments details not found",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)
        )
    )
    // In the future, we can add 401 Authentication errors or any common errors if needed
})
public @interface StandardErrorResponses {

}
