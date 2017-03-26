package main.java;

import main.java.models.ExampleObject;
import main.java.models.ExampleObjectQuery;
import main.java.models.base.BaseObject;
import main.java.models.base.ConnectionManager;
import main.java.models.base.ObjectCollection;

import java.sql.PreparedStatement;

public class baseObjectExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			//TODO:: Get Connection Info From Config File To Allow For Password Security
			ConnectionManager.init("5530u60","jure0kku", "jdbc:mysql://georgia.eng.utah.edu", "5530db60");

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

			PreparedStatement stmnt = ConnectionManager.prepareStatement("SELECT * FROM "+ExampleObject.TableName+" WHERE name = ?;");
			stmnt.setObject(1, "New Entry");

			ExampleObjectQuery query = new ExampleObjectQuery();
			ObjectCollection collection = query.getCollectionFromObjectResult(stmnt.executeQuery());

			for(BaseObject object: collection){
				System.out.println("Example Object, Id: "+object.getField("Id")+" | Name: "+ object.getField("Name"));
			}

			stmnt.close();

			ConnectionManager.commit();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			ConnectionManager.rollbackAll();

		}


		ConnectionManager.closeConnection();
	}

}
