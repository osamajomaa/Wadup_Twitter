import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.mongodb.DB;


public final class Wrapper {

	static String SERVER_NAME = "localhost";
	static String DATABASE_NAME = "TweetsDB";
	static int PORT = 27017;
	
	private Wrapper() {}	
	
	public static void plotTopUsers(Database db, Charting charting) {
		Map<String,Integer> users = db.getTopDocs("User", "MentionCount", "ScreenName", 10);
		charting.plotBarChart(users, "Most Popular User on Twitter", "User Screen Name", "User Mention Count");
	}
	
	public static void plotTopHashTags(Database db, Charting charting) {
		Map<String,Integer> users = db.getTopDocs("HashTag", "HashTagCount", "HashTagName", 10);
		charting.plotBarChart(users, "Trending Hashtags on Twitter", "HashTag Name", "HashTag Count");
	}
	
	public static void plotLangsDistro(Database db, Charting charting, String hashtag) {
		Map<String,Integer> langs = db.getLangDistro(hashtag);
		charting.plotPieChart(langs, "Distribution of Languages for #"+hashtag);
	}
	
	public static void plotLocsOnMap(Database db, Charting charting, String hashtag) {
		List<String> locs = db.getArray("HashTag", "HashTagName", hashtag, "Locations");
		charting.plotMap(locs);
	}
	
	public static void plotHashtagTweetersDistro(Database db, Charting charting, String hashtag) {
		Map<String,Integer> tweeters = db.getTopArrayDocs("HashTag", "HashTagName", hashtag, "HashTagUsers", "Name", 0);
		Map<Integer,Integer> freqs = new HashMap<Integer,Integer>();
		for(Entry<String, Integer> entry : tweeters.entrySet()) {
			if (!freqs.containsKey(entry.getKey()))
				freqs.put(entry.getValue(), 1);
			else
				freqs.put(entry.getValue(), freqs.get(entry.getValue())+1);
		}
		charting.plotLineChart(freqs, "Numer of Tweets with #"+hashtag+" by Screen Name", "Screen Name Frequency", 
								"Number of Tweets with #"+hashtag);
	}
	
	
	public static void main(String[] args) {
		Database db = new Database();
		
		db.createDB(SERVER_NAME, PORT, DATABASE_NAME);
		
		/*
		db.createCollection("User");
		db.createUniqueIndex("User", "ScreenName");
		
		db.createCollection("HashTag");
		db.createUniqueIndex("HashTag", "HashTagName");
		
		db.createCollection("Link");
		db.createUniqueIndex("Link", "LinkString");
		
		Streamer twitterStreamer = new Streamer();
		twitterStreamer.grabber(db);
		*/
		
		/*
		String dbFile = "DBFiles/MentionedBy.db";
		String newField = "MentionedByCount";
		String idName = "ScreenName";
		String hdfsFile = "HDFS/MentionedBy.hdfs";
		String countField = "MentionCount";
		String collName = "User";
		
		HDFS hdfs = new HDFS(db.db, idName, countField, newField, dbFile, hdfsFile, collName);
		hdfs.writeDataToFile();
		TweetFieldCount fieldCounter = new TweetFieldCount();
		
		String hadoopInput = "HDFS/input";
		String hadoopOutput = "HDFS/output";
		fieldCounter.run(hadoopOutput, hadoopInput);
		
		hdfs.writeBackToDB();
		*/
		
	}

}
