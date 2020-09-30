import javax.net.ssl.*;
import java.security.*;
import java.util.*;

public class Test {
    public static void main(String[] args) throws NoSuchAlgorithmException, KeyManagementException {
        final SSLContext serverContext = SSLContext.getInstance("TLS");
        serverContext.init(null, null, null);
        SSLEngine engine = serverContext.createSSLEngine();
        List<String> jdkSupportedCiphers = Arrays.asList(engine.getEnabledCipherSuites());
        List<String> jdkSupportedProtocols = Arrays.asList(engine.getEnabledProtocols());
        System.out.println(jdkSupportedCiphers);
        System.out.println(jdkSupportedProtocols);
    }
}
