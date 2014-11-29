import java.io.File;
import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoClient;


public class database {
	
	private DB db;
	private Map<String, String> Languages;
	
	public void loadLangs() {
		File fin = new File("utils/langs.txt");
		Scanner reader = null;
		try {
			reader = new Scanner(fin);
			while (reader.hasNextLine()) {
				String[] arr = reader.nextLine().split("\\s+");
				Languages.put(arr[0], arr[1]);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found! Will exit now");
			System.exit(0);
		}		
	}
	
	
	public void createDB(String server, int port, String dbName) {
	
		MongoClient mongoClient = null;
		try {
			mongoClient = new MongoClient(server, port);
		} catch (UnknownHostException e) {
			System.out.println("Error. Unknown Host!");
			System.exit(0);
		}
		db = mongoClient.getDB(dbName);
	}
	
	public void createCollection(String collName) {
		DBObject options = new BasicDBObject();
		options.put("autoIndexId", true);
		db.createCollection(collName, options);
		
	}
	
	public void createUniqueIndex(DBCollection coll, String fieldName) {
		DBObject index = new BasicDBObject();
		index.put(fieldName,1);

		DBObject nameIndexOptions = new BasicDBObject();
		nameIndexOptions.put("unique", true);
		nameIndexOptions.put("sparse", true);
		
		coll.createIndex(index, nameIndexOptions);
	}
	
	
	public void addUser(String fullName, String screenName) {
		DBCollection users = db.getCollection("User");
		BasicDBObject user = new BasicDBObject("FullName", fullName)
							.append("ScreenName", screenName)
							.append("MentionCount", 0)
							.append("MentionedBy", new BasicDBList());
		
		try {
			users.insert(user);
		} catch(DuplicateKeyException exc) {
			//If user already exists, do nothing..
		}
	}
	
	public void addMentionedBy(String mentionee, String mentioner) {
		DBCollection coll = db.getCollection("User");
		DBObject searchObject = new BasicDBObject();
		searchObject.put("ScreenName", mentionee);
		DBObject modifiedObject = new BasicDBObject();
		modifiedObject.put("$push", new BasicDBObject().append("MentionedBy", mentioner));
		coll.update(searchObject, modifiedObject);
	}
	
	public void addHashTag(String tagName) {
		DBCollection coll = db.getCollection("HashTag");
		BasicDBList langs = new BasicDBList();
		for(Map.Entry<String, String> lang : Languages.entrySet())
			langs.add(new BasicDBObject().append(lang.getValue(), 0));
		BasicDBObject HashTag = new BasicDBObject("HashTagName", tagName)
		.append("Langs", langs)
		.append("CoOccurHashTags", new BasicDBList())
		.append("HashTagUsers", new BasicDBList())
		.append("Locations", new BasicDBList());
		try {
			coll.insert(HashTag);
		} catch(DuplicateKeyException exc) {
			//If hash tag already exists, do nothing..
		}		
	}
	
	
	public void IncrementLang(String lang, String tagName) {
		DBCollection coll = db.getCollection("HashTag");
		BasicDBObject updateCommand = new BasicDBObject("$inc", new BasicDBObject("Langs.$."+lang, 1));
		BasicDBObject query = new BasicDBObject();
	    query.put("HashTagName", tagName);
		coll.update(query, updateCommand);
	}
	
	public void addCoOccuringHashTag(String tagName, Set<String> coOccurTags) {
		DBCollection coll = db.getCollection("HashTag");
		BasicDBList allTags = new BasicDBList();
		for(String tag : coOccurTags)
			allTags.add(tag);
		DBObject searchObject = new BasicDBObject();
		searchObject.put("HashTagName", tagName);
		DBObject modifiedObject = new BasicDBObject();
		BasicDBObject allTagsObject = new BasicDBObject().append("$each", allTags);
		modifiedObject.put("$push", new BasicDBObject().append("CoOccuringTags", allTagsObject));
		coll.update(searchObject, modifiedObject);
	}
	
	public void adduserUsingHashTag(String tagName, String userName) {
		DBCollection coll = db.getCollection("HashTag");
		DBObject searchObject = new BasicDBObject();
		searchObject.put("HashTagName", tagName);
		DBObject modifiedObject = new BasicDBObject();
		modifiedObject.put("$push", new BasicDBObject().append("HashTagUsers", userName));
		coll.update(searchObject, modifiedObject);
	}
	
	public void addHashTagLocation(String tagName, String latitude, String longitude) {
		DBCollection coll = db.getCollection("HashTag");
		DBObject searchObject = new BasicDBObject();
		searchObject.put("HashTagName", tagName);
		DBObject modifiedObject = new BasicDBObject();
		modifiedObject.put("$push", new BasicDBObject().append("Locations",
								new BasicDBObject("Latitude", latitude).append("Longitude", longitude)));
		coll.update(searchObject, modifiedObject);
	}
	
	public void addLink(String linkString) {
		DBCollection users = db.getCollection("Link");
		BasicDBObject link = new BasicDBObject("LinkString", linkString)
							.append("UsedBy", new BasicDBList());
		try {
			users.insert(link);
		} catch(DuplicateKeyException exc) {
			//If user already exists, do nothing..
		}
	}
	
	public void addLinkUsedBy(String user, String linkString) {
		DBCollection coll = db.getCollection("User");
		DBObject searchObject = new BasicDBObject();
		searchObject.put("LinkString", linkString);
		DBObject modifiedObject = new BasicDBObject();
		modifiedObject.put("$push", new BasicDBObject().append("UsedBy", user));
		coll.update(searchObject, modifiedObject);
	}

}
