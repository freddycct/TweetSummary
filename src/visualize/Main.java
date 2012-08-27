package visualize;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Map.Entry;

import decay_lda.Tweet;
import np_lda.NounPhrase;
import gauss_lda.InferModel;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

public class Main
{
	private static String opt_expt    = "experiment_name";
	private static String opt_ignore     = "ignore_words";
	private static String opt_iterations = "iterations";
	private static String opt_topics     = "num_topics";
	private static Options options       = new Options();
	
	public static void initOptions()
	{
		options.addOption("X",    opt_expt,       true, "Experiment Name");
		options.addOption("I",    opt_ignore,     true, "Words to ignore");
		options.addOption("ITER", opt_iterations, true, "# of Iterations");
		options.addOption("K",    opt_topics,     true, "Number of Topics");
	}
	
	public static void read(InferModel model) throws Exception
	{
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while((line=br.readLine())!=null)
		{
			String [] tokens = line.split("\t");
			Tweet tweet      = new Tweet();
			tweet.status_id  = Long.parseLong(tokens[0]);
			tweet.user_id    = Integer.parseInt(tokens[1]);
			tweet.content    = tokens[2];
			tweet.time       = Long.parseLong(tokens[3]);

			while((line = br.readLine()).length() > 0)
			{
				//<NP text> <tab> <topic>
				tokens = line.split("\t");
				NounPhrase np = tweet.add(tokens[0], model.stop_words, model.vocabulary);
				np.z = Integer.parseInt(tokens[1]);
			}
			model.add(tweet);
		}
		br.close();
	}
	
	public static void save(InferModel model) throws Exception
	{
		//This function here prints 4 files that can be used for visualization.
		//1) The model parameters file, this is needed so that you can continue training the model
		//2) The word distribution in the topics
		//3) The distribution of the tweets
		//4) The mean and variances of the topics
		
		//This portion save the parameters of the model so that the input(from the pipein) is the same.
		BufferedWriter bw = new BufferedWriter(new FileWriter(String.format("%s_K=%d.gauss_lda", model.expt_name, model.K)));
		Tweet current = model.head;
		while(current != null)
		{
			bw.write(String.format("%d\t%d\t%s\t%d", current.status_id, current.user_id, current.content, (current.time * 1000) + model.start_time));
			bw.newLine();
			for(NounPhrase np : current.nps)
			{
				bw.write(String.format("%s\t%d", np.text, np.z));
				bw.newLine();
			}
			bw.newLine();
			current = current.next;
		}
		bw.close();
		//End of saving the model
		
		//This portion save the words of the topics (unsorted, I will sort it later)
		for(int k=0;k<model.K;k++)
		{
			bw = new BufferedWriter(new FileWriter(String.format("%s_K=%d.%d", model.expt_name, model.K, k+1)));
			for(Entry<String, Double> entry : model.topics[k].phi.entrySet())
			{
				bw.write(String.format("%1.4f\t%s", Math.log(entry.getValue() + InferModel.BETA) - Math.log(model.topics[k].sum_phi + model.V * InferModel.BETA), entry.getKey()));
				bw.newLine();
			}
			bw.close();
		}
		//End of topics' words
		
		//This portion prints out the distribution of the tweets
		bw = new BufferedWriter(new FileWriter(String.format("%s_K=%d.distribution", model.expt_name, model.K)));
		current = model.head;
		while(current != null)
		{
			current.time = (current.time * 1000) + model.start_time;
			double [] p = current.getTopicDistribution(model.K, model.decay);
			bw.write(String.format("%1.4f", p[0]));
			for(int k=1;k<model.K;k++)
			{
				bw.write(String.format(",%1.4f", p[k]));
			}
			bw.write(String.format("\t%s\t%s", current.content, (new Timestamp(current.time)).toString()));
			bw.newLine();
			current = current.next;
		}
		bw.close();
		//End printing distribution
		
		//This portion prints out the mean and variances (Gaussian) of the topics
		bw = new BufferedWriter(new FileWriter(String.format("%s_K=%d.gaussians", model.expt_name, model.K)));
		for(int k=0;k<model.K;k++)
		{
			bw.write(String.format("%f\t%e\t%e", model.topics[k].sum_phi, model.topics[k].getMean() * 1000 + model.start_time, model.topics[k].getVar() * 1000000));
			bw.newLine();
		}
		bw.close();
		//End printing the gaussian topics
	}
	
	public static void main(String [] args) throws Exception
	{
		if(args.length < 3)
		{
			System.out.println("Need at least 3 arguments");
			System.out.println("--experiment_name=<name>              [This is compulsory]");
			System.out.println("--iterations=<iterations>             [This is compulsory]");
			System.out.println("--num_topics=<K>                      [This is compulsory]");
			System.out.println("--ignore_words=<additional stopwords> [This is optional]");
			
			System.exit(1);
		}
		
		//Read in the parameters from command line
		PosixParser parser = new PosixParser();
		initOptions();
		CommandLine cli = parser.parse(options, args);
		//End of reading from command line
		
		//Create an instance of the Gaussian Topic Model
		InferModel model = new InferModel(cli);

		//Read in the Tweets from Unix pipe
		read(model);
		//End of reading from pipe
		
		//Initialize the model
		model.init();
		
		//Estimate the parameters of the model
		model.infer();
		
		//Save the model after training
		save(model);
	}
}
