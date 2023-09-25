package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import sample.controllers.*;

import java.net.URL;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader();
        URL xmlUrl2 = getClass().getResource("views/Home.fxml");
        loader.setLocation(xmlUrl2);

        Parent root = loader.load();


        Scene scene = new Scene(root);
        primaryStage.initStyle(StageStyle.TRANSPARENT);

        scene.setFill(Color.TRANSPARENT);
        root.setStyle("-fx-background-color: null;");       //this is must/ because if we dont put any controllers(eg.buttn) but only texts
                                                            //and other stuffs.. the setFill() alone can do the transparent job , but if we
                                                            //use controllers java and css internally set a background color to our scene

                                                            //so we have to change the css color of the root to null as well inorder to get the transparent

        primaryStage.setScene(scene);


        Home homeController  = loader.getController();

        Cell cell=new Cell();
        cell.homeController=homeController;         //have to access a method in home from Cell controller..so passing the home controllers current instance to celll controller


        CreateORRenamePlayList pController = new CreateORRenamePlayList();                  //same thing above
        pController.homeController=homeController;

        MusicView mController = new MusicView();                                    //same thing above
        mController.homeController=homeController;


        playListIn piController = new playListIn();                         //same thing above
        piController.homeController=homeController;

        PlayListView pvController = new PlayListView();                         //same thing above
        pvController.homeController=homeController;

        primaryStage.setOnShowing(new EventHandler<WindowEvent>() {         //if i call createmusicview() inside Home's initialize method it will create the MusicView fxml page but insides music controller i cant access the homecontroller(this will be null)(variable) becoz currently loader.load is called(29 Main) but (54 main) is not called yet so i wont have access to homecontroller from the music controllers (variable homecontroller)  BUT with this setOnshow method, The music's (MusicView controllers homecontroller variable) has access to the homecontroller
            @Override
            public void handle(WindowEvent event) {
                homeController.createMusicView();
                //create playlist buttons

                try {
                    homeController.createPlaylistButtons();                     //this will initialize everything about playlists
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });



        primaryStage.show();


    }


    public static void main(String[] args) {
        launch(args);

    }
}
