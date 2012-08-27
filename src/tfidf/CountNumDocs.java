package tfidf;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CountNumDocs 
{
	public static void main(String [] args) throws Exception
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = br.readLine();
		String [] tokens = line.split("\t");
		String cur_doc = tokens[0];
		long num_doc = 1;
		String doc;
		while((line=br.readLine())!=null)
		{
			tokens = line.split("\t");
			doc = tokens[0];
			if(doc.equals(cur_doc))
			{
				continue;
			}
			else
			{
				cur_doc = doc;
				num_doc++;
			}
		}
		System.out.println(num_doc);
	}
}
