package demo;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @author chaokunyang
 */
public class RequestExample {

    public static void main(String[] args) {
        RestClientExecutor.execute(RequestExample::async);
    }

    private static void sync(RestClient restClient) {
        try {
            // Response response = restClient.performRequest("get", "_cluster/health");
            // System.out.println(response);

            Response response = restClient.performRequest("GET", "/",
                    Collections.singletonMap("pretty", "true"));
            System.out.println(EntityUtils.toString(response.getEntity()));

//index a document
            HttpEntity entity = new NStringEntity(
                    "{\n" +
                            "    \"user\" : \"kimchy\",\n" +
                            "    \"post_date\" : \"2009-11-15T14:12:12\",\n" +
                            "    \"message\" : \"trying out Elasticsearch\"\n" +
                            "}", ContentType.APPLICATION_JSON);

            Response indexResponse = restClient.performRequest(
                    "PUT",
                    "/twitter/tweet/1",
                    Collections.emptyMap(),
                    entity);
            System.out.println(indexResponse);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void async(RestClient restClient) {
        int numRequests = 10;

        List<HttpEntity> httpEntities = new ArrayList<>();
        for(int i = 0; i < numRequests; i++) {
            httpEntities.add(new NStringEntity(
                    "{\n" +
                            "    \"user\" " + i + " : \"kimchy " + i + ",\n" +
                            "    \"post_date\" : \"2009-11-15T14:12:12\",\n" +
                            "    \"message\" : \"trying out Elasticsearch\"\n" +
                            "}", ContentType.APPLICATION_JSON));
        }

        final CountDownLatch latch = new CountDownLatch(numRequests);

        for (int i = 0; i < numRequests; i++) {
            restClient.performRequestAsync(
                    "PUT",
                    "/twitter/tweet/" + i,
                    Collections.emptyMap(),
                    //assume that the documents are stored in an entities array
                    httpEntities.get(i),
                    new ResponseListener() {
                        @Override
                        public void onSuccess(Response response) {
                            System.out.println(response);
                            latch.countDown();
                        }

                        @Override
                        public void onFailure(Exception exception) {
                            latch.countDown();
                        }
                    }
            );
        }

        //wait for all requests to be completed
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
