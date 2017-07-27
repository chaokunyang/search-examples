package demo.search;

import demo.ClientExecutor;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;

/**
 * @author chaokunyang
 */
public class TerminateAfterExample {

    public static void main(String[] args) {
        ClientExecutor.execute(TerminateAfterExample::terminate);
    }

    private static void terminate(Client client) {
        SearchResponse sr = client.prepareSearch("get-together")
                .setTerminateAfter(1000)
                .get();

        if (sr.isTerminatedEarly()) {
            // We finished early
            System.out.println("isTerminatedEarly");
        }
    }

}
