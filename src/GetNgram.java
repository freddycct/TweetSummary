import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedList;

public class GetNgram 
{
	public static LinkedList<String> getNgrams(String line, int N, HashSet<String> stop_words)
	{
		LinkedList<String> ngrams = new LinkedList<String>();
		String [] tokens = line.split("[ ]+");
		for(int i = 0; i < tokens.length - N + 1; i++)
		{
			String ngram = "";
			int j;
			for(j = i; j < i + N; j++)
			{
				if(tokens[j].matches("[A-Za-z0-9$%]+") && !stop_words.contains(tokens[j].toLowerCase()) && tokens[j].length() > 2)
				{
					ngram += tokens[j] + " ";
				}
				else
				{
					break;
				}
			}
			if(j == i+N)
			{
				ngrams.add(ngram.trim().toLowerCase());
			}
		}
		return ngrams;
	}
	
	public static void main(String [] args) throws Exception
	{
		HashSet<String> stop_words = new HashSet<String>();
		
		int N = Integer.parseInt(args[0]);
		String line;
		BufferedReader br;
		br = new BufferedReader(new FileReader("jar/smart-common-words.txt"));
		line = br.readLine();
		String [] tokens = line.split(",");
		for(int i=0;i<tokens.length;i++)
		{
			stop_words.add(tokens[i]);
		}
		br.close();
		
		br = new BufferedReader(new InputStreamReader(System.in));
		while((line=br.readLine())!=null)
		{
			//this is a sentence
			LinkedList<String> list = getNgrams(line, N, stop_words);
			for(String ngram : list)
			{
				System.out.println(ngram);
			}
		}
		br.close();
	}
}
