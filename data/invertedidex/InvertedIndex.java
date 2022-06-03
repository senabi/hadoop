import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.*;
import java.util.StringTokenizer;

import javax.naming.Context;
import javax.print.attribute.IntegerSyntax;

import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class InvertedIndex {

  public static class TokenizerMapper
      extends Mapper<Object, Text, Text, Text> {

    private Text word = new Text();
    private Text fileLoc = new Text();

    public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
      String fileName = ((FileSplit) context.getInputSplit()).getPath().getName();
      fileLoc.set(fileName);

      StringTokenizer itr = new StringTokenizer(value.toString());
      while (itr.hasMoreTokens()) {
        word.set(itr.nextToken());
        context.write(word, fileLoc);
      }
    }
  }

  public static class TextAppendReducer
      extends Reducer<Text, Text, Text, Text> {

    public void reduce(Text key, Iterable<Text> values,
        Context context) throws IOException, InterruptedException {

      Set<Text> out = new HashSet<Text>();
      StringBuilder sb = new StringBuilder();
      for (Text value : values) {
        if (!out.contains(value)) {
          sb.append(value.toString() + " ");
        }
        out.add(value);
      }
      context.write(key, new Text(sb.toString()));
    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "Inverted Index");
    job.setJarByClass(InvertedIndex.class);
    job.setMapperClass(TokenizerMapper.class);
    // job.setCombinerClass(TextAppendReducer.class);
    job.setReducerClass(TextAppendReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);

    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));

    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}