package demo.document;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.ClientExecutor;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * @author chaokunyang
 */
public class IndexExample {

    public static void main(String[] args) throws UnknownHostException {
        ClientExecutor.execute(IndexExample::index);
    }

    /**
     * <p>The index operation automatically creates an index if it has not been created before (check out the create index API for manually creating an index), and also automatically creates a dynamic type mapping for the specific type if one has not yet been created (check out the put mapping API for manually creating a type mapping).</p>
     * <br/>
     * 手动索引
     * <pre>
     curl -XPUT '10.10.100.59:9200/twitter/tweet/1?pretty' -H 'Content-Type: application/json' -d'
     {
         "user" : "kimchy",
         "post_date" : "2009-11-15T14:12:12",
         "message" : "trying out Elasticsearch"
     }'
     </pre>
     <pre>
     curl -XPOST '10.10.100.59:9200/twitter/tweet?pretty' -H 'Content-Type: application/json' -d'
     {
     "user" : "kimchy",
     "post_date" : "2009-11-15T14:12:12",
     "message" : "trying out Elasticsearch"
     }'
     </pre>
     * 删除索引
     * <pre>curl -XDELETE 'localhost:9200/twitter?pretty'</pre>
     * @param client
     */
    public static void index(Client client) {
        helpers(client);
    }

    // public static void str(Client client) {
    //     String json = "{" +
    //             "\"user\":\"kimchy\"," +
    //             "\"postDate\":\"2013-01-30\"," +
    //             "\"message\":\"trying out Elasticsearch\"" +
    //             "}";
    //
    //     IndexResponse response = client.prepareIndex("twitter", "tweet")
    //             .setSource(json)
    //             .get();
    // }

    public static void map() {
        Map<String, Object> json = new HashMap<String, Object>();
        json.put("user","kimchy");
        json.put("postDate",new Date());
        json.put("message","trying out Elasticsearch");
    }

    public static void serialize() {
        // instance a json mapper
        ObjectMapper mapper = new ObjectMapper(); // create once, reuse

       //  generate json
       // byte[] json = mapper.writeValueAsBytes(yourbeaninstance);
    }

    public static void helpers(Client client) {
        try {
            IndexResponse response = client.prepareIndex("twitter", "tweet", "1")
                    .setSource(jsonBuilder()
                            .startObject()
                            .field("user", "kimchy")
                            .field("postDate", new Date())
                            .field("message", "trying out Elasticsearch")
                            .endObject()
                    )
                    .get();
            System.out.println(response);


            IndexResponse response2 = client.prepareIndex("twitter", "tweet", "1")
                    .setSource(jsonBuilder()
                            .startObject()
                            .field("user", "kimchy")
                            .field("postDate", new Date())
                            .field("message", "trying out Elasticsearch")
                            .endObject()
                    )
                    .get();
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // try {
        //     XContentBuilder builder = jsonBuilder()
        //             .startObject()
        //             .field("user", "kimchy")
        //             .field("postDate", new Date())
        //             .field("message", "trying out Elasticsearch")
        //             .endObject();
        //
        //     // startArray
        //
        //     // to see the generated JSON content, you can use the string() method.
        //     String json = builder.string();
        //
        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
    }
}
