import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CalculatePrecRecall 
{
	public static void append(HashMap<String, Integer> doc, LinkedList<String> ngrams)
	{
		for(String ngram : ngrams)
		{
			if(doc.containsKey(ngram))
			{
				doc.put(ngram, doc.get(ngram) + 1);
			}
			else
			{
				doc.put(ngram, 1);
			}
		}
	}
	
	public static HashMap<String, Integer> aggregate(LinkedList<String> ngrams)
	{
		HashMap<String, Integer> aggregated = new HashMap<String, Integer>();
		append(aggregated, ngrams);
		return aggregated;
	}
	
	public static int actual(HashMap<String, Integer> doc)
	{
		int tmp = 0;
		for(int w : doc.values())
		{
			tmp += w;
		}
		return tmp;
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
		{
			return 0;
		}
		else
		{
			int num_ref = ref_doc.get(key);
			int match = Math.min(ref_doc.get(key), entry.getValue());
			if(num_ref > match)
			{
				ref_doc.put(key,  num_ref - match);
			}
			else
			{
				ref_doc.remove(key);
			}
			return match;
		}
	}
	
	public static void main(String [] args) throws Exception
	{
		//read in stop words
		HashSet<String> stop_words = new HashSet<String>();
		BufferedReader br = new BufferedReader(new FileReader("jar/smart-common-words.txt"));
		String line = br.readLine();
		String [] tokens = line.split(",");
		for(int i=0;i<tokens.length;i++)
		{
			stop_words.add(tokens[i]);
		}
		br.close();
		
		//HashMap<String, Integer> ref_1grams = CountNgram.readGrams("/mnt/freddy/data/tweets/sfbay_facebook_ipo.1grams");
		//HashMap<String, Integer> ref_2grams = CountNgram.readGrams("/mnt/freddy/data/tweets/sfbay_facebook_ipo.2grams");
		//HashMap<String, Integer> ref_3grams = CountNgram.readGrams("/mnt/freddy/data/tweets/sfbay_facebook_ipo.3grams");
		HashMap<String, Integer> ref_1grams = CountNgram.readGrams("testset/facebook_ipo.wiki.1grams");
		HashMap<String, Integer> ref_2grams = CountNgram.readGrams("testset/facebook_ipo.wiki.2grams");
		HashMap<String, Integer> ref_3grams = CountNgram.readGrams("testset/facebook_ipo.wiki.3grams");
		
		int [] actual    = new int[3];
		int [] tp        = new int[3];
		int [] predicted = new int[3];
		
		actual[0] = actual(ref_1grams);
		actual[1] = actual(ref_2grams);
		actual[2] = actual(ref_3grams);
		
		for(int n=0;n<3;n++)
		{
			tp[n] = 0;
			predicted[n] = 0;
		}
		
		br = new BufferedReader(new InputStreamReader(System.in));
		int c = 0;
		while((line=br.readLine())!=null)
		{
			c++;
			tokens = line.split("\t");
			String content = tokens[1];
			//now find the 1, 2, 3 grams in this line
			
			LinkedList<String> cand_1grams_list = GetNgram.getNgrams(content, 1, stop_words);
			LinkedList<String> cand_2grams_list = GetNgram.getNgrams(content, 2, stop_words);
			LinkedList<String> cand_3grams_list = GetNgram.getNgrams(content, 3, stop_words);
			
			HashMap<String, Integer> cand_1grams = aggregate(cand_1grams_list);
			HashMap<String, Integer> cand_2grams = aggregate(cand_2grams_list);
			HashMap<String, Integer> cand_3grams = aggregate(cand_3grams_list);
			
			//after matching subtract from the ref_ngrams
			
			tp[0] += match(ref_1grams, cand_1grams);
			tp[1] += match(ref_2grams, cand_2grams);
			tp[2] += match(ref_3grams, cand_3grams);
			
			predicted[0] += cand_1grams_list.size();
			predicted[1] += cand_2grams_list.size();
			predicted[2] += cand_3grams_list.size();
			
			for(int n = 0; n < 3; n++)
			{
				double recall             = (double) tp[n] / (double) actual[n];
				double precision          = (double) tp[n] / ((double) predicted[n] + 1e-22);
				double modified_precision = (double) tp[n] / (double) c;
				System.out.print(String.format("%e\t%e\t%e\t", precision, modified_precision, recall));
			}
			System.out.println();
		}
		br.close();
	}
}
