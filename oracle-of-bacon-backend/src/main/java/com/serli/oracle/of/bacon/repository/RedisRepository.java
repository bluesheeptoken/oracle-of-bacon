package com.serli.oracle.of.bacon.repository;

import redis.clients.jedis.Jedis;

import java.util.List;

public class RedisRepository {
    private final Jedis jedis;

    public RedisRepository() {
        this.jedis = new Jedis("localhost");
    }

    public List<String> getLastTenSearches() {
        return jedis.lrange("lastTenSearches", 0, -1);
    }

    public void putSearch(String value) {
        // Delete duplication
        jedis.lrem("lastTenSearches", -1, value);
        // Limit to 10 the top searches
        if (this.getLastTenSearches().size() >= 10){
            jedis.rpop("lastTenSearches");
        }
        // Add the last research
        jedis.lpush("lastTenSearches", value);
}
}
