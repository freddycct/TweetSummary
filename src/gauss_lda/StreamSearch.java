package gauss_lda;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.List;
import java.util.TreeSet;

import newalgo.Tagger;
import np_lda.NPLexer;
import np_lda.NounPhrase;
import np_lda.TagTweets;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.PosixParser;

import decay_lda.Tweet;

public class StreamSearch extends InferModel 
{
	TreeSet<Tweet> tweets_tree;
	
	public StreamSearch(CommandLine cli) throws Exception 
	{
		super(cli);
		tweets_tree = new TreeSet<Tweet>();
	}

	public void search() throws Exception
	{
		for(Tweet tweet : tweets)
		{
			tweets_tree.add(tweet);
		}
		
		Tagger tagger = new Tagger();
		String modelFilename = "jar/model.alldata.gz";
		tagger.loadModel(modelFilename);
		NPLexer lexer = new NPLexer();
		
		double [] p = new double[K];
		
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		br.readLine(); //read the header
		while((line=br.readLine())!=null)
		{
			String [] tokens = line.split("\t");
			Tweet tweet = new Tweet();
			tweet.status_id = Long.parseLong(tokens[0]);
			tweet.user_id = Integer.parseInt(tokens[1]);
			tweet.content = tokens[2];
			tweet.time = Timestamp.valueOf(tokens[3]).getTime();
			
			tweet.time = (tweet.time - start_time)/1000;
			
			if(tweet.content.trim().length() <= 2)
				continue;
			
			List<String> nps = TagTweets.findNPs(tagger, lexer, tweet.content);
			for(String text : nps)
			{
				tweet.add(text, stop_words);
			}
			
			Tweet prev = tweets_tree.floor(tweet);
			
			tweet.init2(K, prev);
			tweet.initTheta(K, decay);
			
			//first infer the topics of this tweet
			for(NounPhrase np : tweet.nps)
			{
				int z = sample(p, tweet, np);
				np.z = z;
				tweet.includeTopic(z, np.getWeight());
			}
			
			for(int i=0;i<iterations;i++)
			{
				for(NounPhrase np : tweet.nps)
				{
					int z = np.z;
					tweet.excludeTopic(z,  np.getWeight());
					z = sample(p, tweet, np);
					np.z = z;
					tweet.includeTopic(z,  np.getWeight());
				}
			}
			
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
			
			p = tweet.getTopicDistribution(K);
			
			System.out.print(String.format("%1.4f", p[0]));
			for(int k=1;k<K;k++)
			{
				System.out.print(String.format(",%1.4f", p[k]));
			}
			tweet.time = (tweet.time * 1000) + start_time;
			System.out.println(String.format("\t%s\t%s\t%d\t%f\t%e", tweet.content, (new Timestamp(tweet.time)).toString(), tweet.time, log_mle, perplexity));
		}
		br.close();
	}
	
	public static void main(String [] args) throws Exception
	{
		PosixParser parser = new PosixParser();
		initOptions();
		CommandLine cli = parser.parse(options, args);
		
		StreamSearch model = new StreamSearch(cli);
		String line;
		BufferedReader br;

		//Read in the input file generated
		br = new BufferedReader(new FileReader(String.format("%s/data/tweets/%s_K=%d.gauss_lda", model.prefix_dir, model.expt_name, model.K)));
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
			if(tweet.getWeight() > 0)
			{
				model.tweets.add(tweet);
			}
		}
		br.close();
		//End of reading input file

		//perform the model inference
		model.init();
		model.search();
	}
}
