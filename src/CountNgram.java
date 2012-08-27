import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map.Entry;

public class CountNgram 
{
	public static HashMap<String, Integer> readGrams(String file) throws Exception
	{
		HashMap<String, Integer> doc = new HashMap<String, Integer>();
		String line;
		BufferedReader br = new BufferedReader(new FileReader(file));
		while((line=br.readLine())!=null)
		{
			String [] tokens = line.split("\t");
			int n = Integer.parseInt(tokens[0]);
			String gram = tokens[1];
			doc.put(gram, n);
		}
		br.close();
		return doc;
	}
	
	public static int countLines(String file) throws Exception
	{
		int n = 0;
		BufferedReader br = new BufferedReader(new FileReader(file));
		while(br.readLine() != null)
		{
			n++;
		}
		br.close();
		return n;
	}
	
	public static int match(HashMap<String, Integer> ref_doc, HashMap<String, Integer> cand_doc)
	{
		int numerator = 0;
		for(Entry<String, Integer> entry : cand_doc.entrySet())
		{
			numerator += match(ref_doc, entry);
		}
		return numerator;
	}
	
	public static int match(HashMap<String, Integer> ref_doc, Entry<String, Integer> entry)
	{
		String key = entry.getKey();
		if(!ref_doc.containsKey(key))
			return 0;
		else
		{
			//System.out.println(key);
			return Math.min(ref_doc.get(key), entry.getValue());
		}
	}
	
	public static void main(String [] args) throws Exception
	{
		String cand_file = args[0];
		String ref_file  = args[1];
		
		
		HashMap<String, Integer> cand_doc = readGrams(cand_file);
		HashMap<String, Integer> ref_doc  = readGrams(ref_file);
		
		//int num_lines = countLines(cand_file);
		
		/*
		int ref_denominator = 0;
		for(Entry<String, Integer> entry : ref_doc.entrySet())
		{
			ref_denominator += entry.getValue();
		}
		int denominator = 0;
		*/
		
		int numerator = match(ref_doc, cand_doc);
		
		/*
		for(Entry<String, Integer> entry : cand_doc.entrySet())
		{
			numerator   += match(ref_doc, entry);
			//denominator += entry.getValue();
		}
		*/
		
		//double pn = (double)numerator / (double)num_lines;
		//System.out.println(String.format("%f", pn));
		
		System.out.println(numerator);
		
		//double pn = (double)numerator / (double)denominator;
		//double cn = (double)numerator / (double)ref_denominator;
		
		//System.out.println(String.format("%f\t%f", pn, cn));
	}
}
