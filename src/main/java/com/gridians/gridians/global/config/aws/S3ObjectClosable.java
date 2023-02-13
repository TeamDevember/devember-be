package com.gridians.gridians.global.config.aws;

import com.amazonaws.services.s3.model.S3Object;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Closeable;
import java.io.IOException;

@Data
@AllArgsConstructor
public class S3ObjectClosable implements Closeable {
	private final S3Object s3Object;

	@Override
	public void close() throws IOException {
		s3Object.getObjectContent().abort();
		s3Object.close();
	}
}
