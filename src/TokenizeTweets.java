import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TokenizeTweets 
{
	public static void main(String [] args) throws Exception
	{
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		br.readLine(); //remove headers
		while((line=br.readLine())!=null)
		{
			String [] tokens = line.split("\t");
			//line.s
			String content = tokens[2];
			
			content = content.toLowerCase().trim();
			tokens = content.split("[ \t\n\r\f]+");
			for(String w : tokens)
			{
				if(w.length() > 0)
					System.out.println(w);
			}
		}
		br.close();
	}
}
