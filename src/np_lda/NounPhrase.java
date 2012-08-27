package np_lda;

import java.util.HashMap;
import lda.Word;

public class NounPhrase 
{
	public int z;
	public double sum_weight;
	public String text;
	public HashMap<String, Word> words;
	
	public NounPhrase()
	{
		sum_weight = 0;
		words = new HashMap<String, Word>();
	}
	
	public void addWord(String text, double weight)
	{
		sum_weight += weight;
		if(words.containsKey(text))
		{
			words.get(text).increment();
		}
		else
		{
			words.put(text, new Word(text, weight));
		}
	}
	
	public double getWeight()
	{
		return sum_weight;
	}
}
