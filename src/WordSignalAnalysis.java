import java.io.BufferedReader;
import java.io.InputStreamReader;

public class WordSignalAnalysis 
{
	public static void main(String [] args) throws Exception 
	{
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		int n = 1;
		while((line=br.readLine())!=null)
		{
			String [] tokens = line.split("\t");
			long time = Long.parseLong(tokens[4]);
			tokens = tokens[2].trim().toLowerCase().split("[ \t\n\r\f]+");
			
			for(int i=0;i<tokens.length;i++)
			{
				//System.out.println(String.format("%d\t%d\t%s", n, time, tokens[i]));
				System.out.println(String.format("%d\t%s", time, tokens[i]));
			}
			n++;
		}
		br.close();
	}
}
