package com.github.samyuan1990.FabricJavaPool.test;

import com.github.samyuan1990.FabricJavaPool.ExecuteResult;
import com.github.samyuan1990.FabricJavaPool.FabricConnectionPoolFactory;
import com.github.samyuan1990.FabricJavaPool.api.FabricConnection;
import com.github.samyuan1990.FabricJavaPool.util.TestUtil;

import org.apache.commons.pool2.impl.GenericObjectPool;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class CallableTest {

    public static int clientCount = 15;
    private static GenericObjectPool<FabricConnection> pool = FabricConnectionPoolFactory.getSDKPool(TestUtil.getUser(), "mychannel");
    private static FabricConnection[] connections = new FabricConnection[clientCount];


    public static void main(String[] args) {

    }


    private static class Client implements Callable<List<ExecuteResult>> {

        private int clientID;
        private FabricConnection connection;
        private int count;

        public Client(FabricConnection connection, int count, int clientID) {
            this.connection = connection;
            this.count = count;
            this.clientID = clientID;
        }

        @Override
        public List<ExecuteResult> call() throws Exception {

            List<ExecuteResult> list = new ArrayList<>();
            ExecuteResult er = null;
            String time = "" + System.currentTimeMillis();
            time += ("_" + clientID);
            try {
                System.out.println("Client[" + clientID + "] start at: " + LocalDateTime.now());
                for(int i = 0; i < count; i++) {
                    er =  connection.invoke("whrcbank", "signing", "{\"contract_id\":\"contract_" + time + "_" + i + "\",\"cust_id\":\"custom_id_" + time + "_" + i + "\",\"sign_branch_no\":\"\",\"sign_teller_no\":\"\",\"sign_acc_no\":\"\",\"sign_card_no\":\"\",\"sign_type\":\"\",\"sign_kind\":\"\",\"sign_kind_desc\":\"\",\"sign_desc\":\"\",\"sign_stat\":\"\",\"sign_stat_desc\":\"\",\"sign_date\":\"\",\"sign_off_date\":\"\",\"effective_date\":\"\",\"expired_date\":\"\",\"sign_ebank_acct\":\"\",\"last_update_sys\":\"\",\"last_update_user\":\"\",\"last_update_time\":\"\",\"tx_seq_no\":\"\",\"id_1\":\"\",\"id_2\":\"\",\"id_3\":\"\",\"id_4\":\"\",\"id_5\":\"\"}");
                    list.add(er);
                }
                System.out.println("Client[" + clientID + "] done at: " + LocalDateTime.now());
            } catch (Exception e) {
                //e.printStackTrace();
            }

            return list;
        }
    }

}
