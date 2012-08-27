import java.io.*;
import java.sql.*;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

public class DB2Text 
{
	public static void main(String [] args) throws Exception
	{
		int start_year = 2006;
		int end_year = 2011;
		
		int start_year_start_month = 7;
		int end_year_end_month = 12;
		
		String homedir = System.getProperty( "user.home" );
		//String outfile = String.format("%s/sfbay_us_tweets_072012.txt", homedir);
		String outfile = String.format("%s/us_political_tweets.txt", homedir);
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(outfile));

		int numrows = 0;
		System.out.println("Writing....");

		Class.forName ("com.mysql.jdbc.Driver");
		Connection conn = null;
		System.out.println("Connecting....");
		
		//String hostname = "10.0.106.64";
		//String hostname = "10.0.106.39";
		String hostname = "10.0.106.62";
		String userid   = "freddy";
		//String pass     = "freddy123";
		String pass     = "freddy123456";
		int port        = 3306;

		//String database = "us_tweet_stream";
		String database = "us_political_tweet";
		
		do
		{
			try
			{
				conn = DriverManager.getConnection(String.format("jdbc:mysql://%s:%d?socketTimeout=0&connectTimeout=5", hostname, port), userid, pass);
			}
			catch (CommunicationsException ce)
			{
				System.out.println("Not connected, reconnecting....");	
			}

		} while(conn == null || !conn.isValid(1));
		System.out.println("Connected");
		
		//Statement stmt = conn.createStatement();
		Statement stmt = conn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
		stmt.setFetchSize(Integer.MIN_VALUE);
		stmt.setQueryTimeout(0);
		
		bw.write("status_id\tuser_id\tcontent\tgmt_time\tgeo\tplace_id");
		bw.newLine();
		
		
		for(int year = start_year; year <= end_year; year++)
		{
			int start_month = year == start_year ? start_year_start_month :  1;
			int end_month   = year == end_year   ? end_year_end_month     : 12;
			
			for(int month = start_month; month <= end_month; month++)
			{
				if(!conn.isValid(1))
				{
					System.out.println("Not connected, reconnecting....");
					do
					{
						try
						{
							conn = DriverManager.getConnection(String.format("jdbc:mysql://%s:%d?socketTimeout=0&connectTimeout=5", hostname, port), userid, pass);
						}
						catch (CommunicationsException ce)
						{
							System.out.println("Not connected, reconnecting....");	
						}
					} while(conn == null || !conn.isValid(1));
					
					System.out.println("Connected");
					stmt = conn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
					stmt.setFetchSize(Integer.MIN_VALUE);
					//stmt = conn.createStatement();
					stmt.setQueryTimeout(0);
				}
				
				String query = String.format("select status_ID, user_ID, content, published_time_GMT, geo, place_ID from %s.tweet_%d_%02d", database, year, month);
				System.out.println(query);

				ResultSet rs = stmt.executeQuery(query);
				while (rs.next())
				{
					String status_id = rs.getString(1);
					String user_id   = rs.getString(2);
					String content   = rs.getString(3);
					String gmt_time  = rs.getString(4);
					String geo       = rs.getString(5);
					String place_id  = rs.getString(6);
					
					content = content.replaceAll("[\t\n\r\f]+", " ");
					content = content.replaceAll("[^ a-zA-Z0-9\\[\\]\\;',./`=\\-~!@#$%\\^&*()_+{}|:\"<>?]", "");

					if(content.length() > 0)
					{
						bw.write(String.format("%s\t%s\t%s\t%s\t%s\t%s", status_id, user_id, content, gmt_time, geo, place_id));
						bw.newLine();
						numrows++;
					}
					
					if(numrows % 100000 == 0)
					{
						System.out.println("Processed: " + numrows);
						bw.flush();
					}
				}
				rs.close();
			}
		}
		System.out.println("Closing connection");
		stmt.close();
		conn.close();
		bw.close();
	}
}
