package com.codegym.dao;
import com.codegym.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class UserDAO implements IUserDAO {
 private static String config(String key,String fallback){String value=System.getenv(key);return value==null?fallback:value;}
 protected Connection getConnection() throws SQLException {
  try {Class.forName("com.mysql.cj.jdbc.Driver");}catch(ClassNotFoundException ex){throw new SQLException("MySQL JDBC driver missing",ex);}
  return DriverManager.getConnection(config("DB_URL","jdbc:mysql://localhost:3306/demo?serverTimezone=UTC&characterEncoding=UTF-8"),config("DB_USER","root"),config("DB_PASSWORD",""));
 }
 @Override public User getUserById(int id){
  try(Connection c=getConnection();CallableStatement s=c.prepareCall("{CALL get_user_by_id(?)}")){
   s.setInt(1,id);try(ResultSet r=s.executeQuery()){return r.next()?new User(id,r.getString("name"),r.getString("email"),r.getString("country")):null;}
  }catch(SQLException ex){throw new IllegalStateException("Cannot load user",ex);}
 }
 @Override public void insertUserStore(User user) throws SQLException {
  try(Connection c=getConnection();CallableStatement s=c.prepareCall("{CALL insert_user(?, ?, ?)}")){
   s.setString(1,user.getName());s.setString(2,user.getEmail());s.setString(3,user.getCountry());s.executeUpdate();
  }
 }
 @Override public User selectUser(int id){return getUserById(id);}
 @Override public void insertUser(User user) throws SQLException{insertUserStore(user);}
 @Override public List<User> selectAllUsers(){
  try(Connection c=getConnection();PreparedStatement s=c.prepareStatement("SELECT id,name,email,country FROM users ORDER BY id");ResultSet r=s.executeQuery()){
   List<User> result=new ArrayList<>();while(r.next())result.add(new User(r.getInt("id"),r.getString("name"),r.getString("email"),r.getString("country")));return result;
  }catch(SQLException ex){throw new IllegalStateException("Cannot list users",ex);}
 }
 @Override public boolean deleteUser(int id) throws SQLException{
  try(Connection c=getConnection();PreparedStatement s=c.prepareStatement("DELETE FROM users WHERE id=?")){s.setInt(1,id);return s.executeUpdate()>0;}
 }
 @Override public boolean updateUser(User u) throws SQLException{
  try(Connection c=getConnection();PreparedStatement s=c.prepareStatement("UPDATE users SET name=?,email=?,country=? WHERE id=?")){s.setString(1,u.getName());s.setString(2,u.getEmail());s.setString(3,u.getCountry());s.setInt(4,u.getId());return s.executeUpdate()>0;}
 }
 @Override public void addUserTransaction(User user, int[] permissionIds) throws SQLException {
  try (Connection connection = getConnection()) {
   boolean originalAutoCommit = connection.getAutoCommit();
   connection.setAutoCommit(false);
   try {
    int userId;
    try (PreparedStatement statement = connection.prepareStatement(
       "INSERT INTO users (name,email,country) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
     statement.setString(1,user.getName()); statement.setString(2,user.getEmail()); statement.setString(3,user.getCountry());
     if(statement.executeUpdate()!=1) throw new SQLException("User was not inserted");
     try(ResultSet keys=statement.getGeneratedKeys()) {
      if(!keys.next()) throw new SQLException("Missing generated user ID");
      userId=keys.getInt(1);
     }
    }
    if(permissionIds!=null && permissionIds.length>0) {
     try(PreparedStatement assignment=connection.prepareStatement(
        "INSERT INTO user_permission (user_id,permission_id) VALUES (?,?)")) {
      for(int permissionId:permissionIds) {
       assignment.setInt(1,userId); assignment.setInt(2,permissionId); assignment.executeUpdate();
      }
     }
    }
    connection.commit();
   } catch(SQLException | RuntimeException failure) {
    try { connection.rollback(); } catch(SQLException rollbackFailure) { failure.addSuppressed(rollbackFailure); }
    throw failure;
   }
   // Restore only after a successful commit. On failure the connection is closed;
   // enabling auto-commit after a failed rollback could commit partial data.
   connection.setAutoCommit(originalAutoCommit);
  }
 }
 private static final String SQL_INSERT="INSERT INTO Employee(name,salary,created_Date) VALUES(?,?,?)";
 private static final String SQL_UPDATE="UPDATE Employee SET salary=? WHERE name=?";
 private static final String SQL_TABLE_CREATE="CREATE TABLE Employee(id INT AUTO_INCREMENT PRIMARY KEY,name VARCHAR(100) NOT NULL,salary DECIMAL(15,2) NOT NULL,created_Date TIMESTAMP)";
 private static final String SQL_TABLE_DROP="DROP TABLE IF EXISTS Employee";
 @Override public void insertUpdateWithoutTransaction() throws SQLException {
  try(Connection conn=getConnection();Statement statement=conn.createStatement()) {
   // Exercise-only table: each run recreates Employee. Never use production data.
   conn.setAutoCommit(true);
   statement.execute(SQL_TABLE_DROP);
   statement.execute(SQL_TABLE_CREATE);
   try(PreparedStatement psInsert=conn.prepareStatement(SQL_INSERT);
       PreparedStatement psUpdate=conn.prepareStatement(SQL_UPDATE)) {
    psInsert.setString(1,"Quynh");
    psInsert.setBigDecimal(2,new java.math.BigDecimal("10"));
    psInsert.setTimestamp(3,Timestamp.valueOf(java.time.LocalDateTime.now()));
    psInsert.executeUpdate();
    psInsert.setString(1,"Ngan");
    psInsert.setBigDecimal(2,new java.math.BigDecimal("20"));
    psInsert.setTimestamp(3,Timestamp.valueOf(java.time.LocalDateTime.now()));
    psInsert.executeUpdate();
    // Intentional bug required by the exercise: salary parameter 1 is unset.
    psUpdate.setBigDecimal(2,new java.math.BigDecimal("999.99"));
    psUpdate.setString(2,"Quynh");
    psUpdate.executeUpdate();
   }
  }
 }
}