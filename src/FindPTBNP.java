import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class FindPTBNP 
{
	public static void main(String [] args) throws Exception
	{
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		PTBLexer ptb = new PTBLexer();
		while((line=br.readLine())!=null)
		{
			ptb.setString(line);
			List<String> nps = ptb.lex();
			for(String np : nps)
			{
				System.out.println(np.toLowerCase());
			}
		}
		br.close();
	}
}
