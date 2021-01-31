package com.github.samyuan1990.FabricJavaPool.util;

import java.io.File;
import java.nio.file.Paths;

import com.github.samyuan1990.FabricJavaPool.Util;
import org.hyperledger.fabric.sdk.ChaincodeID;
import org.hyperledger.fabric.sdk.User;
import static java.lang.String.format;

public class TestUtil {

    public static String userName = "Admin@org1.whrcbank.com";
    private static String userSk = "./src/main/resources/crypto-config/peerOrganizations/org1.whrcbank.com/users/Admin@org1.whrcbank.com/msp/keystore";
    private static String userCert = "./src/main/resources/crypto-config/peerOrganizations/org1.whrcbank.com/users/Admin@org1.whrcbank.com/msp/signcerts/Admin@org1.whrcbank.com-cert.pem";

    public static String peerName = "peer0";
    public static String org = "org1";
    public static String mspId = "Org1MSP";

    public static String myChannel = "mychannel";
    public static String myCC = "whrcbank";
    public static String myCCVersion = "1.0";
    public static ChaincodeID chaincodeID = ChaincodeID.newBuilder().setName(myCC).setVersion(myCCVersion).build();

    public static String netWorkConfig = "./src/main/resources/connection-org1.yaml";

    private TestUtil() {
    }

    public static User getUser() {
        User appuser = null;
        File sampleStoreFile = new File(System.getProperty("user.home") + File.separator + "test.properties");
        if (sampleStoreFile.exists()) { //For testing start fresh
            sampleStoreFile.delete();
        }
        final SampleStore sampleStore = new SampleStore(sampleStoreFile);
        try {
            appuser = sampleStore.getMember(peerName, org, mspId,
                    new File(String.valueOf(Util.findFileSk(Paths.get(userSk).toFile()))),
                    new File(userCert));
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return appuser;
    }


}
