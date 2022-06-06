package cn.com.dhfabric;

import org.bouncycastle.crypto.CryptoException;
import org.hyperledger.fabric.sdk.Enrollment;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;

public class Register {

    private static final String org1keyFolderPath = "F:\\01DownloadFrormLLQ\\fabric-sdk\\fabric\\src\\main\\resources\\crypto-config\\peerOrganizations\\org1.example.com\\users\\Admin@org1.example.com\\msp\\keystore";
    private static final String org1keyFileName = "243a956b748e93c851274bb20da4beaadb3472ca68ec3c64b483d94285b5dc95_sk";
    private static final String org1certFolderPath = "F:\\01DownloadFrormLLQ\\fabric-sdk\\fabric\\src\\main\\resources\\crypto-config\\peerOrganizations\\org1.example.com\\users\\Admin@org1.example.com\\msp\\admincerts";
    private static final String org1certFileName = "Admin@org1.example.com-cert.pem";

    private static final String org2keyFolderPath = "F:\\01DownloadFrormLLQ\\fabric-sdk\\fabric\\src\\main\\resources\\crypto-config\\peerOrganizations\\org2.example.com\\users\\Admin@org2.example.com\\msp\\keystore";
    private static final String org2keyFileName = "e1a8aadff540b52b660e963e4dd9ca3d92faafd021db2465776a30d31f44cecc_sk";
    private static final String org2certFolderPath = "F:\\01DownloadFrormLLQ\\fabric-sdk\\fabric\\src\\main\\resources\\crypto-config\\peerOrganizations\\org2.example.com\\users\\Admin@org2.example.com\\msp\\admincerts";
    private static final String org2certFileName = "Admin@org2.example.com-cert.pem";

    private static  final String tlsOrg1PeerFilePath = "F:\\01DownloadFrormLLQ\\fabric-sdk\\fabric\\src\\main\\resources\\crypto-config\\peerOrganizations\\org1.example.com\\peers\\peer0.org1.example.com\\msp\\tlscacerts\\tlsca.org1.example.com-cert.pem";
    private static  final String tlsOrg2PeerFilePath = "F:\\01DownloadFrormLLQ\\fabric-sdk\\fabric\\src\\main\\resources\\crypto-config\\peerOrganizations\\org2.example.com\\peers\\peer0.org2.example.com\\msp\\tlscacerts\\tlsca.org2.example.com-cert.pem";


    public static UserContext register (String orgName) throws InvalidKeySpecException, NoSuchAlgorithmException, CryptoException, IOException {

        UserContext userContext = new UserContext();

        if (orgName.equals("org1")){
            userContext.setAffiliation("Org1");
            userContext.setMspId("Org1MSP");
            userContext.setAccount("楚雁潮");
            userContext.setName("admin");
            userContext.setPeer0DnsName("peer0.org1.example.com");
            userContext.setPeer1DnsName("peer1.org1.example.com");
            userContext.setPeer0grpcUrl("grpcs://peer0.org1.example.com:7051");
            userContext.setPeer1grpcUrl("grpcs://peer1.org1.example.com:8051");
            userContext.setTlsFilePath(tlsOrg1PeerFilePath);
            Enrollment enrollment =  UserUtils.getEnrollment(org1keyFolderPath,org1keyFileName,org1certFolderPath,org1certFileName);
            userContext.setEnrollment(enrollment);
        }else if (orgName.equals("org2")){
            userContext.setAffiliation("Org2");
            userContext.setMspId("Org2MSP");
            userContext.setAccount("梁新月");
            userContext.setName("admin");
            userContext.setPeer0DnsName("peer0.org2.example.com");
            userContext.setPeer1DnsName("peer1.org2.example.com");
            userContext.setPeer0grpcUrl("grpcs://peer0.org2.example.com:9051");
            userContext.setPeer1grpcUrl("grpcs://peer1.org2.example.com:10051");
            userContext.setTlsFilePath(tlsOrg2PeerFilePath);
            Enrollment enrollment =  UserUtils.getEnrollment(org2keyFolderPath,org2keyFileName,org2certFolderPath,org2certFileName);
            userContext.setEnrollment(enrollment);
        }else {
            System.out.println("Org input error!");
        }
        return userContext;
    }
}