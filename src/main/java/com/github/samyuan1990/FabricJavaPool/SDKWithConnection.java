package com.github.samyuan1990.FabricJavaPool;

import com.github.samyuan1990.FabricJavaPool.api.FabricConnection;
import com.github.samyuan1990.FabricJavaPool.config.FabricJavaPoolConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;

public class SDKWithConnection {

    public static GenericObjectPool<FabricConnection> pool = null;

    private FabricConnection tmpFabricConnection = null;

    public void setConfigPath(String path){
        FabricJavaPoolConfig.setConfigFile(path);
    }

    public void init()throws Exception{
        if (pool == null){
            pool = FabricConnectionPoolFactory.getGatewayPool("admin", "mychannel");
        }
        if(tmpFabricConnection == null){
            tmpFabricConnection = pool.borrowObject();
        }
    }

    public void initWithPath(String path)throws Exception{
        setConfigPath(path);
        init();
    }

    public void close(){
        if(tmpFabricConnection != null){
            pool.returnObject(tmpFabricConnection);
        }
    }
    /**
     *
     * @param signRecord {"contract_id":"contract_id1","cust_id":"custom_id1","sign_branch_no":"","sign_teller_no":"","sign_acc_no":"","sign_card_no":"","sign_type":"","sign_kind":"","sign_kind_desc":"","sign_desc":"","sign_stat":"","sign_stat_desc":"","sign_date":"","sign_off_date":"","effective_date":"","expired_date":"","sign_ebank_acct":"","last_update_sys":"","last_update_user":"","last_update_time":"","tx_seq_no":"","id_1":"","id_2":"","id_3":"","id_4":"","id_5":""}
     * @return {"status_code":"200","operation":"signing","payload":""}
     * @throws Exception
     */
    public String Sign(String signRecord) throws Exception {
        if(tmpFabricConnection == null){
            init();
        }
        return tmpFabricConnection.invoke("whrcbank", "signing", signRecord).getResult();
    }

    /**
     *
     * @param signOffData {"contract_id":"contract_id_1","cust_id":"custom_id_1","sign_stat":"","sign_stat_desc":"","sign_off_date":"","expired_date":"","last_update_sys":"","last_update_user":"","last_update_time":""}
     * @return {"status_code":"200","operation":"signOff","payload":""}
     * @throws Exception
     */
    public String SignOff(String signOffData) throws Exception {
        if(tmpFabricConnection == null){
            init();
        }
        return tmpFabricConnection.invoke("whrcbank", "signOff", signOffData).getResult();
    }

    /**
     *
     * @param signRecord {"contract_id":"contract_id1","cust_id":"custom_id1","sign_branch_no":"","sign_teller_no":"","sign_acc_no":"","sign_card_no":"","sign_type":"","sign_kind":"","sign_kind_desc":"","sign_desc":"","sign_stat":"","sign_stat_desc":"","sign_date":"","sign_off_date":"","effective_date":"","expired_date":"","sign_ebank_acct":"","last_update_sys":"","last_update_user":"","last_update_time":"","tx_seq_no":"","id_1":"","id_2":"","id_3":"","id_4":"","id_5":""}
     * @return {"status_code":"200","operation":"check ecif","payload":""}
     * @throws Exception
     */
    public String CheckEcif(String signRecord) throws Exception {
        if(tmpFabricConnection == null){
            init();
        }
        return tmpFabricConnection.invoke("whrcbank", "checkEcif", signRecord).getResult();
    }

    /**
     *
     * @param signRecords {"contract_id":"contract_id1","cust_id":"custom_id1","sign_branch_no":"","sign_teller_no":"","sign_acc_no":"","sign_card_no":"","sign_type":"","sign_kind":"","sign_kind_desc":"","sign_desc":"","sign_stat":"","sign_stat_desc":"","sign_date":"","sign_off_date":"","effective_date":"","expired_date":"","sign_ebank_acct":"","last_update_sys":"","last_update_user":"","last_update_time":"","tx_seq_no":"","id_1":"","id_2":"","id_3":"","id_4":"","id_5":""}
     * @return {"status_code":"200","operation":"check ecif","payload":""}
     * @throws Exception
     */
    public String CheckBatchEcif(String signRecords) throws Exception {
        if(tmpFabricConnection == null){
            init();
        }
        return tmpFabricConnection.invoke("whrcbank", "checkBatchEcif", signRecords).getResult();
    }

    /**
     *
     * @param customID 客户id
     * @return {"status_code":"200","operation":"signing","payload":"xxx"}
     * @throws Exception
     */
    public String GetContractIDsByCustID(String customID) throws Exception {
        if(tmpFabricConnection == null){
            init();
        }
        return tmpFabricConnection.query("whrcbank", "getContractIDsByCustID", customID).getResult();
    }

    /**
     *
     * @param customID 客户id
     * @return {"status_code":"200","operation":"query","payload":"xxx"}
     * @throws Exception
     */
    public String GetContractsByCustID(String customID) throws Exception {
        if(tmpFabricConnection == null){
            init();
        }
        return tmpFabricConnection.query("whrcbank", "getContractsByCustID", customID).getResult();
    }

    /**
     *
     * @param contractID 签约号
     * @return {"status_code":"200","operation":"query","payload":"xxx"}
     * @throws Exception
     */
    public String GetContractByContractID(String contractID) throws Exception {
        if(tmpFabricConnection == null){
            init();
        }
        return tmpFabricConnection.query("whrcbank", "getContractByContractID", contractID).getResult();
    }

    public static void main(String[] args)throws Exception {
        SDKWithConnection sdkWithConnection = new SDKWithConnection();
        sdkWithConnection.initWithPath("D:/test/sdk/resources/FabricJavaPool.properties");
        sdkWithConnection.Sign("");
    }
}
