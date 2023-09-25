package sample.controllers;

import com.jfoenix.controls.JFXButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import sample.databaseHandler.DAO;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Cell implements Initializable{


    @FXML
    private HBox hbox;

    @FXML
    private Label song;

    @FXML
    private JFXButton button;

    @FXML
    private FontAwesomeIcon playPause;

    @FXML
    private Label singer;

    @FXML
    private Label album;
    @FXML
    private JFXButton add;


    private ContextMenu playListContextMenu=new ContextMenu();




    @FXML
    private Label time;

    private boolean playListCheck;
    private  String playListName=null;

    private MenuItem playListMeniItem=new MenuItem();
    ArrayList<MenuItem> playListMenuItemsArray = new ArrayList<>();




    public static Home homeController;
    Home hme = homeController;
    private File file;


    public File getFile() {
        return file;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {






        button.setOnAction(e->play());


    }




    public void setSong(File file) {


        String s = file.getName().replaceAll(".mp3","");
        this.song.setText(s);
        this.file=file;
    }

    private void play() {



        hme.setSong(file.getPath(),time.getText(),playListCheck,playListName);


    }

    public String getTime() {
        return time.getText();
    }

    public String getSong() {
        return file.getName();
    }

    public void setSinger(String singer) {
        this.singer.setText(singer);
    }

    public Label getSinger() {
        return singer;
    }

    public Label getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album.setText(album);
    }


    public void setTime(String time) {
        this.time.setText(time);
    }



    public void setPlayListCheck(boolean playListCheck) {
        this.playListCheck = playListCheck;
    }

    public boolean isPlayListCheck() {
        return playListCheck;
    }

    public String getPlayListName() {
        return playListName;
    }

    public void setPlayListName(String playListName) {
        this.playListName = playListName;
    }

    public void youArePlaying(){
        song.styleProperty().unbind();                      //if you bind a property thn inorder to change the property again u have to unbind it other wise it will throw error
        song.setStyle("" + "-fx-text-fill: #8e99f3;");
        album.styleProperty().unbind();
        album.setStyle("" + "-fx-text-fill: #8e99f3;");
        singer.styleProperty().unbind();
        singer.setStyle("" + "-fx-text-fill: #8e99f3;");
        time.styleProperty().unbind();
        time.setStyle("" + "-fx-text-fill: #8e99f3;");






    }


    //when i chnage the cell's text color to purple when its played, there after if i play another song the previous song's color wont change back to #dacccc(even the background color of that is #dacccc(light orange) in stylesheet) or to white(when hovered) becoz of the inline style i applied in the code. inorder to solve this issue a  code should be made(inline), but unfortunately we cant use :hover(or any css selectors) in inline(java code) so we have to use a binding technique inorder to achieve that
    public void youAreStopped() {

        song.styleProperty().bind(Bindings.when(hbox.hoverProperty())
                .then("-fx-text-fill:  white")
                .otherwise("-fx-text-fill: #dacccc"));

        album.styleProperty().bind(Bindings.when(hbox.hoverProperty())
                .then("-fx-text-fill:  white")
                .otherwise("-fx-text-fill: #dacccc"));

        singer.styleProperty().bind(Bindings.when(hbox.hoverProperty())
                .then("-fx-text-fill:  white")
                .otherwise("-fx-text-fill: #dacccc"));
        time.styleProperty().bind(Bindings.when(hbox.hoverProperty())
                .then("-fx-text-fill:  white")
                .otherwise("-fx-text-fill: #dacccc"));

         }






    @FXML
    void action(ContextMenuEvent event) {
        singer.getScene().getStylesheets().add(playListIn.class.getResource("/sample/Css.css").toExternalForm());
        playListContextMenu.getItems().clear();
        playListMenuItemsArray.clear();

        for(FXMLLoader loader :homeController.playListButtonsLoader){
            PlayListView pv = loader.getController();
            playListMeniItem = new MenuItem(pv.getpLayListViewName());
            playListContextMenu.getItems().add(playListMeniItem);
            playListMenuItemsArray.add(playListMeniItem);

        }
        add.setContextMenu(playListContextMenu);
        DAO dao = new DAO();



        for(int i = 0; i < playListMenuItemsArray.size(); i++) {                                            //it wont work if its in initiaqzlie // the first for loopo is must ...without that only, last context menu items surce comes

            playListMenuItemsArray.get(i).setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    for (MenuItem p : playListMenuItemsArray) {
                        if (event.getSource().equals(p)) {
                            DAO dao = new DAO();
                            try {
                                dao.addSongToPlayList(dao.getPlayListId(p.getText()),dao.getSongId(getSong()));//adding the song to the plaList database
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            for(FXMLLoader loader :homeController.playListButtonsLoader){                   //adding the song to plasyListView
                                PlayListView pv = loader.getController();

                                if(pv.getpLayListViewName().equals(p.getText())){
                                    pv.addOneSongToPlayList(p.getText(),getSong());
                                }
                            }

                        }

                    }

                }
            });
        }




    }






}
