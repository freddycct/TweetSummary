import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Vector;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class WordHistogram 
{
	static long second = 1000;
	static long minute = 60 * second;
	static long hour   = 60 * minute;
	static long day    = 24 * hour; //86400000
	static long week   = 7  * day;
	static long month  = 4  * week;
	
	public static void increment(HashMap<Integer, Double> bin, int index)
	{
		if(bin.containsKey(index))
		{
			bin.put(index, bin.get(index) + 1.0);
		}
		else
		{
			bin.put(index, 1.0);
		}
	}
	
	public static void print(HashMap<Integer, Double> bin_counts, HashMap<Integer, Double> word_bin_counts, int num_bins, String current_word)
	{
		Vector<Double> normalized_values = new Vector<Double>();
		double sum = 0;
		for(Entry<Integer, Double> entry : word_bin_counts.entrySet())
		{
			double normalized = entry.getValue() / bin_counts.get(entry.getKey());  
			entry.setValue(normalized);
			normalized_values.add(normalized);
			sum += normalized;
		}
		
		Collections.sort(normalized_values);
		double mean = sum/normalized_values.size();
		//double median = normalized_values.get(normalized_values.size() / 2);
		
		int sustain = 7;
		
		double max_sustain = 0;
		int last_element = normalized_values.size() - 1;
		for(int i=0;i < sustain && last_element - i >= 0;i++)  
		{
			max_sustain += normalized_values.get(last_element - i);
		}
		
		//double max = normalized_values.lastElement();
		//double h_1 = (max_sustain/(sustain * mean))  * Math.pow(Math.log((double)num_bins/(double)word_bin_counts.size()), 2);
		
		//this weighing scheme works for facebook_ipo of topics = 7 and decay = 1e-6
		double idf = Math.pow(Math.log((double)num_bins/(double)word_bin_counts.size()), 2);
		double h_1 = max_sustain * idf;
		
		//System.out.println(String.format("%f\t%f\t%f\t%s", max_sustain, idf, h_1, current_word));
		System.out.println(String.format("%f\t%s", h_1, current_word));
	}
	
	public static void main(String [] args) throws Exception
	{
		//initialize the bins of the histogram
		HashMap<Integer, Double> bin_counts = new HashMap<Integer, Double>(); 
		
		//the file which contains the time of all the words
		String all_words_time = args[0];
		
		//start reading in
		String line;
		BufferedReader br = new BufferedReader(new FileReader(all_words_time));
		
		//read in the start time
		long start_time = Long.parseLong(br.readLine());
		long current_time = start_time;
		int index = 0; 
		increment(bin_counts, index);
		
		long width = day;
		
		//now fill up the histogram
		while((line=br.readLine())!=null)
		{
			current_time = Long.parseLong(line);
			index = (int)((current_time - start_time)/width);
			increment(bin_counts, index);
		}
		br.close();
		//end of filling up the histogram
		
		//determine the number of bins
		long end_time = current_time;
		int num_bins = (int)((end_time - start_time)/width) + 1;
		
		//Now read in stop words list
		/*
		HashSet<String> stop_words = new HashSet<String>();
		br = new BufferedReader(new FileReader("jar/smart-common-words.txt"));
		line = br.readLine();
		String [] tokens = line.split(",");
		for(int i=0;i<tokens.length;i++)
		{
			stop_words.add(tokens[i]);
		}
		*/
		//End of stop words list
		
		//Initialize the histogram for this particular word
		HashMap<Integer, Double> word_bin_counts = new HashMap<Integer, Double>();
		
		//Initialize the number of unique authors for this word
		HashSet<Long> user_ids = new HashSet<Long>();
		HashSet<Long> status_ids = new HashSet<Long>();
		
		//Read the first line
		br = new BufferedReader(new InputStreamReader(System.in));
		line   = br.readLine();
		String [] tokens = line.split("\t");
		
		long status_id = Long.parseLong(tokens[0]);
		long user_id = Long.parseLong(tokens[1]);
		current_time = Long.parseLong(tokens[2]);
		String current_word = tokens[3];
		
		status_ids.add(status_id);
		user_ids.add(user_id);
		index = (int)((current_time - start_time) / width);
		increment(word_bin_counts, index);
		
		while((line=br.readLine())!=null)
		{
			tokens = line.split("\t");
			status_id = Long.parseLong(tokens[0]);
			user_id = Long.parseLong(tokens[1]);
			current_time = Long.parseLong(tokens[2]);
			index = (int)((current_time - start_time) / width);
			
			if(!current_word.equals(tokens[3]))
			{
				//print out the score of this word
				//first find out the number of bins that is not empty
				
				print(bin_counts, word_bin_counts, num_bins, current_word);
				
				status_ids = new HashSet<Long>();
				user_ids = new HashSet<Long>();
				word_bin_counts = new HashMap<Integer, Double>();
			}
			status_ids.add(status_id);
			user_ids.add(user_id);
			increment(word_bin_counts, index);
			current_word = tokens[3];
		}
		br.close();
		
		print(bin_counts, word_bin_counts, num_bins, current_word);
	}
}
