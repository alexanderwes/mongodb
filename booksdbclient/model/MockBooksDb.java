/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package booksdbclient.model;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;
import booksdbclient.model.Author;
import booksdbclient.model.Book;
import booksdbclient.model.Genre;
import booksdbclient.model.MockBooksDb;


/**
 * A mock implementation of the BooksDBInterface interface to demonstrate how to
 * use it together with the user interface.
 *
 * Your implementation should access a real database.
 *
 * @author anderslm@kth.se
 */
public class MockBooksDb implements BooksDbInterface {

	private FindIterable find;
	private MongoCollection<Document> authorsCollection;
	private MongoCollection<Document> booksCollection;
	

	public MockBooksDb() {
		
	}

    @Override
    public boolean connect(String database) {
    	
    	String database_ = "localhost";
		int port = 27017;
		String userName = "client";
		String password = "password";
	
		MongoCredential credential = MongoCredential.createCredential(userName, "library", password.toCharArray());
		MongoClient mongoClient = new MongoClient(new ServerAddress(database_, port), Arrays.asList(credential));
		
		if (mongoClient.getServerAddressList() != null) {
			MongoDatabase mongoDatabase = mongoClient.getDatabase("library");
			booksCollection = mongoDatabase.getCollection("books");
			authorsCollection = mongoDatabase.getCollection("authors");
			return true;
		}		
		else
			return true;
    }

    @Override
    public boolean disconnect() {
    	
//    	mongoClient.close();
    	
    	
		return false;
    }

	@Override
    public List<Book> searchBooksByTitle(String searchTitle) {
	
    	List<Book> list = new ArrayList<>();
    	
    	if (booksCollection != null) {
    	
    		List<Document> books = booksCollection.find(Filters.regex("title", searchTitle, "i")).into(new ArrayList<Document>());
	   	
	    	list = copyBooksToList(books);
	    	list = fixList(list);
	    	list = fixList2(list);
    	}
    	
    	return list;
    }

	@Override
	public List<Book> searchBooksByAuthor(String name) {
	
		List<Book> list = new ArrayList<>();
		if (booksCollection != null) {
	    	List<Document> books = booksCollection.find(Filters.regex("authors.name", name, "i")).into(new ArrayList<Document>());
	
	    	list = copyBooksToList(books);
	    	list = fixList(list);
	    	list = fixList2(list);
		}
		return list;
	}

	@Override
	public List<Book> searchBooksByISBN(String isbn) {

		List<Book> list = new ArrayList<>();
		
		if (booksCollection != null) {
	    	List<Document> books = booksCollection.find(Filters.regex("isbn", isbn)).into(new ArrayList<Document>());
	    	
	    	list = copyBooksToList(books);
	    	list = fixList(list);
	    	list = fixList2(list);
	    	
		}
		return list;
	}

	@Override
	public List<Book> searchBooksByRating(String rating) {
		
		List<Book> list = new ArrayList<>();
		
		if (booksCollection != null) {
	    	List<Document> books = booksCollection.find(eq("rating", Integer.parseInt(rating))).into(new ArrayList<Document>());
	    	
	    	list = copyBooksToList(books);
	    	list = fixList(list);
	    	list = fixList2(list);
		}
		return list;
	}

	@Override
	public List<Book> searchBooksByGenre(Genre genre) {
	
		List<Book> list = new ArrayList<>();
		
		if (booksCollection != null) {
	    	List<Document> books = booksCollection.find(Filters.regex("genre", genre.toString())).into(new ArrayList<Document>());
	    	
	    	list = copyBooksToList(books);
	    	list = fixList(list);
	    	list = fixList2(list);
    	
		}
		return list;
	}

	@Override
	public boolean insertBook(Book book) {
		
	boolean checkBook = false;
	boolean checkAuthor = false;
	
	if (booksCollection != null) {
		
		for (int i=0; i<getBooks().size(); i++) {
	
			if (book.getIsbn().equals(getBooks().get(i).getIsbn())) {
				checkBook = true;
				break;
			}
		}
		
		for (int i=0; i<getAuthors().size(); i++) {
			if (book.getAuthors().get(0).getId().equals(getAuthors().get(i).getId())) {
				checkAuthor = true;
				break;
			}
		}
		
		if (checkBook == false && checkAuthor == true) {
			Document document = new Document ("title", book.getTitle())
					.append("isbn", book.getIsbn())
					.append("genre", book.getGenre().toString())
					.append("rating", book.getRating())
					.append("authors", Arrays.asList(new Document ("name", book.getAuthors().get(0).getName())
					.append("dob", book.getAuthors().get(0).getDob().toString())
					.append("id", book.getAuthors().get(0).getId())));
					
					booksCollection.insertOne(document);
					return true;
		}
			if (checkBook == false && checkAuthor == false) {
				
				Document authorDoc = new Document("name", book.getAuthors().get(0).getName())
				.append("dob", book.getAuthors().get(0).getDob().toString())
				.append("id", book.getAuthors().get(0).getId());
				authorsCollection.insertOne(authorDoc);			
				
				Document document = new Document ("title", book.getTitle())
				.append("isbn", book.getIsbn())
				.append("genre", book.getGenre().toString())
				.append("rating", book.getRating())
				.append("authors", Arrays.asList(new Document ("name", book.getAuthors().get(0).getName())
				.append("dob", book.getAuthors().get(0).getDob().toString())
				.append("id", book.getAuthors().get(0).getId())));
				
				booksCollection.insertOne(document);
								

				
				return true;
			}
		}
	return false;
	}

