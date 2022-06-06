package cn.com.dhfabric;

import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.TransactionRequest;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.exception.ProposalException;
import org.hyperledger.fabric.sdk.exception.TransactionException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DHChaincode {

    private static  final String tlsOrderFilePath = "F:\\01DownloadFrormLLQ\\fabric-sdk\\fabric\\src\\main\\resources\\crypto-config\\ordererOrganizations\\example.com\\orderers\\orderer.example.com\\msp\\tlscacerts\\tlsca.example.com-cert.pem";

    public Map query(UserContext userContext,String initArgs[]) throws InvalidKeySpecException, NoSuchAlgorithmException, CryptoException, IOException, IllegalAccessException, InvalidArgumentException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, org.hyperledger.fabric.sdk.exception.CryptoException, ProposalException, TransactionException, org.bouncycastle.crypto.CryptoException {

        FabricClient fabricClient = new FabricClient(userContext);
        Peer peer0 = fabricClient.getPeer(userContext.getPeer0DnsName(),userContext.getPeer0grpcUrl(),userContext.getTlsFilePath());
        Peer peer1 = fabricClient.getPeer(userContext.getPeer1DnsName(),userContext.getPeer1grpcUrl(),userContext.getTlsFilePath());
        List<Peer> peers = new ArrayList<>();
        peers.add(peer0);
        peers.add(peer1);
        Map map =  fabricClient.queryChaincode(peers,"mychannel", TransactionRequest.Type.GO_LANG,"dhex","query",initArgs);
        return map;

    }


    public void invoke(UserContext userContext,String initArgs[]) throws IllegalAccessException, InvalidArgumentException, InstantiationException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, CryptoException, ProposalException, TransactionException {

        FabricClient fabricClient = new FabricClient(userContext);
        Peer peer0 = fabricClient.getPeer(userContext.getPeer0DnsName(),userContext.getPeer0grpcUrl(),userContext.getTlsFilePath());
        Peer peer1 = fabricClient.getPeer(userContext.getPeer1DnsName(),userContext.getPeer1grpcUrl(),userContext.getTlsFilePath());
        List<Peer> peers = new ArrayList<>();
        peers.add(peer0);
        peers.add(peer1);
        Orderer order = fabricClient.getOrderer("orderer.example.com","grpcs://orderer.example.com:7050",tlsOrderFilePath);
        fabricClient.invoke("mychannel", TransactionRequest.Type.GO_LANG,"dhex",order,peers,"save",initArgs);

    }
}
