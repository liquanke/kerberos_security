package DB;


import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class DB { //���� HandleSql ��
    static Connection con; //���� Connection ����
    static PreparedStatement pStmt;//����Ԥ���� PreparedStatement ����
    static ResultSet res;//������� ResultSet ����
    static String url = "jdbc:mysql://localhost:3306/mydatabase?serverTimezone=UTC";
    static String user = "root";
    static String password = "19990214";


    public Connection getConnection() {//��������ֵΪ Connection �ķ���
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("���ݿ��������سɹ�");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            con = DriverManager.getConnection(url,user,password);
            System.out.println("���ݿ����ӳɹ�");
            //con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return con;
    }
    public static void main(String[] args) {//������
    	/*
        //Main h = new Main();//�����������
    	DB h=new DB();
        con=h.getConnection();
        
        String testidc="12345678";
        if(FindID(testidc)!=null) {
        	System.out.print("��Ӧ����Ϊ��");
        	System.out.println(FindID(testidc));
        }
       else System.out.println("��idc������");
        Insert("58967894","nihoa");
        System.out.println();
        */
         
    }

    public String FindID(String idc){//���idc���ڣ��򷵻����룬��������ڣ��򷵻�null

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
                System.out.println("��idc������");
                return null;
            }
        }catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public  boolean Insert(String idc,String kc){//�������ݣ�������ݴ����򷵻�false������ɹ��򷵻�true
        try {
            Statement state=con.createStatement();
            if(FindID(idc) == null)//������ݿ��в����ڸ�idc������в���
            {
                String sql="insert into myusers values('"+idc+"','"+kc+"')";   //SQL���
                state.executeUpdate(sql);         //��sql����ϴ������ݿ�ִ��
                
                System.out.println("Insertidc:"+idc);
                System.out.println("Insertsql:"+sql);
                //con.close();
                System.out.println("true");
                return true;
            }
            else{
                System.out.println("�����Ѵ��ڣ����ܲ���");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}




