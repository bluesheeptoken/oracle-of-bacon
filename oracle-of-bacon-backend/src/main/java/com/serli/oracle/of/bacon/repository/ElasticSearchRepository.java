package com.serli.oracle.of.bacon.repository;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ElasticSearchRepository {

    private final RestHighLevelClient client;

    public ElasticSearchRepository() {
        client = createClient();
    }

    public static RestHighLevelClient createClient() {
        return new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")
                )
        );
    }

    public List<String> getActorsSuggests(String searchQuery) throws IOException {
        // Create SearchRequest on index actors
        SearchRequest searchRequest = new SearchRequest("actors");
        // Return only actor type, not meaningful here
        searchRequest.types("actor");

        // Create a match query
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("name", searchQuery));
        searchRequest.source(searchSourceBuilder);

        // Get synchronous result
        SearchResponse searchResponse = client.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();

        // Convert result into an ArrayList<String>
        List<String> result = new ArrayList<String>();
        for (SearchHit hit : searchHits) {
            result.add((String) hit.getSourceAsMap().get("name"));
        }
        return result;
    }
}
