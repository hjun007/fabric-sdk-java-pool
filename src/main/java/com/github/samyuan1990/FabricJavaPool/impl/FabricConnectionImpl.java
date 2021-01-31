package com.github.samyuan1990.FabricJavaPool.impl;

import java.util.Collection;

import com.github.samyuan1990.FabricJavaPool.ExecuteResult;
import com.github.samyuan1990.FabricJavaPool.RunTimeException;
import com.github.samyuan1990.FabricJavaPool.Util;
import com.github.samyuan1990.FabricJavaPool.api.FabricConnection;
import com.github.samyuan1990.FabricJavaPool.config.FabricJavaPoolConfig;
import lombok.extern.slf4j.Slf4j;
import org.hyperledger.fabric.sdk.*;

@Slf4j
public class FabricConnectionImpl implements FabricConnection {

    public FabricConnectionImpl() {
        this.hfclient = HFClient.createNewInstance();
    }

    public FabricConnectionImpl(HFClient hfclient, Channel mychannel, User user) {
        this.hfclient = hfclient;
        this.mychannel = mychannel;
        this.user = user;
    }

    private HFClient hfclient;

    public Channel getMychannel() {
        return mychannel;
    }

    public void setMychannel(Channel mychannel) {
        this.mychannel = mychannel;
    }

    private Channel mychannel;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private User user;

    @Override
    public ExecuteResult query(String chainCode, String fcn, String... arguments) throws Exception {
        ChaincodeID cci = ChaincodeID.newBuilder().setName(chainCode).build();
        return this.query(cci, fcn, arguments);
    }

    public ExecuteResult query(ChaincodeID chaincodeID, String fcn, String... arguments) throws Exception {
        TransactionProposalRequest transactionProposalRequest = hfclient.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(chaincodeID);
        transactionProposalRequest.setFcn(fcn);
        transactionProposalRequest.setArgs(arguments);
        transactionProposalRequest.setUserContext(getUser());

        //使用发现服务必须确保至少有一个节点开启了发现服务
        Channel.DiscoveryOptions discoveryOptions = Channel.DiscoveryOptions.createDiscoveryOptions();
        // 随机选取满足背书策略的peer节点组合 ENDORSEMENT_SELECTION_RANDOM
        // 选取满足背书策略的，状态最新、块高最大的peer节点组合 ENDORSEMENT_SELECTION_LEAST_REQUIRED_BLOCKHEIGHT
        discoveryOptions.setEndorsementSelector(ServiceDiscovery.EndorsementSelector.ENDORSEMENT_SELECTION_RANDOM);
        discoveryOptions.setForceDiscovery(false);//false:默认2分钟发现一次，true:每次发送都发现一次
        discoveryOptions.setInspectResults(true);//false:sdk检查背书策略，true:sdk不坚持背书策略

        Collection<ProposalResponse> queryPropResp = getMychannel().sendTransactionProposalToEndorsers(transactionProposalRequest, discoveryOptions);
        return processQueryProposalResponse(queryPropResp);
    }

    public ExecuteResult invoke(ChaincodeID chaincodeID, String fcn, String... arguments) throws Exception {
        TransactionProposalRequest transactionProposalRequest = hfclient.newTransactionProposalRequest();
        transactionProposalRequest.setChaincodeID(chaincodeID);
        transactionProposalRequest.setFcn(fcn);
        transactionProposalRequest.setArgs(arguments);
        transactionProposalRequest.setUserContext(getUser());

        //使用发现服务必须确保至少有一个节点开启了发现服务
        Channel.DiscoveryOptions discoveryOptions = Channel.DiscoveryOptions.createDiscoveryOptions();
        // 随机选取满足背书策略的peer节点组合 ENDORSEMENT_SELECTION_RANDOM
        // 选取满足背书策略的，状态最新、块高最大的peer节点组合 ENDORSEMENT_SELECTION_LEAST_REQUIRED_BLOCKHEIGHT
        discoveryOptions.setEndorsementSelector(ServiceDiscovery.EndorsementSelector.ENDORSEMENT_SELECTION_RANDOM);
        discoveryOptions.setForceDiscovery(false);//false:默认2分钟发现一次，true:每次发送都发现一次
        discoveryOptions.setInspectResults(true);//false:sdk检查背书策略，true:sdk不坚持背书策略

        Collection<ProposalResponse> invokePropResp = getMychannel().sendTransactionProposalToEndorsers(transactionProposalRequest, discoveryOptions);
        ExecuteResult eR = processProposalResponses(invokePropResp);
        getMychannel().sendTransaction(invokePropResp); //CompletableFuture<BlockEvent.TransactionEvent> events
        return eR;
    }

    @Override
    public ExecuteResult invoke(String chainCode, String fcn, String... arguments) throws Exception {
        ChaincodeID cci = ChaincodeID.newBuilder().setName(chainCode).build();
        return this.invoke(cci, fcn, arguments);
    }

    private ExecuteResult processProposalResponses(Collection<ProposalResponse> propResp) throws RunTimeException {
        String payload = "";
        int i = 0;
        for (ProposalResponse response : propResp) {
            if (response.getStatus() == ChaincodeResponse.Status.SUCCESS) {
                if (i == 0) {
                    payload = response.getProposalResponse().getResponse().getPayload().toStringUtf8();
                }
                String currentPayload = response.getProposalResponse().getResponse().getPayload().toStringUtf8();
                if (null != payload && null != currentPayload && !payload.equals(currentPayload)) {
                    throw new RunTimeException(response.getStatus(), Util.resultOnPeersDiff);
                }
            } else {
                throw new RunTimeException(response.getStatus(), Util.errorHappenDuringQuery);
            }
            i++;
        }
        return new ExecuteResult(payload, propResp);
    }

    private ExecuteResult processQueryProposalResponse(Collection<ProposalResponse> propResp) throws  RunTimeException {
        String policy = new FabricJavaPoolConfig().getQueryPolicy();
        //System.out.println(policy);
        int n = policy.charAt(0) - '0';
        if (n > propResp.size()) {
            System.out.println("query policy error");
            throw new RunTimeException(null, "query policy error");
        }
        String payload = "";
        int i = 0;
        for (ProposalResponse response : propResp) {
            //System.out.println(response.getPeer().getName());
            if (response.getStatus() == ChaincodeResponse.Status.SUCCESS) {
                if (i == 0) {
                    payload = response.getProposalResponse().getResponse().getPayload().toStringUtf8();
                }
                String currentPayload = response.getProposalResponse().getResponse().getPayload().toStringUtf8();
                if (null != payload && null != currentPayload && !payload.equals(currentPayload)) {
                    throw new RunTimeException(response.getStatus(), Util.resultOnPeersDiff);
                }
                i++;
            } else {
                throw new RunTimeException(response.getStatus(), Util.errorHappenDuringQuery);
            }
            if (i >= n) {
                break;
            }
        }
        return new ExecuteResult(payload, propResp);
    }
}
