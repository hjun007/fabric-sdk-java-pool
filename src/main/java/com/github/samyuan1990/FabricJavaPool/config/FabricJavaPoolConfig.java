package com.github.samyuan1990.FabricJavaPool.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

import com.github.samyuan1990.FabricJavaPool.Util;
import com.github.samyuan1990.FabricJavaPool.util.SampleStore;
import lombok.Data;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.hyperledger.fabric.sdk.User;

@Data
public class FabricJavaPoolConfig extends GenericObjectPoolConfig {

    private boolean useCache;
    private String cacheURL;

    public boolean isUseCache() {
        return useCache;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    public String getCacheURL() {
        return cacheURL;
    }

    public void setCacheURL(String cacheURL) {
        this.cacheURL = cacheURL;
    }

    public int getCacheTimeout() {
        return cacheTimeout;
    }

    public void setCacheTimeout(int cacheTimeout) {
        this.cacheTimeout = cacheTimeout;
    }

    private int cacheTimeout;
    private static String configFile = "./src/main/resources/FabricJavaPool.properties";
    public static void setConfigFile(String configFile) { FabricJavaPoolConfig.configFile = configFile; }

    private String userName;
    private String userSkFile;
    private String userSignCertFile;

    private String peerName;
    private String organization;
    private String mspId;

    private String channelName;
    private String chaincodeName;
    private String chaincodeVersion;

    private String networkConfigFile;
    private String walletPath;

    private String queryPolicy;

    public FabricJavaPoolConfig() {
        File file = new File(configFile);
        if(file.exists()) {
            loadConfig(configFile);
        } else {
            System.out.println("config file not exist");
        }
    }

    public void loadConfig(String file) {
        try {
            InputStream in = new FileInputStream(file);
            Properties properties = new Properties();
            properties.load(in);
            this.setMaxTotal(Integer.valueOf(properties.getProperty("maxTotal")).intValue());
            this.setMaxIdle(Integer.valueOf(properties.getProperty("maxIdle")).intValue());
            this.setMinIdle(Integer.valueOf(properties.getProperty("minIdle")).intValue());
            this.setMaxWaitMillis(Integer.valueOf(properties.getProperty("maxWaitMillis")).intValue());
            this.setUseCache(Boolean.valueOf(properties.getProperty("UseCache")));
            if (this.isUseCache()) {
                this.setCacheURL(properties.getProperty("cacheURL"));
                this.setCacheTimeout(Integer.valueOf(properties.getProperty("cacheTimeout")).intValue());
            }

            this.setUserName(properties.getProperty("userName"));
            this.setUserSkFile(properties.getProperty("privateKeyPath"));
            this.setUserSignCertFile(properties.getProperty("signCertFile"));
            this.setPeerName(properties.getProperty("peerName"));
            this.setOrganization(properties.getProperty("peerOrg"));
            this.setMspId(properties.getProperty("mspId"));
            this.setChannelName(properties.getProperty("channelName"));
            this.setChaincodeName(properties.getProperty("chaincodeName"));
            this.setChaincodeVersion(properties.getProperty("chaincodeVersion"));
            this.setNetworkConfigFile(properties.getProperty("networkConfigFile"));
            this.setWalletPath(properties.getProperty("walletPath"));
            this.setQueryPolicy(properties.getProperty("queryPolicy"));

        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    public User getUser() {
        User appuser = null;
        File sampleStoreFile = new File(System.getProperty("user.home") + File.separator + "test.properties");
        if (sampleStoreFile.exists()) { //For testing start fresh
            sampleStoreFile.delete();
        }
        final SampleStore sampleStore = new SampleStore(sampleStoreFile);
        try {
            appuser = sampleStore.getMember(peerName, organization, mspId,
                    new File(String.valueOf(Util.findFileSk(Paths.get(userSkFile).toFile()))),
                    new File(userSignCertFile));
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return appuser;
    }

    @Override
    public String toString() {
        return "FabricJavaPoolConfig{" +
                "useCache=" + useCache +
                ", cacheURL='" + cacheURL + '\'' +
                ", cacheTimeout=" + cacheTimeout +
                ", userName='" + userName + '\'' +
                ", userSkFile='" + userSkFile + '\'' +
                ", userSignCertFile='" + userSignCertFile + '\'' +
                ", peerName='" + peerName + '\'' +
                ", organization='" + organization + '\'' +
                ", mspId='" + mspId + '\'' +
                ", channelName='" + channelName + '\'' +
                ", chaincodeName='" + chaincodeName + '\'' +
                ", chaincodeVersion='" + chaincodeVersion + '\'' +
                ", networkConfigFile='" + networkConfigFile + '\'' +
                ", walletPath='" + walletPath + '\'' +
                ", queryPolicy='" + queryPolicy + '\'' +
                '}';
    }
}
