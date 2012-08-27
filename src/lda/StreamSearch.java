package lda;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.List;

import newalgo.Tagger;
//import np_lda.NPLexer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.PosixParser;

public class StreamSearch extends InferModel<Tweet, Topic>
{
	public StreamSearch(CommandLine cli) throws Exception
	{
		super(cli);
	}
	
	public void search() throws Exception
	{
		Tagger tagger = new Tagger();
		String modelFilename = "jar/model.alldata.gz";
		tagger.loadModel(modelFilename);
		//NPLexer lexer = new NPLexer();
		
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
			
			if(tweet.content.trim().length() <= 2)
				continue;
			
			List<String> nps = TagTweets2.findNPs(tagger, lexer, tweet.content);
			for(String text : nps)
			{
				if( ( (text.matches("[$A-Za-z0-9%]+") && text.length() > 2) || text.matches("[0-9]+") ) && !stop_words.contains(text) )
				{
					Word w = new Word(text, 1.0);
					tweet.add(w);
				}
			}
			
			tweet.init(K);
			
			//first infer the topics of this tweet
			for(Word word : tweet.words)
			{
				int z = sample(p, tweet, word);
				word.z = z;
				tweet.includeTopic(z, word.getWeight());
			}
			
			for(int i=0;i<5;i++)
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
			System.out.println(String.format("\t%s\t%s\t%d\t%f\t%e", tweet.content, (new Timestamp(tweet.time)).toString(), tweet.time, log_mle, perplexity));
		}
		br.close();
	}
	
	public static void main(String [] args) throws Exception
	{
		//first load the paramters of the learned file
		PosixParser parser = new PosixParser();
		initOptions();
		CommandLine cli = parser.parse(options, args);
		
		StreamSearch model = new StreamSearch(cli);
		String line;
		BufferedReader br;
		
		br = new BufferedReader(new FileReader(String.format("%s/data/tweets/%s_K=%d.lda", model.prefix_dir, model.expt_name, model.K)));
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
		
		//initialize
		model.init();
		model.search();
	}
}
