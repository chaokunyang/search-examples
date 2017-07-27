package demo.document;

import demo.ClientExecutor;
import org.elasticsearch.action.get.GetResponse;

/**
 * @author chaokunyang
 */
public class GetExample {
    public static void main(String[] args) {
        ClientExecutor.execute((client) -> {
            GetResponse response = client.prepareGet("twitter", "tweet", "1").get();
            System.out.println(response);

            System.out.println("operation will be executed on a separate thread when executed locally.");
            System.out.println(client.prepareGet("twitter", "tweet", "1").setOperationThreaded(false).get());
        });
    }
}
