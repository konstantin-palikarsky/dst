package dst.ass1.doc.impl;

import com.mongodb.client.MongoDatabase;
import dst.ass1.doc.IDocumentQuery;
import org.bson.Document;

import java.util.List;

public class DocumentQuery implements IDocumentQuery {
    private final MongoDatabase db;

    public DocumentQuery(MongoDatabase db) {
        this.db = db;
    }

    @Override
    public List<Document> getAverageOpeningHoursOfRestaurants() {
        return null;
    }

    @Override
    public List<Document> findDocumentsByNameWithinPolygon(String name, List<List<Double>> polygon) {
        return null;
    }

    @Override
    public List<Document> findDocumentsByType(String type) {
        return null;
    }
}
