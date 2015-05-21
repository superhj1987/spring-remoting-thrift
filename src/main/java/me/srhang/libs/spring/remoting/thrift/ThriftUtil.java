package me.srhang.libs.spring.remoting.thrift;

import org.apache.thrift.TProcessor;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TNonblockingTransport;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Constructor;

/**
 * Author: Bryant Hang
 * Date: 15/5/21
 * Time: 15:31
 */
public class ThriftUtil {
    /**
     * string to find the {@link org.apache.thrift.TProcessor} implementation
     * inside the Thrift class
     */
    public static String PROCESSOR_NAME = "$Processor";
    /**
     * String to find interface of the class inside the Thrift class
     */
    public static String IFACE_NAME = "$Iface";
    /**
     * String to find client inside the Thrift class
     */
    public static String CLIENT_NAME = "$Client";

    public static String ASYNC_CLIENT_NAME = "$AsyncClient";

    public static Class<?> getThriftServiceInnerClassOrNull(Class<?> thriftServiceClass, String match, boolean isInterface) {
        if (thriftServiceClass == null) {
            return null;
        }

        Class<?>[] declaredClasses = thriftServiceClass.getDeclaredClasses();
        for (Class<?> declaredClass : declaredClasses) {
            if (declaredClass.isInterface()) {
                if (isInterface && declaredClass.getName().contains(match)) {
                    return declaredClass;
                }
            } else {
                if (!isInterface && declaredClass.getName().contains(match)) {
                    return declaredClass;
                }
            }
        }
        return null;
    }

    public static TProcessor buildProcessor(Class<?> svcInterface, Object service) throws Exception {
        Class<TProcessor> processorClass = (Class<TProcessor>) getThriftServiceInnerClassOrNull(svcInterface.getEnclosingClass(), PROCESSOR_NAME, false);
        Assert.notNull(processorClass, "the processor class must not be null");
        Constructor<TProcessor> constructor = ClassUtils.getConstructorIfAvailable(processorClass, svcInterface);
        Assert.notNull(constructor);
        return constructor.newInstance(service);
    }

    public static Constructor<?> getClientConstructor(Class<?> svcInterface){
        String client = svcInterface.getName().indexOf("Async") > 0 ? ASYNC_CLIENT_NAME : CLIENT_NAME;
        Class<?>[] args = svcInterface.getName().indexOf("Async") > 0 ? new Class[]{TProtocolFactory.class, TAsyncClientManager.class, TNonblockingTransport.class} : new Class[]{TProtocol.class};
        Class<?> clientClass = getThriftServiceInnerClassOrNull(svcInterface.getEnclosingClass(), client, false);
        Assert.notNull(clientClass, "the client class must not be null");
        Constructor<?> constructor = ClassUtils.getConstructorIfAvailable(clientClass, args);
        Assert.notNull(constructor);
        return constructor;
    }

    public static Object buildClient(Class<?> svcInterface, TProtocol protocol) throws Exception {
        return getClientConstructor(svcInterface).newInstance(protocol);
    }
}