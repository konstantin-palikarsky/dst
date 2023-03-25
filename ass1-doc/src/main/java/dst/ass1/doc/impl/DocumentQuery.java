package dst.ass1.doc.impl;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import dst.ass1.doc.IDocumentQuery;
import dst.ass1.jpa.util.Constants;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class DocumentQuery implements IDocumentQuery {
    private final MongoCollection<Document> collection;


    public DocumentQuery(MongoDatabase db) {
        collection = db.getCollection(Constants.COLL_LOCATION_DATA);
    }

    @Override
    public List<Document> getAverageOpeningHoursOfRestaurants() {
        return null;
    }

    @Override
    public List<Document> findDocumentsByNameWithinPolygon(String name, List<List<Double>> polygon) {
        var docs = collection.find(
                Filters.and(
                        Filters.regex("name", "^.*" + name + ".*"),
                        Filters.geoWithinPolygon("geo.coordinates", polygon)
                ));

        return documentsToList(docs);
    }

    @Override
    public List<Document> findDocumentsByType(String type) {

        var docs =
                collection.find(
                        new Document("type", new Document("$eq", type))
                );

        return documentsToList(docs);
    }

    private List<Document> documentsToList(FindIterable<Document> docs) {
        var resultList = new ArrayList<Document>();

        try (MongoCursor<Document> cursor = docs.iterator()) {
            while (cursor.hasNext()) {
                resultList.add(cursor.next());
            }
        }

        return resultList;
    }

}
