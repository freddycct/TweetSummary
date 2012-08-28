package decay_lda;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map.Entry;

import lda.Topic;
import lda.Word;
import np_lda.NounPhrase;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.math3.special.Gamma;
//import org.apache.commons.math3.distribution.NormalDistribution;

public class InferModel<TopicType extends Topic> extends np_lda.InferModel<Tweet, TopicType>
{
	protected static String opt_decay = "decay";
	public double [] decay;
	public Tweet head;
	public long start_time;
	
	public static void initOptions()
	{
		np_lda.InferModel.initOptions();
		options.addOption("D", opt_decay, true, "delta decay value");
	}
	
	public InferModel(CommandLine cli) throws Exception
	{
		super(cli);
		double tmp_decay = cli.hasOption(opt_decay) ? Double.parseDouble(cli.getOptionValue(opt_decay)) : -Math.log(0.5) / (60*60);
		decay = new double[K];
		
		for(int k=0;k<K;k++)
		{
			decay[k] = tmp_decay;
		}
	}
	
	public double countMLE2(Tweet tweet)
	{
		double log_mle = 0;
		for(NounPhrase np : tweet.nps)
		{
			double integral = 0;
			for(int k=0;k<K;k++)
			{
				double log_sum = 0;
				for(Word w : np.words.values())
				{
					log_sum += w.getWeight() * ( Math.log(topics[k].get(w.text) + BETA) - Math.log(topics[k].sum_phi + V * BETA) );  
				}
				log_sum += Math.log(tweet.local_theta[k] + ALPHA) - Math.log(tweet.sum_local_theta + K * ALPHA);
				integral += Math.exp(log_sum);
			}
			log_mle += Math.log(integral);
		}
		return log_mle;
	}
	
	public double countMLE(Tweet tweet)
	{
		double log_mle = 0;
		for(NounPhrase np : tweet.nps)
		{
			double integral = 0;
			for(int k=0;k<K;k++)
			{
				double log_sum = 0;
				for(Word w : np.words.values())
				{
					log_sum += w.getWeight() * ( Math.log(topics[k].get(w.text) + BETA) - Math.log(topics[k].sum_phi + V * BETA) );  
				}
				log_sum += Math.log(tweet.theta[k] + ALPHA) - Math.log(tweet.sum_theta + K * ALPHA);
				integral += Math.exp(log_sum);
			}
			log_mle += Math.log(integral);
		}
		return log_mle;
	}
	
	public double countMLE()
	{
		double log_mle = 0;
		for(Tweet tweet : tweets)
		{
			log_mle += countMLE(tweet);
		}
		return log_mle;
	}
	
	public void print() throws IOException
	{
		BufferedWriter bw;
		for(int k=0;k<K;k++)
		{
			bw = new BufferedWriter(new FileWriter(String.format("%s/results/%s/K=%d/decay_lda/topic_%d.txt", prefix_dir, expt_name, K, k+1)));
			for(Entry<String, Double> entry : topics[k].phi.entrySet())
			{
				bw.write(String.format("%1.4f\t%s\n", Math.log(entry.getValue() + BETA) - Math.log(topics[k].sum_phi + V * BETA), entry.getKey()));
			}
			bw.close();
		}
		
		bw = new BufferedWriter(new FileWriter(String.format("%s/results/%s/K=%d/decay_lda/transition.txt", prefix_dir, expt_name, K)));
		Tweet current = head;
		while(current != null)
		{
			double log_mle = countMLE2(current);
			double perplexity;
			
			if(log_mle >= 0)
			{
				perplexity = 1e99;
			}
			else
			{
				perplexity = Math.exp(-log_mle /current.getWeight());
			}
					
			current.time = (current.time * 1000) + start_time;
			double [] p = current.getTopicDistribution(K, decay);
			bw.write(String.format("%1.4f", p[0]));
			for(int k=1;k<K;k++)
			{
				bw.write(String.format(",%1.4f", p[k]));
			}
			
			bw.write(String.format("\t%s\t%s\t%d\t%f\t%e\n", current.content, (new Timestamp(current.time)).toString(), current.time, log_mle, perplexity));
			current = current.next;
		}
		bw.close();
	}
	
