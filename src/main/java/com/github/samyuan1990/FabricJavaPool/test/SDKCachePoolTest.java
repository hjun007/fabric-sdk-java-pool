//package com.github.samyuan1990.FabricJavaPool.test;
//
//import java.io.File;
//import java.io.IOException;
//import java.lang.reflect.InvocationTargetException;
//import java.util.concurrent.*;
//import org.apache.log4j.Logger;
//import org.hyperledger.fabric.sdk.Channel;
//import org.hyperledger.fabric.sdk.HFClient;
//import org.hyperledger.fabric.sdk.NetworkConfig;
//import org.hyperledger.fabric.sdk.exception.CryptoException;
//import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
//import org.hyperledger.fabric.sdk.exception.NetworkConfigurationException;
//import org.hyperledger.fabric.sdk.security.CryptoSuite;
//
//public class SDKCachePoolTest<T> implements Runnable {
//    private static Logger infolog = Logger.getLogger(SDKCachePoolTest.class);
//    @SuppressWarnings("rawtypes")
//    private static ParaContent content; // invoke方法参数
//    private static int count;// 通过count查询参数
//    private static String[] runPara = ReadInvokePara.readFixPara();// 运行状态参数
//    private static Semaphore se = new Semaphore(Integer.valueOf(runPara[0]));// 并发量
//    public Channel buildCommonChannel(String channelName, String chainCode) throws IOException, CryptoException, InvalidArgumentException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NetworkConfigurationException {
//        CryptoSuite cryptoSuite = CryptoSuite.Factory.getCryptoSuite();
//        HFClient hfclient = HFClient.createNewInstance();
//        hfclient.setCryptoSuite(cryptoSuite);
//        NetworkConfig networkConfig = NetworkConfig.fromYamlFile(new File("./src/main/resources/connection-org1.yaml"));
//        hfclient.setUserContext(appUser);
//        hfclient.loadChannelFromConfig(channel, networkConfig);
//        Channel myChannel = hfclient.getChannel(channel);
//        myChannel.initialize();
//    }
//    public T preTestAsync(String channelName, String chainCode, String fcn, Class<? extends T> cl, String... args)throws FabricException, IOException {
//        T t = buildCommonChannel(channelName, chainCode).invokeAsync(fcn, cl, args);
//        return t;
//    }
//    @SuppressWarnings({ "rawtypes" })
//    public static void main(String[] args) {
//        int runCount = 0; // 运行总次数
//        ExecutorService executor = Executors.newCachedThreadPool();
//        while (runCount <= Integer.valueOf(runPara[1])) {
//            ++runCount;
//            executor.execute(new SDKCachePoolTest<>());
//        }
//        executor.shutdown();
//        infolog.info("压测结束");
//    }
//    @SuppressWarnings("unchecked")
//    @Override
//    public void run() {
//        try {
//            se.acquire(); // 请求
//            ++count;
//            content = ReadInvokePara.readPara(count);
//            if (null != content) {
//                preTestAsync(content.getChannelName(), content.getChainCode(), content.getFcn(),
//                        (Class<? extends T>) String.class, content.getArgs());
//                int invokewait = Integer.valueOf(runPara[4]);
//                if (0 != invokewait) {
//                    Thread.sleep(invokewait);
//                }
//            }
//        } catch (Exception e) {
//            //e.printStackTrace();
//        }
//        se.release(); // 释放
//    }
//}