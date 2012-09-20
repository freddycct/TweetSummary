package gauss_lda;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map.Entry;

import lda.Word;
import np_lda.NounPhrase;
import decay_lda.Tweet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.math3.special.Gamma;
//import org.apache.commons.math3.distribution.NormalDistribution;

public class InferModel extends decay_lda.InferModel<Topic>
{
	public InferModel(CommandLine cli) throws Exception
	{
		super(cli);
	}

	public static double gaussianPDF(double x, double mean, double variance)
	{
		//variance is sigma square
		return (1/Math.sqrt(2 * Math.PI * variance)) * Math.exp(- Math.pow(x - mean, 2) / (2 * variance));
	}

	public static double gaussianLogPDF(double x, double mean, double variance)
	{
		return -0.5 * Math.log(2 * Math.PI * variance) - Math.pow(x - mean, 2)/(2 * variance);
	}

	public void inferDecay(int k)
	{
		//NormalDistribution nd = new NormalDistribution(topics[k].getMean(), topics[k].getStd());
		//double t = nd.inverseCumulativeProbability(0.25);
		//t = (topics[k].getMean() - t);
		//decay[k] = - Math.log(0.5)/t;
		//decay[k] = Math.sqrt(-2 * topics[k].getVar() * Math.log(0.5));
		//decay[k] = - Math.log(0.5)
		decay[k] = Math.log(2) / Math.sqrt(2 * topics[k].getVar() * Math.log(2));
	}

	public void inferDecay()
	{
		for(int k=0;k<K;k++)
		{
			inferDecay(k);
		}
	}

	/*
	public void readWeights() throws Exception
	{
		words_weight = new HashMap<String, Double>();
		BufferedReader br = new BufferedReader(new FileReader(weight_file));
		String line;
		while((line=br.readLine())!=null)
		{
			String [] tokens = line.split("\t");
			double weight = Double.parseDouble(tokens[0]);
			String word = tokens[1];
			words_weight.put(word, weight);
		}
	}
	 */

	/*
	 * //Only use Top 10 words in each Topic
		for(int k=0;k<K;k++)
		{
			LinkedList<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>();
			for(Entry<String, Double> entry : topics[k].phi.entrySet())
			{
				list.add(entry);
			}
			Collections.sort(list, topic_word_comp);

			double sum = 0;
			Iterator<Entry<String, Double>> iter = list.iterator();
			for(int v = 0; v < 10; v++)
			{
				sum += iter.next().getValue();
			}

			iter = list.iterator();
			for(int v = 0; v < 10; v++)
			{
				Entry<String, Double> entry = iter.next();
				entry.setValue(entry.getValue() / sum);
			}

			while(iter.hasNext())
			{
				iter.next().setValue(0.0);
			}
		}

		//end of the hack...
	 */

	public void print() throws IOException
	{
		BufferedWriter bw;
		for(int k=0;k<K;k++)
		{
			bw = new BufferedWriter(new FileWriter(String.format("%s/results/%s/K=%d/gauss_lda/topic_%d.txt", prefix_dir, expt_name, K, k+1)));
			for(Entry<String, Double> entry : topics[k].phi.entrySet())
			{
				bw.write(String.format("%1.4f\t%s\n", Math.log(entry.getValue() + BETA) - Math.log(topics[k].sum_phi + V * BETA), entry.getKey()));
			}
			bw.close();
		}

		bw = new BufferedWriter(new FileWriter(String.format("results/%s/K=%d/gauss_lda/gaussian_topics.txt", expt_name, K)));
		for(int k=0;k<K;k++)
		{
			bw.write(String.format("%f\t%f\t%f\t%e\n", topics[k].sum_phi, topics[k].getMean() * 1000 + start_time, topics[k].getVar() * 1000000, decay[k]));
		}
		bw.close();

		bw = new BufferedWriter(new FileWriter(String.format("%s/results/%s/K=%d/gauss_lda/transition.txt", prefix_dir, expt_name, K)));
		Tweet current = head;
		while(current != null)
		{
			double log_mle = countMLE(current);
			double perplexity;

			if(log_mle >= 0)
			{
				perplexity = 1e99;
			}
			else
			{
				perplexity = Math.exp(-log_mle / current.getWeight());
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

	/*
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
				log_sum += Math.log(tweet.local_theta[k] + ALPHA) - Math.log(tweet.sum_local_theta + K * ALPHA) + np.getWeight() * gaussianLogPDF(tweet.time, topics[k].getMean(), topics[k].getVar());
				integral += Math.exp(log_sum);
			}
			log_mle += Math.log(integral);
		}
		return log_mle;
	}
	*/

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
				log_sum += Math.log(tweet.theta[k] + ALPHA) - Math.log(tweet.sum_theta + K * ALPHA) + np.getWeight() * gaussianLogPDF(tweet.time, topics[k].getMean(), topics[k].getVar());
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
			log_gamma_sum += Math.log(tweet.theta[k] + ALPHA) + np.getWeight() * gaussianLogPDF(tweet.time, topics[k].getMean(), topics[k].getVar());
			p[k] = Math.exp(log_gamma_sum + exp_multiplier);
			sum_p += p[k];
		}
		return sample(p, sum_p);
	}

	/*
	public int sample2(double [] p, Tweet tweet, NounPhrase np)
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
	 */
	
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
						topics[z].excludeWord(w, tweet.time);
					}

