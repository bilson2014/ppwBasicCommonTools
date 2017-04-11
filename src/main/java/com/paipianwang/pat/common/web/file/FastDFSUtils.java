package com.paipianwang.pat.common.web.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FastDFSUtils {

	private final FastDFSClient client = null;

	public String upload(File file, String fileName) {

		final String fileId = client.uploadFile(file, fileName);
		return fileId;
	}

	public String upload(MultipartFile file) {
		final String fileId = client.uploadFile(file);
		return fileId;
	}

	public String upload(InputStream inputStream, String fileName) {
		final String fileId = client.uploadFile(inputStream, fileName);
		return fileId;
	}

	public void download(final String fileId, final String destFilePath) throws IOException {

		final InputStream stream = client.downloadFile(fileId);
		final File destFile = new File(destFilePath);
		FileUtils.copyInputStreamToFile(stream, destFile);
	}

	public InputStream download(final String fileId) throws IOException {
		return client.downloadFile(fileId);
	}

	public int delete(final String fileId) {

		final int result = client.deleteFile(fileId);
		return result;
	}

	public String locateFileStoragePath() {
		final String result = client.locateSource();
		return result;
	}
}
