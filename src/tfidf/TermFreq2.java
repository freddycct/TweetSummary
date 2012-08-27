package tfidf;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TermFreq2 
{
	//Reduce Function of a Map-Reduce Task, aggregates all the keys
	public static void main(String [] args) throws Exception
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));	
		String line = br.readLine();
		String [] tokens = line.split("\t");
		
		String cur_doc = tokens[0];
		String cur_word = tokens[1];
		
		long num_words = 1;
		long word_cnt  = 1;
		
		String doc, word;
		
		while((line=br.readLine())!=null)
		{
			tokens = line.split("\t");
			doc = tokens[0];
			word = tokens[1];
			
			if(doc.equals(cur_doc))
			{
				num_words++;
				if(word.equals(cur_word))
				{
					word_cnt++;
				}
				else
				{
					System.out.println(String.format("%s\t%s\t%d", cur_doc, cur_word, word_cnt));
					word_cnt = 1;
					cur_word = word;
				}
			}
			else
			{
				System.out.println(String.format("%s\t%s\t%d", cur_doc, cur_word, word_cnt));
				word_cnt = 1;
				cur_word = word;
				System.out.println(String.format("%s\t!NUM_WORDS\t%d", cur_doc, num_words));
				num_words = 1;
				cur_doc = doc;
			}
		}
		System.out.println(String.format("%s\t%s\t%d", cur_doc, cur_word, word_cnt));
		System.out.println(String.format("%s\t!NUM_WORDS\t%d", cur_doc, num_words));
		/*
		int word_count = 1;
		int key1_count = 1;
		int key2_count = 1;
		String cur_key1 = tokens[0];
		String cur_key2 = tokens[1];
		String key1, key2;
		
		while((line=br.readLine())!=null)
		{
			tokens = line.split("\t");
			key1 = tokens[0];
			key2 = tokens[1];
			
			if(cur_key1.equals(key1))
			{
				word_count++;
				if(cur_key2.equals(key2))
				{
					//current document continue to have this word
					key2_count++;
				}
				else
				{
					//a new document has this word
					System.out.println(String.format("%s\t%s\t%d", cur_key1, cur_key2, key2_count));
					key1_count++;
					key2_count = 1;
					cur_key2 = key2;
				}
			}
			else
			{
				//a new word & a new document
				System.out.println(String.format("%s\t%s\t%d", cur_key1, cur_key2, key2_count));
				System.out.println(String.format("%s\t!DOC_COUNT\t%d", cur_key1, key1_count));
				System.out.println(String.format("%s\t!WORD_COUNT\t%d", cur_key1, word_count));
				word_count = 1;
				key1_count = 1;
				key2_count = 1;
				cur_key1 = key1;
				cur_key2 = key2;
			}
		}
		System.out.println(String.format("%s\t%s\t%d", cur_key1, cur_key2, key2_count));
		System.out.println(String.format("%s\t!DOC_COUNT\t%d", cur_key1, key1_count));
		System.out.println(String.format("%s\t!WORD_COUNT\t%d", cur_key1, word_count));
		*/
	}
}
