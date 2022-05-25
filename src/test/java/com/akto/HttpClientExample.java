package com.akto;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import com.amazonaws.encryptionsdk.AwsCrypto;
import com.amazonaws.encryptionsdk.CommitmentPolicy;
import com.amazonaws.encryptionsdk.CryptoResult;
import com.amazonaws.encryptionsdk.kms.KmsMasterKey;
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider;

public class HttpClientExample {
    private static String keyArn;
    private static String plaintext;

    public static void main(final String[] args) {
        keyArn = args[0];
        plaintext = args[1];

        // Instantiate the SDK
        final AwsCrypto crypto = AwsCrypto.standard();

        // Set up the master key provider
        final KmsMasterKeyProvider prov = KmsMasterKeyProvider.builder().withDefaultRegion("us-east-1").buildStrict(keyArn);

        // Set up the encryption context
        // NOTE: Encrypted data should have associated encryption context
        // to protect its integrity. This example uses placeholder values.
        // For more information about the encryption context, see
        // https://docs.aws.amazon.com/encryption-sdk/latest/developer-guide/concepts.html#encryption-context
        final Map<String, String> context = Collections.singletonMap("ExampleContextKey", "ExampleContextValue");

        // Encrypt the data
        //        
        // final CryptoResult<byte[], KmsMasterKey> encryptResult = crypto.encryptData(prov, plaintext.getBytes(StandardCharsets.UTF_8), context);
        final byte[] ciphertext = new byte[] {
            2, 5, 120, 57, 96, -100, -55, -96, 3, 63, 32, -25, -69, 2, -37, 9, 79, -74, 123, 20, -79, 115, -89, -83, 42, -49, 120, 45, 118, 125, -21, 127, 100, -112, 98, 0, -121, 0, 2, 0, 17, 69, 120, 97, 109, 112, 108, 101, 67, 111, 110, 116, 101, 120, 116, 75, 101, 121, 0, 19, 69, 120, 97, 109, 112, 108, 101, 67, 111, 110, 116, 101, 120, 116, 86, 97, 108, 117, 101, 0, 21, 97, 119, 115, 45, 99, 114, 121, 112, 116, 111, 45, 112, 117, 98, 108, 105, 99, 45, 107, 101, 121, 0, 68, 65, 117, 110, 86, 106, 104, 73, 85, 122, 116, 85, 111, 77, 118, 73, 100, 102, 70, 47, 56, 101, 104, 50, 71, 111, 113, 69, 82, 99, 87, 49, 47, 104, 65, 105, 79, 56, 73, 65, 72, 76, 48, 89, 83, 79, 82, 83, 97, 106, 57, 97, 109, 90, 81, 103, 80, 80, 111, 85, 101, 79, 51, 87, 113, 109, 65, 61, 61, 0, 1, 0, 7, 97, 119, 115, 45, 107, 109, 115, 0, 75, 97, 114, 110, 58, 97, 119, 115, 58, 107, 109, 115, 58, 117, 115, 45, 101, 97, 115, 116, 45, 49, 58, 50, 51, 51, 57, 48, 53, 49, 50, 53, 53, 51, 48, 58, 107, 101, 121, 47, 101, 52, 97, 102, 55, 55, 99, 102, 45, 102, 51, 55, 97, 45, 52, 99, 97, 48, 45, 98, 99, 54, 48, 45, 51, 99, 98, 53, 49, 100, 54, 51, 54, 49, 98, 56, 0, -72, 1, 2, 1, 0, 120, -25, -29, 127, -56, 33, -122, -42, 75, 78, 20, 30, 60, -85, 103, 106, 126, -15, -83, 123, -30, 74, -70, 51, 12, -74, 19, -110, 16, -15, -18, -59, -119, 1, -82, 33, 109, -26, -108, -83, 23, -125, 30, -90, 6, 43, 58, -52, 109, -71, 0, 0, 0, 126, 48, 124, 6, 9, 42, -122, 72, -122, -9, 13, 1, 7, 6, -96, 111, 48, 109, 2, 1, 0, 48, 104, 6, 9, 42, -122, 72, -122, -9, 13, 1, 7, 1, 48, 30, 6, 9, 96, -122, 72, 1, 101, 3, 4, 1, 46, 48, 17, 4, 12, 62, -84, -37, -35, 114, -57, 63, 32, -68, 20, -124, 73, 2, 1, 16, -128, 59, -48, -61, 38, -1, -95, 28, -98, 61, -37, -8, -57, -94, 52, 90, -59, 118, -125, -69, 51, -116, 59, -38, 87, -21, -44, 93, -112, -128, -5, -78, -25, -33, 91, 64, -126, -82, -21, 51, 61, 70, 50, 99, 12, -35, -127, 47, 7, 76, 2, -87, -72, -13, -58, 83, -39, -65, 14, -101, 42, 2, 0, 0, 16, 0, 25, 3, -29, 113, 106, 107, -28, -113, -88, -66, -11, 125, -100, -60, 121, 77, 117, 15, -20, -8, -100, 96, -81, 117, -30, 74, -105, -83, 55, 50, 118, -8, -47, -19, -113, -28, -69, -46, 40, 43, -65, -26, -66, -122, -126, 6, -38, -78, -1, -1, -1, -1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 9, -121, -35, -68, -14, 102, -23, -35, -58, -51, -27, -102, -78, 101, 58, -83, -59, -5, 72, 124, 31, 29, -121, 37, -19, -92, 0, 103, 48, 101, 2, 49, 0, -64, -61, 61, 123, 28, 68, -32, 8, 109, 37, -13, 112, 102, 15, 62, -114, -4, 51, 13, -95, -55, 66, 25, -7, 88, -29, -49, -102, -106, -66, -106, 62, 118, -67, 58, 110, 24, -82, 64, 96, 83, 86, 117, 7, 52, -96, 115, -43, 2, 48, 59, -74, -23, 76, -91, -120, 12, 58, 121, 80, 123, -16, 22, 43, -3, 25, -104, 56, 87, 5, -83, -2, 106, -15, -78, -67, -115, -30, -23, -80, -84, -35, 52, -61, -1, 94, 21, 110, 111, -122, -86, -124, 57, -119, -3, -35, -111, -95
        };
        System.out.println("Ciphertext: " + Arrays.toString(ciphertext));

        // Decrypt the data
        final CryptoResult<byte[], KmsMasterKey> decryptResult = crypto.decryptData(prov, ciphertext);
        // Your application should verify the encryption context and the KMS key to
        // ensure this is the expected ciphertext before returning the plaintext
        if (!decryptResult.getMasterKeyIds().get(0).equals(keyArn)) {
            throw new IllegalStateException("Wrong key id!");
        }

        // The AWS Encryption SDK may add information to the encryption context, so check to
        // ensure all of the values that you specified when encrypting are *included* in the returned encryption context.
        assert Arrays.equals(decryptResult.getResult(), plaintext.getBytes(StandardCharsets.UTF_8));

        // The data is correct, so return it. 
        System.out.println("Decrypted: " + new String(decryptResult.getResult(), StandardCharsets.UTF_8));
    }
}
