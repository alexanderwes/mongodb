package mongoDbClient;


import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import java.util.Arrays;
import java.util.Iterator;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Filters.*;
import com.mongodb.client.result.DeleteResult;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.UpdateResult;
import booksdbclient.model.Author;
import booksdbclient.model.Book;
import booksdbclient.model.Genre;
import booksdbclient.model.MockBooksDb;
import booksdbclient.view.BooksPane;
import javafx.scene.Scene;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class Client {

	public static void main (String[] args) {
	
		
		MockBooksDb mock = new MockBooksDb();
	
		mock.connect("dum");
	
		List<Book> list;	
		list = mock.getBooks();
		
//		list = mock.searchBooksByTitle("Ja");
		
		for (int i=0; i<list.size(); i++)
			System.out.println(list.get(i));
				
		
//		for (MongoCursor<Document> cursor = find.iterator(); cursor.hasNext();) {
//			Document doc = cursor.next();
//			Book book = new Book(doc.getString("isbn"), doc.getString("title"), 
//					Genre.valueOf(doc.getString("genre")), doc.getDouble("rating").intValue());	
//			
//			Object info = doc.get("author");		
//			
//			Author author = new Author(((Document) info).getString("name"), 
//					LocalDate.parse(((Document) info).getString("dob")));
//			
//			book.addAuthor(author);
//			author.addIsbn(book.getIsbn());
//			System.out.println(book);
//		}

	}
	
}
