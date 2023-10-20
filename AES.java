//not our code. This is just for learning purposes and to see how everything works. 
//code is from https://www.section.io/engineering-education/implementing-aes-encryption-and-decryption-in-java/
// we used this code to understand line by line what the algorithm was doing


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.util.Base64;
import java.util.Scanner;

public class AES {
    
    // here we are initailizing the key size to 128
    private SecretKey key;
    private final int KEY_SIZE = 128;
    private final int DATA_LENGTH = 128;
    private Cipher encryptionCipher;

    //this method creates the keys.
    public void init() throws Exception {
        //object to generate the key
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES"); 

        //getting the size of the key
        keyGenerator.init(KEY_SIZE); 
        
        //assigning the value of key. Generate key creates an secret key
        key = keyGenerator.generateKey(); 
    }

    //here we are encrypting the message
    //here we are passing the key that we generated
    public String encrypt(String data) throws Exception { 
        byte[] dataInBytes = data.getBytes();

        //this is to get the cipher instance that we need
        encryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");

        //to encrypt
        encryptionCipher.init(Cipher.ENCRYPT_MODE, key); 

        byte[] encryptedBytes = encryptionCipher.doFinal(dataInBytes);

        //we are returning the 
        return encode(encryptedBytes);
    }

    //this helps us decrypt the cipher
    public String decrypt(String encryptedData) throws Exception {

        byte[] dataInBytes = decode(encryptedData);

         //this is to get the cipher instance that we need after transformation
        Cipher decryptionCipher = Cipher.getInstance("AES/GCM/NoPadding");

        GCMParameterSpec spec = new GCMParameterSpec(DATA_LENGTH, encryptionCipher.getIV());
        decryptionCipher.init(Cipher.DECRYPT_MODE, key, spec);
        byte[] decryptedBytes = decryptionCipher.doFinal(dataInBytes);
        return new String(decryptedBytes);
    }


    //this is to turn the bytes of the array to a string. Called by the encrypt function
    private String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    //this is to turn the bytes to atring. It is called by the decrypt function
    private byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }

    //main method
    public static void main(String[] args) {
        try {
            AES aes_encryption = new AES();
            aes_encryption.init();

            Scanner sc= new Scanner(System.in);
            System.out.println("please enter the message to encrypt");
            String message= sc.nextLine();
            //passing the message to the encrypt function

            String encryptedData = aes_encryption.encrypt(message);
            String decryptedData = aes_encryption.decrypt(encryptedData);

            System.out.println("Encrypted Data : " + encryptedData);
            System.out.println("Decrypted Data : " + decryptedData);
        } catch (Exception ignored) {
        }
    }
}
