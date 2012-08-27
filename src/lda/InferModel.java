package lda;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import lda.MTRandom;

import newalgo.Tagger;
//import np_lda.NPLexer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

public class InferModel<TweetType extends Tweet, TopicType extends Topic>
{
	protected int iterations;
	protected final double exp_multiplier = 10;
	
	public static final double ALPHA   = 0.1;
	public static final double BETA    = 0.1;	
	public static final double EPSILON = 2.2204e-16;
	
	protected MTRandom rand;
	protected LinkedList<TweetType> tweets;
	
	public TopicType [] topics;
	public HashSet<String> stop_words;
	public HashSet<String> vocabulary;
	
	public int V;
	protected int D;
	public int K;
	
	public String expt_name;
	protected String prefix_dir;
	
	//For Apache Commons CommandLine parser
	protected static Options options    = new Options();
	
	protected static String opt_expt    = "experiment_name";
	protected static String opt_topics  = "num_topics";
	protected static String opt_ignore  = "ignore_words";
	protected static String opt_dir     = "prefix_dir";
	protected static String opt_iterations = "iterations";
	//End CLI
	
	public static void initOptions()
	{
		options.addOption("X", opt_expt,   true, "Experiment Name");
		options.addOption("K", opt_topics, true, "Number of Topics");		
		options.addOption("I", opt_ignore, true, "Words to ignore");
		options.addOption("P", opt_dir,    true, "Prefix Directory");
		options.addOption("ITER", opt_iterations, true, "# of Iterations");
	}
	
	public InferModel() throws Exception
	{
		rand       = new MTRandom(280682);
		stop_words = new HashSet<String>();
		vocabulary = new HashSet<String>();
		tweets     = new LinkedList<TweetType>();
		
		//Read in the stop words in the stop word list
		BufferedReader br = new BufferedReader(new FileReader("jar/smart-common-words.txt"));
		addStopWords(br.readLine());
		br.close();
		//End of Stop Words
	}
	
	public InferModel(CommandLine cli) throws Exception
	{
		this();
		expt_name  = cli.getOptionValue(opt_expt);
		K          = Integer.parseInt(cli.getOptionValue(opt_topics));
		prefix_dir = cli.getOptionValue(opt_dir);
		iterations = cli.hasOption(opt_iterations) ? Integer.parseInt(cli.getOptionValue(opt_iterations)) : 50;
		
		if(cli.hasOption(opt_ignore))
		{
			String [] ignore = cli.getOptionValue(opt_ignore).split("[ \t\n\r\f]+");
			for(String w : ignore)
			{
				stop_words.add(w.toLowerCase());
			}
		}
	}
	
	public void addStopWords(String line)
	{
		String [] tokens = line.split(",");
		for(int i=0;i<tokens.length;i++)
		{
			stop_words.add(tokens[i]);
		}
	}
	
	public double countMLE(TweetType tweet)
	{
		double log_mle = 0;
		for(Word w : tweet.words)
		{
			double integral = 0;
			for(int k=0;k<K;k++)
			{
				double log_sum = Math.log(tweet.theta[k] + ALPHA) - Math.log(tweet.sum_theta + K * ALPHA) + Math.log(topics[k].get(w.text) + BETA) - Math.log(topics[k].sum_phi + V * BETA);
				integral += Math.exp(log_sum);
			}
			log_mle += w.getWeight() * Math.log(integral);
		}
		return log_mle;
	}
	
	public double countMLE()
	{
		double log_mle = 0;
		for(TweetType tweet : tweets)
		{
			log_mle += countMLE(tweet);
		}
		return log_mle;
	}
	
	public void print(String modelname) throws IOException
	{	
		BufferedWriter bw;

		for(int k=0;k<K;k++)
		{
			bw = new BufferedWriter(new FileWriter(String.format("%s/results/%s/K=%d/%s/topic_%d.txt", prefix_dir, expt_name, K, modelname, k+1)));
			for(Entry<String, Double> entry : topics[k].phi.entrySet())
			{
				bw.write(String.format("%1.4f\t%s\n", Math.log(entry.getValue() + BETA) - Math.log(topics[k].sum_phi + V * BETA), entry.getKey()));
			}
			bw.close();
		}
		
		bw = new BufferedWriter(new FileWriter(String.format("%s/results/%s/K=%d/%s/transition.txt", prefix_dir, expt_name, K, modelname)));
		Collections.sort(tweets);
		for(TweetType tweet : tweets)
		{
			double log_mle = countMLE(tweet);
			double perplexity;
			if(log_mle >= 0)
			{
				perplexity = 1e99;
			}
			else
			{
				perplexity = Math.exp(-log_mle /tweet.getWeight());
			}
			
			double [] p = tweet.getTopicDistribution(K);
			bw.write(String.format("%1.4f", p[0]));
			for(int k=1;k<K;k++)
			{
				bw.write(String.format(",%1.4f", p[k]));
			}
			bw.write(String.format("\t%s\t%s\t%d\t%f\t%e\n", tweet.content, (new Timestamp(tweet.time)).toString(), tweet.time, log_mle, perplexity));
		}
		bw.close();
	}
	
	
	
