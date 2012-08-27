package lda;

public class Word 
{
	public int z;
	public String text;
	
	private int count;
	private double weight;
	
	public Word(String text, double weight)
	{
		this.count = 1;
		this.text = text;
		this.weight = weight;
	}
	
	public double getWeight()
	{
		return weight * count;
	}
	
	public void increment()
	{
		count++;
	}
}