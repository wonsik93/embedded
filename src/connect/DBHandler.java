package connect;

import java.awt.List;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DBHandler {
	private String dburl;
	private String userid;
	private String userpw;
	private Connection connect;
	private PreparedStatement preparedStatement = null;

	public DBHandler(String dburl, String userid, String userpw) {
		this.dburl = dburl;
		this.userid = userid;
		this.userpw = userpw;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(dburl, userid, userpw);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void insertJSON(String jsondata, String query, String... params) {
		try {
			JSONParser jsonParser = new JSONParser();
			Object obj = jsonParser.parse(jsondata);
			JSONArray jsonarray = (JSONArray) obj;
			preparedStatement = connect.prepareStatement(query);
			for (int i = 0; i < jsonarray.size(); i++) {
				JSONObject jsonObject = (JSONObject) jsonarray.get(i);
				for (int j = 0; j < params.length; j++) {
					preparedStatement.setString(j + 1, (String) jsonObject.get(params[j]));
				}
				preparedStatement.executeUpdate();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void doQuery(String query) {
		try {
			preparedStatement = connect.prepareStatement(query);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void doQuery(String query, ArrayList list, String column) {
		try {

			preparedStatement = connect.prepareStatement(query);
			ResultSet ipset = preparedStatement.executeQuery();
			while (ipset.next()) {
				list.add(ipset.getString(column));
				System.out.println("list add : " + ipset.getString(column));
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
