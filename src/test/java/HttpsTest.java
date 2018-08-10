import org.junit.Assert;
import org.junit.Test;

import javax.net.ssl.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

public class HttpsTest {
  private SSLSocketFactory factory;

  /**
   * Make a HTTPS GET request to a site with a self-signed certificate or a certificate signed by a
   * self-signed ROOT certificate. Since a keystore with the Root is used, the request can be made.
   *
   * @throws Exception
   */
  @Test
  public void testSelfSignedCertWithKeyStore() throws Exception {
    KeyStore keyStore = KeyStore.getInstance("JKS");
    InputStream in = new FileInputStream("cacerts.jks");
    keyStore.load(in, "changeit".toCharArray());
    TrustManagerFactory tmf =
        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    tmf.init(keyStore);
    SSLContext ctx = SSLContext.getInstance("TLS");
    ctx.init(null, tmf.getTrustManagers(), null);
    SSLSocketFactory sslFactory = ctx.getSocketFactory();
    httpsGet("https://gce.ileci.com", sslFactory);
  }

  /**
   * Make a HTTPS GET request to a site with a self-signed certificate or a certificate signed by a
   * self-signed Root certificate. An <code>
   * SSLHandshakeException</code> should be thrown.
   *
   * @throws Exception
   */
  @Test
  public void testSelfSignedCert() throws Exception {
    try {
      httpsGet("https://gce.ileci.com");
      Assert.fail("SSLHandshakeException should be thrown for self signed certificate");
    } catch (SSLHandshakeException ex) {
      if (!(ex instanceof SSLHandshakeException)) {
        throw ex;
      }
    }
  }

  /**
   * Makes a HTTP GET request to a site with a valid HTTPS certificate. The request can be make.
   *
   * @throws Exception
   */
  @Test
  public void testValidCert() throws Exception {
    httpsGet("https://www.baidu.com");
  }

  /**
   * Makes a HTTP GET request to a site with an expired certificate. An <code>SSLHandshakeException
   * </code> should be thrown.
   *
   * @throws Exception
   */
  @Test
  public void testExpiredCert() throws Exception {
    try {
      httpsGet("https://expired.badssl.com");
      Assert.fail("SSLHandshakeException should be thrown for expired certificate");
    } catch (Exception ex) {
      if (!(ex instanceof SSLHandshakeException)) {
        throw ex;
      }
    }
  }

  /**
   * Makes a HTTP GET request to a site with an expired certificate. Since the custom
   * SSLSocketFactory is used, the request can also be made.
   *
   * @throws Exception
   */
  @Test
  public void testExpiredCertWithSslFactory() throws Exception {
    setSslFactory();
    httpsGet("https://expired.badssl.com");
    restoreSslFactory();
  }

  /**
   * Sets a custom SSLSocketFactory which skips the certificate verification.
   *
   * @throws NoSuchAlgorithmException
   * @throws KeyManagementException
   */
  private void setSslFactory() throws NoSuchAlgorithmException, KeyManagementException {
    factory = HttpsURLConnection.getDefaultSSLSocketFactory();
    TrustManager[] trustAllCerts =
        new TrustManager[] {
          new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
              return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType) {}

            @Override
            public void checkServerTrusted(X509Certificate[] certs, String authType) {}
          }
        };

    SSLContext sc = SSLContext.getInstance("SSL");
    sc.init(null, trustAllCerts, new java.security.SecureRandom());
    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
  }

  /** Restore the default SSLSocketFactory. */
  private void restoreSslFactory() {
    HttpsURLConnection.setDefaultSSLSocketFactory(factory);
  }

  /**
   * Makes a HTTPS get GET request with the default SSLSocketFactory.
   *
   * @param urlStr
   * @throws MalformedURLException
   * @throws IOException
   */
  private void httpsGet(String urlStr) throws MalformedURLException, IOException {
    httpsGet(urlStr, null);
  }

  /**
   * Makes a HTTPS GET request and prints its response.
   *
   * @param urlStr URL
   * @param sslSocketFactory If null, use the default.
   * @throws MalformedURLException
   * @throws IOException
   */
  private void httpsGet(String urlStr, SSLSocketFactory sslSocketFactory)
      throws MalformedURLException, IOException {
    URL url = new URL(urlStr);
    HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
    if (sslSocketFactory != null) {
      conn.setSSLSocketFactory(sslSocketFactory);
    }

    BufferedReader br = bufferedReader(conn);
    String inputLine;
    while ((inputLine = br.readLine()) != null) {
      System.out.println(inputLine);
    }
    br.close();
  }

  private static BufferedReader bufferedReader(HttpsURLConnection conn) throws IOException {
    InputStream in = conn.getInputStream();
    InputStreamReader r = new InputStreamReader(in);
    BufferedReader br = new BufferedReader(r);
    return br;
  }
}
