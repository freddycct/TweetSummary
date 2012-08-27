package lda;

import java.sql.Timestamp;
import java.util.HashSet;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class SummarizeEvent 
{
	public static double klDivergence(double [] p, double [] q)
	{
		int N = p.length;
		double div = 0;
		for(int n=0;n<N;n++)
		{
			div += p[n] * (Math.log(p[n]) - Math.log(q[n]));
		}
		return div;
	}
	
	public static double informationGain(HashSet<Tweet> selected_tweets, Tweet tweet)
	{
		double min_ig = 1.0;
		for(Tweet t : selected_tweets)
		{
			double ig = klDivergence(t.theta, tweet.theta);
			if(ig < min_ig)
			{
				min_ig = ig;
			}
		}
		return min_ig;
	}
	
	public static Tweet parseLine(String line)
	{
		String [] tokens = line.split("\t");
		Tweet tweet = new Tweet();
		tweet.content = tokens[1];
		tweet.time = Long.parseLong(tokens[3]);
		tokens = tokens[0].split(",");
		tweet.theta = new double[tokens.length];
		double sum = 0;
		for(int k=0;k<tokens.length;k++)
		{
			tweet.theta[k] = Double.parseDouble(tokens[k]);
			sum += tweet.theta[k];
		}
		
		//Re-normalize again due to precision error...
		for(int k=0;k<tokens.length;k++)
		{
			tweet.theta[k] = tweet.theta[k]/sum; 
		}
		return tweet;
	}
	
	public static void main(String [] args) throws Exception
	{
		double kappa = Double.parseDouble(args[0]);
		
		HashSet<Tweet> selected_tweets = new HashSet<Tweet>();
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while((line=br.readLine())!=null)
		{
			Tweet tweet = parseLine(line);
			double ig = informationGain(selected_tweets, tweet);
			if(ig > kappa)
			{
				selected_tweets.add(tweet);
			}
		}
		br.close();
		
		for(Tweet tweet : selected_tweets)
		{
			System.out.print(String.format("%1.4f", tweet.theta[0]));
			for(int k=1;k<tweet.theta.length;k++)
			{
				System.out.print(String.format(",%1.4f", tweet.theta[k]));
			}
			System.out.println(String.format("\t%s\t%s\t%d", tweet.content, (new Timestamp(tweet.time)).toString(), tweet.time));
		}
	}
}
