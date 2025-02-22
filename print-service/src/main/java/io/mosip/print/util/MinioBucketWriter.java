package io.mosip.print.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.mosip.commons.khazana.spi.ObjectStoreAdapter;
import io.mosip.print.logger.PrintLogger;
import io.mosip.print.spi.BucketWriter;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * The class MinioBucketWriter to write generated uins in a bucket
 * 
 * @author CONDEIS
 *
 */
@Component
public class MinioBucketWriter implements BucketWriter {
	static Logger printLogger = PrintLogger.getLogger(MinioBucketWriter.class);
	@Value("${minio.api.url}}")
	private String minioApiURl;
	@Value("${minio.port.number}")
	private int portNumber;
	@Value("${minio.url.ssl.secured}")
	private boolean sslSecured;
	@Value("${minio.client.id}")
	private String minioClientId;
	@Value("${minio.secret.key}")
	private String minioSecretKey;
	@Value("${minio.bucket.name}")
	private String bucketName;

	private static final String source = "PRINT_SERVICE";
	private static final String process = "PRINTING";
	private static final String account = "PRINTING-MANAGER";

	@Autowired
	@Qualifier("S3Adapter")
	private ObjectStoreAdapter s3Adapter;

	/**
	 * @param fullPath
	 * @param rid
	 * @param folder
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 */
	public boolean copyToMinio(String fullPath, String rid, String folder)
			throws IOException, NoSuchAlgorithmException, InvalidKeyException {
		printLogger.info("Received a request to write card referenced " + rid + "into bucket");

	try {
		printLogger.info("Trying to write into  " + rid + "into bucket for url "+minioApiURl);
			MinioClient minioClient = MinioClient.builder()
				.endpoint(minioApiURl,portNumber, sslSecured)
				.credentials(minioClientId, minioSecretKey).build();
		boolean found = minioClient
				.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
		if (!found) {
			printLogger.info("Creating bucket" + bucketName);
				minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
			} else {
				printLogger.info("Bucket already exists" + bucketName);
			}
			minioClient.uploadObject(UploadObjectArgs.builder().bucket(bucketName)
					.object("GENERATED/" + folder + "/" + folderFormat() + "/" + rid + ".pdf").filename(fullPath)
					.build());
			printLogger.info(fullPath + " is successfully uploaded as " + "object " + rid + " to bucket 'uins'.");
			return true;
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String trace = sw.toString();
			printLogger.error("Error occurred: " + trace);
			return false;
		}

	}

	public boolean writeInBucket(String registrationId, byte[] content) {
		File fileCreated = new File(registrationId + ".pdf");
		try {
			Files.write(fileCreated.toPath(), content);
			copyToMinioKhazana(registrationId, new ByteArrayInputStream(content));
	//		copyToMinio(fileCreated.getName().toString(), registrationId, "");
		} catch (InvalidKeyException e) {
			printLogger.error("Invalid key exception " + e.getMessage());
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			printLogger.error("No such algorithm exception " + e.getMessage());
		} catch (IOException e) {
			printLogger.error("IO Exception " + registrationId + " " + e.getMessage());
		}
		return false;
	}

	/**
	 * 
	 * @return folderFormatted in a specific way
	 */
	private String folderFormat() {
		String folderFormat = "";
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdf;
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		folderFormat = sdf.format(date);
		return folderFormat;
	}

	boolean copyToMinioKhazana(String rid, InputStream data)
			throws IOException, NoSuchAlgorithmException, InvalidKeyException {
		boolean response = false;
		String fileName = "GENERATED/" + folderFormat() + "/" + rid + ".pdf";
		printLogger.info("Received a request to write card referenced " + "" + "into bucket");
		try {
			response = s3Adapter.putObject(account, bucketName, source, process, fileName, data);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			String trace = sw.toString();
			printLogger.error("Error occurred: " + trace);
			return false;
		}

		return response;
	}
}
