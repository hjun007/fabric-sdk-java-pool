package com.github.samyuan1990.FabricJavaPool.test;

import com.github.samyuan1990.FabricJavaPool.ExecuteResult;
import com.github.samyuan1990.FabricJavaPool.FabricConnectionPoolFactory;
import com.github.samyuan1990.FabricJavaPool.api.FabricConnection;
import com.github.samyuan1990.FabricJavaPool.config.FabricJavaPoolConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SDKWhrcBank {
    public static FabricJavaPoolConfig config = new FabricJavaPoolConfig();
    private static GenericObjectPool<FabricConnection> pool = FabricConnectionPoolFactory.getSDKPool(config.getUser(), config.getChannelName());
    private final static int poolSize = 1;
    private final static int txCount = 100;
    private static FabricConnection[] connections = new FabricConnection[poolSize];

    private static void initConnections()throws Exception{
        //System.out.println(pool.getMaxIdle());
        for (int i = 0; i < poolSize; i++){
            System.out.print("" + i + ": ");
            System.out.println(LocalDateTime.now());
            connections[i] = pool.borrowObject();
        }
    }

    private static FabricConnection get(int i){
        return connections[i % poolSize];
    }

    public static void main(String[] args) throws Exception {
        //FabricJavaPoolConfig.setConfigFile("path/to/FabricJavaPool.properties");
        initConnections();
        testMultiThread();
    }

    public static List<String> testMultiThread() throws Exception {
        List<CompletableFuture<String>> list = new ArrayList<>();
        long a = System.currentTimeMillis();
        String time = "" + System.currentTimeMillis();
        for (int i = 0; i < txCount; i++){
            final int fi = i;
            String finalTime = time;
            CompletableFuture<String> completableFuture =  CompletableFuture.supplyAsync(() ->{
                ExecuteResult executeResult = null;
                try {
                    FabricConnection fabricConnection = get(fi);
                    executeResult = fabricConnection.invoke("whrcbank", "signing", "{\"contract_id\":\"contract_id_" + finalTime + "_" + fi + "\",\"cust_id\":\"custom_id_" + finalTime + "_" + fi + "\",\"sign_branch_no\":\"\",\"sign_teller_no\":\"\",\"sign_acc_no\":\"\",\"sign_card_no\":\"\",\"sign_type\":\"\",\"sign_kind\":\"\",\"sign_kind_desc\":\"\",\"sign_desc\":\"\",\"sign_stat\":\"\",\"sign_stat_desc\":\"\",\"sign_date\":\"\",\"sign_off_date\":\"\",\"effective_date\":\"\",\"expired_date\":\"\",\"sign_ebank_acct\":\"\",\"last_update_sys\":\"\",\"last_update_user\":\"\",\"last_update_time\":\"\",\"tx_seq_no\":\"\",\"id_1\":\"\",\"id_2\":\"\",\"id_3\":\"\",\"id_4\":\"\",\"id_5\":\"\"}");
                    //executeResult = fabricConnection.query("whrcbank", "getContractsByCustID", "custom_id_4x");
                    //System.out.println(executeResult.getResult());
                } catch (Exception e) {
                    //e.printStackTrace();
                }
                return "success";
            });
            list.add(completableFuture);
        }
        CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(list.toArray(new CompletableFuture[0]));
        try {
            combinedFuture.get();
            long b = System.currentTimeMillis();
            System.out.println("time: " + ((b - a) / 1000.0));
            System.out.println("TPS:  " + ((float)txCount / ((b - a) / 1000.0)));

        }catch (Exception e){
            //e.printStackTrace();
        }

        List<String> ret = new ArrayList<>();
        for (CompletableFuture<String> completableFuture : list){
            ret.add(completableFuture.get());
        }
        return ret;

    }

}