					z = sample(p, tweet, np);
					np.z = z;

					tweet.includeTopic(z, np.getWeight());

					for(Word w : np.words.values())
					{
						topics[z].includeWord(w, tweet.time);
					}
				}
			}

			inferDecay();
			System.out.println();
			/*
			System.out.print("\tMLE: ");
			mle = countMLE();
			//actually, compute the mle here to verify the convergence of the algorithm
			System.out.println(String.format("%1.4f", mle));
			 */
		}
	}

	public void init()
	{
		V = vocabulary.size();
		D = tweets.size();

		topics = new Topic[K];
		for(int k=0;k<K;k++)
		{
			topics[k] = new Topic(k);
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
					topics[z].includeWord(w, tweet.time);
				}
			}
			prev = tweet;
		}
		prev.next = null;

		if(iterations == 0)
		{
			inferDecay();
		}

		for(Tweet tweet : tweets)
		{
			for(int k=0;k<K;k++)
			{
				if(tweet.prev != null)
				{
					double tmp = tweet.prev.theta[k] * Math.exp(- decay[k] * (tweet.time - tweet.prev.time));
					tweet.theta[k]  += tmp;
					tweet.sum_theta += tmp;
				}
			}
		}
	}



	public void summarize() throws IOException
	{
		BufferedWriter bw = new BufferedWriter(new FileWriter(String.format("%s/results/%s/K=%d/gauss_lda/coherent.txt", prefix_dir, expt_name, K)));

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
					log_sum += Math.log(tweet.theta[z] + ALPHA) - Math.log(tweet.sum_theta + K * ALPHA) + np.getWeight() * gaussianLogPDF((tweet.time - start_time)/1000, topics[k].getMean(), topics[k].getVar());
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

	/*
	public void test() throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader(String.format("testset/%s.wiki", expt_name)));
		String line;
		
		Tagger tagger = new Tagger();
		String modelFilename = "jar/model.alldata.gz";
		tagger.loadModel(modelFilename);
		NPLexer lexer = new NPLexer();
		
		double [] p = new double[K];
		
		int n = 0;
		double squared_error = 0;
		
		while((line=br.readLine())!=null)
		{
			n++;
			String [] tokens = line.split("\t");
			Tweet tweet = new Tweet();
			tweet.time = Timestamp.valueOf(tokens[0]).getTime();
			tweet.content = tokens[1];
			
			//only extract unigrams
			List<String> nps = TagTweets.findNPs(tagger, lexer, tweet.content);
			for(String text : nps)
			{
				NounPhrase np = new NounPhrase();
				np.text = text;
				tokens  = np.text.split("[ ]+");
				for(String text2 : tokens)
				{
					if( ( (text2.matches("[$A-Za-z0-9%]+") && text2.length() > 2) || text2.matches("[0-9]+") ) && !stop_words.contains(text2) )
					{
						np.addWord(text2, 1.0);
					}
				}
				if(np.getWeight() > 0)
				{
					tweet.addNP(np);
				}
			}
			
			tweet.init(K);
			tweet.local_theta = new double[K];
			for(int k=0;k<K;k++)
			{
				tweet.theta[k] = 0.0;
				tweet.local_theta[k] = 0.0;
			}
			//first infer the topics of this tweet			
			for(NounPhrase np : tweet.nps)
			{
				int z = sample2(p, tweet, np);
				np.z = z;
				tweet.includeTopic(z, np.getWeight());
			}
			
			for(int i=0;i<iterations;i++)
			{
				for(NounPhrase np : tweet.nps)
				{
					int z = np.z;
					tweet.excludeTopic(z,  np.getWeight());
					z = sample2(p, tweet, np);
					np.z = z;
					tweet.includeTopic(z,  np.getWeight());
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
	*/
	
	public static void main(String [] args) throws Exception 
	{
		PosixParser parser = new PosixParser();
		initOptions();
		CommandLine cli = parser.parse(options, args);

		InferModel model = new InferModel(cli);

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

		//save the inferred stuff
		if(model.iterations > 0)
		{
			model.save("gauss_lda");
		}

		//print out the words in the topics
		model.print();

		model.summarize();
		//model.test();
	}
}
