package sample.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import sample.databaseHandler.DAO;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Array;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PlayListView implements Initializable {
    public static Home homeController;
    @FXML
    private ScrollPane scrollPaneMusic;

    @FXML
    private VBox vboxSongs;
    String pLayListViewName;

    public ArrayList<String> songsInThePlayList =new ArrayList<String> ();
    public ArrayList<Cell> songsInThePlayListCell =new ArrayList<Cell> ();




    DAO dao = new DAO();


    public void setpLayListViewName(String pLayListViewName) {
        this.pLayListViewName = pLayListViewName;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //resize the vbox inside scroll pane when scroll pane is maximized// this is the only way to do
        scrollPaneMusic.setFitToHeight(true);
        scrollPaneMusic.setFitToWidth(true);
        

    }

    public String getpLayListViewName() {
        return pLayListViewName;
    }



    public void addPlayListSongs(String playListName) throws Exception {

        Node node = null;
        ResultSet rs = dao.getPlayListSongs(playListName);                  //getting the songs from the playlist's database


        while(rs.next()){                                                   //adding only the appropriate  song cell to the playlist view's vbpx
            Songs s = new Songs();
            File f = new File(homeController.getPathOfThisSong(rs.getString(1)));
            s.setTitle(f);
            FXMLLoader loader2 = new FXMLLoader();


            URL xmlUrl2 = getClass().getResource("/sample/views/Cell.fxml");
            loader2.setLocation(xmlUrl2);

            try {
                node = loader2.load();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Cell cell = loader2.getController();
            cell.setPlayListCheck(true);
            cell.setPlayListName(playListName);
            homeController.setCellInfo(s,cell);

            vboxSongs.getChildren().add(node);



            songsInThePlayList.add(cell.getSong());           //adding the song to the playlist's arraylist in home//this is a string arraylist
            songsInThePlayListCell.add(cell);                 //same as above but this is the cell of that songs (need this becoz have to change the color of the cell when a song is played or stopped)

        }


    }


    public void addOneSongToPlayList(String playListName,String songName){
        Node node=null;
        Songs s = new Songs();
        File f = new File(homeController.getPathOfThisSong(songName));
        s.setTitle(f);
        FXMLLoader loader2 = new FXMLLoader();


        URL xmlUrl2 = getClass().getResource("/sample/views/Cell.fxml");
        loader2.setLocation(xmlUrl2);

        try {
            node = loader2.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Cell cell = loader2.getController();
        cell.setPlayListCheck(true);
        cell.setPlayListName(playListName);
        homeController.setCellInfo(s,cell);

        vboxSongs.getChildren().add(node);



        songsInThePlayList.add(cell.getSong());           //adding the song to the playlist's arraylist in home//this is a string arraylist
        songsInThePlayListCell.add(cell);                 //same as above but this is the cell of that songs (need this becoz have to change the color of the cell when a song is played or stopped)

    }




}
