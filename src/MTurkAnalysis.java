import java.io.InputStreamReader;
import au.com.bytecode.opencsv.CSVReader;

public class MTurkAnalysis 
{
	public static void main(String [] args) throws Exception
	{
		String [] events = { "bp", "facebook_ipo", "japan", "obamacare", "wallstreet" };
		int num_events = events.length;
		int [][] scores = new int [num_events][4];
		for(int i=0;i<num_events;i++)
		{
			for(int j=0;j<4;j++)
			{
				scores[i][j] = 0;
			}
		}
		
		CSVReader reader = new CSVReader(new InputStreamReader(System.in));
		String [] nextLine;
		reader.readNext(); // discard the header
	    while ((nextLine = reader.readNext()) != null) 
	    {
	        // nextLine[] is an array of values from the line
	        for(int i=0;i<num_events;i++)
			{
	        	//System.out.println(nextLine[27 + i]);
	        	String [] votes = nextLine[27 + i].split("\\|");
	        	for(String vote : votes)
				{
	        		//System.out.println(vote);
	        		
					if(vote.matches("Summary[1-4]"))
					{
						int v = Integer.parseInt(vote.replace("Summary", "")) - 1;
						scores[i][v]++;
					}
					else
					{
						//is a comment, do nothing for now.
					}
				}
			}
	    }
	    
	    int [] total_scores = new int[4];
	    for(int j=0;j<4;j++)
    	{
    		total_scores[j] = 0;
    	}
	    
	    for(int i=0;i<num_events;i++)
		{
	    	for(int j=0;j<4;j++)
	    	{
	    		total_scores[j] += scores[i][j];
	    	}
		}
	    
	    for(int i=0;i<num_events;i++)
		{
			System.out.println(String.format("%s: %d %d %d %d", 
					events[i], scores[i][0], scores[i][1], scores[i][2], scores[i][3]));
		}
	    System.out.println(String.format("Total: %d %d %d %d", 
				total_scores[0], total_scores[1], total_scores[2], total_scores[3]));
	}
}
