import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESFileEncryption {

    // AES key size in bits
    public static final int AES_KEY_SIZE = 256;

    // GCM nonce/IV size in bytes
    public static final int GCM_IV_SIZE = 12;

    // GCM tag size in bytes
    public static final int GCM_TAG_SIZE = 16;

    // Encrypts a file using AES-256-GCM
    public static void encryptFile(File inputFile, File outputFile, SecretKeySpec key) throws Exception {
        // Get a cipher instance of AES-256-GCM
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        // Generate a random IV
        byte[] iv = new byte[GCM_IV_SIZE];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);

        // Create a GCM parameter spec with the IV and tag size
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_SIZE * 8, iv);

        // Initialize the cipher in encrypt mode with the key and spec
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);

        // Write the IV to the output file as a prefix
        FileOutputStream out = new FileOutputStream(outputFile);
        out.write(iv);

        // Wrap the output stream with a cipher output stream
        CipherOutputStream cos = new CipherOutputStream(out, cipher);

        // Read the input file and write to the cipher output stream
        FileInputStream in = new FileInputStream(inputFile);
        byte[] buffer = new byte[8192];
        int nread;
        while ((nread = in.read(buffer)) > 0) {
            cos.write(buffer, 0, nread);
        }

        // Close the streams
        in.close();
        cos.close();
    }

    // Decrypts a file using AES-256-GCM
    public static void decryptFile(File inputFile, File outputFile, SecretKeySpec key) throws Exception {
        // Get a cipher instance of AES-256-GCM
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        // Read the IV from the input file as a prefix
        FileInputStream in = new FileInputStream(inputFile);
        byte[] iv = new byte[GCM_IV_SIZE];
        in.read(iv);

        // Create a GCM parameter spec with the IV and tag size
        GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_SIZE * 8, iv);

        // Initialize the cipher in decrypt mode with the key and spec
        cipher.init(Cipher.DECRYPT_MODE, key, spec);

        // Wrap the input stream with a cipher input stream
        CipherInputStream cis = new CipherInputStream(in, cipher);

        // Write the decrypted data to the output file
        FileOutputStream out = new FileOutputStream(outputFile);
        byte[] buffer = new byte[8192];
        int nread;
        while ((nread = cis.read(buffer)) > 0) {
            out.write(buffer, 0, nread);
        }

        // Close the streams
        out.close();
        cis.close();
    }
}
