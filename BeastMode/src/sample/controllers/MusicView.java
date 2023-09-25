package sample.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MusicView implements Initializable {



    @FXML
    private ScrollPane scrollPaneMusic;
    @FXML
    private VBox vboxSongs;
    @FXML
    private AnchorPane anchorpane;
    public static Home homeController;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //resize the vbox inside scroll pane when scroll pane is maximized// this is the only way to do
        scrollPaneMusic.setFitToHeight(true);
        scrollPaneMusic.setFitToWidth(true);



        createCells();
    }

    private void createCells() {


        Node node = null;

        for (Songs s : homeController.songsAll) {


            FXMLLoader loader2 = new FXMLLoader();

            URL xmlUrl2 = getClass().getResource("/sample/views/Cell.fxml");
            loader2.setLocation(xmlUrl2);

            try {
                node = loader2.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Cell cell = loader2.getController();
            cell.setPlayListCheck(false);
            homeController.setCellInfo(s,cell);
            homeController.currentController.add(loader2);

            vboxSongs.getChildren().add(node);


        }
    }
}
