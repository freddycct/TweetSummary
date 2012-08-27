package np_lda;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;

import lda.Topic;
import lda.Word;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.math3.special.Gamma;

public class InferModel<TweetType extends Tweet, TopicType extends Topic> extends lda.InferModel<TweetType, TopicType>
{
	public InferModel(CommandLine cli) throws Exception
	{
		super(cli);
	}
	
	public double countMLE(TweetType tweet)
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
		for(TweetType tweet : tweets)
		{
			log_mle += countMLE(tweet);
		}
		return log_mle;
	}
	
	public int sample(double [] p, TweetType tweet, NounPhrase np)
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
		double mle = countMLE();
		System.out.println(String.format("Iterations: 0/%d\tMLE: %1.4f", iterations, mle));
		
		for(int i=0;i<iterations;i++)
		{
			System.out.print(String.format("Iterations: %d/%d", i+1, iterations));
			Collections.shuffle(tweets, rand);
			for(TweetType tweet : tweets)
			{
				for(NounPhrase np : tweet.nps)
				{
					int z = np.z;
					tweet.excludeTopic(z,  np.getWeight());
					
					for(Word w : np.words.values())
					{
						topics[z].excludeWord(w);
					}
					
					z = sample(p, tweet, np);
					np.z = z;
					
					for(Word w : np.words.values())
					{
						topics[z].includeWord(w);
					}
					tweet.includeTopic(z, np.getWeight());
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
			topics[k]  = (TopicType)new Topic(k);
		}
		
		for(TweetType tweet : tweets)
		{
			tweet.init(K);
			
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
		}
	}
	
	public void save(String model) throws Exception
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(String.format("%s/data/tweets/%s_K=%d.%s", prefix_dir, expt_name, K, model)));
		for(TweetType tweet : tweets)
		{
			bw.write(String.format("%d\t%d\t%s\t%d", tweet.status_id, tweet.user_id, tweet.content, tweet.time));
			bw.newLine();
			for(NounPhrase np : tweet.nps)
			{
				bw.write(String.format("%s\t%d", np.text, np.z));
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
			for(NounPhrase np : tweet.nps)
			{
				for(Word w : np.words.values())
				{
					topics[w.z].sum_time += w.getWeight() * ((tweet.time - start_time)/1000);
				}
			}
		}
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(String.format("%s/results/%s/K=%d/np_lda/coherent.txt", prefix_dir, expt_name, K)));
		
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
	
	public void add(TweetType tweet)
	{
		if(tweet.getWeight() > 0)
		{
			tweets.add(tweet);
		}
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
			Tweet tweet  = new Tweet();
			tweet.status_id  = Long.parseLong(tokens[0]);
			tweet.user_id    = Integer.parseInt(tokens[1]);
			tweet.content    = tokens[2];
			tweet.time       = Long.parseLong(tokens[3]);
			
			while((line = br.readLine()).length() > 0)
			{
				tokens  = line.split("\t");
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
			model.save("np_lda");
		}
		
		//print out the words in the topics
		model.print("np_lda");
		model.summarize();
	}
}
