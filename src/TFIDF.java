import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

public class TFIDF 
{
	public static void increment(HashMap<String, Double> map, String key)
	{
		if(map.containsKey(key))
		{
			map.put(key, map.get(key) + 1.0);
		}
		else
		{
			map.put(key, 1.0);
		}
	}
	
	public static void main(String [] args) throws Exception 
	{
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		HashMap<String, Double> words_tf = new HashMap<String, Double>();
		HashMap<String, Double> words_idf = new HashMap<String, Double>();
		int n = 0;
		while((line=br.readLine())!=null)
		{
			String [] tokens = line.split("\t");
			tokens = tokens[2].trim().toLowerCase().split("[ \t\n\r\f]+");
			
			HashSet<String> words = new HashSet<String>();
			
			for(String w : tokens)
			{
				increment(words_tf, w);
				
				if(!words.contains(w))
				{
					words.add(w);
					increment(words_idf, w);
				}
			}
			n++;
		}
		br.close();
		
		for(Entry<String, Double> entry : words_idf.entrySet())
		{
			entry.setValue(Math.log((double)n / entry.getValue()));
				
			// Print out stuff 
			double tf    = words_tf.get(entry.getKey());
			double idf   = entry.getValue();
			double tfidf = tf * idf;
			System.out.println(String.format("%f\t%f\t%f\t%s", tf, idf, tfidf, entry.getKey())) ;
			// End print
		}
	}
}
