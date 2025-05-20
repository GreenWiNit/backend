package com.example.green;

import java.time.LocalDateTime;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.green.domain.request.PostRequest;
import com.example.green.domain.response.PostResponse;
import com.example.green.global.api.ApiErrorStandard;
import com.example.green.global.api.ApiTemplate;
import com.example.green.global.api.ExamplePostResponseMessage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/posts")
@Tag(name = "게시글 API", description = "게시글 생성, 조회, 수정, 삭제 API")
@RequiredArgsConstructor
public class ExamplePostController {

	@Operation(summary = "게시글 생성", description = "게시글을 생성합니다.")
	@ApiErrorStandard
	@ApiResponse(
		responseCode = "200",
		description = "게시글 생성 성공",
		content = @Content(schema = @Schema(implementation = PostResponse.class))
	)
	@PostMapping
	public ApiTemplate<PostResponse> createPost(@Valid @RequestBody PostRequest request) {
		PostResponse response = new PostResponse(
			1L,
			request.getTitle(),
			request.getContent(),
			"홍길동",
			LocalDateTime.now()
		);
		return ApiTemplate.ok(ExamplePostResponseMessage.POST_CREATED, response);
	}

	@Operation(summary = "게시글 수정", description = "게시글 ID로 게시글을 수정합니다.")
	@ApiErrorStandard
	@ApiResponse(
		responseCode = "200",
		description = "게시글 수정 성공",
		content = @Content(schema = @Schema(implementation = PostResponse.class))
	)
	@PutMapping("/{postId}")
	public ApiTemplate<PostResponse> updatePost(
		@Parameter(
			name = "postId",
			description = "수정할 게시글의 ID",
			in = ParameterIn.PATH,
			required = true,
			example = "1"
		)
		@PathVariable Long postId,
		@Valid @RequestBody PostRequest request
	) {
		PostResponse response = new PostResponse(
			postId,
			request.getTitle(),
			request.getContent(),
			"홍길동",
			LocalDateTime.now()
		);
		return ApiTemplate.ok(ExamplePostResponseMessage.POST_UPDATED, response);
	}

	@Operation(summary = "게시글 삭제", description = "게시글 ID로 게시글을 삭제합니다.")
	@ApiErrorStandard
	@ApiResponse(
		responseCode = "200",
		description = "게시글 삭제 성공",
		content = @Content(schema = @Schema(implementation = Void.class))
	)
	@DeleteMapping("/{postId}")
	public ApiTemplate<Void> deletePost(
		@Parameter(
			name = "postId",
			description = "삭제할 게시글의 ID",
			in = ParameterIn.PATH,
			required = true,
			example = "1"
		)
		@PathVariable Long postId
	) {
		return ApiTemplate.ok(ExamplePostResponseMessage.POST_DELETED);
	}

	@Operation(summary = "게시글 조회", description = "게시글 ID로 게시글을 조회합니다.")
	@ApiErrorStandard
	@ApiResponse(
		responseCode = "200",
		description = "게시글 조회 성공",
		content = @Content(schema = @Schema(implementation = PostResponse.class))
	)
	@GetMapping("/{postId}")
	public ApiTemplate<PostResponse> getPostById(
		@Parameter(
			name = "postId",
			description = "조회할 게시글의 ID",
			in = ParameterIn.PATH,
			required = true,
			example = "1"
		)
		@PathVariable Long postId
	) {
		PostResponse response = new PostResponse(
			postId,
			"제목 예시",
			"본문 예시",
			"홍길동",
			LocalDateTime.now()
		);
		return ApiTemplate.ok(ExamplePostResponseMessage.POST_FOUND, response);
	}
}
