package lda;

import java.util.HashMap;

public class Topic implements Comparable<Topic>
{
	public int z;
	public double sum_time;
	public double sum_phi;
	public HashMap<String, Double> phi;
	
	public Topic(int z)
	{
		this.z = z;
		phi = new HashMap<String, Double>();
		sum_phi = 0;
		sum_time = 0;
	}
	
	public double getMean()
	{
		return sum_time/sum_phi;
	}
	
	public double get(String key)
	{
		if(phi.containsKey(key))
			return phi.get(key);
		else
			return 0;
	}
	
	public void includeWord(Word w)
	{
		String key = w.text;
		double weight = 0;
		if(phi.containsKey(key))
		{
			weight = phi.get(key);
		}
		else
		{
			weight = 0;
		}
		phi.put(key, weight + w.getWeight());
		sum_phi += w.getWeight();
	}
		
	public void excludeWord(Word w)
	{
		String key = w.text;
		double weight = phi.get(key);
		phi.put(key,  weight - w.getWeight());
		sum_phi -= w.getWeight();
	}

	@Override
	public int compareTo(Topic arg0) 
	{
		return Double.compare(getMean(), arg0.getMean());
	}
}
