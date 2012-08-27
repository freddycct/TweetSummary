package tfidf;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HtTermFreq1 
{
	//Map function of the Map-Reduce Task, generate the keys
	public static void main(String [] args) throws Exception
	{
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		br.readLine(); //remove headers
		while((line=br.readLine())!=null)
		{
			String [] tokens = line.split("\t");
			if(tokens.length == 5)
			{
				String hashtag = tokens[4];
				String content = tokens[2];
				//Tokenize the tweets now using Brendan's tokenizer

				//List<String> toks = Twokenize.tokenizeForTagger_J(content);
				content = content.toLowerCase().trim();
				tokens  = content.split("[ \t\n\r\f]+");
				for(String w : tokens)
				{
					//print out <word status_id>
					if(w.length() > 0)
						System.out.println(String.format("%s\t%s", hashtag, w));
				}
			}
		}
		br.close();
	}
}
