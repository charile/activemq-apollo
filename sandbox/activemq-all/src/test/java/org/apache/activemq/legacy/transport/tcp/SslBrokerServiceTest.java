/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.legacy.transport.tcp;

import junit.framework.Test;
import junit.textui.TestRunner;

import org.apache.activemq.apollo.broker.Broker;
import org.apache.activemq.apollo.broker.BrokerFactory;
import org.apache.activemq.legacy.transport.TransportBrokerTestSupport;
import org.apache.activemq.transport.TransportFactory;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.experimental.theories.Theories;
import org.junit.runner.RunWith;

/**
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
@RunWith(Theories.class)
@Ignore
public class SslBrokerServiceTest extends TransportBrokerTestSupport {

    @BeforeClass
    static public void createScenarios() throws Exception {
        SCENARIOS = combinations().asBeans(transportScenerios("ssl://localhost:0"));
    }    
    
//    @Override
//    protected Broker createBroker() throws Exception {
//    	Broker broker = BrokerFactory.createBroker(new URI("jaxb:classpath:non-persistent-activemq.xml"));
//    	broker.addTransportServer(TransportFactory.bind(new URI(getBindLocation())));
//        
//// TODO:    	
////        KeyManager[] km = getKeyManager();
////        TrustManager[] tm = getTrustManager();
////        connector = service.addSslConnector(getBindLocation(), km, tm, null);
////        
////        // for client side
////        SslTransportFactory sslFactory = new SslTransportFactory();
////        sslFactory.setKeyAndTrustManagers(km, tm, null);
////        TransportFactory.registerTransportFactory("ssl", sslFactory);
//        
//        return broker;
//    }
//    
//
//    private TrustManager[] getTrustManager() throws Exception {
//        TrustManager[] trustStoreManagers = null;
//        KeyStore trustedCertStore = KeyStore.getInstance(SslTransportBrokerTest.KEYSTORE_TYPE);
//        
//        trustedCertStore.load(new FileInputStream(SslTransportBrokerTest.TRUST_KEYSTORE), null);
//        TrustManagerFactory tmf  = 
//            TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
//  
//        tmf.init(trustedCertStore);
//        trustStoreManagers = tmf.getTrustManagers();
//        return trustStoreManagers; 
//    }
//
//    private KeyManager[] getKeyManager() throws Exception {
//        KeyManagerFactory kmf = 
//            KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());  
//        KeyStore ks = KeyStore.getInstance(SslTransportBrokerTest.KEYSTORE_TYPE);
//        KeyManager[] keystoreManagers = null;
//        
//        byte[] sslCert = loadClientCredential(SslTransportBrokerTest.SERVER_KEYSTORE);
//        
//       
//        if (sslCert != null && sslCert.length > 0) {
//            ByteArrayInputStream bin = new ByteArrayInputStream(sslCert);
//            ks.load(bin, SslTransportBrokerTest.PASSWORD.toCharArray());
//            kmf.init(ks, SslTransportBrokerTest.PASSWORD.toCharArray());
//            keystoreManagers = kmf.getKeyManagers();
//        }
//        return keystoreManagers;          
//    }
//
//    private static byte[] loadClientCredential(String fileName) throws IOException {
//        if (fileName == null) {
//            return null;
//        }
//        FileInputStream in = new FileInputStream(fileName);
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        byte[] buf = new byte[512];
//        int i = in.read(buf);
//        while (i  > 0) {
//            out.write(buf, 0, i);
//            i = in.read(buf);
//        }
//        in.close();
//        return out.toByteArray();
//    }
//
//    protected void setUp() throws Exception {
//        maxWait = 10000;
//        super.setUp();
//    }
//
//    public static Test suite() {
//        return suite(SslBrokerServiceTest.class);
//    }
//
//    public static void main(String[] args) {
//        TestRunner.run(suite());
//    }
}
