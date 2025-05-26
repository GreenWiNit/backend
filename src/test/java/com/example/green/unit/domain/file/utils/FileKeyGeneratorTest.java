package com.example.green.unit.domain.file.utils;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.green.domain.file.utils.FileKeyGenerator;
import com.example.green.global.utils.IdUtils;
import com.example.green.global.utils.TimeUtils;

@ExtendWith(MockitoExtension.class)
class FileKeyGeneratorTest {

	@Mock
	private IdUtils idUtils;
	@Mock
	private TimeUtils timeUtils;
	@InjectMocks
	private FileKeyGenerator fileKeyGenerator;

	@Test
	void 올바른_형식의_파일키를_생성한다() {
		// given
		given(idUtils.generateUniqueId(8)).willReturn("abcd1234");
		given(timeUtils.getFormattedDate("yyyyMMdd")).willReturn("20250523");
		given(timeUtils.getCurrentTimeMillis()).willReturn(1703318400000L);

		// when
		String result = fileKeyGenerator.generate("profile", ".png");

		// then
		String expected = "profile/ab/20250523/abcd1234_1703318400000.png";
		assertThat(result).isEqualTo(expected);
	}

	@Test
	void 확장자가_없는_파일도_파일키를_생성한다() {
		// given
		given(idUtils.generateUniqueId(8)).willReturn("abcd1234");
		given(timeUtils.getFormattedDate("yyyyMMdd")).willReturn("20250523");
		given(timeUtils.getCurrentTimeMillis()).willReturn(1703318400000L);

		// when
		String result = fileKeyGenerator.generate("profile", "");

		// then
		String expected = "profile/ab/20250523/abcd1234_1703318400000";
		assertThat(result).isEqualTo(expected);
	}
}