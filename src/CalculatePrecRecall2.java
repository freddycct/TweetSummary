import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CalculatePrecRecall2 
{
	public static void main(String [] args) throws Exception
	{
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while((line=br.readLine())!=null)
		{
			String [] tokens = line.split("\t");
			double precision = 0;
			double recall = 0;
			for(int i=0;i<3;i++)
			{
				precision += Bleu.weights[i] * Double.parseDouble(tokens[2*i]);
				recall    += Bleu.weights[i] * Double.parseDouble(tokens[2*i + 1]); 
			}
			System.out.println(String.format("%e\t%e", precision, recall));
		}
		br.close();
	}
}
