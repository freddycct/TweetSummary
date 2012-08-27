package lda;

import java.util.LinkedList;

public class Tweet implements Comparable<Tweet>
{
	public long status_id;
	public long time;
	public int user_id;
	public String content;
	public double sum_weight;
	
	public double [] theta;
	public double sum_theta;
	
	protected LinkedList<Word> words;

	public Tweet()
	{
		sum_weight = 0;
		words = new LinkedList<Word>();
	}

	public void init(int K)
	{
		theta     = new double[K];
		for(int k=0;k<K;k++)
		{
			theta[k] = 0.0;
		}
		sum_theta = 0;
	}
	
	public void includeTopic(int z, double weight)
	{
		theta[z]  += weight;
		sum_theta += weight;
	}
	
	public void excludeTopic(int z, double weight)
	{
		theta[z]  -= weight;
		sum_theta -= weight;
	}

	public void add(Word word)
	{
		words.add(word);
		sum_weight += word.getWeight();
	}
	
	public double getWeight()
	{
		return sum_weight;
	}

	@Override
	public int compareTo(Tweet t)
	{
		if(time > t.time)
			return 1;
		else if(time < t.time)
			return -1;
		else
			return 0;
	}
	
	public double [] getTopicDistribution(int K)
	{
		double [] p = new double[K];
		double sum_p = 0;
		for(int k=0;k<K;k++)
		{
			p[k] = (theta[k] + InferModel.ALPHA) / (sum_theta + K * InferModel.ALPHA);
			sum_p += p[k];
		}
		
		for(int k=0;k<K;k++)
		{
			p[k] = p[k] / sum_p;
		}
		return p;
	}
}
