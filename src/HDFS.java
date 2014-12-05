import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;


public class HDFS {
	
	DB db;
	String newField;
	String idName;
	String dbFile;
	String hdfsFile;
	String countField;
	String collName;
	
	public HDFS(DB db, String idName, String countField, String newField, String dbFile, String hdfsFile, String collName) {
		this.db = db;
		this.idName = idName;
		this.dbFile = dbFile;
		this.hdfsFile = hdfsFile;
		this.countField = countField;
		this.collName = collName;
	}
	
	public void writeDataToFile() {
		File inFile = new File(dbFile);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(inFile));
			DBCursor cursor = db.getCollection(collName).find();
			while (cursor.hasNext()) {
				DBObject obj = cursor.next();
				BasicDBList list = (BasicDBList) obj.get(countField);
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
	
	public Map<String,List<String>> readDataFromFile() {
		File fin = new File(hdfsFile);
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
		return allFields;
	}
	
	
	public void writeBackToDB() {
		DBCollection coll = db.getCollection(collName);
		Map<String,List<String>> allFields = readDataFromFile();
		for(Entry<String, List<String>> entry : allFields.entrySet()) {
			DBObject query = new BasicDBObject(idName, entry.getKey());
			BasicDBList list = new BasicDBList();
			for(String elem : entry.getValue()) {
				String[] elemArr = elem.split("\\s+");
				DBObject obj = new BasicDBObject("Name", elemArr[0]).append("Count", elemArr[1]);
				list.add(obj);
			}
			BasicDBObject updateCommand = new BasicDBObject("$set", new BasicDBObject(newField, list));
			coll.update(query, updateCommand);
		}
	}

}
