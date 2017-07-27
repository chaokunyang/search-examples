package demo.document;

import demo.ClientExecutor;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * @author chaokunyang
 */
public class UpdateExample {

    public static void main(String[] args) {
        ClientExecutor.execute(client -> {
            // updateRequest(client);
            // prepareUpdate(client);
            // scriptUpdate(client);
            upsert(client);
        });
    }

    private static void upsert(Client client) {
        try {
            IndexRequest indexRequest = new IndexRequest("twitter", "tweet", "2")
                    .source(jsonBuilder()
                            .startObject()
                            .field("name", "Joe Smith")
                            .field("gender", "male")
                            .endObject());

            UpdateRequest updateRequest = new UpdateRequest("twitter", "tweet", "2")
                    .doc(jsonBuilder()
                            .startObject()
                            .field("gender", "male")
                            .endObject())
                    .upsert(indexRequest); // If the document does not exist, the one in indexRequest will be added
            client.update(updateRequest).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void scriptUpdate(Client client) {
        UpdateRequest updateRequest = new UpdateRequest("twitter", "tweet", "1")
                .script(new Script("ctx._source.gender = \"male\""));
        try {
            UpdateResponse updateResponse = client.update(updateRequest).get();
            System.out.println(updateRequest);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static void prepareUpdate(Client client) {
        UpdateResponse response = client.prepareUpdate("twitter", "tweet", "1")
                .setScript(new Script(ScriptType.INLINE, "painless", "ctx._source.gender = \"male\"", Collections.emptyMap()))
                .get();
        System.out.println("---script update---");
        System.out.printf("response: %s", response);
        System.out.println();

        System.out.println("---json update---");
        try {
            // Document which will be merged to the existing one.
            UpdateResponse response1 = client.prepareUpdate("twitter", "tweet", "1")
                    .setDoc(jsonBuilder()
                            .startObject()
                            .field("gender", "female")
                            .endObject())
                    .get();
            System.out.println(response1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The update API also support passing a partial document, which will be merged into the existing document (simple recursive merge, inner merging of objects, replacing core "keys/values" and arrays). 
     * @param client
     */
    private static void updateRequest(Client client) {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.index("twitter");
        updateRequest.type("tweet");
        updateRequest.id("1");
        try {
            updateRequest.doc(jsonBuilder()
                    .startObject()
                    .field("gender", "male")
                    .endObject());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            client.update(updateRequest).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}
