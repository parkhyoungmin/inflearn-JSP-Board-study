package ibs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class IbsDAO {

	private Connection conn;
	private ResultSet rs;
	
	public IbsDAO() {
		try {
			String dbURL = "jdbc:mysql://localhost:3306/IBS";
			String dbID = "root";
			String dbPassword = "root";
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(dbURL, dbID, dbPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getDate() {
		String SQL = "SELECT NOW()";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public int getNext() {
		String SQL = "SELECT ibsID FROM IBS ORDER BY ibsID DESC";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) + 1;
			}
			return 1; // 첫 번째 게시물인 경우
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public int write(String ibsTitle, String userID, String ibsContent) {
		String SQL = "INSERT INTO IBS VALUES (?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, getNext());
			pstmt.setString(2, ibsTitle);
			pstmt.setString(3, userID);
			pstmt.setString(4, getDate());
			pstmt.setString(5, ibsContent);
			pstmt.setInt(6, 1);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	public ArrayList<Ibs> getList(int pageNumber) {
		String SQL = "SELECT * FROM IBS WHERE ibsID < ? AND ibsAvailable = 1 ORDER BY ibsID DESC LIMIT 10";
		ArrayList<Ibs> list = new ArrayList<Ibs>();
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, getNext() - (pageNumber - 1) * 10);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Ibs ibs = new Ibs();
				ibs.setIbsID(rs.getInt(1));
				ibs.setIbsTitle(rs.getString(2));
				ibs.setUserID(rs.getString(3));
				ibs.setIbsDate(rs.getString(4));
				ibs.setIbsContent(rs.getString(5));
				ibs.setIbsAvailable(rs.getInt(6));
				list.add(ibs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public boolean nextPage(int pageNumber) {
		String SQL = "SELECT * FROM IBS WHERE ibsID < ? AND ibsAvailable = 1";
		try {
			PreparedStatement pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, getNext() - (pageNumber - 1) * 10);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
}
