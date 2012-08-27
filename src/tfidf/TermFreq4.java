package tfidf;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TermFreq4 
{
	public static void main(String [] args) throws Exception
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = br.readLine();
		String [] tokens = line.split("\t");
		
		String cur_word = tokens[0];
		String word;
		long word_in_docs = 1;
		while((line=br.readLine())!=null)
		{
			System.out.println(line);
			tokens = line.split("\t");
			word = tokens[0];
			
			if(cur_word.equals(word))
			{
				word_in_docs++;
			}
			else
			{
				//a new word & a new document
				System.out.println(String.format("%s\t!DOC_COUNT\t%d", cur_word, word_in_docs));
				word_in_docs = 1;
				cur_word = word;
			}
		}
		System.out.println(String.format("%s\t!DOC_COUNT\t%d", cur_word, word_in_docs));
	}
}
