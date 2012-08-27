package gauss_lda;

import lda.Word;

public class Topic extends lda.Topic
{
	private double sum_time_sq;
	
	public Topic(int k)
	{
		super(k);
		sum_time_sq = 0;
	}
	
	public void includeWord(Word w, long time)
	{
		sum_time += w.getWeight() * time;
		sum_time_sq += w.getWeight() * Math.pow(time, 2);
		includeWord(w);
	}
	
	public double getVar()
	{
		return (sum_time_sq / sum_phi) - Math.pow(getMean(), 2);
	}
	
	public double getStd()
	{
		return Math.sqrt(getVar());
	}
	
	public void excludeWord(Word w, long time)
	{
		sum_time -= w.getWeight() * time;
		sum_time_sq -= w.getWeight() * Math.pow(time, 2);
		excludeWord(w);
	}
}
