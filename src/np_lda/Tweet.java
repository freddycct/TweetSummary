package np_lda;

import java.util.HashSet;
import java.util.LinkedList;

public class Tweet extends lda.Tweet 
{
	public LinkedList<NounPhrase> nps;
	
	public Tweet()
	{
		sum_weight = 0;
		nps = new LinkedList<NounPhrase>();
	}
	
	public void addNP(NounPhrase np)
	{
		nps.add(np);
		sum_weight += np.getWeight();
	}
	
	public NounPhrase add(String text, HashSet<String> stop_words)
	{
		NounPhrase np = new NounPhrase();
		np.text = text;
		String [] tokens  = text.split("[ ]+");
		
		for(String w : tokens)
		{
			if( ( (w.matches("[$A-Za-z0-9%]+") && w.length() > 2) || w.matches("[0-9]+") ) && !stop_words.contains(w) )
			{
				np.addWord(w, 1.0);
			}
		}
		if(np.getWeight() > 0)
		{
			addNP(np);					
		}
		return np;
	}
	
	public NounPhrase add(String text, HashSet<String> stop_words, HashSet<String> vocabulary)
	{
		NounPhrase np = new NounPhrase();
		np.text = text;
		String [] tokens  = text.split("[ ]+");
		
		for(String w : tokens)
		{
			if( ( (w.matches("[$A-Za-z0-9%]+") && w.length() > 2) || w.matches("[0-9]+") ) && !stop_words.contains(w) )
			{
				np.addWord(w, 1.0);
				vocabulary.add(w);
			}
		}
		if(np.getWeight() > 0)
		{
			addNP(np);					
		}
		
		return np;
	}
}
