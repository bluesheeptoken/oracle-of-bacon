package com.serli.oracle.of.bacon.repository;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import org.bson.Document;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Filters.eq;

import java.util.Optional;

/*
On importe la collection actors dans mongodb avec la commande suivante:
mongoimport --db bacon --collection actors --type csv --file [path to actors file] --headerline
*/

public class MongoDbRepository {
    private final MongoCollection<Document> actorCollection;

    public MongoDbRepository() {
        this.actorCollection= new MongoClient("localhost", 27017).getDatabase("bacon").getCollection("actors");
    }

    public String getActorByName(String name) {
        String json = "";
        try{
            Document doc = (Document) actorCollection.find(eq("name:ID",name)).first();
            return doc.toJson();
        }catch(Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        return json;
    }
}
