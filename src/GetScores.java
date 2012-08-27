import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class GetScores 
{
	public static int countLines(String prefix_dir, String event, String model, int K, double kappa) throws Exception
	{
		BufferedReader br = new BufferedReader(new FileReader(String.format(
				"%s/results/%s/K=%d/%s/kappa=%1.1f/summary.txt", prefix_dir, event, K, model, kappa)));
		int n=0;
		//String line;
		//while((line=br.readLine())!=null)
		while(br.readLine()!=null)
		{
			n++;
		}
		return n;
	}
	
	public static void main(String [] args) throws Exception
	{
		String prefix_dir = args[0];
		int K = Integer.parseInt(args[1]);
		String event = args[2];
		
		String [] models = { "lda", "np_lda", "decay_lda", "gauss_lda" };
		BufferedWriter [] br_models = new BufferedWriter[models.length];
		
		for(int i=0;i<models.length;i++)
		{
			br_models[i] = new BufferedWriter(new FileWriter(String.format("results/%s/K=%d/score_%s.txt", event, K, models[i])));
		}
		
		/*
		for(double kappa=0.1;kappa<=0.9;kappa+=0.1)
		{
			BufferedReader br = new BufferedReader(new FileReader(String.format("%s/results/%s/K=%d/kappa=%1.1f_bleu.txt", 
					prefix_dir, event, K, kappa)));
			for(int i=0;i<models.length;i++)
			{
				String [] tokens = br.readLine().split("\t");
				double prec   = Double.parseDouble(tokens[0]);
				double recall = Double.parseDouble(tokens[1]);
				int lines   = countLines(prefix_dir, event, models[i], K, kappa);
				br_models[i].write(String.format("%1.1f\t%d\t%f\t%f", kappa, lines, prec, recall));
				br_models[i].newLine();
			}
			br.close();
		}
		*/
		
		BufferedReader br = new BufferedReader(new FileReader(String.format("%s/results/%s/K=%d/coherent_bleu.txt", prefix_dir, event, K)));
		for(int i=0;i<models.length;i++)
		{
			double prec   = Double.parseDouble(br.readLine());
			//String [] tokens = br.readLine().split("\t");
			//double prec   = Double.parseDouble(tokens[0]);
			//double recall = Double.parseDouble(tokens[1]);
			//br_models[i].write(String.format("%f\t%f", prec, recall));
			br_models[i].write(String.format("%f", prec));
			br_models[i].newLine();
		}
		br.close();
		
		for(int i=0;i<models.length;i++)
		{
			br_models[i].close();
		}
	}
}
