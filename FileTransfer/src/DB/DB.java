package DB;


import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DB { //创建 HandleSql 类
    static Connection con; //声明 Connection 对象
    static PreparedStatement pStmt;//声明预处理 PreparedStatement 对象
    static ResultSet res;//声明结果 ResultSet 对象
    static String url = "jdbc:mysql://localhost:3306/mydatabase?serverTimezone=UTC";
    static String user = "root";
    static String password = "19990214";


    public Connection getConnection() {//建立返回值为 Connection 的方法
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("数据库驱动加载成功");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            con = DriverManager.getConnection(url,user,password);
            System.out.println("数据库连接成功");
            //con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }
    public static void main(String[] args) {//主方法
    	/*
        //Main h = new Main();//创建本类对象
    	DB h=new DB();
        con=h.getConnection();
        
        String testidc="12345678";
        if(FindID(testidc)!=null) {
        	System.out.print("对应密码为：");
        	System.out.println(FindID(testidc));
        }
       else System.out.println("该idc不存在");
        Insert("58967894","nihoa");
        System.out.println();
        */
         
    }

    public String FindID(String idc){//如果idc存在，则返回密码，如果不存在，则返回null

        try {
            Statement state = con.createStatement();
            String sql="select Kc from myusers where IDc='"+idc+"'";
            System.out.println("FindIDidc:"+idc);
            System.out.println("FindIDsql:"+sql);
            
            ResultSet re=state.executeQuery(sql);
            if(re.next()){
                //con.close();
                return re.getString(1);
            }
            else{
                //con.close();
                System.out.println("该idc不存在");
                return null;
            }
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public  boolean Insert(String idc,String kc){//插入数据，如果数据存在则返回false，插入成功则返回true
        try {
            Statement state=con.createStatement();
            if(FindID(idc) == null)//如果数据库中不存在该idc，则进行插入
            {
                String sql="insert into myusers values('"+idc+"','"+kc+"')";   //SQL语句
                state.executeUpdate(sql);         //将sql语句上传至数据库执行
                
                System.out.println("Insertidc:"+idc);
                System.out.println("Insertsql:"+sql);
                //con.close();
                System.out.println("true");
                return true;
            }
            else{
                System.out.println("数据已存在，不能插入");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}