	public int sample(double [] p, Tweet tweet, NounPhrase np)
	{
		double sum_p = 0;
		for(int k=0;k<K;k++)
		{
			double log_gamma_sum = 0;
			for(Word w : np.words.values())
			{
				log_gamma_sum += Gamma.logGamma(BETA + topics[k].get(w.text) + w.getWeight()) - Gamma.logGamma(BETA + topics[k].get(w.text));
			}
			log_gamma_sum += Gamma.logGamma(V * BETA + topics[k].sum_phi) - Gamma.logGamma(V * BETA + topics[k].sum_phi + np.getWeight());
			log_gamma_sum += Math.log(tweet.theta[k] + ALPHA);
			p[k] = Math.exp(log_gamma_sum + exp_multiplier);
			sum_p += p[k];
		}
		return sample(p, sum_p);
	}
	
	
	public void infer()
	{
		double [] p = new double[K];
		double mle  = countMLE();
		System.out.println(String.format("Iterations: 0/%d\tMLE: %1.4f", iterations, mle));
		
		for(int i=0;i<iterations;i++)
		{
			System.out.print(String.format("Iterations: %d/%d", i+1, iterations));
			
			Collections.shuffle(tweets, rand);
			
			for(Tweet tweet : tweets)
			{
				tweet.initTheta(K, decay);
				for(NounPhrase np : tweet.nps)
				{
					int z = np.z;
					
					tweet.excludeTopic(z, np.getWeight());
					
					for(Word w : np.words.values())
					{
						topics[z].excludeWord(w);
					}
					
					z = sample(p, tweet, np);
					np.z = z;
					
					tweet.includeTopic(z, np.getWeight());
					
					for(Word w : np.words.values())
					{
						topics[z].includeWord(w);
					}
				}
			}
			System.out.println();
			/*
			System.out.print("\tMLE: ");
			mle = countMLE();
			//actually, compute the mle here to verify the convergence of the algorithm
			System.out.println(String.format("%1.4f", mle));
			*/
		}
	}
	
	@SuppressWarnings("unchecked")
	public void init()
	{
		V = vocabulary.size();
		D = tweets.size();
		
		topics = (TopicType[]) new Topic[K];
		for(int k=0;k<K;k++)
		{
			topics[k] = (TopicType) new Topic(k);
		}
		
		//Arrange the tweets in chronological order
		Collections.sort(tweets);
		head = tweets.getFirst();
		start_time = head.time;
		
		//Randomly initialize the topics
		Tweet prev = null;
		for(Tweet tweet : tweets)
		{
			//Create the data structures for theta & local_theta, also assign previous tweet to this tweet
			
			//make it smaller to avoid overflow
			tweet.time = (tweet.time - start_time) / 1000;
			tweet.init(K, prev);
			
			for(int k=0;k<K;k++)
			{
				if(prev != null)
				{
					tweet.theta[k] = prev.theta[k] * Math.exp(- decay[k] * (tweet.time - prev.time));
					tweet.sum_theta += tweet.theta[k];
				}
			}
			
			for(NounPhrase np : tweet.nps)
			{
				int z;
				if(np.z < 0)
				{
					z = rand.nextInt(K);
					np.z = z;
				}
				else
				{
					z = np.z;
				}
				tweet.includeTopic(z, np.getWeight());
				
				for(Word w : np.words.values())
				{
					topics[z].includeWord(w);
				}
			}
			prev = tweet;
		}
		prev.next = null;
	}
	
	public void save(String model) throws Exception
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(String.format("%s/data/tweets/%s_K=%d.%s", prefix_dir, expt_name, K, model)));
		Tweet current = head;
		while(current != null)
		{
			bw.write(String.format("%d\t%d\t%s\t%d", current.status_id, current.user_id, current.content, (current.time * 1000) + start_time));
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
	}
	
	public void summarize() throws IOException
	{
		Tweet current = head;
		while(current != null)
		{
			for(NounPhrase np : current.nps)
			{
				for(Word w : np.words.values())
				{
					topics[w.z].sum_time += w.getWeight() * ((current.time - start_time)/1000);
				}
			}
			current = current.next;
		}
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(String.format("%s/results/%s/K=%d/decay_lda/coherent.txt", prefix_dir, expt_name, K)));
		
		Arrays.sort(topics);
		for(int k=0;k<K;k++)
		{
			int z = topics[k].z;
			//now z is the earliest topic, find the most representative tweet for this topic
			Tweet best = tweets.getFirst();
			double best_perplexity = 1e99;
			
			for(Tweet tweet : tweets)
			{
				//calculate the likelihood score for this topic z
				double log_mle = 0;
				for(NounPhrase np : tweet.nps)
				{
					double log_sum = 0;
					for(Word w : np.words.values())
					{
						log_sum += w.getWeight() * ( Math.log(topics[k].get(w.text) + BETA) - Math.log(topics[k].sum_phi + V * BETA) );  
					}
					log_sum += Math.log(tweet.theta[z] + ALPHA) - Math.log(tweet.sum_theta + K * ALPHA);
					log_mle += log_sum;
				}
				
				double perplexity = Math.exp(-log_mle / tweet.getWeight());
				if(perplexity < best_perplexity)
				{
					best_perplexity = perplexity;
					best = tweet;
				}
			}
			double [] p = best.getTopicDistribution(K, decay);
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
	
	public static void main(String [] args) throws Exception 
	{
		PosixParser parser = new PosixParser();
		initOptions();
		CommandLine cli = parser.parse(options, args);
		
		InferModel<Topic> model = new InferModel<Topic>(cli);
		
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
				//NP <tab> topic
				tokens = line.split("\t");
				NounPhrase np = tweet.add(tokens[0], model.stop_words, model.vocabulary); 
				np.z = Integer.parseInt(tokens[1]);
			}
			model.add(tweet);
		}
		br.close();
		//End of reading input file
		
		//perform the model inference
		model.init();
		
		//do the sampling for the topics
		model.infer();
		
		if(model.iterations > 0)
		{
			model.save("decay_lda");
		}
		
		//print out the words in the topics
		model.print();
		
		model.summarize();
	}
}