	public int sample(double [] p, double sum_p)
	{
		double r = rand.nextDouble();
		r = r * sum_p;
		for(int k=0;k<K-1;k++)
		{
			if(r < p[k]) return k;
			p[k+1] += p[k]; 
		}
		return K-1;
	}
	
	public int sample(double [] p, TweetType tweet, Word w)
	{
		double sum_p = 0;
		for(int k=0;k<K;k++)
		{
			p[k] = Math.log(tweet.theta[k] + ALPHA) + Math.log(topics[k].get(w.text) + BETA) - Math.log(topics[k].sum_phi + V * BETA);
			p[k] = Math.exp(p[k] + exp_multiplier);
			sum_p += p[k];
		}
		return sample(p, sum_p);
	}
	
	public void infer()
	{
		double [] p = new double[K];
		double mle = countMLE();
		System.out.println(String.format("Iterations: 0/%d\tMLE: %1.4f", iterations, mle));
		
		for(int i=0;i<iterations;i++)
		{
			System.out.print(String.format("Iterations: %d/%d", i+1, iterations));
			
			Collections.shuffle(tweets, rand);
			
			for(TweetType tweet : tweets)
			{
				for(Word word : tweet.words)
				{
					int z = word.z;
					tweet.excludeTopic(z, word.getWeight());
					topics[z].excludeWord(word);
					
					z = sample(p, tweet, word);
					word.z = z;
					
					tweet.includeTopic(z, word.getWeight());
					topics[z].includeWord(word);
				}
			}
			
			//System.out.println();
			
			System.out.print("\tMLE: ");
			mle = countMLE();
			//actually, compute the mle here to verify the convergence of the algorithm
			System.out.println(String.format("%1.4f", mle));
		}
	}
	
	@SuppressWarnings("unchecked")
	public void init()
	{
		V = vocabulary.size();
		D = tweets.size();

		topics = (TopicType [])new Topic[K];
		
		for(int k=0;k<K;k++)
		{
			topics[k]  = (TopicType)new Topic(k);
		}
			
		//Randomly initialize the topics
		for(TweetType tweet : tweets)
		{
			tweet.init(K);
			
			for(Word w : tweet.words)
			{
				int z;
				if(w.z < 0)
				{
					z = rand.nextInt(K);
					w.z = z;
				}
				else
				{
					z = w.z;
				}
				tweet.includeTopic(z, w.getWeight());
				topics[z].includeWord(w);
			}
		}
	}
	
