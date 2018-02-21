package com.serli.oracle.of.bacon.loader.elasticsearch;

import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;

public class BulkHelper {
    private BulkRequest bulk;

    public BulkHelper() {
        this.bulk = new BulkRequest();
    }

    public void empty() {
        this.bulk = new BulkRequest();
    }

    public void add(int index, String line) {
        bulk.add(new IndexRequest("actors", "actor", "actor"+index)
                .source(XContentType.JSON, "name", line));
    }

    public void execute(RestHighLevelClient client) throws IOException {
        client.bulk(bulk);
    }


}
