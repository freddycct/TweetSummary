package lda;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.HashSet;

public class DocComp 
{
	public static int addWord(HashMap<String, Integer> word_index, String word)
	{
		if(!word_index.containsKey(word))
		{
			word_index.put(word, word_index.size());
		}
		return word_index.get(word);
	}
	
	public static void addWordtoDoc(HashMap<String, Integer> doc, String word)
	{
		if(doc.containsKey(word))
		{
			doc.put(word, doc.get(word) + 1);
		}
		else
		{
			doc.put(word, 1);
		}
	}
	
	public static double norm(HashMap<String, Integer> doc)
	{
		double norm = 0;
		for(Integer value : doc.values())
		{
			norm += value * value;
		}
		return Math.sqrt(norm);
	}
	
	public static double get(HashMap<String, Integer> doc, String w)
	{
		if(doc.containsKey(w))
			return (double)doc.get(w);
		else
			return 0.0;
	}
	
	public static double cosDist(HashMap<String, Integer> word_index, HashMap<String, Integer> wiki_doc, HashMap<String, Integer> summary_doc)
	{
		double norm_wiki    = norm(wiki_doc);
		double norm_summary = norm(summary_doc);
		
		double dot = 0;
		for(String w : word_index.keySet())
		{
			dot += get(wiki_doc, w) * get(summary_doc, w);
		}
		
		return dot/(norm_wiki * norm_summary);
	}
	
	public static void main(String [] args) throws Exception
	{
		String wikifile     = args[0];
		String summaryfile  = args[1];
		String ignore_words = args[2];
		String stopfile     = "jar/smart-common-words.txt";
		
		HashSet<String> stop_words = new HashSet<String>();
		HashMap<String, Integer> word_index  = new HashMap<String, Integer>();
		
		HashMap<String, Integer> wiki_doc    = new HashMap<String, Integer>();
		HashMap<String, Integer> summary_doc = new HashMap<String, Integer>();
		
		String line;
		
		//Read in Stop Words
		BufferedReader br = new BufferedReader(new FileReader(stopfile));
		line = br.readLine();
		String [] tokens = line.split(",");
		for(int i=0;i<tokens.length;i++)
		{
			stop_words.add(tokens[i]);
		}
		br.close();
		
		tokens = ignore_words.split("[ \t\n\r\f]+");
		for(String w : tokens)
		{
			stop_words.add(w.toLowerCase());
		}
		//End of Reading Stop Words
		
		br = new BufferedReader(new FileReader(wikifile));
		while((line=br.readLine())!=null)
		{
			tokens = line.split("[ ]+");
			for(String w : tokens)
			{
				if(!stop_words.contains(w))
				{
					addWord(word_index, w.toLowerCase());
					addWordtoDoc(wiki_doc, w.toLowerCase());
				}
			}
		}
		br.close();
		
		br = new BufferedReader(new FileReader(summaryfile));
		while((line=br.readLine())!=null)
		{
			//each line is a tweet
			tokens = line.split("\t");
			String content = tokens[1];
			content = content.replaceAll("[\\[\\]\\;',./`=\\-~!@#\\^&*()_+{}|:\"<>?]+", " ");
			tokens  = content.split("[ ]+");
			for(String w : tokens)
			{
				if(!stop_words.contains(w))
				{
					addWord(word_index, w.toLowerCase());
					addWordtoDoc(summary_doc, w.toLowerCase());
				}
			}
		}
		br.close();
		
		double cosine_distance = cosDist(word_index, wiki_doc, summary_doc);
		System.out.println(cosine_distance);
	}
}
