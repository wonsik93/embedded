package connect;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Random;
import java.net.InetAddress;
import java.net.NetworkInterface;

public class NodeConnect {
	private HttpURLConnection conn;
	private Connection connect;
	PreparedStatement preparedStatement = null;
	private String String_URL = "";
	private ArrayList<String> iplist;

	public NodeConnect(String url) {
		this.String_URL = url;
	}

	public String generatePublicKey() {
		Random random = new Random();
		return String.valueOf(random.nextInt());
	}

	public String getEncodedString(Properties params) {
		StringBuffer text = new StringBuffer(256);
		Enumeration names = params.propertyNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			String value = params.getProperty(name);
			try {
				text.append(URLEncoder.encode(name, "utf-8") + "=" + URLEncoder.encode(value, "utf-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (names.hasMoreElements())
				text.append("&");
		}
		return text.toString();
	}

	public String getLocalServerIP() {
		String myIP = "";
		boolean isConnected = false;
		InetAddress inetAddress = null;
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIP = intf.getInetAddresses(); enumIP.hasMoreElements();) {
					inetAddress = enumIP.nextElement();
					if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()
							&& inetAddress.isSiteLocalAddress()) {
						isConnected = true;
						myIP = inetAddress.toString().substring(1);

					}

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return myIP;
	}

	public void contactMainServer() {
		Thread th = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Properties params = new Properties();
					params.setProperty("name", "raspberry1");
					params.setProperty("publicKey", generatePublicKey());
					params.setProperty("ip", getLocalServerIP());
					String EncodedString = getEncodedString(params);
					URL url = new URL(String_URL + "/newUser");
					conn = (HttpURLConnection) url.openConnection();
					conn.setRequestMethod("POST");
					conn.setDoOutput(true);
					conn.setDoInput(true);
					conn.setUseCaches(false);
					conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
					conn.connect();

					DataOutputStream out = new DataOutputStream(conn.getOutputStream());
					out.writeBytes(EncodedString);
					out.flush();
					out.close();

					BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
					StringBuilder sb = new StringBuilder();
					String line;

					while ((line = br.readLine()) != null) {
						sb.append(line + "\n");
					}
					br.close();
					Setting.dbhandler.insertJSON(sb.toString(), "INSERT INTO test VALUES (?,?,?)", "name","mykey","ip");
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					conn.disconnect();
				}
			}
		});
		th.start();
	}

	public void contactNode() {
		OpenSocket socket = new OpenSocket();
		ArrayList<String> iplist = new ArrayList<>();
		DBHandler dbhandler = new DBHandler(Setting.DBURL, Setting.DB_USERID, Setting.DB_USERPW);
		dbhandler.doQuery("SELECT ip FROM test", iplist, "ip");

		for (int i = 0; i < iplist.size(); i++) {
			socket.connClient(iplist.get(i), 5555);
		}
	}
}
