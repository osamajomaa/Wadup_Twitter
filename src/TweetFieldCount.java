import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class TweetFieldCount {

  public static class TokenizerMapper
       extends Mapper<Object, Text, Text, IntWritable>{

    private final static IntWritable one = new IntWritable(1);

    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
	context.write(value, one);
    }
  }

  public static class IntSumReducer
       extends Reducer<Text,IntWritable,Text,IntWritable> {
    private IntWritable result = new IntWritable();

    public void reduce(Text key, Iterable<IntWritable> values,
                       Context context
                       ) throws IOException, InterruptedException {
      int sum = 0;
      for (IntWritable val : values) {
        sum += val.get();
      }
      result.set(sum);
      context.write(key, result);
    }
  }

  public void run(String output, String input) {
	  	Configuration conf = new Configuration();
	    Job job = null;
		try {
			job = Job.getInstance(conf, "tweet field count");
		} catch (IOException e) {
			System.out.println("Couldn't get an instance of Job. Will exit now!");
			System.exit(0);
		}
	    job.setJarByClass(TweetFieldCount.class);
	    job.setMapperClass(TokenizerMapper.class);
	    job.setCombinerClass(IntSumReducer.class);
	    job.setReducerClass(IntSumReducer.class);
	    job.setOutputKeyClass(Text.class);
	    job.setOutputValueClass(IntWritable.class);
	    try {
			FileInputFormat.addInputPath(job, new Path(input));
		} catch (IllegalArgumentException | IOException e) {
			System.out.println("Path is not valid. Will exit now!");
			System.exit(0);
		}
	    FileOutputFormat.setOutputPath(job, new Path(output));
	    try {
			System.exit(job.waitForCompletion(true) ? 0 : 1);
		} catch (ClassNotFoundException | IOException | InterruptedException e) {
			System.out.println("Job completed unsuccessfully. Will exit now!");
			System.exit(0);
		}
  }

}
