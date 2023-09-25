package sample.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sample.databaseHandler.DAO;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;

public class playListIn implements Initializable {
    public static Home homeController;
    @FXML
    private JFXButton btn;
    private int id;
    private SimpleStringProperty name=new SimpleStringProperty();               //this can be string aswell..(Just used this for expermntal)

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        btn.setOnAction(e->showThePlayListItems());

    }

    public void setInfo(int id,String name){

        this.id = id;
       this.name.set(name);


        btn.setText(this.name.getValue());

    }

    //this will show the appropriate playListView(scrollpane with all songs)
    private void showThePlayListItems() {


        for(FXMLLoader loader:homeController.playListButtonsLoader){
            PlayListView pv = loader.getController();
            if(btn.getText().equals(pv.getpLayListViewName())){
                AnchorPane Ap =null;                                //this will change the anchorpaneAll to playListView's anchorpane
                Ap = loader.getRoot();
                homeController.anchorPaneAll.getChildren().setAll(Ap);

            }
        }

        homeController.currentButton=btn.getText();
        homeController.changeBoarderOfTheButton();

    }



    @FXML                                           //adding styles to the context menu(only way-cant add through scene builder)
    void action(MouseEvent event) {
        btn.getScene().getStylesheets().add(playListIn.class.getResource("/sample/Css.css").toExternalForm());
    }





    @FXML
    void playPlayList(ActionEvent event) {
        showThePlayListItems();
        homeController.playPlayList(btn.getText());                                                      //playing the playlist when playList button is pressed
    }



    @FXML
    void deletePlaylist(ActionEvent event) throws Exception {
        DAO dao = new DAO();
        dao.deletePlaylist(btn.getText());



        Iterator<Button> itr = homeController.listOfButtonsArray.iterator();                //deleting the playList's button from playList button array
        while (itr.hasNext()) {
            Button b = itr.next();					//this line is must
            if(b.getText().equals(btn.getText())){
                itr.remove();
                homeController.playListVbox.getChildren().remove(b);
            }

        }


        Iterator<FXMLLoader> itr2 = homeController.playListButtonsLoader.iterator();     //deleting the playList's view from playList view array
        while (itr2.hasNext()) {
            FXMLLoader l = itr2.next();					//this line is must
            PlayListView pv2 = l.getController();
            if(pv2.pLayListViewName.equals(btn.getText())){
                itr2.remove();
            }

        }



        FXMLLoader loader2=homeController.musicLoader;               //changeing the view to music when a playList gets deleted
        AnchorPane Ap =null;
        Ap = loader2.getRoot();

        homeController.anchorPaneAll.getChildren().setAll(Ap);
        homeController.deletBoarderOfTheButton();
    }

    @FXML
    void renamePlaylist(ActionEvent event) {
        Stage stage=new Stage();
        FXMLLoader loader2 = new FXMLLoader();
        URL xmlUrl2 = getClass().getResource("/sample/views/CreateORRenamePlayeList.fxml");
        loader2.setLocation(xmlUrl2);

        try {
            loader2.load();

        }catch(IOException e) {
            e.printStackTrace();
        }

        CreateORRenamePlayList corp = loader2.getController();
        corp.setNewORRename("Rename");
        corp.setOldPlayListName(btn.getText());
        Parent root = loader2.getRoot();

        Scene scene = new Scene(root);

        stage.initModality(Modality.APPLICATION_MODAL);                     //cant go to back stage without closing this stage
        stage.initStyle(StageStyle.UNDECORATED);

        GaussianBlur blur = new GaussianBlur(); //
        homeController.getroot().setEffect(blur);

        stage.setScene(scene);
        //stage.initStyle(StageStyle.UTILITY);//                            this will hide the task bar icon for this stage...but we cant give two stage styles to the same stage



        stage.showAndWait();

    }





}




