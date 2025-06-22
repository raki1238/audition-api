package com.audition.web;

import com.audition.common.exception.SystemException;
import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import com.audition.service.AuditionService;
import com.audition.web.annotations.StandardErrorResponses;
import com.audition.web.dto.PostIdParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@Tag(name = "Audition APIs", description = "APIs for fetching Posts(using filter, postId), Comments for Posts")
public class AuditionController {

    private final transient AuditionService auditionService;
    private static final String HTTP_200_OK = "200";

    public AuditionController(final AuditionService auditionService) {
        this.auditionService = auditionService;
    }

    /* GET API to fetch posts using a filter
     * filter -> string to filter the posts with this string in title or body
     * page -> which page a consumer want to view the results.
     * size -> total no.of records for a page/request
     */
    @Operation(
        summary = "Get all Posts", description = "Fetches all the posts with data filter(optional)"
    )
    @StandardErrorResponses
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = HTTP_200_OK, description = "Successfully retrieved posts",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                array = @ArraySchema(schema = @Schema(implementation = AuditionPost.class))
            )
        )
    })
    @RequestMapping(value = "/posts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AuditionPost> getPosts(
        @Parameter(description = "Filter posts by title or body", example = "motivation")
        @RequestParam(value = "filter", required = false) final String filter,

        @Parameter(description = "Starting page number of filtered posts", example = "1")
        @RequestParam(defaultValue = "1")
        @Positive(message = "'page' value should be positive integer value(>0)") final int page,

        @Parameter(description = "Maximum number of records to be returned per page", example = "10")
        @RequestParam(defaultValue = "10")
        @Max(value = 25, message = "'size' value should not exceed 25")
        @Positive(message = "'size' value should be positive integer value") final int size
    ) {
        if (filter != null) {
            if (filter.isBlank()) {
                // Raise an exception as filter shouldn't be blank
                throw new SystemException("Filter must not be blank when provided.", "Bad Request",
                    HttpStatus.BAD_REQUEST.value());
            }
            return auditionService.getPostsWithFilter(filter, page, size);
        }
        return auditionService.getPosts(page, size);

    }


    /* GET API to fetch the post details by postId
     * postId -> Unique Id for each post
     */
    @Operation(
        summary = "Get Post details by Id", description = "Fetches all the post details when postId is provided"
    )
    @StandardErrorResponses
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = HTTP_200_OK, description = "Successfully retrieved post details",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AuditionPost.class)
            )
        )
    })
    @RequestMapping(value = "/posts/{postId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody AuditionPost getPostsById(
        final PostIdParam postIdParam
    ) {
        return auditionService.getPostById(postIdParam.postId());
    }


    // TODO Add additional methods to return comments for each post. Hint: Check https://jsonplaceholder.typicode.com/
    /* GET API to fetch the post details along with comments by postId
     * postId -> Unique Id for each post
     */
    @Operation(
        summary = "GET comments for a post", description = "Gets Post details along with all comments"
    )
    @StandardErrorResponses
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = HTTP_200_OK, description = "Successfully retrieved post details",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AuditionPost.class)
            )
        )
    })
    @RequestMapping(value = "/posts/{postId}/comments", method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody AuditionPost getPostWithComments(
        final PostIdParam postIdParam) {
        return auditionService.getPostWithComments(postIdParam.postId());
    }

    /* GET API to fetch only post's comments as a list for a given postId
     * postId -> Unique Id for each post
     */
    @Operation(
        summary = " GET comments for a Post", description = "Fetches comments list for a particular post"
    )
    @StandardErrorResponses
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = HTTP_200_OK, description = "Successfully retrieved post details",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = AuditionPost.class)
            )
        )
    })
    @RequestMapping(value = "/comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AuditionComment> getCommentsForPost(
        @Parameter(description = "Unique identifier of the post", required = true)
        @NotNull(message = "postId is required")
        @Positive(message = "postId must be greater than 0")
        @RequestParam("postId") final Long postId) {
        return auditionService.getCommentsByPostId(postId);
    }

}
