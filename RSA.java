import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Base64.Encoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class RSA {
    
static PrivateKey privateKey;
static PublicKey publicKey;

    public static void main(String args[]) throws NoSuchAlgorithmException, InvalidKeyException, FileNotFoundException, InvalidKeySpecException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, IOException{
        System.out.println("RSA WITH GENERATOR");
        withGenerator("This is a secret message");
        System.out.println("------------------------------------------");
        System.out.println("RSA FROM SCRATCH");
        fromScratch(12);



    }

    public static void withGenerator(String secretMessage) throws NoSuchAlgorithmException, FileNotFoundException, IOException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
        //Generate the key pair
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keyPair = generator.generateKeyPair();

        //Get the private and public keys
        privateKey = keyPair.getPrivate();
        publicKey = keyPair.getPublic();

        //Store the keys in a file for safe keeping
        try(FileOutputStream fileOutputStream = new FileOutputStream("publicKeyFile.key")){
            fileOutputStream.write(publicKey.getEncoded());
        }
        try(FileOutputStream fileOutputStream = new FileOutputStream("privateKeyFile.key")){
            fileOutputStream.write(privateKey.getEncoded());
        }

        //Get key from the file
        File publicKeyFile = new File("publicKeyFile.key");
        File privateKeyFile = new File("privateKeyFile.key");
        byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
        byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());
        KeyFactory keyFactoryPublic = KeyFactory.getInstance("RSA");
        KeyFactory keyFactoryPrivate = KeyFactory.getInstance("RSA");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        keyFactoryPublic.generatePublic(publicKeySpec);
        keyFactoryPrivate.generatePrivate(privateKeySpec);

        //Encrypt
        Cipher encryption = Cipher.getInstance("RSA");
        encryption.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] secretMessageBytes = secretMessage.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedMessageBytes = encryption.doFinal(secretMessageBytes);
        String encodedMessage = Base64.getEncoder().encodeToString(encryptedMessageBytes);
        
        //Decrypt
        Cipher decryption = Cipher.getInstance("RSA");
        decryption.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedMessageBytes = decryption.doFinal(encryptedMessageBytes);
        String plainMessage = new String(decryptedMessageBytes, StandardCharsets.UTF_8);
        
        //print results
        System.out.println("Secret Message: "+ secretMessage);
        System.out.println("Encrypted Message: " + encodedMessage);
        System.out.println("Decrypted Message: " + plainMessage);
        
    }

    public static void fromScratch(int msg)
    {
        int p, q, n, z, d = 0, e, i;
        double c;
        BigInteger msgback;
 
        // 1st prime number p
        p = 2;
        System.out.println("p: " + p);
 
        // 2nd prime number q
        q = 7;
        System.out.println("q: " + q);

        //calculate n to use in public key
        n = p * q;
        System.out.println("n: " + n);

        //calculate z (totient(n)) 
        z = (p - 1) * (q - 1);
        System.out.println("totient: " + z);
        
        //calculate e to use in public key -> small exponent, int, not a factor of n, -1 < e < totient(n)
        for (e = 2; e < z; e++) {
 
            // e is for public key exponent
            if (gcd(e, z) == 1) {
                break;
            }
        }
        System.out.println("e: " + e);

        //caluculate d -> the private key
        for (i = 0; i <= 9; i++) {
            int x = 1 + (i * z);
 
            // d is for private key exponent
            if (x % e == 0) {
                d = x / e;
                break;
            }
        }
        System.out.println("the value of d = " + d);

        System.out.println("Secret Message: " + msg);

        //Encrypt
        c = (Math.pow(msg, e)) % n;
        System.out.println("Encrypted Message: " + c);

        //Converting to BigInteger
        BigInteger N = BigInteger.valueOf(n);
        BigInteger C = BigDecimal.valueOf(c).toBigInteger();

        //Decrypt
        msgback = (C.pow(d)).mod(N);
        System.out.println("Decrypted Message: " + msgback);
    }
 
    static int gcd(int e, int z)
    {
        if (e == 0)
            return z;
        else
            return gcd(z % e, e);
    }


}
