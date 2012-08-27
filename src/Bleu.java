import java.io.BufferedReader;
import java.io.FileReader;

public class Bleu 
{
	public static final double [] weights = { 0.2, 0.3, 0.5 };
	
	public static void main(String [] args) throws Exception
	{
		String prefix_dir = args[0];
		String event = args[1];
		int K = Integer.parseInt(args[2]);
		//String kappa = args[3];
		
		double [] prec   = new double[4];
		//double [] recall = new double[4];
		
		for(int i=0;i<4;i++)
		{
			prec[i]   = 0.0;
			//recall[i] = 0.0;
		}
		
		double [] weights = { 0.2, 0.3, 0.5 };
		//double [] weights = { 0.5, 0.5 };
		
		for(int n=1;n<4;n++)
		{
			//BufferedReader br = new BufferedReader(new FileReader(String.format("%s/results/%s/K=%d/kappa=%s_%dgrams.txt", prefix_dir, event, K, kappa, n)));
			BufferedReader br = new BufferedReader(new FileReader(String.format("%s/results/%s/K=%d/coherent_%dgrams.txt", prefix_dir, event, K, n)));
			for(int i=0;i<4;i++)
			{
				prec[i] += weights[n-1] * Integer.parseInt(br.readLine());
				//String [] tokens = br.readLine().split("\t");
				//prec[i]   += weights[n-1] * Math.log(Double.parseDouble(tokens[0]));
				//recall[i] += weights[n-1] * Math.log(Double.parseDouble(tokens[1])); 
			}
			br.close();
		}
		for(int i=0;i<4;i++)
		{
			//prec[i]   = Math.exp(prec[i]);
			//recall[i] = Math.exp(recall[i]);
			//System.out.println(String.format("%f\t%f", prec[i], recall[i]));
			System.out.println(String.format("%f", prec[i]));
		}
	}
}
