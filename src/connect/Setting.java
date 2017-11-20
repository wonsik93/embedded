package connect;

public class Setting {
	final static String DBURL = "jdbc:mysql://localhost/imbeded";
	final static String DB_USERID = "root";
	final static String DB_USERPW = "raspberry";
	
	final static DBHandler dbhandler = new DBHandler(Setting.DBURL, Setting.DB_USERID, Setting.DB_USERPW);
}
