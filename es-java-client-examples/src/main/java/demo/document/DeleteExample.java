package demo.document;

import demo.ClientExecutor;
import org.elasticsearch.action.delete.DeleteResponse;

/**
 * @author chaokunyang
 */
public class DeleteExample {

    public static void main(String[] args) {
        ClientExecutor.execute(client -> {
            DeleteResponse response = client.prepareDelete("twitter", "tweet", "1").get();
            System.out.println(response);
        });
    }

}
