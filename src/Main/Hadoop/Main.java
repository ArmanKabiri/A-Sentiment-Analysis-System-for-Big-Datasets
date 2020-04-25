package Main.Hadoop;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * @author Arman Kabiri
 *
 */
public class Main extends Configured implements Tool {

    public static final Log log = LogFactory.getLog(Main.class);

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        // TO debug:
        log.info("STARTING MAIN");
        long startTime = System.currentTimeMillis();
        int exitCode = ToolRunner.run(new Configuration(), new Main(), args);
        long endTime = System.currentTimeMillis();
        System.out.println("RunTime:");
        System.out.println((endTime - startTime) / 1000);
        System.out.println("Seconds");
        System.exit(exitCode);

        // TO Run:
        //        Main driver = new Main();
        //        driver.run(args);
    }

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = getConf();
        //        if (conf == null) {
        //       	 conf = new Configuration();
        //        }
       
        Job job = new Job(conf, "Sentiment Analysis Task");

        job.setJarByClass(Main.class);
        job.setMapperClass(SAMapper.class);
        job.setReducerClass(SAReducer.class);

        job.setOutputKeyClass(IntWritable.class);
        job.setOutputValueClass(Text.class);

        job.setMapOutputKeyClass(IntWritable.class);
        job.setMapOutputValueClass(MapWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));     //DataSet path
        FileOutputFormat.setOutputPath(job, new Path(args[1]));     //Results Path

        //DistributedCaches:
        DistributedCache.addCacheFile(new Path(args[2]).toUri(), job.getConfiguration());     // Lexicon
        DistributedCache.addCacheFile(new Path(args[3]).toUri(), job.getConfiguration());     // DataSet_MetaData
        DistributedCache.addCacheFile(new Path(args[4]).toUri(), job.getConfiguration());     // Lexicon_MetaData

        //Wait for the job to complete and print if the job was successful or not
        int returnValue = job.waitForCompletion(true) ? 0 : 1;

        if (job.isSuccessful()) {
            System.out.println("Job was successful");
        } else if (!job.isSuccessful()) {
            System.out.println("Job was not successful");
        }

        return returnValue;
    }
}