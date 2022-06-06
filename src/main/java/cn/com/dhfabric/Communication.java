package cn.com.dhfabric;

import com.alibaba.fastjson.JSON;
import lzy.secret.DiffieHellman;
import lzy.secret.HexStringTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Map;
import java.util.Scanner;

public class Communication {

    private static final Logger log = LoggerFactory.getLogger(Communication.class);



    public String communication () throws Exception {

        String num = "17";
        StringBuffer comData = new StringBuffer();

        // 1.‘卫星A’生成密钥对
        comData.append("\n======卫星A密钥对生成阶段======");
        KeyPair keyPairA = DiffieHellman.generateKeyPair();
        comData.append("\n···卫星A密钥对生成成功！");
        comData.append("\n=========================\n");

        // 2.‘卫星A’将自己的公钥发送到Hyperldeger Fabric，传递给‘卫星B’，实际上公钥对象中封装了大质数P、生成元G和‘卫星A’的公钥Y
        comData.append("\n=======S-G公钥传递阶段=======");
        PublicKey publicKeyA = keyPairA.getPublic();
        byte[] publicKeyAArray = publicKeyA.getEncoded();
        comData.append("\n···卫星A公钥："+HexStringTool.byteArrayToHex(publicKeyAArray));
        String initArgs1[] = {"Sat-Gro","{\"date\":\"20220602\",\"num\":\"" + num + "\",\"publicKeyArray\":\"" + HexStringTool.byteArrayToHex(publicKeyAArray) + "\"}"};
        DHChaincode dhChaincode = new DHChaincode();
        dhChaincode.invoke(Register.register("org1"),initArgs1);
        comData.append("\n···卫星A公钥存入Fabric网络成功！");

        // 3.‘卫星B’从Hyperldeger Fabric获取‘卫星A’公钥，根据‘卫星A’的公钥对象生成自己的密钥对
        Thread.sleep(1000);
        Map mapa = (Map) JSON.parse((String) dhChaincode.query(Register.register("org2"),new String[]{"Sat-Gro"}).get(200));
        comData.append("\n···卫星B从Fabric网络获取卫星A公钥成功！");
        comData.append("\n===========================\n");
        comData.append("\n=======卫星B密钥对生成阶段=======");
        String publicKeyAString = (String) mapa.get("publicKeyArray");
        byte[] republicKeyAArray = HexStringTool.hexToByteArray(publicKeyAString);
        KeyPair keyPairB = DiffieHellman.generateKeyPair(republicKeyAArray);
        comData.append("\n···卫星B密钥对生成成功！");
        comData.append("\n===========================\n");

        // 4.‘卫星B’将自己的公钥发送到Hyperldeger Fabric，回送给‘卫星A’
        comData.append("\n========G-S公钥传递阶段========");
        Thread.sleep(1000);
        PublicKey publicKeyB = keyPairB.getPublic();
        byte[] publicKeyBArray = publicKeyB.getEncoded();
        comData.append("\n···卫星B公钥："+HexStringTool.byteArrayToHex(publicKeyBArray));
        String initArgs2[] = {"Gro-Sat","{\"date\":\"20220423\",\"num\":\"" + num + "\",\"publicKeyArray\":\"" + HexStringTool.byteArrayToHex(publicKeyBArray) + "\"}"};
        dhChaincode.invoke(Register.register("org1"),initArgs2);
        comData.append("\n···卫星B公钥存入Fabric网络成功！");

        // 5.‘卫星A’从Hyperldeger Fabric获取‘卫星B’公钥
        Thread.sleep(1000);
        Map mapb = (Map) JSON.parse((String) dhChaincode.query(Register.register("org1"),new String[]{"Gro-Sat"}).get(200));
        String publicKeyBString = (String) mapb.get("publicKeyArray");
        byte[] republicKeyBArray = HexStringTool.hexToByteArray(publicKeyBString);
        comData.append("\n···卫星A从Fabric网络获取卫星B公钥成功！");
        comData.append("\n===========================\n");

        // 5.‘卫星A’根据自己的私钥和‘卫星B’的公钥生成对称密钥
        comData.append("\n========对称密钥生成阶段========");
        PublicKey republicKeyB = DiffieHellman.genPublicKey(publicKeyBArray);
        SecretKey secretKeyA = DiffieHellman.generateSecretKeyBySHA256(republicKeyB, keyPairA.getPrivate());

        // 6.‘卫星B’根据自己的私钥和‘卫星A’的公钥生成对称密钥
        PublicKey republicKeyA = DiffieHellman.genPublicKey(publicKeyAArray);
        SecretKey secretKeyB = DiffieHellman.generateSecretKeyBySHA256(republicKeyA, keyPairB.getPrivate());
        comData.append("\n卫星A生成的对称密钥：");
        comData.append(HexStringTool.byteArrayToHex(secretKeyA.getEncoded()));
        comData.append("\n卫星B生成的对称密钥：");
        comData.append(HexStringTool.byteArrayToHex(secretKeyB.getEncoded()));
        comData.append("\n===========================\n");

        comData.append("\n=====密钥协商成功，信息交互阶段=====");
        String message = "桃李不言，下自成蹊。";
        byte[] data = message.getBytes(Charset.forName("UTF-8"));

        // 初始化分组密码分组模式的初始化向量，本例AES CBC模式，分组长度128bit，所以IV也是128bit
        byte[] iv = DiffieHellman.initIV(16);
        // 加密
        byte[] ciphertext = DiffieHellman.encryption(secretKeyA, iv, data);
        comData.append("\n卫星A加密前的明文："+message);
        comData.append("\n卫星A加密后的密文：");
        comData.append(HexStringTool.byteArrayToHex(ciphertext));
        // 解密
        byte[] plaintext = DiffieHellman.decryption(secretKeyB, iv, ciphertext);
        comData.append("\n卫星B解密后的明文：");
        comData.append(HexStringTool.byteArrayToHex(plaintext));
        // 将解密后的字节数组还原成UTF8编码的字符
        comData.append("\n解密后的明文还原成UTF8编码："+new String(plaintext, Charset.forName("UTF-8")));
        comData.append("\n===========================\n");

        return comData.toString();
    }

}