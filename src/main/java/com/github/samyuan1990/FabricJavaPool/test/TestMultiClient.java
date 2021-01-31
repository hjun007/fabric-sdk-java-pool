package com.github.samyuan1990.FabricJavaPool.test;

import com.github.samyuan1990.FabricJavaPool.ExecuteResult;
import com.github.samyuan1990.FabricJavaPool.FabricConnectionPoolFactory;
import com.github.samyuan1990.FabricJavaPool.api.FabricConnection;
import com.github.samyuan1990.FabricJavaPool.config.FabricJavaPoolConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestMultiClient {

    public static int clientCount = 1;
    public static int count = 1000;
    public static FabricJavaPoolConfig config = new FabricJavaPoolConfig();
    private static GenericObjectPool<FabricConnection> pool = FabricConnectionPoolFactory.getSDKPool(config.getUser(), config.getChannelName());
    private static FabricConnection[] connections = new FabricConnection[clientCount];

    private static void initConnections() throws Exception{
        for (int i = 0; i < clientCount; i++){
            System.out.print("" + i + ": ");
            System.out.println(LocalDateTime.now());
            connections[i] = pool.borrowObject();
        }
    }

    public static void main(String[] args) throws Exception {

        //FabricJavaPoolConfig.setConfigFile("xxx");
        initConnections();

        ExecutorService executorService = Executors.newCachedThreadPool();
        for(int i=0;i<clientCount;i++) {
            executorService.execute(new Client(connections[i], count, i));
        }
    }

    private static class Client implements Runnable {

        private int clientID;
        private FabricConnection connection;
        private int count;

        public Client(FabricConnection connection, int count, int clientID) {
            this.connection = connection;
            this.count = count;
            this.clientID = clientID;
        }

        @Override
        public void run(){
            String time = "" + System.currentTimeMillis();
            time += ("_" + clientID);
            try {
                System.out.println("Client[" + clientID + "] start at: " + LocalDateTime.now());
                for(int i = 0; i < count; i++) {
                    ExecuteResult er =  connection.invoke("whrcbank", "signing", "{\"contract_id\":\"contract_" + time + "_" + i + "\",\"cust_id\":\"custom_id_" + time + "_" + i + "\",\"sign_branch_no\":\"\",\"sign_teller_no\":\"\",\"sign_acc_no\":\"\",\"sign_card_no\":\"\",\"sign_type\":\"\",\"sign_kind\":\"\",\"sign_kind_desc\":\"\",\"sign_desc\":\"\",\"sign_stat\":\"\",\"sign_stat_desc\":\"\",\"sign_date\":\"\",\"sign_off_date\":\"\",\"effective_date\":\"\",\"expired_date\":\"\",\"sign_ebank_acct\":\"\",\"last_update_sys\":\"\",\"last_update_user\":\"\",\"last_update_time\":\"\",\"tx_seq_no\":\"\",\"id_1\":\"\",\"id_2\":\"\",\"id_3\":\"\",\"id_4\":\"\",\"id_5\":\"\"}");
                    //System.out.println(er.getResult());
                }
                System.out.println("Client[" + clientID + "] done at: " + LocalDateTime.now());
                pool.returnObject(connection);
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }

}
