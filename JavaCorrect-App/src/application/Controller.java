package application;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.UUID;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class Controller
implements Initializable {
	@FXML
	private TextField projectNameField;
	@FXML
	private DatePicker deadlineDatePicker;
	@FXML
	private Button expectedOutputButton;
	@FXML
	private Button studentListButton;
	@FXML
	private SplitMenuButton projectNameButton;
	@FXML
	private TableView<StudentProject> studentProjectsTable;
	@FXML
	private TableColumn<StudentProject, String> studentNameColumn;
	@FXML
	private TableColumn<StudentProject, String> studentIdColumn;
	@FXML
	private TableColumn<StudentProject, Integer> markColumn;
	@FXML
	private TableColumn<StudentProject, Date> sendDateColumn;
	
	@Override
	public void initialize(final URL url, final ResourceBundle resourceBundle) {
		studentNameColumn.setCellValueFactory(new PropertyValueFactory<StudentProject, String>("name"));
		studentIdColumn.setCellValueFactory(new PropertyValueFactory<StudentProject, String>("studentId"));
		markColumn.setCellValueFactory(new PropertyValueFactory<StudentProject, Integer>("mark"));
		sendDateColumn.setCellValueFactory(new PropertyValueFactory<StudentProject, Date>("sendDate"));
		
		studentProjectsTable.setItems(parseStudentProjectList()); //Populate the table

		expectedOutputButton.setOnMouseClicked((event) -> {
			final FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Select file");
			fileChooser.setSelectedExtensionFilter(new ExtensionFilter("Text (.txt)", "*.txt"));
			final String path = Optional.ofNullable(fileChooser.showOpenDialog(null)).map(File::getPath).orElse("");
			expectedOutputButton.setText(path);
		});
		studentListButton.setOnMouseClicked((event) -> {
			final FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Select file");
			fileChooser.setSelectedExtensionFilter(new ExtensionFilter("Comma Separated Values (.csv)", "*.csv"));
			final String path = Optional.ofNullable(fileChooser.showOpenDialog(null)).map(File::getPath).orElse("");
			studentListButton.setText(path);
		});
	}
	
	public static final class StudentProject {
		private final SimpleStringProperty name;
		private final SimpleStringProperty studentId;
		private final SimpleIntegerProperty mark;
		private final SimpleObjectProperty<Date> sendDate;

		public StudentProject(final String name, final String studentId, final Integer mark, final Date sendDate) {
			this.name = (name == null) ? new SimpleStringProperty() : new SimpleStringProperty(name);
			this.studentId = (studentId == null) ? new SimpleStringProperty() : new SimpleStringProperty(studentId);
			this.mark = (mark == null) ? new SimpleIntegerProperty() : new SimpleIntegerProperty(mark);
			this.sendDate = (sendDate == null) ? new SimpleObjectProperty<>() : new SimpleObjectProperty<>(sendDate);
		}
		
		public String getName() {
			return name.get();
		}

		public String getStudentId() {
			return studentId.get();
		}
		
		public Integer getMark() {
			return mark.get();
		}
		
		public Date getSendDate() {
			return sendDate.get();
		}
	}

	private ObservableList<StudentProject> parseStudentProjectList() {
		//TODO Get students from DB and create the list
		return FXCollections.emptyObservableList();
		
		// Sample :
		//		return FXCollections.observableArrayList(
		//			new StudentProject("Jean DUPONT", "985656", null, null),
		//			new StudentProject("Paul SMITH", "765654", 16, new Date())
		//		);
	}

	@FXML
	private void handleCreateProjectAction() throws ClassNotFoundException {
		final String projectName = projectNameField.getText();
		final LocalDate deadline = deadlineDatePicker.getValue();
		final String expectedOutputPath = expectedOutputButton.getText();
		final String studentListPath = studentListButton.getText();
		String dateExpi = deadline.getYear()+"-"+deadline.getMonth()+"-"+deadline.getDayOfMonth();
		final String projectId = UUID.randomUUID()+"";
		//TODO Parse files + send data to DB + display response
		try {
			MysqlPropertiesParser properties = new MysqlPropertiesParser();
			Connection myqlco = MysqlConnexion.getInstance(properties);
			
			System.out.println(properties.getDbname());
			
			String insertStudent =
			"INSERT INTO " + properties.getDbname() + 
			".PROJET (idProjet, dateExpi, intituleProjet) " + 
			"VALUES (?, ?, ?);";
			java.sql.PreparedStatement preparedstatement = myqlco.prepareStatement(insertStudent);
			preparedstatement.setString(1,projectId);
			preparedstatement.setDate(2, java.sql.Date.valueOf(dateExpi));
			preparedstatement.setString(3, projectName);
			
			preparedstatement.executeUpdate();
		}
		catch(SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
		}
	}
	
	@FXML
	private void handleDeleteProjectAction() {
		//TODO Send delete request to DB + display response
	}
}