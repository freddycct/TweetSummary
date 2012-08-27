import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;

public class GetDate 
{
	public static void main(String [] args) throws IOException
	{
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while(true)
		{
			line = br.readLine();
			if(line.matches("[0-9]+"))
			{
				System.out.println(new Timestamp(Long.parseLong(line)).toString());
			}
			else
			{
				System.out.println(Timestamp.valueOf(line).getTime());
			}
		}
	}
}