	public void save(String model) throws Exception
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(String.format("%s/data/tweets/%s_K=%d.%s", prefix_dir, expt_name, K, model)));
		for(TweetType tweet : tweets)
		{
			bw.write(String.format("%d\t%d\t%s\t%d", tweet.status_id, tweet.user_id, tweet.content, tweet.time));
			bw.newLine();
			for(Word w : tweet.words)
			{
				bw.write(String.format("%s\t%d", w.text, w.z));
				bw.newLine();
			}
			bw.newLine();
		}
		bw.close();
	}
	
	public void summarize() throws IOException
	{
		Collections.sort(tweets);
		double start_time = tweets.getFirst().time;
		for(TweetType tweet : tweets)
		{
			for(Word w : tweet.words)
			{
				topics[w.z].sum_time += w.getWeight() * ((tweet.time - start_time)/1000);
			}
		}
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(String.format("%s/results/%s/K=%d/lda/coherent.txt", prefix_dir, expt_name, K)));
		Arrays.sort(topics);
		for(int k=0;k<K;k++)
		{
			int z = topics[k].z;
			//now z is the earliest topic, find the most representative tweet for this topic
			TweetType best = tweets.getFirst();
			double best_perplexity = 1e99;
			for(TweetType tweet : tweets)
			{
				//calculate the likelihood score for this topic z
				double log_mle = 0;
				for(Word w : tweet.words)
				{
					double log_sum = Math.log(tweet.theta[z] + ALPHA) - Math.log(tweet.sum_theta + K * ALPHA) + Math.log(topics[k].get(w.text) + BETA) - Math.log(topics[k].sum_phi + V * BETA);
					log_mle += w.getWeight() * log_sum;
				}
				double perplexity = Math.exp(-log_mle / tweet.getWeight());
				if(perplexity < best_perplexity)
				{
					best_perplexity = perplexity;
					best = tweet;
				}
			}
			double [] p = best.getTopicDistribution(K);
			bw.write(String.format("%1.4f", p[0]));
			for(int kk=1;kk<K;kk++)
			{
				bw.write(String.format(",%1.4f", p[kk]));
			}
			bw.write(String.format("\t%s\t%s", best.content, (new Timestamp(best.time)).toString()));
			bw.newLine();
		}
		bw.close();
	}
	
	@SuppressWarnings("unchecked")
	public void test() throws Exception
	{
		Collections.sort(tweets);
		long start_time = tweets.getFirst().time;
		for(TweetType tweet : tweets)
		{
			for(Word w : tweet.words)
			{
				topics[w.z].sum_time += w.getWeight() * ((tweet.time - start_time)/1000);
			}
		}
		
		BufferedReader br = new BufferedReader(new FileReader(String.format("testset/%s.wiki", expt_name)));
		String line;
		
		Tagger tagger = new Tagger();
		String modelFilename = "jar/model.alldata.gz";
		tagger.loadModel(modelFilename);
		//NPLexer lexer = new NPLexer();
		
		double [] p = new double[K];
		double squared_error = 0;
		int n = 0;
		while((line=br.readLine())!=null)
		{
			n++;
			//System.out.println(line);
			String [] tokens = line.split("\t");
			TweetType tweet = (TweetType) new Tweet();
			tweet.time = Timestamp.valueOf(tokens[0]).getTime();
			tweet.content = tokens[1];
			
			//only extract unigrams
			//List<String> nps = TagTweets2.findNPs(tagger, lexer, tweet.content);
			List<String> nps = TagTweets2.findNPs(tagger, tweet.content);
			for(String text : nps)
			{
				if( ( (text.matches("[$A-Za-z0-9%]+") && text.length() > 2) || text.matches("[0-9]+") ) && !stop_words.contains(text) )
				{
					Word w = new Word(text, 1.0);
					w.z = -1;
					tweet.add(w);	
				}
			}
			
			//Now predict time of this tweet
			tweet.init(K);
			for(int k=0;k<K;k++)
			{
				tweet.theta[k] = 0.0;
			}
			
			//first infer the topics of this tweet
			for(Word word : tweet.words)
			{
				int z = sample(p, tweet, word);
				word.z = z;
				tweet.includeTopic(z, word.getWeight());
			}
			
			for(int i=0;i<iterations;i++)
			{
				for(Word word : tweet.words)
				{
					int z = word.z;
					tweet.excludeTopic(z, word.getWeight());
					z = sample(p, tweet, word);
					word.z = z;
					tweet.includeTopic(z, word.getWeight());
				}
			}
			p = tweet.getTopicDistribution(K);
			double predicted_time = 0;
			for(int k=0;k<K;k++)
			{
				predicted_time += p[k] * topics[k].getMean();
			}
			predicted_time = (predicted_time * 1000) + start_time;
			squared_error += Math.pow(predicted_time - tweet.time, 2);
			//System.out.println(String.format("%1.3e:\t%s", Math.pow(predicted_time - tweet.time, 2), line));
			System.out.println(String.format("%s\t%s", 
					(new Timestamp(tweet.time)).toString(),
					(new Timestamp(Math.round(predicted_time))).toString()
					));
		}
		br.close();
		System.out.println(String.format("Mean Squared Error: %e", squared_error/n));
	}
		
	public static void main(String [] args) throws Exception 
	{
		PosixParser parser = new PosixParser();
		initOptions();
		CommandLine cli = parser.parse(options, args);
		
		InferModel<Tweet, Topic> model = new InferModel<Tweet, Topic>(cli);
		
		String line;
		BufferedReader br;
								
		//Read in the input file generated
		br = new BufferedReader(new InputStreamReader(System.in));
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
				//Word <tab> Topic
				tokens = line.split("\t");
				String text = tokens[0];
				
				if( ( (text.matches("[$A-Za-z0-9%]+") && text.length() > 2) || text.matches("[0-9]+") ) && !model.stop_words.contains(text) )
				{
					Word w = new Word(text, 1.0);
					w.z = Integer.parseInt(tokens[1]);
					tweet.add(w);
					
					model.vocabulary.add(w.text);
				}
			}
			model.tweets.add(tweet);
		}
		br.close();
		//End of reading input file

		//initialize
		model.init();
		
		//do the sampling for the topics
		model.infer();
		
		//save to disk so don't have to always retrain it
		if(model.iterations > 0)
		{
			model.save("lda");
		}
		
		//print out the words in the topics
		model.print("lda");
		
		//model.test();
		//print out summaries
		model.summarize();
	}
}
