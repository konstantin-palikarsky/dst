package dst.ass1.doc.impl;

 import com.mongodb.MongoClient;
 import com.mongodb.client.MongoDatabase;
 import dst.ass1.doc.IDocumentRepository;
 import dst.ass1.jpa.model.ILocation;
 import dst.ass1.jpa.util.Constants;

 import java.util.Map;

public class DocumentRepository implements IDocumentRepository {

    private final MongoDatabase db;

    public DocumentRepository(){
       try( MongoClient mongoClient = new MongoClient()){

           db = mongoClient.getDatabase(Constants.MONGO_DB_NAME);

       }
    }

    @Override
    public void insert(ILocation location, Map<String, Object> locationProperties) {

    }
}
