package mongoDbClient;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import booksdbclient.model.Book;
import booksdbclient.model.Genre;
import booksdbclient.model.MockBooksDb;
import booksdbclient.view.BooksPane;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Application start up.
 *
 * @author anderslm@kth.se
 */
public class BooksDbClientMain extends Application {

    @Override
    public void start(Stage primaryStage) throws SQLException {

        MockBooksDb booksDb = new MockBooksDb(); // model
        // Don't forget to connect to the db, somewhere...

        
        BooksPane root = new BooksPane(booksDb);

        Scene scene = new Scene(root, 800, 600);
        
        primaryStage.setTitle("Books Database Client");
        // add an exit handler to the stage (X) ?
        primaryStage.setOnCloseRequest(event -> {
            try {
                booksDb.disconnect();
                primaryStage.close();
            } catch (Exception e) {
            	
            }
        });
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
