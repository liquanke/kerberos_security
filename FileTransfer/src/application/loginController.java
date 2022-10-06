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
	//login����ؼ�
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
	
	
	//������ؼ�
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
	
	private String name = null;//�洢listview��ѡ�е��ļ�
	
	//private String Kcv1;
	//private String[] p = {"qwertyui", "12345678"};
	
	public static ClientFuction cf = new ClientFuction();
	
	/*public void initialize(URL url, ResourceBundle resources) {
        // Initialization code can go here. 
        // The parameters url and resources can be omitted if they are not needed
		cf = new ClientFuction();
		
    }*/
		
	//��½���湦�ܺ���
	//��½��ť����
	
	public void loginEvent() throws IOException {
		//�õ�������е�����
		String[] message = new String[2];
		message[0] = text_name.getText();
		message[1] = text_password.getText();
		
		
		//������֤���ܺ���
		cf.init();
		cf.textarea = this.textarea;
		boolean b = cf.verify(message[0], message[1]);
		System.out.println("��֤�������ɵ���Կ: " + cf.Kcv);
		
		//��¼
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("��¼");
		alert.setHeaderText("��¼�ɹ�!��ӭʹ�ù�������ϵͳ\n ����ˢ�°�ť��ȡ����Ŀ¼Ŷ");
		alert.showAndWait();
		
		if (b == true) {//��֤�ɹ���ת������ҳ��
			try {
				Parent anotherRoot = FXMLLoader.load(getClass().getResource("mainMenu.fxml"));
		        Stage anotherStage = new Stage();
		        anotherStage.setTitle("���˽���");
		        anotherStage.setScene(new Scene(anotherRoot, 800, 400));
		        anotherStage.show();
		     } catch (Exception e){
		            e.printStackTrace();
		       }  
		}
		else {//��֤ʧ�ܣ�������ʾ��
			Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("��½");
			alert.setHeaderText("��½ʧ��!");
			alert.showAndWait();
		}
		//return message;
	}
	
	
	//ע�ᰴť����
	public String[] registerEvent() throws IOException {
		//��ȡ���������
		String[] message = new String[2];
		message[0] = text_name.getText();
		message[1] = text_password.getText();
		
		cf.textarea = this.textarea;
		//this.textarea.appendText("123");
		boolean b = cf.register(message[0], message[1]);
		
		if (b == true) {//ע��ɹ���������ʾ��
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("ע��");
			alert.setHeaderText("ע��ɹ�!");
			alert.showAndWait();
		}
		else {//ע��ʧ�ܣ�������ʾ��
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("ע��");
			alert.setHeaderText("ע��ʧ��!");
			alert.showAndWait();
		}
		
		return message;
	}

	//�رհ�ť����
	public void closeEvent(){
		Stage stage = (Stage) but_close.getScene().getWindow();
	    stage.close();
	}

	//��ҳ�湦�ܺ����������غ��ϴ���������������ĵ��ı�����
	
	//�ϴ���ť����
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
		boolean b = cf.uploadfile1(fileName);//�����ϴ��ļ�����
		
		if(b == true) {//�ȸ��ݷ������˵ķ���ֵ�ж�һ���Ƿ��ϴ��ɹ�
			//list.getItems().add(fileName);
			progress_1.setProgress(1.0);
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("�ϴ�");
			alert.setHeaderText("�ϴ��ɹ�!");
			alert.showAndWait();
		}
		else {//δ�ϴ��ɹ�������������ʾ��
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("�ϴ�");
			alert.setHeaderText("�ϴ�ʧ��!");
			alert.showAndWait();
		}
		progress_1.setProgress(-100.0);
	}
	
	
	//ˢ�°�ť����
	public  void refreshEvent() throws IOException{
		list.getItems().clear(); 
		//�Ȼ�ȡ�������˷��ص��ļ��б��ٽ�������Ĳ���
		cf.textarea = this.textarea;
		cf.textarea_1 = this.textarea_1;
		cf.textarea_2 = this.textarea_2;
		
		System.out.println("ˢ�¹������ɵ���Կ: " + cf.Kcv);
		String[] fileList = cf.getdirectory();
		
		if (fileList.length == 0) {//�ļ��б�Ϊ�գ�������ʾ��
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("ˢ��");
			alert.setHeaderText("��������Ŀ¼Ϊ��!");
			alert.showAndWait();
		}
		else {//�ļ��б�Ϊ�գ������ļ��б�
			for (int i = 0; i < fileList.length; ++i) {
		    	list.getItems().add(fileList[i]);
		    }
		}
		progress_1.setProgress(-100.0);
	    
	    //����listview����
	    list.getSelectionModel().selectedItemProperty().addListener(
		        (ObservableValue<? extends String> ov, String old_val, 
		            String new_val) -> {
		            	name = new_val;
		    });
	}

	//ɾ����ť����
	public void delEvent() {
		//���ݷ������˵ķ���ֵ������
		cf.textarea_1 = this.textarea_1;
		cf.textarea_2 = this.textarea_2;
		cf.pb = this.progress_1;
		if(name == null) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("����");
			alert.setHeaderText("δѡ���ļ�!");
			alert.showAndWait();
			return;
		}
		boolean b = cf.deletefile(name);
		if(b == true) {//��������ɾ���ļ��ɹ�
			list.getItems().remove(name);
		}
		else {//��������ɾ���ļ�ʧ��
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("ɾ��");
			alert.setHeaderText("ɾ��ʧ��!");
			alert.showAndWait();
		}
		
	}
	
	//���ذ�ť���ܣ�Ҫ���Ͻ�����
	public void downEvent() {
       
		progress_1.setProgress(0.0);
		cf.textarea_1 = this.textarea_1;
		cf.textarea_2 = this.textarea_2;
		if(name == null) {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("����");
			alert.setHeaderText("δѡ���ļ�!");
			alert.showAndWait();
			return;
		}
		boolean b = cf.downloadfile1(name);
		if (b == true) {
			progress_1.setProgress(1.0);
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("����");
			alert.setHeaderText("���سɹ�!");
			alert.showAndWait();
		}
		else {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("����");
			alert.setHeaderText("����ʧ��!");
			alert.showAndWait();
		}
		progress_1.setProgress(-100.0);
	}
}
