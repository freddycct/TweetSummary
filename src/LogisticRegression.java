import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;

class Data
{
	int label;
	double [] features;
	String text;
	
	public Data(String line)
	{
		String [] tokens = line.split("\t");
		int D = tokens.length - 2;
		label = Integer.parseInt(tokens[0]);
		features = new double[D];
		for(int d=0;d<D;d++)
		{
			features[d] = Double.parseDouble(tokens[d+1]);
		}
		text = tokens[D+1];
	}
}

public class LogisticRegression 
{
	public static double calculateLikelihood(HashSet<Data> data_set, double [] etas, int D)
	{
		double likelihood = 0;
		for(Data data : data_set)
		{
			int y = data.label;
			double [] feature_vector = data.features;
			double numerator = 1;
			double denominator = 1;
			
			double denominator_exp = 0;
			for(int d = 0; d < D; d++)
			{
				denominator_exp += etas[d] * feature_vector[d];
			}
			denominator_exp = Math.exp(denominator_exp);
			
			if(y == 0)
			{
					numerator = denominator_exp;
			}
			
			denominator += denominator_exp;
			likelihood += Math.log(numerator) - Math.log(denominator);
		}
		return likelihood;
	}
	
	public static void main(String [] args) throws Exception
	{
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		HashSet<Data> data_set = new HashSet<Data>();
		double epsilon = 1e-4;
		Data dat = null;
		while((line=br.readLine())!=null)
		{
			dat = new Data(line);
			data_set.add(dat);
		}
		br.close();
		
		int D = dat.features.length;
		double [] etas = new double[D];
		
		for(int d=0;d<D;d++)
		{
			etas[d] = 0.0;
		}
		
		//training starts
		for(int i=0;i<100;i++)
		{
			for(Data data : data_set)
			{
				int y = data.label;
				double [] feature_vector = data.features;
				if(y == -1) continue;
				
				double denominator = 1;
				double denominator_exp = 0;
				for(int d=0;d<D;d++)
				{
					denominator_exp += etas[d] * feature_vector[d];
				}
				double label_cache = denominator_exp;
				denominator += Math.exp(denominator_exp);
				
				double numerator_exp = label_cache;
				double numerator = Math.exp(numerator_exp);
				double p = numerator/denominator;
				double delta = (double)y;

				for(int d=0;d<D;d++)
				{
					double x = feature_vector[d];
					if(x == 0) continue;
					double gradient = x * (delta - p);

					//System.out.println(gradient);
					
					denominator -= Math.exp(numerator_exp);
					numerator_exp -= etas[d] * x;

					etas[d] += epsilon * gradient;

					//update the cache
					numerator_exp += etas[d] * x;
					label_cache = numerator_exp;
					numerator = Math.exp(numerator_exp);
					denominator += numerator;
					p = numerator/denominator;
				}//end of for(String feature : features)
			}
			//System.out.println(calculateLikelihood(data_set, etas, D));
		}
		
		//classification time
		
		for(Data data : data_set)
		{
			double [] feature_vector = data.features;
			double numerator_exp = 0;
			for(int d=0;d<D;d++)
			{
				numerator_exp += etas[d] * feature_vector[d];
			}
			numerator_exp = Math.exp(numerator_exp);
			double p =  numerator_exp / (1+numerator_exp);
			
			System.out.println(String.format("%f\t%s", p, data.text));
		}
	}
}
