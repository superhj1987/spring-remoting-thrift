package me.srhang.libs.spring.remoting;

import me.srhang.libs.spring.remoting.thrift.ThriftUtil;
import me.srhang.libs.util.HttpClientUtil;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.THttpClient;
import org.junit.Test;

/**
 * Author: Bryant Hang
 * Date: 15/5/22
 * Time: 10:04
 */
public class TestThrift {
    @Test
    public void testBuildProcessor() throws Exception {
        ThriftUtil.buildProcessor(HelloWorldService.Iface.class, new HelloWorldImp());
    }

    @Test
    public void testBuildClint() throws Exception {
        ThriftUtil.buildClient(HelloWorldService.Iface.class, new TBinaryProtocol.Factory().getProtocol(
                new THttpClient("", new HttpClientUtil().getHttpClient())));
    }
}
