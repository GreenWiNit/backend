package com.example.green.domain.file.domain;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import com.example.green.domain.file.domain.vo.FileMetaData;
import com.example.green.domain.file.domain.vo.FileStatus;
import com.example.green.domain.file.domain.vo.Purpose;
import com.example.green.domain.file.exception.FileException;
import com.example.green.domain.file.exception.FileExceptionMessage;

class FileEntityTest {

	@Test
	void 파일_정보는_임시상태로_생성이_된다() {
		// given
		FileMetaData fileMetaData = mock(FileMetaData.class);
		Purpose purpose = mock(Purpose.class);
		String fileKey = "fileKey";

		// when
		FileEntity fileEntity = FileEntity.create(fileMetaData, fileKey, purpose);

		assertThat(fileEntity.getFileStatus()).isEqualTo(FileStatus.TEMPORARY);
		assertThat(fileEntity.getMetaData()).isEqualTo(fileMetaData);
		assertThat(fileEntity.getPurpose()).isEqualTo(purpose);
		assertThat(fileEntity.getFileKey()).isEqualTo(fileKey);
	}

	@Test
	void 파일_객체_생성시_메타_데이터는_필수_정보이다() {
		// given
		Purpose purpose = mock(Purpose.class);
		String fileKey = "fileKey";

		// when & then
		assertThatThrownBy(() -> FileEntity.create(null, fileKey, purpose))
			.isInstanceOf(FileException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", FileExceptionMessage.REQUIRED_FILE_METADATA);
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {"      "})
	void 파일_객체_생성시_파일키_정보가_없거나_공백이라면_예외가_발생한다(String fileKey) {
		// given
		FileMetaData fileMetaData = mock(FileMetaData.class);
		Purpose purpose = mock(Purpose.class);

		// when & then
		assertThatThrownBy(() -> FileEntity.create(fileMetaData, fileKey, purpose))
			.isInstanceOf(FileException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", FileExceptionMessage.REQUIRED_FILE_KEY);
	}

	@Test
	void 파일_객체의_목적_정보가_없으면_예외가_발생한다() {
		// given
		FileMetaData fileMetaData = mock(FileMetaData.class);
		String fileKey = "fileKey";

		// when & then
		assertThatThrownBy(() -> FileEntity.create(fileMetaData, fileKey, null))
			.isInstanceOf(FileException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", FileExceptionMessage.REQUIRED_FILE_PURPOSE);
	}

	@Test
	void 임시_파일은_영구파일로_저장할_수_있다() {
		// given
		FileMetaData fileMetaData = mock(FileMetaData.class);
		Purpose purpose = mock(Purpose.class);
		String fileKey = "fileKey";
		FileEntity fileEntity = FileEntity.create(fileMetaData, fileKey, purpose);

		// when
		fileEntity.markAsPermanent();

		// then
		assertThat(fileEntity.getFileStatus()).isEqualTo(FileStatus.PERMANENT);
	}

	@Test
	void 제거된_파일은_영구파일로_복원할_수_없다() {
		// given
		FileMetaData fileMetaData = mock(FileMetaData.class);
		Purpose purpose = mock(Purpose.class);
		String fileKey = "fileKey";
		FileEntity fileEntity = FileEntity.create(fileMetaData, fileKey, purpose);
		fileEntity.markDeleted();

		// when
		assertThatThrownBy(fileEntity::markAsPermanent)
			.isInstanceOf(FileException.class)
			.hasFieldOrPropertyWithValue("exceptionMessage", FileExceptionMessage.CANNOT_RESTORE_DELETED_FILE);
	}
}