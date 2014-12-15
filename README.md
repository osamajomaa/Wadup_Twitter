Wadup_Twitter
=============

A big-data application that analyzes Twitter live feeds metadata to answer questions such as:
- Who is the most popular user on Twitter for a certain period of time?
- What are the most popular hashtags on Twitter, where they where tweeted and in which languages?
- What is the most shared link on Twitter and who shared it the most?

Wadup_Twitter comprises three main steps:
- Data Collection
- Data Analysis
- Data Visualization

#Data Collection
Wadup_Twitter used Twitter Streaming API to get tweets and their metadata with low latency. Since it is written in Java, It uses Twitter4J library to establish the connection to the API and add a listener that fetches the live feeds. The hashtag, tweeter and link metadata is extracted and stored in a Mongo database for later processing. The database has three main collections:
- User
- HashTag
- Link

The User collection contains the tweeter Screen Name and Mentioners. The HashTag contains the hashtag name, tweeters, languages and locations. The Link collection contains the link URL and tweeters. 

#Data Processing
Wadup_Twitter uses Apache Hadoop to count the number of tweeting times for each hashtag by each user, the number of tweeting times for each link by each user, the number of mentioning times for each user by each mentioner. The results are written back to the database as new array documents.

#Data Visualization
Wadup_Twitter uses JFreeChart, an open-source Java charting library. 
