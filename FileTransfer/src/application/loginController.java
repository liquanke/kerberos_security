package application;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Test.ClientFuction;

public class loginController {
	//login界面控件
	@FXML
	private Button but_register;
	@FXML
	private TextField text_name;
	@FXML
	private PasswordField text_password;
	@FXML
	private Button but_login;
	@FXML
	private Button but_close;
	@FXML
	private TextArea textarea;
	@FXML
	private ListView<String> list1;
	
	
	//主界面控件
	@FXML
	private Button but_up;
	@FXML
	private Button but_del;
	@FXML
	private Button but_re;
	@FXML
	private Button bot_down;
	@FXML
	private ListView<String> list;
	@FXML
	private ProgressBar progress_1;
	@FXML
	private TextArea textarea_1;
	@FXML
	private TextArea textarea_2;
	
	private String name = null;//存储listview中选中的文件
	
	//private String Kcv1;
	//private String[] p = {"qwertyui", "12345678"};
	
	public static ClientFuction cf = new ClientFuction();
	
	/*public void initialize(URL url, ResourceBundle resources) {
        // Initialization code can go here. 
        // The parameters url and resources can be omitted if they are not needed
		cf = new ClientFuction();
		
    }*/
		
	//登陆界面功能函数
	//登陆按钮功能
	
	public void loginEvent() throws IOException {
		//得到输入框中的内容
		String[] message = new String[2];
		message[0] = text_name.getText();
		message[1] = text_password.getText();
		
		
		//调用认证功能函数
		cf.init();
		cf.textarea = this.textarea;
		boolean b = cf.verify(message[0], message[1]);
		System.out.println("认证过程生成的密钥: " + cf.Kcv);
		
		//登录
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("登录");
		alert.setHeaderText("登录成功!欢迎使用共享网盘系统\n 请用刷新按钮获取最新目录哦");
		alert.showAndWait();
		
		if (b == true) {//验证成功跳转到个人页面
			try {
				Parent anotherRoot = FXMLLoader.load(getClass().getResource("mainMenu.fxml"));
		        Stage anotherStage = new Stage();
		        anotherStage.setTitle("个人界面");
		        anotherStage.setScene(new Scene(anotherRoot, 800, 400));
		        anotherStage.show();
		     } catch (Exception e){
		            e.printStackTrace();
		       }  
		}
		else {//验证失败，弹出提示框
			Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("登陆");
			alert.setHeaderText("登陆失败!");
			alert.showAndWait();
		}
		//return message;
	}
	
	
	//注册按钮功能
	public String[] registerEvent() throws IOException {
		//获取输入框内容
		String[] message = new String[2];
		message[0] = text_name.getText();
		message[1] = text_password.getText();
		
		cf.textarea = this.textarea;
		//this.textarea.appendText("123");
		boolean b = cf.register(message[0], message[1]);
		
		if (b == true) {//注册成功，弹出提示框
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("注册");
			alert.setHeaderText("注册成功!");
			alert.showAndWait();
		}
		else {//注册失败，弹出提示框
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("注册");
			alert.setHeaderText("注册失败!");
			alert.showAndWait();
		}
		
		return message;
	}

	//关闭按钮功能
	public void closeEvent(){
		Stage stage = (Stage) but_close.getScene().getWindow();
	    stage.close();
	}

	//主页面功能函数，在下载和上传过程中输出明密文到文本框中
	
	//上传按钮功能
	public void upEvent() throws IOException, InterruptedException{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Choose File");
		
		Stage primaryStage = null;
		File file = fileChooser.showOpenDialog(primaryStage);
		String fileName = null;
		if(file != null) {
			fileName = file.getAbsolutePath();
			//System.out.println(fileName);
		}
		
		progress_1.setProgress(0.0);
		cf.textarea_1 = this.textarea_1;
		cf.textarea_2 = this.textarea_2;
		cf.pb = this.progress_1;
		boolean b = cf.uploadfile1(fileName);//调用上传文件函数
		
		if(b == true) {//先根据服务器端的返回值判断一下是否上传成功
			//list.getItems().add(fileName);
			progress_1.setProgress(1.0);
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("上传");
			alert.setHeaderText("上传成功!");
			alert.showAndWait();
		}
		else {//未上传成功，弹出错误提示框
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("上传");
			alert.setHeaderText("上传失败!");
			alert.showAndWait();
		}
		progress_1.setProgress(-100.0);
	}
	
	
	//刷新按钮功能
	public  void refreshEvent() throws IOException{
		list.getItems().clear(); 
		//先获取服务器端返回的文件列表，再进行下面的操作
		cf.textarea = this.textarea;
		cf.textarea_1 = this.textarea_1;
		cf.textarea_2 = this.textarea_2;
		
		System.out.println("刷新过程生成的密钥: " + cf.Kcv);
		String[] fileList = cf.getdirectory();
		
		if (fileList.length == 0) {//文件列表为空，弹出提示框
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("刷新");
			alert.setHeaderText("服务器端目录为空!");
			alert.showAndWait();
		}
		else {//文件列表不为空，更新文件列表
			for (int i = 0; i < fileList.length; ++i) {
		    	list.getItems().add(fileList[i]);
		    }
		}
		progress_1.setProgress(-100.0);
	    
	    //启动listview监听
	    list.getSelectionModel().selectedItemProperty().addListener(
		        (ObservableValue<? extends String> ov, String old_val, 
		            String new_val) -> {
		            	name = new_val;
		    });
	}

	//删除按钮功能
	public void delEvent() {
		//根据服务器端的返回值来进行
		cf.textarea_1 = this.textarea_1;
		cf.textarea_2 = this.textarea_2;
		cf.pb = this.progress_1;
		if(name == null) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("下载");
			alert.setHeaderText("未选择文件!");
			alert.showAndWait();
			return;
		}
		boolean b = cf.deletefile(name);
		if(b == true) {//服务器端删除文件成功
			list.getItems().remove(name);
		}
		else {//服务器端删除文件失败
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("删除");
			alert.setHeaderText("删除失败!");
			alert.showAndWait();
		}
		
	}
	
	//下载按钮功能，要加上进度条
	public void downEvent() {
       
		progress_1.setProgress(0.0);
		cf.textarea_1 = this.textarea_1;
		cf.textarea_2 = this.textarea_2;
		if(name == null) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("下载");
			alert.setHeaderText("未选择文件!");
			alert.showAndWait();
			return;
		}
		boolean b = cf.downloadfile1(name);
		if (b == true) {
			progress_1.setProgress(1.0);
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("下载");
			alert.setHeaderText("下载成功!");
			alert.showAndWait();
		}
		else {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("下载");
			alert.setHeaderText("下载失败!");
			alert.showAndWait();
		}
		progress_1.setProgress(-100.0);
	}
}
