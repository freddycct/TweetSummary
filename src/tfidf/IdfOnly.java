package tfidf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class IdfOnly 
{
	public static void increment(HashMap<String, Integer> doc_count, String w)
	{
		if(doc_count.containsKey(w))
		{
			doc_count.put(w, doc_count.get(w) + 1);
		}
		else
		{
			doc_count.put(w, 1);
		}
	}
	
	public static void main(String [] args) throws IOException
	{
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		br.readLine(); //remove headers
		
		int num_docs = 0;
		HashMap<String, Integer> doc_count = new HashMap<String, Integer>();
		
		while((line=br.readLine())!=null)
		{
			num_docs++;
			String [] tokens = line.split("\t");
			String content   = tokens[2];
			//this is a document
			content = content.toLowerCase().trim();
			tokens = content.split("[ \t\n\r\f]+");
			
			HashSet<String> words = new HashSet<String>();
			
			for(String w : tokens)
			{
				//print out <word status_id>
				if(w.length() > 0)
				{
					if(!words.contains(w))
					{
						words.add(w);
						increment(doc_count, w);
					}
				}
			}
		}
		br.close();
		
		for(Entry<String, Integer> entry : doc_count.entrySet())
		{
			System.out.println(String.format("%s\t%f", entry.getKey(), Math.log((double)num_docs/(double)entry.getValue())));
		}
	}
}
