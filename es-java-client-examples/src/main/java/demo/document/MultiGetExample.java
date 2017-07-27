package demo.document;

import demo.ClientExecutor;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.client.Client;

/**
 * @author chaokunyang
 */
public class MultiGetExample {

    public static void main(String[] args) {
        ClientExecutor.execute((MultiGetExample::multiGet));
    }

    private static void multiGet(Client client) {
        MultiGetResponse multiGetItemResponses = client.prepareMultiGet()
                .add("twitter", "tweet", "1")
                .add("twitter", "tweet", "2", "3", "4")
                .add("another", "type", "foo")
                .get();

        for (MultiGetItemResponse itemResponse : multiGetItemResponses) {
            GetResponse response = itemResponse.getResponse();
            if (response != null && response.isExists()) {
                String json = response.getSourceAsString();
                System.out.println(json);
            }
        }
    }


}
