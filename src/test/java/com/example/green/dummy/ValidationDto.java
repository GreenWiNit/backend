package com.example.green.dummy;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ValidationDto(
	@NotBlank(message = "메세지는 필수입니다.")
	@Size(min = 2, max = 8, message = "메세지 길이는 2~8입니다")
	String test,
	@NotNull(message = "null 일 수 없습니다.")
	@Min(value = 1, message = "1 이상이어야 합니다.")
	@Max(value = 3, message = "3 이하여야 합니다.")
	@Positive(message = "음수는 안됩니다")
	Integer age,
	Boolean nullable
) {
}
