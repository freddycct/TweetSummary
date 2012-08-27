package decay_lda;

public class Tweet extends np_lda.Tweet
{
	public Tweet prev, next;
	
	public double [] local_theta;
	public double sum_local_theta;

	public Tweet()
	{
		super();
	}

	public void init2(int K, Tweet prev)
	{
		init(K);
		local_theta = new double[K];
		for(int k=0;k<K;k++)
		{
			local_theta[k] = 0.0;
		}
		sum_local_theta = 0;
		
		this.prev = prev;
	}
	
	public void init(int K, Tweet prev)
	{
		init(K);
		local_theta = new double[K];
		for(int k=0;k<K;k++)
		{
			local_theta[k] = 0.0;
		}
		sum_local_theta = 0;
		
		this.prev = prev;
		if(prev!=null)
		{
			prev.next = this;
		}
	}
	
	public void includeTopic(int z, double weight)
	{
		local_theta[z]  += weight;
		sum_local_theta += weight;
		
		theta[z]  += weight;
		sum_theta += weight;
	}
	
	public void excludeTopic(int z, double weight)
	{
		local_theta[z]  -= weight;
		sum_local_theta -= weight;
		
		theta[z]  -= weight;
		sum_theta -= weight;
	}
	
	
	public double [] getTopicDistribution(int K, double [] decay)
	{
		double [] p = new double[K];
		double sum_p = 0;
		
		initTheta(K, decay);
		for(int k=0;k<K;k++)
		{
			//p[k] = (local_theta[k] + InferModel.ALPHA) / (sum_local_theta + K * InferModel.ALPHA);
			p[k] = (theta[k] + InferModel.ALPHA) / (sum_theta + K * InferModel.ALPHA);
			sum_p += p[k];
		}

		for(int k=0;k<K;k++)
		{
			p[k] = p[k] / sum_p;
		}
		return p;
	}
	

	public void initTheta(int K, double [] decay)
	{
		sum_theta = 0;
		for(int k=0;k<K;k++)
		{
			theta[k] = local_theta[k];
			if(prev != null)
			{
				theta[k] += prev.theta[k] * Math.exp(- decay[k] * (time - prev.time));
			}
			sum_theta += theta[k];
		}
	}
}
