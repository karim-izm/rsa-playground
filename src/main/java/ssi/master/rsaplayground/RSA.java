package ssi.master.rsaplayground;
import java.math.BigInteger;
import java.util.Random;

public class RSA {

    private BigInteger privateKey;
    private BigInteger publicKey;
    private BigInteger modulus;

    public void generateKeys(int keyLength) {
        // Generate two large random prime numbers, p and q
        Random random = new Random();
        BigInteger p = BigInteger.probablePrime(keyLength, random);
        BigInteger q = BigInteger.probablePrime(keyLength, random);

        // Calculate n = p * q
        modulus = p.multiply(q);

        // Calculate the totient function phi = (p - 1) * (q - 1)
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));

        // Choose a random integer e such that 1 < e < phi and gcd(e, phi) = 1
        BigInteger e;
        do {
            e = new BigInteger(phi.bitLength(), random);
        } while (e.compareTo(BigInteger.ONE) <= 0 || e.compareTo(phi) >= 0 || !e.gcd(phi).equals(BigInteger.ONE));

        publicKey = e;

        // Calculate the modular multiplicative inverse of e mod phi (private key)
        privateKey = e.modInverse(phi);
    }

    public BigInteger encrypt(BigInteger message) {
        return message.modPow(publicKey, modulus);
    }

    public BigInteger decrypt(BigInteger encryptedMessage) {
        return encryptedMessage.modPow(privateKey, modulus);
    }

    public static void main(String[] args) {
        RSA rsa = new RSA();
        rsa.generateKeys(128);

        // Test encryption and decryption
        BigInteger plaintext = new BigInteger("123456789");
        BigInteger ciphertext = rsa.encrypt(plaintext);
        BigInteger decryptedText = rsa.decrypt(ciphertext);

        System.out.println("Plaintext: " + plaintext);
        System.out.println("Ciphertext: " + ciphertext);
        System.out.println("Decrypted text: " + decryptedText);
    }
}

