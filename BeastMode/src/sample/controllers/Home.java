package sample.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.*;
import javafx.util.Duration;
import sample.databaseHandler.DAO;

import java.io.*;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;


public class Home implements Initializable {

    @FXML
    private FontAwesomeIcon playPause;
    @FXML
    private FontAwesomeIcon repeatButton;
    @FXML
    private AnchorPane root;
    @FXML
    private JFXSlider volumeSlider;
    @FXML
    private JFXSlider timeSlider;

    @FXML
    private JFXButton playButton;
    @FXML
    private JFXButton filePath;


    @FXML
    private Label currentTime;
    @FXML
    private Label endTime;
    @FXML
    private FontAwesomeIcon volumeGlyph;
    @FXML
    private JFXButton musicButton;
    @FXML
    public AnchorPane anchorPaneAll;                   //main anchor pane on the ride side to show the songs
    @FXML
    private JFXButton recent;

    @FXML
    public VBox playListVbox;
    @FXML
    private ScrollPane scrollPanePlayList;
    @FXML
    private JFXButton next;
    @FXML
    private JFXButton playListMainButton;










    DAO dao = new DAO();
    ArrayList<FXMLLoader> currentController = new ArrayList<>();
    ArrayList<FXMLLoader> playListButtonsLoader = new ArrayList<>();
    static MediaPlayer mp=null;                       //garbage collection stops music afer 10 seconds so static
    boolean repeat = true;
    double x = 0,y=0;                                 //to drag the stage
    boolean max = true;                               //maximum minimum size decider
    //play()
    boolean play = true;                              //to know the play button is pressed or not
    ObservableList<Songs> songsAll = FXCollections.observableArrayList();
    private int music=0;                              //to check how many times is music button pressed
    private String songFromTheCell;
    List<File> selectedFiles;                         //to store the paths to songs


    FXMLLoader musicLoader = new FXMLLoader();
    public boolean checkPlayList=false;             //to check whether a playlist is playing or not
    ObservableList<String> currentSongsInThePlayList = FXCollections.observableArrayList();

    /*To change the border colour of a particular button when its cliked */
    public ArrayList<Button> listOfButtonsArray = new ArrayList<>();
    public String currentButton;
    private int buttonClickCount=0;
    public String oldButton="";


    //Step1 to read all the songs from the path
    void HomeConstructor() throws IOException {                         //if i declare it as a constructor it want run because the controllers objects is never created using new(), so declared this constructor as a method ti run on initialize
        File folder = null;

        FileReader fr = new FileReader("path.txt");
        BufferedReader br =  new BufferedReader(fr);
        folder = new File(br.readLine());

        br.close();
        HomeConstructor(folder);

                                                //sending the folder path to store the songs from the sub directory of the folders as well


    }




    //Step2 to read all the songs from the path
    void HomeConstructor(File path) throws IOException {
        File[] fList = path.listFiles();                                  //getting the folder and every files inside that


        if(fList != null) {                                              //if the files(folders and files) is not null

            for (File f : fList){                                        //select that file from the file list
                if (f.isFile()&&f.getName().endsWith(".mp3")) {          //if the selected file is a file ending with mp3 than create a song class and add that class to thge songList(Array)

                    Songs s = new Songs();
                    s.setTitle(f);
                    songsAll.add(s);

                } else if (f.isDirectory()) {                           //if selected file is a directory

                    HomeConstructor(new File(f.getAbsolutePath()));            //send that directory recursively--> so this method ku thrpi directory wandhu adhu ulluka ikra files a filter panni edkum
                }
            }
        }


    }               //Ellam OKay but ippa oru folder onnawdhuke wardhu endu weppame sub folder ah... apa adha recursive a anpuwam..thn adhula ikra files ku eatha padi add aawum Songslistla? thn??? engada parent folder la ikra rendawdhu sub folder, adhuku keela wara maththa files ku ellam epdi return(back) powdhu???????




