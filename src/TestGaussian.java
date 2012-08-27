import org.apache.commons.math3.distribution.NormalDistribution;

public class TestGaussian 
{
	public static void main(String [] args)
	{
		double mean     = 1337215609117.184800;
		double variance = 82079460604218752.000000;
		NormalDistribution nd = new NormalDistribution(mean, Math.sqrt(variance));
		double t1 = nd.inverseCumulativeProbability(0.25);
		double diff = mean - t1;
		double t2 = mean + diff;
		System.out.println(String.format("%f, %f", t1, t2));
		double p = nd.cumulativeProbability(t1,  t2);
		System.out.println(p);
	}
}
