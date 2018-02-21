package com.serli.oracle.of.bacon.api;

import com.serli.oracle.of.bacon.repository.ElasticSearchRepository;
import com.serli.oracle.of.bacon.repository.MongoDbRepository;
import com.serli.oracle.of.bacon.repository.Neo4JRepository;
import com.serli.oracle.of.bacon.repository.RedisRepository;
import net.codestory.http.annotations.Get;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class APIEndPoint {
    private final Neo4JRepository neo4JRepository;
    private final ElasticSearchRepository elasticSearchRepository;
    private final RedisRepository redisRepository;
    private final MongoDbRepository mongoDbRepository;

    public APIEndPoint() {
        neo4JRepository = new Neo4JRepository();
        elasticSearchRepository = new ElasticSearchRepository();
        redisRepository = new RedisRepository();
        mongoDbRepository = new MongoDbRepository();
    }

    @Get("bacon-to?actor=:actorName")
    public String getConnectionsToKevinBacon(String actorName) {
        List<Neo4JRepository.GraphItem> graphe = (List<Neo4JRepository.GraphItem>) neo4JRepository.getConnectionsToKevinBacon(actorName);
        String result = "[\n";
        for(Neo4JRepository.GraphItem item : graphe){
            result += "{\n" +
                "\"data\": {\n" +
                item.toJson() +
                "},\n";
        }
        
        result = result.substring(0, result.length()-2) + "]";
        redisRepository.putSearch(actorName);

        return result;
    }

    @Get("suggest?q=:searchQuery")
    public List<String> getActorSuggestion(String searchQuery) throws IOException {
        return elasticSearchRepository.getActorsSuggests(searchQuery);
    }

    @Get("last-searches")
    public List<String> last10Searches() {
        return redisRepository.getLastTenSearches();
    }

    @Get("actor?name=:actorName")
    public String getActorByName(String actorName) {
        return mongoDbRepository.getActorByName(actorName);
    }
}
