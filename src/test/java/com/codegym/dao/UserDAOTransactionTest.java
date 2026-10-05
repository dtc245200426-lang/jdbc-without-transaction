package com.codegym.dao;
import com.codegym.model.User;
import java.sql.*;
import org.junit.*;
import static org.junit.Assert.*;
public class UserDAOTransactionTest {
 private String url;
 private UserDAO dao;
 @Before public void setup() throws Exception {
  url="jdbc:h2:mem:"+java.util.UUID.randomUUID()+";MODE=MySQL;DB_CLOSE_DELAY=-1";
  dao=new UserDAO(){@Override protected Connection getConnection() throws SQLException {return DriverManager.getConnection(url);}};
  try(Connection c=DriverManager.getConnection(url);Statement s=c.createStatement()) {
   s.execute("CREATE TABLE users(id INT AUTO_INCREMENT PRIMARY KEY,name VARCHAR(50),email VARCHAR(50),country VARCHAR(50))");
   s.execute("CREATE TABLE permission(id INT PRIMARY KEY)");
   s.execute("INSERT INTO permission VALUES(1),(2),(3),(4)");
   s.execute("CREATE TABLE user_permission(user_id INT REFERENCES users(id),permission_id INT REFERENCES permission(id),PRIMARY KEY(user_id,permission_id))");
  }
 }
 private int count(String table) throws Exception {try(Connection c=DriverManager.getConnection(url);Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT COUNT(*) FROM "+table)){r.next();return r.getInt(1);}}
 @Test public void commitsUserAndBothPermissions() throws Exception {
  dao.addUserTransaction(new User("A","a@example.com","VN"),new int[]{1,4});
  assertEquals(1,count("users")); assertEquals(2,count("user_permission"));
  try(Connection c=DriverManager.getConnection(url);Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT COUNT(*) FROM users u JOIN user_permission p ON u.id=p.user_id WHERE p.permission_id IN (1,4)")){r.next();assertEquals(2,r.getInt(1));}
 }
 @Test public void invalidPermissionRollsBackUserAndEarlierAssignment() throws Exception {
  try{dao.addUserTransaction(new User("A","a@example.com","VN"),new int[]{1,999});fail("Expected FK failure");}catch(SQLException expected){}
  assertEquals(0,count("users"));assertEquals(0,count("user_permission"));
 }
 @Test public void duplicatePermissionRollsBackEverything() throws Exception {
  try{dao.addUserTransaction(new User("A","a@example.com","VN"),new int[]{1,1});fail("Expected duplicate key");}catch(SQLException expected){}
  assertEquals(0,count("users"));assertEquals(0,count("user_permission"));
 }
 @Test public void optionalPermissionsAllowUserOnly() throws Exception {
  dao.addUserTransaction(new User("A","a@example.com","VN"),null);
  assertEquals(1,count("users"));assertEquals(0,count("user_permission"));
 }
}