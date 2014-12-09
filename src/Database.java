import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoClient;


public class Database {
	
	public  DB db;
	public Map<String, String> Languages;
	public int LANGS_COUNT;
	
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
			LANGS_COUNT = Languages.size();
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
	
	public void getFlatFile(String collName, String idName, String fieldName, String fileName) {
		File flatFile = new File(fileName);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(flatFile));
			DBCursor cursor = db.getCollection(collName).find();
			while (cursor.hasNext()) {
				DBObject obj = cursor.next();
				BasicDBList list = (BasicDBList) obj.get(fieldName);
				for(Object elem : list) {
					writer.write(obj.get(idName) + " " + (String)elem);
					writer.newLine();
				}
			}
			writer.close();
		} catch (IOException exc) {
			System.out.println("Errors in output file! Will exit now");
			System.exit(0);
		}
	}
	
	public void writeBackToDB(String fileName, String collName, String idName, String fieldName) {
		File fin = new File(fileName);
		DBCollection coll = db.getCollection(collName);
		Map<String,List<String>> allFields = new HashMap<String,List<String>>();
		Scanner reader = null;
		try {
			reader = new Scanner(fin);
			while (reader.hasNextLine()) {
				String[] arr = reader.nextLine().split("\\s+");
				if (!allFields.containsKey(arr[0]))
					allFields.put(arr[0], new ArrayList<String>());
				allFields.get(arr[0]).add(arr[1]+" "+arr[2]);
			}
			reader.close();
			
		} catch (FileNotFoundException e) {
			System.out.println("File Not Found! Will exit now");
			System.exit(0);
		}
		System.out.print("Done With Parsing");
		for(Entry<String, List<String>> entry : allFields.entrySet()) {
			DBObject query = new BasicDBObject(idName, entry.getKey());
			BasicDBList list = new BasicDBList();
			for(String elem : entry.getValue()) {
				String[] elemArr = elem.split("\\s+");
				DBObject obj = new BasicDBObject("Name", elemArr[0]).append("Count", elemArr[1]);
				list.add(obj);
			}
			BasicDBObject updateCommand = new BasicDBObject("$set", new BasicDBObject(fieldName, list));
			coll.update(query, updateCommand);
		}
	}
	
	
	static <K,V extends Comparable<? super V>>
	SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
	    SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
	        new Comparator<Map.Entry<K,V>>() {
	            @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
	                int res = e1.getValue().compareTo(e2.getValue());
	                return res != 0 ? res : 1;
	            }
	        }
	    );
	    sortedEntries.addAll(map.entrySet());
	    return sortedEntries;
	}
	
	public Map<String, Integer> getTopDocs(String collName, String countField, String nameField, int n) {
		TreeMap<Integer,List<String>> docs = new TreeMap<Integer,List<String>>(Collections.reverseOrder());
		DBCollection coll = db.getCollection(collName);
		DBCursor cursor = coll.find().sort(new BasicDBObject(countField, -1)).limit(n);
		for (DBObject dbObject : cursor) {
			String name = (String)dbObject.get(nameField);
			Integer count = (Integer)dbObject.get(countField);
			if (!docs.containsKey(count))
				docs.put(count, new ArrayList<String>());
			docs.get(count).add(name);
	    }
		Map<String, Integer> sortedDocs = new LinkedHashMap<String, Integer>();
		for(Map.Entry<Integer, List<String>> entry : docs.entrySet()) {
			for(String name : entry.getValue())
				sortedDocs.put(name, entry.getKey());
		}
		return sortedDocs;
	}
	
	public Map<String, Integer> getTopArrayDocs(String collName, String idField, String idName, String docArray, 
												String docArrayField, int max) {
		DBCollection coll = db.getCollection(collName);
		DBObject unwind = new BasicDBObject("$unwind", "$"+docArray);
		DBObject match = new BasicDBObject("$match", new BasicDBObject(idField, idName));
		DBObject project = new BasicDBObject("$project", new BasicDBObject("_id",0).append(docArray, 1));
		
		DBObject limit = new BasicDBObject("$limit", max);
		DBObject sort = new BasicDBObject("$sort", new BasicDBObject(docArray+".Count", -1));
		List<DBObject> pipeline = Arrays.asList(match, project, unwind, sort, limit);
		AggregationOutput output = coll.aggregate(pipeline);
		/*AggregationOptions aggregationOptions = AggregationOptions.builder()
                .batchSize(100)
                .outputMode(AggregationOptions.OutputMode.CURSOR)
                .allowDiskUse(true)
                .build();
		DBCursor cursor = (DBCursor) coll.aggregate(pipeline, aggregationOptions);
		*/
		Map<String, Integer> docs = new LinkedHashMap<String, Integer>();
		for(DBObject result : output.results()) {
			docs.put((String)((DBObject) result.get(docArray)).get(docArrayField), (Integer)((DBObject) result.get(docArray)).get("Count"));
		}
		return docs;
	}
	
	public Map<String,Integer> getAllCountDocs(String collName, String idField, String idName, String countField) {
		DBCollection coll = db.getCollection(collName);
		DBObject query = new BasicDBObject(idField, idName);
		DBCursor cursor = coll.find(query);
		Map<String, Integer> countDocs = new HashMap<String, Integer>();
		while(cursor.hasNext()) {
			BasicDBObject result = (BasicDBObject) cursor.next();
			BasicDBList array = (BasicDBList) result.get(countField);
			for(Object elem : array) {
				String name = (String)((DBObject) elem).get("Name");
				Integer count = (Integer)((DBObject) elem).get("Count");
				countDocs.put(name, count);
			}
		}
		return countDocs;
	}
	
	
	public Map<String,Integer> getLangDistro(String idName, int max) {
		DBCollection coll = db.getCollection("HashTag");
		DBObject unwind = new BasicDBObject("$unwind", "$Langs");
		DBObject match = new BasicDBObject("$match", new BasicDBObject("HashTagName", idName));
		DBObject project = new BasicDBObject("$project", new BasicDBObject("_id",0).append("Langs", 1));
		DBObject sort = new BasicDBObject("$sort", new BasicDBObject("Langs.Count", -1));
		DBObject limit = new BasicDBObject("$limit", max);
		List<DBObject> pipeline = Arrays.asList(unwind, match, project, sort, limit);
		AggregationOutput output = coll.aggregate(pipeline);
		Map<String,Integer> langs = new LinkedHashMap<String,Integer>();
		for(DBObject result : output.results()) {
			BasicDBObject obj = (BasicDBObject) result.get("Langs");
			if ((Integer)obj.get("Count") > 0)
				langs.put(Languages.get((String)obj.get("Lang")), (Integer)obj.get("Count"));
		}
		return langs;
	}
	
	public List<String> getArray(String collName, String idField, String idName, String arrayName) {
		DBCollection coll = db.getCollection(collName);
		DBObject query = new BasicDBObject(idField, idName);
		DBCursor cursor = coll.find(query);
		List<String> dbArray = new ArrayList<String>();
		while(cursor.hasNext()) {
			BasicDBObject result = (BasicDBObject) cursor.next();
			BasicDBList array = (BasicDBList) result.get(arrayName);
			for(Object elem : array) {
				Double lat = (Double)((DBObject) elem).get("Latitude");
				Double lng = (Double)((DBObject) elem).get("Longitude");
				dbArray.add(lat.toString()+","+lng.toString());
			}
		}
		return dbArray;
	}	

}
