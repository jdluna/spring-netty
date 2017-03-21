package com.example.netty.util;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class SSLContextBuilder {

	PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
	
	private PropertyWrapper properties = new PropertyWrapper();
	
	public SSLContextBuilder keyStore(String keyStore) {
		properties.keyStore = resolver.getResource(keyStore);
		return this;
	}
	
	public SSLContextBuilder trustStore(String trustStore) {
		properties.trustStore = resolver.getResource(trustStore);
		return this;
	}
	
	public SSLContextBuilder keyStorePassword(String keyStorePassword) {
		properties.keyStorePassword = keyStorePassword.toCharArray();
		return this;
	}
	
	public SSLContextBuilder trustStorePassword(String trustStorePassword) {
		properties.trustStorePassword = trustStorePassword.toCharArray();
		return this;
	}
	
	public SSLContextBuilder protocol(String protocol) {
		properties.protocol = protocol;
		return this;
	}
	
	public SSLContext build() throws GeneralSecurityException, IOException {
		KeyStore ks = KeyStore.getInstance("JKS");
		KeyStore ts = KeyStore.getInstance("JKS");

		ks.load(properties.keyStore.getInputStream(), properties.keyStorePassword);
		ts.load(properties.trustStore.getInputStream(), properties.trustStorePassword);

		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		kmf.init(ks, properties.keyStorePassword);

		TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
		tmf.init(ts);

		SSLContext sslContext = SSLContext.getInstance(properties.protocol);

		sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

		return sslContext;
	}
	
	class PropertyWrapper {
		
		private Resource keyStore;

		private Resource trustStore;

		private char[] keyStorePassword;

		private char[] trustStorePassword;

		private String protocol = "TLS";
	}
}
