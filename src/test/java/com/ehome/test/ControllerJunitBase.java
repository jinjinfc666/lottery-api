package com.ehome.test;


import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;

import com.meterware.servletunit.ServletTestCase;

public class ControllerJunitBase extends ServletTestCase {

	private final String WEB_DESCRIPTOR = "src/main/webapp/WEB-INF/web.xml";
	
	private final String RESOURCE_BASE = "src/main/webapp";
	
	private final String APP_CONTEXT = "/";
	
	private final int HTTP_PORT = 8080;
	
	private final int HTTPS_PORT = 8443;
	
	private final String KEY_STORE_PATH = "src/main/resources/cert/.keystore";
	
	private final String KEY_STORE_PASSWORD = "tomcat";
	
	private static Logger logger = LogManager.getLogger(ControllerJunitBase.class.getName());
	
	public ControllerJunitBase(String name) {
		super(name);
	}

	Server server;

	@Override
	protected void setUp() throws Exception {
		// 通过代码设置并启动一个服务器，该服务器是servlet的测试容器
		super.setUp();
		server = new Server();
		
		ServerConnector http = createHttpConnector();
		
//		ServerConnector https = createHttpsConnector(KEY_STORE_PATH, KEY_STORE_PASSWORD);
		
//		server.setConnectors(new Connector[] { http, https });
		server.setConnectors(new Connector[] { http });
		WebAppContext context = new WebAppContext();
		context.setDescriptor(WEB_DESCRIPTOR);
        context.setResourceBase(RESOURCE_BASE);
		context.setContextPath(APP_CONTEXT);
		
		server.setHandler(context);
		server.start();
		//server.join();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		server.stop();
	}
	
	private ServerConnector createHttpConnector(){
		// HTTP Configuration
        // HttpConfiguration is a collection of configuration information
        // appropriate for http and https. The default scheme for http is
        // <code>http</code> of course, as the default for secured http is
        // <code>https</code> but we show setting the scheme to show it can be
        // done. The port for secured communication is also set here.
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        http_config.setSecurePort(HTTPS_PORT);
        http_config.setOutputBufferSize(32768);

        // HTTP connector
        // The first server connector we create is the one for http, passing in
        // the http configuration we configured above so it can get things like
        // the output buffer size, etc. We also set the port (8080) and
        // configure an idle timeout.
        ServerConnector http = new ServerConnector(server,
                new HttpConnectionFactory(http_config));
        http.setPort(HTTP_PORT);
        http.setIdleTimeout(30000);
        
        return http;
	}
	
	private ServerConnector createHttpsConnector(String keyStore, String keystorePassword){
		SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(keyStore);
        sslContextFactory.setKeyStorePassword(keystorePassword);
        sslContextFactory.setKeyManagerPassword(keystorePassword);

        // HTTPS Configuration
        // A new HttpConfiguration object is needed for the next connector and
        // you can pass the old one as an argument to effectively clone the
        // contents. On this HttpConfiguration object we add a
        // SecureRequestCustomizer which is how a new connector is able to
        // resolve the https connection before handing control over to the Jetty
        // Server.
        HttpConfiguration https_config = new HttpConfiguration();
        SecureRequestCustomizer src = new SecureRequestCustomizer();
        src.setStsMaxAge(2000);
        src.setStsIncludeSubDomains(true);
        https_config.addCustomizer(src);

        // HTTPS connector
        // We create a second ServerConnector, passing in the http configuration
        // we just made along with the previously created ssl context factory.
        // Next we set the port and a longer idle timeout.
        ServerConnector https = new ServerConnector(server,
            new SslConnectionFactory(sslContextFactory,HttpVersion.HTTP_1_1.asString()),
                new HttpConnectionFactory(https_config));
        https.setPort(HTTPS_PORT);
        https.setIdleTimeout(500000);
		
		return https;
	}
}