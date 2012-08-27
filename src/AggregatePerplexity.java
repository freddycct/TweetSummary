import java.io.BufferedReader;
import java.io.InputStreamReader;

public class AggregatePerplexity 
{
	public static void main(String [] args) throws Exception
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String line = br.readLine();
		String [] tokens = line.split("\t");
		int n = 1;
		String current_key = String.format("%s\t%s", tokens[1], tokens[2]);
		double mle = Double.parseDouble(tokens[3]); 
		double perplexity = Double.parseDouble(tokens[4]);
		
		while((line=br.readLine())!=null)
		{
			tokens = line.split("\t");
			
			String key = String.format("%s\t%s", tokens[1], tokens[2]);
			if(!current_key.equals(key))
			{
				System.out.println(String.format("%s\t%f\t%f", current_key, mle/n, perplexity));
				current_key = key;
				mle = Double.parseDouble(tokens[3]);
				perplexity = Double.parseDouble(tokens[4]);
				n = 1;
			}
			else
			{
				mle += Double.parseDouble(tokens[3]);
				perplexity += Double.parseDouble(tokens[4]);
				n++;
			}
		}
		System.out.println(String.format("%s\t%f\t%f", current_key, mle/n, perplexity));
		
		br.close();
	}
}
