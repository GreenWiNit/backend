package com.example.green.domain.file.domain;

import org.hibernate.annotations.DynamicUpdate;

import com.example.green.domain.common.BaseEntity;
import com.example.green.domain.file.domain.vo.FileMetaData;
import com.example.green.domain.file.domain.vo.FileStatus;
import com.example.green.domain.file.domain.vo.Purpose;
import com.example.green.domain.file.exception.FileException;
import com.example.green.domain.file.exception.FileExceptionMessage;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "files", indexes = {
	@Index(name = "unique_file_key", columnList = "fileKey", unique = true)
})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@DynamicUpdate
public class FileEntity extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "files_id")
	private Long id;

	@Column(nullable = false)
	private String fileKey;

	@Embedded
	private FileMetaData metaData;

	@Enumerated(EnumType.STRING)
	private Purpose purpose;

	@Enumerated(EnumType.STRING)
	private FileStatus fileStatus;

	protected FileEntity(FileMetaData metaData, String fileKey, Purpose purpose) {
		validateBasicConstruction(metaData, fileKey, purpose);
		this.metaData = metaData;
		this.fileKey = fileKey;
		this.purpose = purpose;
		this.fileStatus = FileStatus.TEMPORARY;
	}

	public static FileEntity create(FileMetaData metaData, String fileKey, Purpose purpose) {
		return new FileEntity(metaData, fileKey, purpose);
	}

	private static void validateBasicConstruction(FileMetaData metaData, String fileKey, Purpose purpose) {
		if (metaData == null) {
			throw new FileException(FileExceptionMessage.REQUIRED_FILE_METADATA);
		}
		if (fileKey == null || fileKey.isBlank()) {
			throw new FileException(FileExceptionMessage.REQUIRED_FILE_KEY);
		}
		if (purpose == null) {
			throw new FileException(FileExceptionMessage.REQUIRED_FILE_PURPOSE);
		}
	}

	public void markAsPermanent() {
		if (isDeleted()) {
			throw new FileException(FileExceptionMessage.CANNOT_RESTORE_DELETED_FILE);
		}
		this.fileStatus = FileStatus.PERMANENT;
	}
}
