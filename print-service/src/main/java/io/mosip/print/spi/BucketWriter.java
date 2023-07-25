package io.mosip.print.spi;

/**
 * This interface is has specifications for writing generated uins in an object store bucket.
 * 
 * The user of this interface will have basic functionalities related to storing object.
 * 
 * @author condeis
 * 
 * @since 1.2.0.1
 */
public interface BucketWriter {
	
		boolean writeInBucket(String registrationId, byte[] content);

}
