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
			double modified_precision = 0;
			double recall = 0;
			for(int i=0;i<3;i++)
			{
				precision          += Bleu.weights[i] * Double.parseDouble(tokens[3*i]);
				modified_precision += Bleu.weights[i] * Double.parseDouble(tokens[3*i + 1]);
				recall             += Bleu.weights[i] * Double.parseDouble(tokens[3*i + 2]); 
			}
			System.out.println(String.format("%e\t%e\t%e", precision, modified_precision, recall));
		}
		br.close();
	}
}
