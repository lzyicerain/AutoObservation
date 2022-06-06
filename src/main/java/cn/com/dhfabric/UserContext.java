package cn.com.dhfabric;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;

import java.io.Serializable;
import java.security.Security;
import java.util.Set;

/**
 * @description 用户对象
 */
public class UserContext implements User, Serializable {

    private String name;

    private Set<String> roles;

    private String account;

    private String affiliation;

    private Enrollment enrollment;

    private String mspId;

    private String peer0DnsName;

    public String getPeer0DnsName() {
        return peer0DnsName;
    }

    public void setPeer0DnsName(String peer0DnsName) {
        this.peer0DnsName = peer0DnsName;
    }

    public String getPeer1DnsName() {
        return peer1DnsName;
    }

    public void setPeer1DnsName(String peer1DnsName) {
        this.peer1DnsName = peer1DnsName;
    }

    private String peer1DnsName;

    private String tlsFilePath;

    private String peer0grpcUrl;

    private String peer1grpcUrl;


    public String getTlsFilePath() {
        return tlsFilePath;
    }

    public void setTlsFilePath(String tlsFilePath) {
        this.tlsFilePath = tlsFilePath;
    }

    public String getPeer0grpcUrl() {
        return peer0grpcUrl;
    }

    public void setPeer0grpcUrl(String peer0grpcUrl) {
        this.peer0grpcUrl = peer0grpcUrl;
    }

    public String getPeer1grpcUrl() {
        return peer1grpcUrl;
    }

    public void setPeer1grpcUrl(String peer1grpcUrl) {
        this.peer1grpcUrl = peer1grpcUrl;
    }

    static{
        Security.addProvider(new BouncyCastleProvider());
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    @Override
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    @Override
    public Enrollment getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    @Override
    public String getMspId() {
        return mspId;
    }

    public void setMspId(String mspId) {
        this.mspId = mspId;
    }
}