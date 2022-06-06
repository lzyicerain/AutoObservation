package lzy.secret;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

public class DiffieHellman {
    // JDK文档地址：https://docs.oracle.com/en/java/javase/15/docs/specs/security/standard-names.html
    // 密钥协商算法密钥对生成入参，查看KeyPairGenerator.getInstance文档
    private static String DH_KEY = "DH";
    // 对称加密算法密钥生成入参，参看KeyGenerator.getInstance文档
    private static String AES_KEY = "AES";
    // 分组密码的模式入参，参看Cipher.getInstance文档
    private static String CIPHER_MODE = "AES/CBC/PKCS5Padding";
    private static SecureRandom random = new SecureRandom();

    /***
     * 卫星A生成密钥对
     * KeyPairGenerator.getInstance的入参查看KeyPairGenerator的文档
     *  文档地址：https://docs.oracle.com/en/java/javase/15/docs/specs/security/standard-names.html
     * @return 卫星A的密钥对
     * @throws Exception
     */
    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(DH_KEY);
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 卫星B收到卫星A的公钥数组：
     * 1.先将公钥数组解析成公钥对象
     * 	KeyFactory.getInstance的入参查看KeyFactory的文档
     *  文档地址：https://docs.oracle.com/en/java/javase/15/docs/specs/security/standard-names.html
     * 2.根据卫星A的公钥对象生成自己的密钥对
     * @param publicKeyArray
     * @return
     * @throws Exception
     */
    public static KeyPair generateKeyPair(byte[] publicKeyArray) throws Exception {
        // 将卫星A发过来的公钥数组解析成公钥对象
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKeyArray);
        KeyFactory keyFactory = KeyFactory.getInstance(DH_KEY);
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        // 卫星B根据卫星A的公钥生成自己的密钥对
        DHParameterSpec dhParameterSpec = ((DHPublicKey)publicKey).getParams();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(DH_KEY);
        keyPairGenerator.initialize(dhParameterSpec);
        return keyPairGenerator.generateKeyPair();
    }

    /**
     * 将公钥数组转化为公钥对象：
     * @param publicKeyArray
     * @return 公钥对象
     * @throws Exception
     */
    public static PublicKey genPublicKey(byte[] publicKeyArray) throws Exception {
        // 将卫星A发过来的公钥数组解析成公钥对象
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(publicKeyArray);
        KeyFactory keyFactory = KeyFactory.getInstance(DH_KEY);
        return keyFactory.generatePublic(x509EncodedKeySpec);
    }

    /**
     * 卫星A和卫星B通过自己的私钥和对方的公钥计算生成相同的对称密钥
     *  KeyAgreement.getInstance入参查看相同的文档
     *  文档地址：https://docs.oracle.com/en/java/javase/15/docs/specs/security/standard-names.html
     * 注意：这种实现方式不安全，已不推荐使用，参看JDK 8u161的更新文档
     * 	文档地址：https://www.oracle.com/java/technologies/javase/8u161-relnotes.html
     * @param publicKey 对方的公钥
     * @param privateKey 自己的私钥
     * @return 用于信息加密的对称密钥
     * @throws Exception
     */
    @Deprecated
    public static SecretKey generateSecretKey(PublicKey publicKey, PrivateKey privateKey) throws Exception {
        KeyAgreement keyAgreement = KeyAgreement.getInstance(DH_KEY);
        keyAgreement.init(privateKey);
        keyAgreement.doPhase(publicKey, true);
        // 关注keyAgreement.generateSecret(AES_KEY)，在JDK 8u161前后有很大差异
        return keyAgreement.generateSecret(AES_KEY);
    }

    /**
     * 卫星A和卫星B通过自己的私钥和对方的公钥计算生成相同的对称密钥
     * 注意：当前推荐的用法
     * @param publicKey 对方的公钥
     * @param privateKey 自己的私钥
     * @return 生成的对称密钥
     * @throws Exception
     */
    public static SecretKey generateSecretKeyBySHA256(PublicKey publicKey, PrivateKey privateKey) throws Exception {
        KeyAgreement keyAgreement = KeyAgreement.getInstance(DH_KEY);
        keyAgreement.init(privateKey);
        keyAgreement.doPhase(publicKey, true);
        // 当前推荐的做法
        byte[] keyArray = keyAgreement.generateSecret();
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] digest = messageDigest.digest(keyArray);
        return new SecretKeySpec(digest, AES_KEY);
    }

    /**
     * 生成分组密码分组模式的初始化向量
     * 本例中，AES的分组i128bt，16字节
     * @param length
     * @return
     */
    public static byte[] initIV(int length) {
        return random.generateSeed(length);
    }

    /**
     * 加密
     * @param secretKey 密钥协商阶段协商的对称密钥
     * @param iv 分组密码分组模式的初始化向量，加密和解密需要使用相同的iv
     * @param data 需要加密的明文字节数组
     * @return 密文
     * @throws Exception
     */
    public static byte[] encryption(SecretKey secretKey, byte[] iv, byte[] data) throws Exception {
        // 密钥和算法有关系，和加解密模式没有关系，所以上面分成了AES_KEY和CIPHER_MODE
        Cipher cipher = Cipher.getInstance(CIPHER_MODE);
        // 分组密码分组模式的初始化向量参数，本例中是CBC模式
        IvParameterSpec ivp = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivp);
        return cipher.doFinal(data);
    }

    /**
     * 解密
     * @param secretKey 密钥协商阶段协商的对称密钥
     * @param iv 分组密码分组模式的初始化向量，加密和解密需要使用相同的iv
     * @param data 需要解密的密文的字节数组
     * @return 解密后的明文
     * @throws Exception
     */
    public static byte[] decryption(SecretKey secretKey, byte[] iv, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(CIPHER_MODE);
        // 分组密码分组模式的初始化向量参数，本例中是CBC模式
        IvParameterSpec ivp = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivp);
        return cipher.doFinal(data);
    }
}