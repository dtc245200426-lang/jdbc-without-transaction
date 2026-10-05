package com.codegym.dao;
import java.sql.*;
import java.math.BigDecimal;
import org.junit.Test;
import static org.junit.Assert.*;
public class WithoutTransactionTest {
 @Test public void failedUpdateLeavesBothCommittedInserts() throws Exception {
  String url="jdbc:h2:mem:"+java.util.UUID.randomUUID()+";MODE=MySQL;DB_CLOSE_DELAY=-1";
  UserDAO dao=new UserDAO(){@Override protected Connection getConnection() throws SQLException {return DriverManager.getConnection(url);}};
  try{dao.insertUpdateWithoutTransaction();fail("Expected missing parameter error");}
  catch(SQLException expected){assertTrue(expected.getMessage().contains("1"));}
  try(Connection c=DriverManager.getConnection(url);Statement s=c.createStatement();ResultSet r=s.executeQuery("SELECT name,salary FROM Employee ORDER BY id")) {
   assertTrue(r.next());assertEquals("Quynh",r.getString(1));assertEquals(new BigDecimal("10.00"),r.getBigDecimal(2));
   assertTrue(r.next());assertEquals("Ngan",r.getString(1));assertEquals(new BigDecimal("20.00"),r.getBigDecimal(2));
   assertFalse(r.next());
  }
 }
}