	@Override
	public boolean updateRating(String isbn, int rating) {
		if (booksCollection != null) {
			if (booksCollection.updateOne(eq("isbn", isbn.toLowerCase()), set("rating", rating))
					.getModifiedCount() != 0)
				return true;
		}
		
		return false;
	}

	@Override
	public boolean addAuthor(String isbn, Author author) {
		
		Document author_ = new Document().append("name", author.getName())
                .append("dob", author.getDob().toString())
                .append("id", author.getId());
		if (booksCollection != null) {
			if (booksCollection.updateOne(eq("isbn", isbn),Updates.addToSet("authors", author_)).getModifiedCount() != 0) {
				authorsCollection.insertOne(author_);
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean deleteBook(String isbn) {
		
		if (booksCollection != null) {
			if (booksCollection.deleteOne(eq("isbn", isbn)).getDeletedCount() == 1)
				return true;
		}
		
		return false;
	}

	public List<Author> getAuthors() {

		List<Author> list = new ArrayList<>();
		if (authorsCollection != null) {
			List<Document> authors = authorsCollection.find().into(new ArrayList<Document>());
			
			list = copyAuthorsToList(authors);
		}
		
		return list;
	}
	
	@Override
	public List<Book> getBooks() {		
		List<Book> list = new ArrayList<>();
		if (booksCollection != null) {
	    	List<Document> books = booksCollection.find().into(new ArrayList<Document>());
	    	
	    	list = copyBooksToList(books);
	    	list = fixList(list);
	    	list = fixList2(list);
		}
		return list;
	}	
	
	private List<Author> copyAuthorsToList(List<Document> authors) {
		List<Author> list = new ArrayList<>();
		
		for (int i=0; i<authors.size(); i++) {
			Author author = new Author(authors.get(i).getString("name"), LocalDate.parse(authors.get(i).get("dob").toString()),
					authors.get(i).getString("id"));
			list.add(author);
		}
		
		
		return list;
	}
	
	private List<Book> copyBooksToList(List<Document> books) {
	
		List<Book> list = new ArrayList<>();
    	for (int i=0; i<books.size(); i++) {
    		Book book = new Book(books.get(i).getString("isbn"), books.get(i).getString("title"), 
    				Genre.valueOf(books.get(i).getString("genre")), books.get(i).getInteger("rating"));
    		
    		List<Document> authors = (List<Document>) books.get(i).get("authors");
    		
    		for (int j=0; j<authors.size(); j++) {
	     		Author author = new Author(authors.get(j).getString("name"), 
	     				LocalDate.parse(authors.get(j).get("dob").toString()), authors.get(j).getString("id"));
	     		
	     		author.addIsbn(book.getIsbn());
	    		book.addAuthor(author);
    		}
    		list.add(book);
    	}
    	return list;
	}
	
	/**
	 * Places authors of the same book in a single row, removes extra book
	 * @param books
	 * @return
	 */
	private List<Book> fixList(List<Book> books) {
		for (int i=0; i<books.size(); i++) {
			for (int j=i; j<books.size()-1; j++) {
				if (books.get(i).getIsbn().equals(books.get(j+1).getIsbn())) {
					books.get(i).addAuthor(books.get(j+1).getAuthors().get(0));
					books.remove(j+1);
				}
			}
		}
		return books;	
	}
	
	/**
	 * Konstig funktion som lägger till böcker i författarnas isbn lista
	 * den gämför författarna, om dem är lika fixar grejer
	 * @param books
	 * @return
	 */
	private List<Book> fixList2(List<Book> books) {
		
		for (int i=0; i<books.size(); i++) {
			for (int j=i; j<books.size()-1; j++) {

				for (int k=0; k<books.get(i).getAuthors().size(); k++) {
					for (int m=0; m<books.get(j+1).getAuthors().size(); m++) {
						
						if (books.get(i).getAuthors().get(k).getName().equals(books.get(j+1).getAuthors().get(m).getName())
								&& books.get(i).getAuthors().get(k).getDob().equals(books.get(j+1).getAuthors().get(m).getDob())) {
								books.get(j+1).getAuthors().get(m).addIsbn(books.get(i).getIsbn());
								books.get(i).getAuthors().get(k).addIsbn(books.get(j+1).getIsbn());
						}
					}
				}
			}
		}
		return books;
	}
}







