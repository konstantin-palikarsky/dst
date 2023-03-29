package dst.ass1.doc.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import dst.ass1.doc.IDocumentQuery;
import dst.ass1.jpa.util.Constants;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DocumentQuery implements IDocumentQuery {
    private final MongoCollection<Document> collection;
    private static final Logger LOGGER = LoggerFactory.getLogger(DocumentQuery.class);

    public DocumentQuery(MongoDatabase db) {
        collection = db.getCollection(Constants.COLL_LOCATION_DATA);
    }

    @Override
    public List<Document> getAverageOpeningHoursOfRestaurants() {
        LOGGER.info("MongoDB query for average opening hours of restaurants");

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
                )).into(new ArrayList<>());

        LOGGER.info("Found {} restaurants, and averaged their working time.", docs.size());

        return docs;
    }

    @Override
    public List<Document> findDocumentsByNameWithinPolygon(String name, List<List<Double>> polygon) {
        LOGGER.info("MongoDB queried by name similar to: {} and within Polygon {} ", name, polygon);

        var docs = collection.find(
                Filters.and(
                        Filters.regex("name", "^.*" + name + ".*"),
                        Filters.geoWithinPolygon("geo.coordinates", polygon))
        ).into(new ArrayList<>());

        LOGGER.info("Found {} documents with a name similar to {}, within polygon {}.", docs.size(), name, polygon);

        return docs;
    }

    @Override
    public List<Document> findDocumentsByType(String type) {
        LOGGER.info("MongoDB queried by type {}", type);

        var docs =
                collection.find(
                        new Document("type", type)
                ).into(new ArrayList<>());

        LOGGER.info("Found {} documents with type {}.", docs.size(), type);

        return docs;
    }

}
