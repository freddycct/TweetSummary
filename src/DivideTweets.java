import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;


public class DivideTweets 
{
	public static void main(String [] args) throws Exception
	{
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		BufferedWriter bw1 = new BufferedWriter(new FileWriter(args[0]));
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(args[1]));
		
		for(int n=0; (line=br.readLine())!=null; n++)
		{
			String [] tokens = line.split("\t");
			if(tokens[1].startsWith("RT "))
			{
				bw2.write(String.format("%d", n+1));
				bw2.newLine();
			}
			else
			{
				bw1.write(String.format("%d", n+1));
				bw1.newLine();
			}
		}
		br.close();
		bw1.close();
		bw2.close();
	}
}
