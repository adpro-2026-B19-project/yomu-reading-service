import org.springframework.web.client.RestClient;
import java.lang.reflect.Method;
public class Test {
    public static void main(String[] args) {
        for(Method m : RestClient.RequestHeadersUriSpec.class.getMethods()) {
            if(m.getName().equals("uri")) {
                System.out.println(m);
            }
        }
    }
}
