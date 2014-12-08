import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.mongodb.DB;


public final class Wrapper {

	static String SERVER_NAME = "pc2lab-phi.csi.miamioh.edu";
	static String DATABASE_NAME = "TweetsDB";
	static int PORT = 27017;
	
	private Wrapper() {}	
	
	
	public static void plotTopUsers(Database db, Charting charting) {
		Map<String, Integer> users = db.getTopDocs("User", "MentionCount", "ScreenName", 10);
		charting.plotBarChart(users, "Most Popular User on Twitter", "User Screen Name", "User Mention Count", "images/tops/top_users.png");
	}
	
	public static void plotTopHashTags(Database db, Charting charting) {
		Map<String, Integer> hashtags = db.getTopDocs("HashTag", "HashTagCount", "HashTagName", 11);
		hashtags.remove(hashtags.remove("mtvstars"));
		charting.plotBarChart(hashtags, "Trending Hashtags on Twitter", "HashTag Name", "HashTag Count", "images/tops/top_hashtags.png");
		for(String ht : hashtags.keySet()) {
			plotLangsDistro(db, charting, ht, true);
			plotLangsDistro(db, charting, ht, false);
		}
	}
	
	public static void plotLangsDistro(Database db, Charting charting, String hashtag, boolean eng) {
		Map<String,Integer> langs;
		String chartName;
		if (eng) {
			langs = db.getLangDistro(hashtag, 4);
			chartName = "images/lang_distro/"+hashtag+"_langs_eng.png";
		}
		else {
			langs = db.getLangDistro(hashtag, db.LANGS_COUNT);
			if (langs.containsKey("English"))
				langs.remove("English");
			chartName = "images/lang_distro/"+hashtag+"_langs.png";
		}
		charting.plotPieChart(langs, "Distribution of Languages for #"+hashtag, chartName);
	}
	
	public static void plotLocsOnMap(Database db, Charting charting, String hashtag) {
		List<String> locs = db.getArray("HashTag", "HashTagName", hashtag, "Locations");
		charting.openGMaps(locs);
	}
	
	public static void plotHashtagTweetersDistro(Database db, Charting charting, String hashtag) {
		Map<String,Integer> tweeters = db.getAllCountDocs("HashTag", "HashTagName", hashtag, "HashTagUsersCount");
		Map<Integer,Integer> freqs = new HashMap<Integer,Integer>();
		for(Entry<String, Integer> entry : tweeters.entrySet()) {
			if (!freqs.containsKey(entry.getValue()))
				freqs.put(entry.getValue(), 1);
			else
				freqs.put(entry.getValue(), freqs.get(entry.getValue())+1);
		}
		charting.plotLineChart(freqs, "Numer of Tweets with #"+hashtag+" by Screen Name", "Screen Name Frequency", 
								"Number of Tweets with #"+hashtag, "images/freqs/"+hashtag+"_tweeter_freqs.png");
	}
	
	public static void plotTopHashTagUsers(Database db, Charting charting, String hashtag, int count) {
		Map<String, Integer> users = db.getTopArrayDocs("HashTag", "HashTagName", hashtag, "HashTagUsersCount", "Name", count);
		charting.plotBarChart(users, "Top "+count+" #"+hashtag+" tweeters", "User Screen Name", "Tweeting Count", "images/tops/top_"+hashtag+"_tweeters"+".png");
	}
	
	/*
	public static void plotComparisonChart(Database db, Charting charting, List<String> hashtags) {
		List<HashMap<Integer, Integer>> allFreqs = new ArrayList<HashMap<Integer, Integer>>();
		String title = "Comparison of Tweets by Screen Names between ";
		String hashtagsText = "";
		for(int i=0; i<hashtags.size(); i++) {
			hashtagsText += hashtags.get(i);
			if (i < hashtags.size()-1)
				hashtagsText += ", ";
			Map<String,Integer> tweeters = new HashMap<String, Integer>(db.getAllHashtagUsers(hashtags.get(i)));
			HashMap<Integer,Integer> freqs = new HashMap<Integer,Integer>();
			for(Entry<String, Integer> entry : tweeters.entrySet()) {
				if (!freqs.containsKey(entry.getValue()))
					freqs.put(entry.getValue(), 1);
				else
					freqs.put(entry.getValue(), freqs.get(entry.getValue())+1);
			}
			allFreqs.add(new HashMap<Integer, Integer>(freqs));
		}
		
		title += hashtagsText;
		charting.plotMultipleLineCharts(allFreqs, title, "Screen Name Frequency", 
								"Number of Tweets", "images/freqs/comparison_"+hashtagsText+".png");
	}
	*/
	
	
	
	public static void main(String[] args) {
		Database db = new Database();
		
		db.createDB(SERVER_NAME, PORT, DATABASE_NAME);
		
		Charting charting = new Charting();
		//plotTopUsers(db, charting);
		//plotTopHashTags(db, charting);
		//plotLangsDistro(db, charting, "mtvstars", false);
		//plotLocsOnMap(db, charting, "academiakidscd9");
		//plotHashtagTweetersDistro(db, charting, "rt");
		//List<String> hashtags = Arrays.asList("ferguson", "rt");
		//plotComparisonChart(db, charting, hashtags);
		
		plotTopHashTagUsers(db, charting, "ferguson", 10);
		//Map<String, Integer> map = db.getTopDocs("HashTag", "HashTagCount", "HashTagName", 10);
		
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
