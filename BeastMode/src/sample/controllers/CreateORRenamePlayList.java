package sample.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import sample.databaseHandler.DAO;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class CreateORRenamePlayList implements Initializable {

    @FXML
    private JFXTextField playListName;
    @FXML
    private JFXButton createOrRenameButton;

    public static Home homeController;
    private String newORRename;
    private String oldPlayListName;


    public void setNewORRename(String newORRename) {
        this.newORRename = newORRename;
        if(!(newORRename.equals("New"))){
            createOrRenameButton.setText(newORRename);
        }
    }

    DAO dao = new DAO();





    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }



    @FXML
    void createORRenameThePlayList(ActionEvent event) throws Exception {


        if(newORRename.equals("New")){                              //to create the [playlist
            dao.insertPlaylist(playListName.getText());
            homeController.getroot().setEffect(null);
            playListName.getScene().getWindow().hide();

            FXMLLoader loader2 = new FXMLLoader();

            URL xmlUrl2 = getClass().getResource("/sample/views/playListIn.fxml");
            loader2.setLocation(xmlUrl2);

            Node node = loader2.load();
            playListIn pi = loader2.getController();
            DAO dao = new DAO();
            pi.setInfo(dao.getPlayListId(playListName.getText()),playListName.getText());
            homeController.listOfButtonsArray.add((Button) node);
            homeController.playListVbox.getChildren().add(node);


            FXMLLoader loader = new FXMLLoader();                                                   //adding the playList view to the arraylistIn home controller
            URL xmlUrl = getClass().getResource("/sample/views/PlayListView.fxml");
            loader.setLocation(xmlUrl);

            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            PlayListView pv = loader.getController();
            pv.setpLayListViewName(playListName.getText());
            homeController.playListButtonsLoader.add(loader);

        }
        else{                                                       //to rename the [playlist
            dao.renamePlaylist(oldPlayListName,playListName.getText());
            homeController.getroot().setEffect(null);
            playListName.getScene().getWindow().hide();

            for(Button btn:homeController.listOfButtonsArray){     //changing the name in the playList button
                if(btn.getText().equals(oldPlayListName)){
                    btn.setText(playListName.getText());
                }
            }

            for(FXMLLoader loader:homeController.playListButtonsLoader){       //changhin the name of the playlist in payList viewv class
                PlayListView pv = loader.getController();

                if(pv.pLayListViewName.equals(oldPlayListName)){
                    pv.pLayListViewName = playListName.getText();
                }

                for(Cell cell:pv.songsInThePlayListCell){                       //changing the playlist name in every cells
                    if(cell.getPlayListName().equals(oldPlayListName)){
                        cell.setPlayListName(playListName.getText());
                    }
                }

            }

            if(homeController.oldButton.equals(oldPlayListName)){              //changing the playlist name in oldButton(the orange left border)
                homeController.oldButton = playListName.getText();
            }




        }



    }


    @FXML
    void playListCancel(ActionEvent event) {

        homeController.getroot().setEffect(null);
        playListName.getScene().getWindow().hide();

    }


    public void setOldPlayListName(String oldPlayListName) {
        this.oldPlayListName=oldPlayListName;
    }
}
