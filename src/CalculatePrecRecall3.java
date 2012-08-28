import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CalculatePrecRecall3 
{
	public static void main(String [] args) throws Exception
	{
		int n = 0;
		String line;
		String prev = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while((line=br.readLine())!=null)
		{
			if((n % 1000) == 0)
			{
				System.out.println(line);
			}
			n++;
			prev = line;
		}
		br.close();
		System.out.println(prev);
	}
}
