import java.io.File;
import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import java.util.HashMap;
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


public class Database {
	
	public  DB db;
	public Map<String, String> Languages;
	
	public Database() {
		loadLangs();
		
	}
	
	public void loadLangs() {
		File fin = new File("utils/twitter_langs.txt");
		Languages = new HashMap<String,String>();
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
	
	public void createUniqueIndex(String collName, String fieldName) {
		DBCollection coll = db.getCollection(collName);
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
	
	public void updateMentionedByCount(String mentionee) {
		DBCollection coll = db.getCollection("User");
		BasicDBObject updateCommand = new BasicDBObject("$inc", new BasicDBObject("MentionCount", 1));
		BasicDBObject query = new BasicDBObject("ScreenName", mentionee);
		coll.update(query, updateCommand);
	}
	
	public void addHashTag(String tagName) {
		DBCollection coll = db.getCollection("HashTag");
		BasicDBList langs = new BasicDBList();
		for(Map.Entry<String, String> lang : Languages.entrySet())
			langs.add(new BasicDBObject("Lang", lang.getKey()).append("Count", 0));
		BasicDBObject HashTag = new BasicDBObject("HashTagName", tagName)
		.append("Langs", langs)
		.append("CoOccurHashTags", new BasicDBList())
		.append("HashTagUsers", new BasicDBList())
		.append("HashTagCount", 0)
		.append("Locations", new BasicDBList());
		try {
			coll.insert(HashTag);
		} catch(DuplicateKeyException exc) {
			//If hash tag already exists, do nothing..
		}		
	}
	
	public void updateHashTagCount(String tagName) {
		DBCollection coll = db.getCollection("HashTag");
		BasicDBObject updateCommand = new BasicDBObject("$inc", new BasicDBObject("HashTagCount", 1));
		BasicDBObject query = new BasicDBObject("HashTagName", tagName);
		coll.update(query, updateCommand);
	}
	
	
	public void IncrementLang(String lang, String tagName) {
		DBCollection coll = db.getCollection("HashTag");
		BasicDBObject updateCommand = new BasicDBObject("$inc", new BasicDBObject("Langs.$.Count", 1));
		BasicDBObject query = new BasicDBObject("HashTagName", tagName).append("Langs.Lang", lang);
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
		modifiedObject.put("$push", new BasicDBObject().append("CoOccurHashTags", allTagsObject));
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
	
	public void addHashTagLocation(String tagName, double latitude, double longitude) {
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
							.append("LinkCount", 0)
							.append("UsedBy", new BasicDBList());
		try {
			users.insert(link);
		} catch(DuplicateKeyException exc) {
			//If user already exists, do nothing..
		}
	}
	
	public void addLinkUsedBy(String user, String linkString) {
		DBCollection coll = db.getCollection("Link");
		DBObject searchObject = new BasicDBObject();
		searchObject.put("LinkString", linkString);
		DBObject modifiedObject = new BasicDBObject();
		modifiedObject.put("$push", new BasicDBObject().append("UsedBy", user));
		coll.update(searchObject, modifiedObject);
	}
	
	public void updateLinkCount(String linkString) {
		DBCollection coll = db.getCollection("Link");
		BasicDBObject updateCommand = new BasicDBObject("$inc", new BasicDBObject("LinkCount", 1));
		BasicDBObject query = new BasicDBObject("LinkString", linkString);
		coll.update(query, updateCommand);
	}

}
