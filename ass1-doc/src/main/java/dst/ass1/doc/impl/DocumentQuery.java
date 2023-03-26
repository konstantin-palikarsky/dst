package dst.ass1.doc.impl;

import com.mongodb.client.*;
import com.mongodb.client.model.*;
import dst.ass1.doc.IDocumentQuery;
import dst.ass1.jpa.util.Constants;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DocumentQuery implements IDocumentQuery {
    private final MongoCollection<Document> collection;


    public DocumentQuery(MongoDatabase db) {
        collection = db.getCollection(Constants.COLL_LOCATION_DATA);
    }

    @Override
    public List<Document> getAverageOpeningHoursOfRestaurants() {

        var docs = collection.aggregate(
                Arrays.asList(
                        Aggregates.match(
                                Filters.and(Filters.exists("openHour"),
                                        Filters.exists("closingHour"),
                                        Filters.eq("category", "Restaurant"))
                        ),
                        Aggregates.project(Projections.fields(
                                        Projections.include("name"),
                                        new Document(
                                                "workingHours",
                                                new Document("$subtract",
                                                        Arrays.asList("$closingHour", "$openHour")))
                                )
                        ),
                        Aggregates.group(
                                "$name",
                                Accumulators.avg("averageOpeningHours", "$workingHours")
                        )
                ));

        return documentsToList(docs);
    }

    @Override
    public List<Document> findDocumentsByNameWithinPolygon(String name, List<List<Double>> polygon) {
        var docs = collection.find(
                Filters.and(
                        Filters.regex("name", "^.*" + name + ".*"),
                        Filters.geoWithinPolygon("geo.coordinates", polygon))
        );

        return documentsToList(docs);
    }

    @Override
    public List<Document> findDocumentsByType(String type) {
        var docs = collection.find(Filters.eq("type", type));

        return documentsToList(docs);
    }

    private List<Document> documentsToList(MongoIterable<Document> docs) {
        var resultList = new ArrayList<Document>();

        try (MongoCursor<Document> cursor = docs.iterator()) {
            while (cursor.hasNext()) {
                resultList.add(cursor.next());
            }
        }

        return resultList;
    }

}
