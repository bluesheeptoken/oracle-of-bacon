package com.serli.oracle.of.bacon.repository;


import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;

import java.util.List;
import java.util.ArrayList;


import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.Relationship;
 
//import org.neo4j.graphdb.Transaction;

//import org.apache.struts2.interceptor.SessionAware;
//import org.neo4j.cypher.CypherParser;
//import  org.neo4j.cypher.javacompat.ExecutionEngine;
//import org.neo4j.cypher.javacompat.ExecutionResult;

public class Neo4JRepository {
    private final Driver driver;

    public Neo4JRepository() {
        // Change password to your password
        this.driver = GraphDatabase.driver("bolt://localhost:7687", AuthTokens.basic("neo4j", "hello"));
    }

    public List<?> getConnectionsToKevinBacon(String actorName) {
        Session session = driver.session();

        // Create request
        Statement statement = new Statement("MATCH (Bacon {name:\"Bacon, Kevin (I)\"}) MATCH (TargetActor {name:\""
                + actorName + "\"}) MATCH p = shortestPath((Bacon)-[:PLAYED_IN*]-(TargetActor)) RETURN nodes(p), relationships(p)");
        // Get result
        StatementResult result = session.run(statement);
        Record record = result.single();
        Iterable<Value> nodes = record.get(0).values();
        Iterable<Value> relationships = record.get(1).values();

        // Concatenate result into an ArrayList
        List<GraphItem> r = new ArrayList<>();
        
        for (Value v: nodes) {

            long id = v.asNode().id();
            
            String value;
            String type;
            if (v.get("name").asString() != "null") {
                value = v.get("name").asString();
                type = "Actor";
            }
            else {
                value = v.get("title").asString();
                type = "Movie";
            }

            r.add(new GraphNode(id, value, type));
        }

        for (Value v: relationships) {

            Relationship relation = v.asRelationship();
            long id = relation.id();

            long source = relation.startNodeId();

            long target = relation.endNodeId();
            
            r.add(new GraphEdge(id, source, target, "PLAYED_IN"));
        }
        
        return r;
    }

    public static abstract class GraphItem {
        public final long id;

        private GraphItem(long id) {
            this.id = id;
        }

        abstract public String toJson();

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            GraphItem graphItem = (GraphItem) o;

            return id == graphItem.id;
        }

        @Override
        public int hashCode() {
            return (int) (id ^ (id >>> 32));
        }
    }

    private static class GraphNode extends GraphItem {
        public final String type;
        public final String value;

        public GraphNode(long id, String value, String type) {
            super(id);
            this.value = value;
            this.type = type;
        }

        public String toJson(){
            String json = "{\n";
            json = "\"id\":" + this.id + ",\n" +
                "\"type\": \"" + this.type + "\",\n" +
                "\"value\": \"" + this.value + "\"\n" +
                "}\n";
            return json;
        }
    }

    private static class GraphEdge extends GraphItem {
        public final long source;
        public final long target;
        public final String value;

        public GraphEdge(long id, long source, long target, String value) {
            super(id);
            this.source = source;
            this.target = target;
            this.value = value;
        }

        public String toJson(){
            String json = "{\n";
            json = "\"id\":" + this.id + ",\n" +
                "\"source\": " + this.source + ",\n" +
                "\"target\": " + this.target + ",\n" +
                "\"value\":\"PLAYED_IN\"\n" +
                "}\n";
            return json;
        }
    }
}
