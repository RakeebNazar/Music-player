package sample.databaseHandler;



import java.sql.*;

public class DAO {

    Connection con = null;



    public  void connector() throws Exception {
        //Class.forName("org.sql.JDBC");

        String url = "jdbc:mysql://localhost:3306/playlist";

        con = DriverManager.getConnection(url,"root","bios");


    }


    public void insertPlaylist (String name) throws Exception {

        connector();

        String Query = "insert into playList (name) values(?)";
        PreparedStatement ps = con.prepareStatement(Query);
        ps.setString(1,name);


        ps.executeUpdate();
        con.close();

    }

    public void deletePlaylist (String name) throws Exception {

        connector();

        String Query = "delete from playList where playList.name=?";
        PreparedStatement ps = con.prepareStatement(Query);
        ps.setString(1,name);


        ps.executeUpdate();
        con.close();

    }

    public void renamePlaylist (String newName,String olName )  {

        try {
            connector();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String Query = "update playList set name = ? where playList.name = ?";
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(Query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            ps.setString(1,olName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            ps.setString(2,newName);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        try {
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public ResultSet getPlayListName() throws Exception {
        connector();

        String Query = "select * from playlist";
        PreparedStatement ps = con.prepareStatement(Query);



        ResultSet rs = ps.executeQuery();

        return rs;
    }

    public ResultSet getPlayListSongs(String playListName) throws Exception {
        connector();

        String Query = "SELECT songs.name from playlist_song inner join playlist on playlist_song.idplaylist = playlist.idplaylist  " +
                       "inner join songs on playlist_song.idsong = songs.idsong where playlist.name = ?";
        PreparedStatement ps = con.prepareStatement(Query);
        ps.setString(1,playListName);


        ResultSet rs = ps.executeQuery();

        return rs;
    }

    public int getPlayListId(String name)  {
        int id = 0;
        try {
            connector();
        } catch (Exception e) {
            e.printStackTrace();
        }


        String Query = "select  playlist.idplaylist from playlist where playlist.name = ?";
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(Query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            ps.setString(1,name);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        while(true){
            try {
                if (!rs.next()) break;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                id = rs.getInt(1);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return id;
    }

    public int getSongId(String name)  {
        int id = 0;
        try {
            connector();
        } catch (Exception e) {
            e.printStackTrace();
        }


        String Query = "select  songs.idsong from songs where songs.name = ?";
        PreparedStatement ps = null;
        try {
            ps = con.prepareStatement(Query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            ps.setString(1,name);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        ResultSet rs = null;
        try {
            rs = ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        while(true){
            try {
                if (!rs.next()) break;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                id = rs.getInt(1);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return id;
    }


    public void addSongToPlayList(int playListId, int songId) throws Exception {

        connector();

        String Query = "insert into `playlist`.`playlist_song` (`idplaylist`, `idsong`) values (?, ?)";
        PreparedStatement ps = null;

            ps = con.prepareStatement(Query);

            ps.setInt(1,playListId);

            ps.setInt(2,songId);
           ps.executeUpdate();
           con.close();

    }



    public void addSongToDatabase(String song) throws Exception {
        connector();

        String Query = "insert into songs (name) values(?)";
        PreparedStatement ps = con.prepareStatement(Query);
        ps.setString(1,song);


        ps.executeUpdate();
        con.close();
    }
}