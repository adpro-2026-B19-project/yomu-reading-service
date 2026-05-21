package id.ac.ui.cs.advprog.yomureadingservice.client;
import org.springframework.web.client.RestClient;
import java.lang.reflect.Method;
public class CheckBodyTest {
    public static void main(String[] args) {
        for(Method m : RestClient.RequestBodySpec.class.getMethods()) {
            if(m.getName().equals("body")) {
                System.out.println(m);
            }
        }
    }
}
