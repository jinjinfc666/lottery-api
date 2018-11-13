package com.ehome.test;


import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
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
import org.junit.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
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
	
	protected String queryToken(String userName, String pwd, String clientId) {
		String token = null;
		String sessionId = querySessionId();
		String tokenURL = "http://localhost:8080/oauth/token";
		String captcha = null;
		ObjectMapper mapper = new ObjectMapper();
		
		if(StringUtils.isBlank(sessionId)) {
			return null;
		}
		
		tokenURL += ";jsessionid=" + sessionId;
		captcha = queryCaptcha(sessionId);
		try {
			WebRequest request = new PostMethodWebRequest(tokenURL);
			WebConversation wc = new WebConversation();
			
			request.setParameter("grant_type", "password");
			request.setParameter("client_id", clientId);
			request.setParameter("client_secret", "secret_1");
			request.setParameter("username", userName);
			request.setParameter("password", pwd);
			request.setParameter("captcha", captcha);
			//request.setParameter("jsessionid", sessionId);
			WebResponse response = wc.sendRequest(request);
			
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);

			Assert.assertNotNull(retItems.get("access_token"));
			
			token = (String)retItems.get("access_token");
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		return token;
	}
	
	private String querySessionId() {
		String sessionId = null;
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			WebRequest request = new GetMethodWebRequest("http://localhost:8080/captchas/query-sesionid");
			WebConversation wc = new WebConversation();
			WebResponse response = wc.sendRequest(request);
			
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();
			
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);

			Assert.assertNotNull(retItems.get("data"));
			
			sessionId = (String)((Map)retItems.get("data")).get("sessionId");
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		return sessionId;
	}
	
	private void queryCaptchaImage(String sessionId) {		
		//ObjectMapper mapper = new ObjectMapper();
		try {
			WebRequest request = new GetMethodWebRequest("http://localhost:8080/captchas/verification-code-Img;jsessionid=" + sessionId);
			WebConversation wc = new WebConversation();
			WebResponse response = wc.sendRequest(request);
			
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			/*String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);

			Assert.assertNotNull(retItems.get("data"));
			
			sessionId = (String)retItems.get("data");*/
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	private String queryCaptcha(String sessionId) {
		String captcha = null;
		//String sessionId = null;
		
		
		//sessionId = querySessionId();
		if(StringUtils.isBlank(sessionId)) {
			fail("Can not obtain the session id from the server!!!");
			return null;
		}
		
		queryCaptchaImage(sessionId);
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			WebRequest request = new GetMethodWebRequest("http://localhost:8080/captchas;jsessionid=" + sessionId);
			WebConversation wc = new WebConversation();
			WebResponse response = wc.sendRequest(request);
			
			int  status = response.getResponseCode();
			
			Assert.assertEquals(HttpServletResponse.SC_OK, status);
			String result = response.getText();
			
			Map<String, Object> retItems = null;
			
			retItems = mapper.readValue(result, HashMap.class);
			
			Assert.assertNotNull(retItems);

			Assert.assertNotNull(retItems.get("data"));
			
			captcha = (String)((Map)retItems.get("data")).get("captcha");
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		return captcha;
	}
}