            @Override
            public void initialize(URL location, ResourceBundle resources) {

                //setting the file pathg for the music player
                filePath.setOnAction(e-> {
                    try {
                        choosePath();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });


                try {
                    HomeConstructor();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                addTheSongsToDataBase();


                next.setOnAction(e->next());





                //adding music page with songs to the right aqnchorpaneAll
                musicButton.setOnAction(e-> {
                    try {
                        music();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });

                //play song
                playButton.setOnAction(e->play());

                playListMainButton.setOnAction(e->showPlayLists());
                recent.setOnAction(e->showRecentSongs());



                /*resize the vbox inside scroll pane when scroll pane is maximized
                  this is the only way to do(this can be done through scenebuilder as well)*/
                scrollPanePlayList.setFitToHeight(true);
                scrollPanePlayList.setFitToWidth(true);





                //progress bar
                timeSlider.valueProperty().addListener(new InvalidationListener() {
                    public void invalidated(Observable ov)
                    {
                        if (timeSlider.isPressed()) { // It would set the time
                            // as specified by user by pressing

                            mp.seek(mp.getMedia().getDuration().multiply(timeSlider.getValue() / 100));
                        }
                    }
                });



                //adjusting Volume
                volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
                    double oldValueDouble = oldValue.doubleValue();
                    double newValueDouble = newValue.doubleValue();
                    double volume = newValueDouble;
                    if(50.0<=volume){
                        volumeGlyph.setGlyphName("VOLUME_UP");
                    }else if(0.0<volume){
                        volumeGlyph.setGlyphName("VOLUME_DOWN");
                    }else{
                        volumeGlyph.setGlyphName("VOLUME_OFF");
                    }
                        mp.setVolume(volume/100);


                });                                                                 //i could have done this way -->      addListner(Observable->amethod())

                //adding the buttons to an arraylist inorder to change the color of the border whn one of these button is clicked
                listOfButtonsArray.add(musicButton);
                listOfButtonsArray.add(recent);
                listOfButtonsArray.add(playListMainButton);

            }

    private void addTheSongsToDataBase() {
        DAO dao = new DAO();
        for(Songs s:songsAll){
            try {
                dao.addSongToDatabase(s.getTitle().getName());
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void choosePath() throws IOException {                                          //there is an another thing called flile cooser, this chooser let the user to choose the files(single or multiple) ... we can give condtions like(.pdf,.mp3). if we give conditions like this, only these files will be shown to the user
        DirectoryChooser dc = new DirectoryChooser();
        dc.setInitialDirectory(new File(System.getProperty("user.dir")));
        //fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("*.mp3"));        i am getting the folders not songs so no need
        File path = dc.showDialog(playButton.getScene().getWindow());

        if(path.getPath()!=null) {                                                              //because if we accidently open and close the directory chooser in the app, a blank path is passed to the path(file)
            FileWriter fw = new FileWriter("Path.txt");                              //if you want multiple folders to be selected
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(path.getAbsolutePath());
            bw.newLine();
            bw.close();
        }



    }

    public void createMusicView()  {


        URL xmlUrl2 = getClass().getResource("/sample/views/MusicView.fxml");
        musicLoader.setLocation(xmlUrl2);

        try {
            musicLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    //Method to show the music(all songs) view
    void music() throws IOException {


        currentButton=musicButton.getText();
        changeBoarderOfTheButton();

        AnchorPane Ap =null;
        Ap = musicLoader.getRoot();
        anchorPaneAll.getChildren().setAll(Ap);


    }

    public void createPlaylistButtons() throws Exception {
        ResultSet rs = dao.getPlayListName();
        while(rs.next()){
            FXMLLoader loader2 = new FXMLLoader();

            URL xmlUrl2 = getClass().getResource("/sample/views/playListIn.fxml");
            loader2.setLocation(xmlUrl2);

            Node button = loader2.load();
            playListIn pi = loader2.getController();
            pi.setInfo(rs.getInt(1),rs.getString(2));
            playListVbox.getChildren().add(button);
            listOfButtonsArray.add((Button) button);                                    //adding the buttons to the arraylist(to change the border color of the button when clicked)


            /*this will create every cells(sojngs) inside the play list but wont show it*/
            FXMLLoader loader = new FXMLLoader();
            URL xmlUrl = getClass().getResource("/sample/views/PlayListView.fxml");
            loader.setLocation(xmlUrl);

            try {
                loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            PlayListView pv = loader.getController();
            pv.setpLayListViewName(rs.getString(2));


            try {

                pv.addPlayListSongs(rs.getString(2));             //this will add the songs to the playListview
            } catch (Exception e) {
                e.printStackTrace();
            }


            playListButtonsLoader.add(loader);                  //this will add the loaders of the playList views to a arraylist




        }
    }

    private void showPlayLists() {
        currentButton=playListMainButton.getText();
        changeBoarderOfTheButton();
    }

    private void showRecentSongs() {
        currentButton=recent.getText();
        changeBoarderOfTheButton();
    }


    @FXML
    void createNewPlayList(MouseEvent event) throws IOException {

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
        corp.setNewORRename("New");

        Parent root = loader2.getRoot();

        Scene scene = new Scene(root);

        stage.initModality(Modality.APPLICATION_MODAL);                     //cant go to back stage without closing this stage
        stage.initStyle(StageStyle.UNDECORATED);

        GaussianBlur blur = new GaussianBlur(); //
        this.root.setEffect(blur);

        stage.setScene(scene);
        //stage.initStyle(StageStyle.UTILITY);//                            this will hide the task bar icon for this stage...but we cant give two stage styles to the same stage



        stage.showAndWait();


    }

    //this will play the playList(on context menu play option's click)
    public void playPlayList(String playListName) {
        checkPlayList=true;

        //this will add the current songs in the playList to the arraylist of songs in the playlist /
        for(FXMLLoader loader:playListButtonsLoader){
            PlayListView pv = loader.getController();
            if(playListName.equals(pv.getpLayListViewName())){
                    currentSongsInThePlayList.setAll (pv.songsInThePlayList);
            }
        }



        int i = 0;
        Media md;
        if(mp!=null){
            mp.stop();
            mpMethod(mp,"stop");
        }




        for(String currentPlayListSong:currentSongsInThePlayList) {
            i++;                                                                //teloinh that only one time loopoing is enough becoz we are playing only the first sog in the arraylist
            if (i == 1) {
                for (FXMLLoader cell : currentController) {                     //could have done this without cells(only with the songs in the playlist) but.there is a need for cell controller(to get the end time of the song)
                    Cell songscell = cell.getController();

                    if (currentPlayListSong.equals(songscell.getSong())) {
                        md = new Media(new File(songscell.getFile().getPath()).toURI().toString());
                        mp = new MediaPlayer(md);

                        mp.setVolume(volumeSlider.getValue() / 100);
                        mp.play();


                        endTime.setVisible(true);
                        endTime.setText(songscell.getTime());


                        seekPBandCT();

                        mpMethod(mp, "play");                                   //telling this method to change the song's cell color

                        timeSlider.setValue(0);                                      //positioning the slider to start

                        playPause.setGlyphName("PAUSE");                             //changing font awsome icon to pause

                        play = false;


                    }
                }

            }
        }
    }




    /*playing the specific song when the vbox(cell)(Songs cell playbuttn) is clicked*/
    public void setSong(String name,String time,boolean playListCheck,String playListName){
        checkPlayList=playListCheck;

        if(checkPlayList&&playListName!=null){
            for(FXMLLoader loader:playListButtonsLoader){
                PlayListView pv = loader.getController();
                if(playListName.equals(pv.getpLayListViewName())){
                    currentSongsInThePlayList.setAll (pv.songsInThePlayList);
                }
            }
        }

        Media md;
        songFromTheCell = name;

        if(mp!=null){
            mp.stop();
            mpMethod(mp,"stop");
            mp=null;                    //Y? because 2nd time cell(inoru song a) la play pannakola cell da pudhya controller um play panni kndu ikum,palaya ocntrollewrum play panni kpndu ikum
        }


        md = new Media(new File(songFromTheCell).toURI().toString());
        mp = new MediaPlayer(md);


        mp.setVolume(volumeSlider.getValue()/100);
        mp.play();


        endTime.setVisible(true);
        endTime.setText(time);


        seekPBandCT();

        mpMethod(mp,"play");                                   //telling this method to change the song's cell color

        timeSlider.setValue(0);                                      //positioning the slider to start

        playPause.setGlyphName("PAUSE");                             //changing font awsome icon to pause

        play=false;



    }

    @FXML
    public void  play() {


        if (play == true) {
            mp.setVolume(volumeSlider.getValue()/100);
            mp.play();
            seekPBandCT();
            mpMethod(mp,"play");                                                   //telling this method to change the song's cell color
            playPause.setGlyphName("PAUSE");            //changing font awsome icon
            play=false;


        }else {
            mp.pause();
            playPause.setGlyphName("PLAY");             //changing font awsome icon

            play = true;
        }

    }





    //if the song in the media file and song in the arraylist are same...i++ and loop...thn if i=1 (we will get the next song)
    @FXML
    void next() {

        int index;



        if(checkPlayList){

            Media m;
            mp.stop();
            mpMethod(mp, "stop");


            for (String s : currentSongsInThePlayList) {


                if (s.equals(getSongNameFromMedia())) {

                    index = currentSongsInThePlayList.indexOf(s);
                    if(currentSongsInThePlayList.size()-1==index){

                        index=0;
                    }
                    else{

                        index=index+1;
                    }


                    String s2 = currentSongsInThePlayList.get(index);

                    m = new Media(new File(getPathOfThisSong(s2)).toURI().toString());
                    mp = new MediaPlayer(m);

                    timeSlider.setValue(0);                                                       //positioning the slider to start
                    mp.setVolume(volumeSlider.getValue() / 100);
                    mp.play();

                    playPause.setGlyphName("PAUSE");
                    mpMethod(mp, "play");                                                   //telling this method to change the song's cell color

                    seekPBandCT();                                               //seek progress bar and current time
                    mp.setOnReady(new Runnable() {                              // setyting the correct end time.. mp.getTotalDuration returns UNKNOWN untill it comes inside this setONREADY method(Must)

                        @Override
                        public void run() {
                            endTime.setVisible(true);
                            endTime.setText(convertDurationMillis((int) mp.getTotalDuration().toMillis()));             //convertDurationMills is a method of mine which returns the correct end time... if we try to set the end time like this--> mp.getduration.toMinutes(it gives a the minutes wrong) thts y i created that mathod to get the right enmd time
                        }
                    });

                    break;
                }


            }


        }
        else {

            Media md;
            mp.stop();
            mpMethod(mp, "stop");

            for (Songs s : songsAll) {                                                                //STEP1 looping through all the songs in the songList


                if (s.getTitle().getName().equals(getSongNameFromMedia())) {
                    index = songsAll.indexOf(s);

                    if(songsAll.size()-1==index){

                        index=0;
                    }
                    else{

                        index=index+1;
                    }

                    Songs s2 = songsAll.get(index);
                    md = new Media(new File(s2.getTitle().getPath()).toURI().toString());
                    mp = new MediaPlayer(md);

                    timeSlider.setValue(0);                                                       //positioning the slider to start
                    mp.setVolume(volumeSlider.getValue() / 100);
                    mp.play();

                    playPause.setGlyphName("PAUSE");
                    mpMethod(mp, "play");                                                   //telling this method to change the song's cell color

                    seekPBandCT();                                               //seek progress bar and current time
                    mp.setOnReady(new Runnable() {                              // setyting the correct end time.. mp.getTotalDuration returns UNKNOWN untill it comes inside this setONREADY method(Must)

                        @Override
                        public void run() {
                            endTime.setVisible(true);
                            endTime.setText(convertDurationMillis((int) mp.getTotalDuration().toMillis()));             //convertDurationMills is a method of mine which returns the correct end time... if we try to set the end time like this--> mp.getduration.toMinutes(it gives a the minutes wrong) thts y i created that mathod to get the right enmd time
                        }
                    });

                    break;
                }


            }    //end of foreach
        }
    }





    @FXML
    void previous(MouseEvent event) {
        Media md;
        String song;

        mp.stop();

        mpMethod(mp, "stop");


        int index = 0;


        if (checkPlayList) {
            Media m;
            mp.stop();
            mpMethod(mp, "stop");


            for (String s : currentSongsInThePlayList) {


                if (s.equals(getSongNameFromMedia())) {

                    index = currentSongsInThePlayList.indexOf(s);
                    if (index==0) {

                        index=currentSongsInThePlayList.size()-1;
                    } else {


                        index = index - 1;
                    }


                    String s2 = currentSongsInThePlayList.get(index);

                    m = new Media(new File(getPathOfThisSong(s2)).toURI().toString());
                    mp = new MediaPlayer(m);

                    timeSlider.setValue(0);                                                       //positioning the slider to start
                    mp.setVolume(volumeSlider.getValue() / 100);
                    mp.play();

                    playPause.setGlyphName("PAUSE");
                    mpMethod(mp, "play");                                                   //telling this method to change the song's cell color

                    seekPBandCT();                                               //seek progress bar and current time
                    mp.setOnReady(new Runnable() {                              // setyting the correct end time.. mp.getTotalDuration returns UNKNOWN untill it comes inside this setONREADY method(Must)

                        @Override
                        public void run() {
                            endTime.setVisible(true);
                            endTime.setText(convertDurationMillis((int) mp.getTotalDuration().toMillis()));             //convertDurationMills is a method of mine which returns the correct end time... if we try to set the end time like this--> mp.getduration.toMinutes(it gives a the minutes wrong) thts y i created that mathod to get the right enmd time
                        }
                    });

                    break;
                }


            }


        } else{

            for (Songs s : songsAll) {                                                                //STEP1 looping through all the songs in the songList

                if (s.getTitle().getName().equals(getSongNameFromMedia())) {                                         //chedcking whether current song in the media and current song in the list are same, iF its true i=1, so that in the next loop we'll get the next song in the list
                    index = songsAll.indexOf(s);

                    if (index == 0) {

                        index = songsAll.size() - 1;
                    } else {

                        index = index - 1;
                    }

                    Songs s2 = songsAll.get(index);

                    md = new Media(new File(s2.getTitle().getPath()).toURI().toString());
                    mp = new MediaPlayer(md);

                    timeSlider.setValue(0);                                                       //positioning the slider to start
                    mp.setVolume(volumeSlider.getValue() / 100);                                     //sets the starting volume for the song as in the volume slider
                    mp.play();
                    seekPBandCT();
                    mpMethod(mp, "play");                                                   //telling this method to change the song's cell color
                    playPause.setGlyphName("PAUSE");
                    mp.setOnReady(new Runnable() {                              // setting the correct end time.. mp.getTotalDuration returns UNKNOWN untill it comes inside this setONREADY method(Must)

                        @Override
                        public void run() {
                            endTime.setVisible(true);
                            endTime.setText(convertDurationMillis((int) mp.getTotalDuration().toMillis()));             //convertDurationMills is a method of mine which returns the correct end time... if we try to set the end time like this--> mp.getduration.toMinutes(it gives a the minutes wrong) thts y i created that mathod to get the right enmd time
                        }
                    });

                    break;

                }


            }    //end of foreach
         }
    }


    @FXML
    void loop(MouseEvent event) {

        if(repeat){
            mp.setCycleCount(MediaPlayer.INDEFINITE);
            repeatButton.setFill(Color.ORANGE);
            repeat = false;
        }else{
            mp.setCycleCount(1);
            repeatButton.setFill(Color.WHITE);
            repeat = true;
        }
    }


    void changeBoarderOfTheButton(){
        buttonClickCount++;



        if(buttonClickCount==1){
            for(Button btn:listOfButtonsArray){
                if(btn.getText().equals(currentButton)){
                    btn.setStyle(""                                         //there is no way to style and keep the button in the same style state after a its clicked in css so java code used
                            + "-fx-border-color:#FF7F50;"
                            +"-fx-border-width:0px 0px 0px 5px");
                    oldButton=btn.getText();
                }
            }

        }
        else{
            for(Button btn:listOfButtonsArray){
                if(btn.getText().equals(oldButton)){
                    btn.setStyle(""
                            + "-fx-border-color:none;"
                            +"-fx-border-width:none");
                }
            }

            for(Button btn:listOfButtonsArray){
                if(btn.getText().equals(currentButton)){

                    btn.setStyle(""
                            + "-fx-border-color:#FF7F50;"
                            +"-fx-border-width:0px 0px 0px 5px"
                            );
                    oldButton=btn.getText();
                }
            }

        }



    }

    public void deletBoarderOfTheButton(){


                listOfButtonsArray.get(0).setStyle(""
                        + "-fx-border-color:#FF7F50;"
                        +"-fx-border-width:0px 0px 0px 5px"
                );
                oldButton=listOfButtonsArray.get(0).getText();


    }






    //Method Definitions

    //calling this method when a media(song is played), so from this method i can tell the current controller of the song to change its color
    private void mpMethod(MediaPlayer mp,String status) {
        Cell cell;
        String song = null;


            song =getSongNameFromMedia();


            for(FXMLLoader loader:currentController){               //thjs will change the color of the Commonsongs(cell)

                cell = loader.getController();

                if(cell.getSong().equals(song)){

                    if(status.equals("play")) {
                        cell.youArePlaying();
                    }
                    else if(status.equals("stop")) {
                        cell.youAreStopped();
                    }

                }
            }



            for(FXMLLoader loader:playListButtonsLoader){           //thjs will change the color of the playListSOngs(cell)

                PlayListView pv = loader.getController();

                for(Cell songPlayListCell:pv.songsInThePlayListCell){
                    if(song.equals(songPlayListCell.getSong())){

                        if(status.equals("play")) {
                            songPlayListCell.youArePlaying();
                        }
                        else if(status.equals("stop")) {
                            songPlayListCell.youAreStopped();
                        }

                    }

                }



            }


    }



    /*seeking progress bar and setting the current time on label
     * */
    void seekPBandCT(){
        mp.currentTimeProperty().addListener((observable, oldValue, newValue) -> {

            Double oldValueDouble = oldValue.toMillis();
            Double newValueDouble = newValue.toMillis();

            Duration duration = mp.getTotalDuration();



            if (Math.abs(oldValueDouble - newValueDouble) > 50) {
                Duration currentDuration = mp.getCurrentTime();
                Double durationDouble = duration.toSeconds();
                Double currentDurationDouble = currentDuration.toSeconds();

                timeSlider.setValue(100.0 * currentDurationDouble / durationDouble);
                currentTime.setVisible(true);
                currentTime.setText(String.format("%02d:%02d",
                        (int) (currentDurationDouble / 60),
                        (int) (currentDurationDouble % 60))
                );
            }

        });

        mp.setOnEndOfMedia(() -> {                  //only place to put this code is here , cant put this on initialize method(at initilize mp=null)
            next();
        });
    }


    //this returns the correct duration(ENd Time)
    public String convertDurationMillis(Integer getDurationInMillis){
        int getDurationMillis = getDurationInMillis;

        String convertHours = String.format("%02d", TimeUnit.MILLISECONDS.toHours(getDurationMillis));
        String convertMinutes = String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(getDurationMillis) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(getDurationMillis))); //I needed to add this part.
        String convertSeconds = String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(getDurationMillis) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(getDurationMillis)));


        String getDuration = convertHours + ":" + convertMinutes + ":" + convertSeconds;

        return getDuration;

    }


    /*sets a song's meta data in the cell
     * */
    public void setCellInfo(Songs s, Cell cell) {
        Media media;
        MediaPlayer mediaPlayer ;

        mediaPlayer = new MediaPlayer(new Media(new File(s.getTitle().getPath()).toURI().toString()));             //to get the meta data of the song Step1

        mediaPlayer.getMedia().getMetadata().addListener(new MapChangeListener<String, Object>() {           //Step2
            @Override
            public void onChanged(Change<? extends String, ? extends Object> ch) {
                if (ch.wasAdded()) {
                    if (ch.getKey().equals("album")) {
                        cell.setAlbum(ch.getValueAdded().toString());
                    } else if (ch.getKey().equals("artist")) {
                        cell.setSinger(ch.getValueAdded().toString());
                    }

                }
            }

        });

        MediaPlayer finalMediaPlayer = mediaPlayer;
        mediaPlayer.setOnReady(new Runnable() {                              // settting the correct end time.. mp.getTotalDuration returns UNKNOWN untill it comes inside this setONREADY method(Must)
            @Override
            public void run() {
                cell.setTime(convertDurationMillis((int) finalMediaPlayer.getTotalDuration().toMillis()));         //convertDurationMills is a method of mine which returns the correct end time... if we try to set the end time like this--> mp.getduration.toMinutes(it gives a the minutes wrong) thts y i created that mathod to get the right enmd time
            }
        });

        mediaPlayer=null;
        media=null;



        cell.setSong(s.title);                              //setting the song name

        if(cell.getAlbum().getText().isEmpty()){            //if there were no meta data for ap particular song...displaying the label as unknown
            cell.setAlbum("UnKnown Album");
        }
        if(cell.getSinger().getText().isEmpty()){
            cell.setSinger("UnKnown Artist");
        }

    }





    private String getSongNameFromMedia(){
        String songName = null;

        String mdSource[]=mp.getMedia().getSource().split("/");                            //getting the name of the song from the media path
        for(String a:mdSource) {
            songName =a.replaceAll("%20"," ");                                   //a returns the name of the song(only few sonsg) as aagayam%20thaayaga%20nee%20naan%20  so i am getting rid of that %20

        }

        return songName;
    }



    public String getPathOfThisSong(String songName){

        for(Songs s:songsAll){

            String Source = s.getTitle().getName();

                if(songName.equals(Source)){
                    songName= s.getTitle().getPath();
                }

        }

        return songName;
    }



    public AnchorPane getroot() {
        return root;
    }







    @FXML
    void playlist(ContextMenuEvent event) {

    }



    @FXML
    void volume(MouseEvent event) {

    }












    //Undeccorated Stage
    @FXML
    void close(ActionEvent event) {
        System.exit(0);
    }
    @FXML
    void minimize(ActionEvent event) {
        Stage s = (Stage) playPause.getScene().getWindow();
        s.setIconified(true);
    }

    @FXML
    void maximize(ActionEvent event) {
        Stage s = (Stage) playPause.getScene().getWindow();
        if (max) {

            max=false;


            /*maxiizng screen hides the task bar... so with this code i can override that and show the taskbar*/
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            s.setX(primaryScreenBounds.getMinX());
            s.setY(primaryScreenBounds.getMinY());

            s.setMaxWidth(primaryScreenBounds.getWidth());
            s.setMinWidth(primaryScreenBounds.getWidth());

            s.setMaxHeight(primaryScreenBounds.getHeight());
            s.setMinHeight(primaryScreenBounds.getHeight());

            s.setMaximized(true);



        } else {
            max=true;
            s.setMaximized(false);

        }
    }




    //Drag stage
    @FXML
    void makeDrag(MouseEvent event) {
        Stage s = (Stage) playPause.getScene().getWindow();
        x = s.getX() - event.getScreenX();
        y = s.getY() - event.getScreenY();
    }

    @FXML
    void drag(MouseEvent event) {

        Stage s = (Stage) playPause.getScene().getWindow();
        s.setX(event.getScreenX() + x);
        s.setY(event.getScreenY() + y);
    }



}



