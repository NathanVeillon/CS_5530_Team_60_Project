package main.java;

import main.java.models.ExampleObject;
import main.java.models.base.ConnectionManager;

public class baseObjectExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			//TODO:: Get Connection Info From Config File To Allow For Password Security
			ConnectionManager.init("5530u60","ENTER PASSWORD", "jdbc:mysql://georgia.eng.utah.edu", "5530db60");

			ConnectionManager.startTransaction();

			ExampleObject a = new ExampleObject();
			a.setField("Name", "Hello");
			a.save();

			ExampleObject b = new ExampleObject();
			b.setField("Name", "GoodBye");
			b.save();

			System.out.println("Id Of B: "+b.Id);

			b.setField("Name", "Why?");
			b.save();

			a.delete();

			ExampleObject c = new ExampleObject();
			c.setField("Name","New Entry");

			c.save();

			ConnectionManager.commit();
		}
		catch (Exception e)
		{
			ConnectionManager.rollbackAll();
			e.printStackTrace();
			
		}


		ConnectionManager.closeConnection();
	}

}
