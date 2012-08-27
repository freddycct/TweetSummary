import java.io.BufferedReader;
import java.io.InputStreamReader;

public class NPSignalAnalysis 
{
	public static void main(String [] args) throws Exception 
	{
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while((line=br.readLine())!=null)
		{
			String [] tokens = line.split("\t");
			long status_id = Long.parseLong(tokens[0]);
			long user_id = Long.parseLong(tokens[1]);
			long time = Long.parseLong(tokens[3]);
			
			while((line = br.readLine()).length() > 0)
			{
				//NP <tab> topic
				tokens = line.split("\t");
				tokens = tokens[0].split("[ ]+");
				for(String text : tokens)
				{
					System.out.println(String.format("%d\t%d\t%d\t%s", status_id, user_id, time, text));
				}
			}
		}
		br.close();
	}
}
