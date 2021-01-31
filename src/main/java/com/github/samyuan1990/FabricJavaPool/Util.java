package com.github.samyuan1990.FabricJavaPool;

import org.hyperledger.fabric.sdk.ChaincodeID;

import java.io.File;

import static java.lang.String.format;

public class Util {

    private Util() {

    }

    public static String resultOnPeersDiff = "Result on Peers not same.";

    public static String errorHappenDuringQuery = "Error happen during query.";

    public static ChaincodeID generateChainCodeID(String myChannel, String version) {
        return ChaincodeID.newBuilder().setName(myChannel).setVersion(version).build();
    }

    public static File findFileSk(File directory) {
        File[] matches = directory.listFiles((dir, name) -> name.endsWith("_sk"));
        if (null == matches) {
            throw new RuntimeException(format("Matches returned null does %s directory exist?", directory.getAbsoluteFile().getName()));
        }
        if (matches.length != 1) {
            throw new RuntimeException(format("Expected in %s only 1 sk file but found %d", directory.getAbsoluteFile().getName(), matches.length));
        }
        return matches[0];
    }
}
