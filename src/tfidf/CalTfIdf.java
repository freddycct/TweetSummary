package tfidf;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CalTfIdf 
{
	public static void main(String [] args) throws Exception
	{
		long num_docs = Long.parseLong(args[0]);
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		String line = br.readLine();
		String [] tokens = line.split("\t");
		
		String cur_word = tokens[0];
		assert tokens[1].equals("!DOC_COUNT");
		long doc_count = Long.parseLong(tokens[2]);
		double idf = Math.log((double)num_docs / (double)doc_count);
		
		String word, doc;
		while((line=br.readLine())!=null)
		{
			tokens = line.split("\t");
			word = tokens[0];
			doc = tokens[1];
			if(word.equals(cur_word))
			{
				assert !doc.equals("!DOC_COUNT");
				double tf = Double.parseDouble(tokens[2]);
				double tfidf = tf * idf;
				System.out.println(String.format("%s\t%s\t%.3f", word, doc, tfidf));
			}
			else
			{
				assert doc.equals("!DOC_COUNT");
				doc_count = Long.parseLong(tokens[2]);
				idf = Math.log((double)num_docs / (double)doc_count);
				cur_word = word;
			}
		}
	}
}
