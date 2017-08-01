package com.timeyang.search.jest.example;

import io.searchbox.indices.mapping.PutMapping;

import java.io.IOException;

/**
 * @author chaokunyang
 */
public class MappingExample {

    public static void main(String[] args) throws IOException {
        map1();
    }

    static void map1() throws IOException {
        PutMapping putMapping = new PutMapping.Builder(
                "my_index",
                "my_type",
                "{ \"my_type\" : { \"properties\" : { \"message\" : {\"type\" : \"string\", \"store\" : \"yes\"} } } }"
        ).build();
        Clients.getInstance().execute(putMapping);
    }


}
