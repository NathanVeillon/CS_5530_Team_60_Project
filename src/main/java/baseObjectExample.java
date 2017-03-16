package main.java;

import main.java.models.ExampleObject;
import main.java.models.base.ConnectionManager;

public class baseObjectExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			ConnectionManager.init("5530u60","INSERT PASSWORD", "jdbc:mysql://georgia.eng.utah.edu", "5530db60");

			ConnectionManager.startTransaction();

			// ToDo: Make Objects Get Object After Creation To Fill In The Auto Generated Fields
			//	Will Have To Implement Queries To Get This To Work
			ExampleObject a = new ExampleObject();
			a.setField("Name", "Hello");
			a.save();

			ExampleObject b = new ExampleObject();
//			b.setField("Id", 2);
			b.setField("Name", "GoodBye");
			b.save();



//			b.setField("Name", "Why?");
//			b.save();

			ConnectionManager.commit();
			ConnectionManager.closeConnection();
		}
		catch (Exception e)
		{
			ConnectionManager.rollbackAll();
			e.printStackTrace();
			
		}
	}

}
