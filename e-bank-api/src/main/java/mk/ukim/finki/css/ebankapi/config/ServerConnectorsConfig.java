package mk.ukim.finki.css.ebankapi.config;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServerConnectorsConfig {

    private final static String SECURITY_USER_CONSTRAINT = "CONFIDENTIAL";
    private final static String REDIRECT_PATTERN = "/*";
    private final static String CONNECTOR_PROTOCOL = "org.apache.coyote.http11.Http11NioProtocol";
    private final static String CONNECTOR_SCHEME = "https";


    @Value("${app.ssl.port}")
    private Integer sslPort;

    @Value("${app.ssl.key-store}")
    private String keyStore;

    @Value("${app.ssl.key-store.type}")
    private String keyStoreType;

    @Value("${app.ssl.key-store.password}")
    private String keyStorePassword;

    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat =
                new TomcatServletWebServerFactory() {

                    @Override
                    protected void postProcessContext(Context context) {
                        SecurityConstraint securityConstraint = new SecurityConstraint();
                        securityConstraint.setUserConstraint(SECURITY_USER_CONSTRAINT);
                        SecurityCollection collection = new SecurityCollection();
                        collection.addPattern(REDIRECT_PATTERN);
                        securityConstraint.addCollection(collection);
                        context.addConstraint(securityConstraint);
                    }
                };
        tomcat.addAdditionalTomcatConnectors(createSslConnector());
        return tomcat;
    }

    private Connector createSslConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
        try {
            connector.setScheme("https");
            connector.setSecure(true);
            connector.setPort(sslPort);


            protocol.setSSLEnabled(true);
            protocol.setKeystoreFile(keyStore);
            protocol.setKeystoreType(keyStoreType);
//            protocol.setKeyAlias(keyAlias);
            protocol.setKeystorePass(keyStorePassword);

            return connector;
        } catch (Exception ex) {
            throw new IllegalStateException("can't access keystore: [" + "keystore"
                    + "] or truststore: [" + "keystore" + "]", ex);
        }
    }


}