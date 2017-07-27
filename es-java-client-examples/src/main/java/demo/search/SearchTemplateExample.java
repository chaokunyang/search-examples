package demo.search;

import demo.ClientExecutor;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.SearchTemplateRequestBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chaokunyang
 */
public class SearchTemplateExample {

    public static void main(String[] args) {
        ClientExecutor.execute(SearchTemplateExample::template);
    }

    /**
     * 可以使用文件模板、存储模板、inline template
     * @param client
     */
    private static void template(Client client) {
        Map<String, Object> template_params = new HashMap<>();
        template_params.put("param_gender", "male");

        SearchResponse sr = new SearchTemplateRequestBuilder(client)
                .setScript("{\n" +
                        "        \"query\" : {\n" +
                        "            \"match\" : {\n" +
                        "                \"gender\" : \"{{param_gender}}\"\n" +
                        "            }\n" +
                        "        }\n" +
                        "}")
                .setScriptType(ScriptType.INLINE)
                .setScriptParams(template_params)
                .setRequest(new SearchRequest())
                .get()
                .getResponse();

        System.out.printf("status: %s, hits: %s", sr.status(), sr.getHits().getTotalHits());
    }

}
