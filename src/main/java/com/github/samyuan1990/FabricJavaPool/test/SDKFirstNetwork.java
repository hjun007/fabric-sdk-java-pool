package com.github.samyuan1990.FabricJavaPool.test;

import com.github.samyuan1990.FabricJavaPool.ExecuteResult;
import com.github.samyuan1990.FabricJavaPool.FabricConnectionPoolFactory;
import com.github.samyuan1990.FabricJavaPool.api.FabricConnection;
import com.github.samyuan1990.FabricJavaPool.util.TestUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;

@Slf4j
public class SDKFirstNetwork {
    public static void main(String[] args) throws Exception {
        GenericObjectPool<FabricConnection> pool = FabricConnectionPoolFactory.getSDKPool(TestUtil.getUser(), "mychannel");
        FabricConnection fabricSDKConnection = pool.borrowObject();
        ExecuteResult er = fabricSDKConnection.invoke("mycc", "invoke", "a", "b", "1");
        System.out.println(er.getPropResp().size());
        er = fabricSDKConnection.query("mycc", "query", "a");
        System.out.println(er.getPropResp().size());
        pool.returnObject(fabricSDKConnection);
    }
}
