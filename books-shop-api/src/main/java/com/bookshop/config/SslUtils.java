package com.bookshop.config;

import org.springframework.http.client.SimpleClientHttpRequestFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.cert.X509Certificate;

/**
 * 信任所有 SSL 证书的 RequestFactory。
 * 仅用于本地开发访问 ES 自签名 HTTPS。
 */
public class SslUtils {

    public static class TrustAllRequestFactory extends SimpleClientHttpRequestFactory {

        private static final TrustManager[] TRUST_ALL = new TrustManager[]{
                new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] c, String a) {}
                    public void checkServerTrusted(X509Certificate[] c, String a) {}
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                }
        };

        @Override
        protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
            if (connection instanceof HttpsURLConnection) {
                HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
                try {
                    SSLContext ctx = SSLContext.getInstance("TLS");
                    ctx.init(null, TRUST_ALL, new java.security.SecureRandom());
                    httpsConnection.setSSLSocketFactory(ctx.getSocketFactory());
                    httpsConnection.setHostnameVerifier((host, session) -> true);
                } catch (Exception e) {
                    throw new IOException(e);
                }
            }
            super.prepareConnection(connection, httpMethod);
        }
    }
}
