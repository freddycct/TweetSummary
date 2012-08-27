import java.util.HashSet;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class RemoveDuplicates 
{
	public static void main(String [] args) throws Exception
	{
		HashSet<String> existing_tweets = new HashSet<String>();
		
		String line;
		BufferedReader br = new BufferedReader(new FileReader(args[0]));
		while((line=br.readLine())!=null)
		{
			String [] tokens = line.split("\t");
			existing_tweets.add(tokens[2]);
		}
		br.close();
		
		br = new BufferedReader(new InputStreamReader(System.in));
		while((line=br.readLine())!=null)
		{
			String [] tokens = line.split("\t");
			if(!existing_tweets.contains(tokens[1]))
			{
				System.out.println(line);
			}
		}
		br.close();
	}
}
