import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Reduce 
{
	public static void main(String [] args) throws Exception
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String current = br.readLine();
		int n = 1;
		String line;
		while((line=br.readLine())!=null)
		{
			if(current.equals(line))
			{
				n++;
			}
			else
			{
				System.out.println(String.format("%d\t%s", n, current));
				current = line;
				n=1;
			}
		}
		br.close();
		System.out.println(String.format("%d\t%s", n, current));
	}
}
