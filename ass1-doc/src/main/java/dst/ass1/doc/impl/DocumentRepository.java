package dst.ass1.doc.impl;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import dst.ass1.doc.IDocumentRepository;
import dst.ass1.jpa.model.ILocation;
import dst.ass1.jpa.util.Constants;
import org.bson.Document;

import java.util.Map;

public class DocumentRepository implements IDocumentRepository, AutoCloseable {

    private final MongoClient mongoClient ;
    private final MongoDatabase db;

    public DocumentRepository(){
        mongoClient = new MongoClient();
        db = mongoClient.getDatabase(Constants.MONGO_DB_NAME);
    }

    @Override
    public void insert(ILocation location, Map<String, Object> locationProperties) {
        MongoCollection<Document> collection = db.getCollection(Constants.COLL_LOCATION_DATA);

        Document doc = new Document();
        locationProperties.put("name", location.getName());
        locationProperties.put("location_id", location.getLocationId());
        doc.putAll(locationProperties);

        collection.insertOne(doc);
    }

    @Override
    public void close() throws Exception {
        mongoClient.close();
    }
}
