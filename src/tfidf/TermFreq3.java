package tfidf;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TermFreq3 
{
	public static void main(String [] args) throws Exception
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = br.readLine();
		String [] tokens = line.split("\t");
		
		String cur_doc = tokens[0];
		String cur_word = tokens[1];
		long total_words = Long.parseLong(tokens[2]);
		
		assert cur_word.equals("!NUM_WORDS");
		String doc, word;
		while((line=br.readLine())!=null)
		{
			tokens = line.split("\t");
			doc = tokens[0];
			if(doc.equals(cur_doc))
			{
				word = tokens[1];
				long term_count = Long.parseLong(tokens[2]);
				double tf = (double) term_count / (double) total_words;
				System.out.println(String.format("%s\t%s\t%.3f", word, cur_doc, tf));
			}
			else
			{
				assert tokens[1].equals("!NUM_WORDS");
				total_words = Long.parseLong(tokens[2]);
				cur_doc = doc;
			}
		}
		
		/*
		String cur_key = tokens[1];
		System.out.println(String.format("%s\t%s\t%s", cur_key, tokens[0], tokens[2]));
		long count = Long.parseLong(tokens[2]);
		
		String key;
		
		while((line=br.readLine())!=null)
		{
			tokens = line.split("\t");
			key = tokens[1];
			if(key.equals(cur_key))
			{
				count += Long.parseLong(tokens[2]);
			}
			else
			{
				//print out <doc_id, word, count>
				System.out.println(String.format("%s\t!NUM_WORDS\t%d", cur_key, count));
				cur_key = key;
				count = Long.parseLong(tokens[2]);
			}
			System.out.println(String.format("%s\t%s\t%s", cur_key, tokens[0], tokens[2]));
		}
		System.out.println(String.format("%s\t!NUM_WORDS\t%d", cur_key, count));
		*/
	}
}
