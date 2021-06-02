package pharmacy.client.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pharmacy.model.Doctor;
import pharmacy.model.Medication;
import pharmacy.services.IPharmacyObserver;
import pharmacy.services.IPharmacyServices;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.function.Predicate;


public class DoctorController extends UnicastRemoteObject implements IPharmacyObserver, Serializable {
    private Doctor loggedDoctor;
    @FXML
    private Label loggedDoctorLabel;

    // Server
    private IPharmacyServices server;

    private static final Logger logger = LogManager.getLogger();

    private final ObservableList<Medication> orderList = FXCollections.observableArrayList();

    public DoctorController() throws RemoteException {
    }

    private Predicate<Medication> createPredicate(String searchText) {
        return medication -> {
            if (searchText == null || searchText.isEmpty()) return true;
            return searchFindsMedication(medication, searchText);
        };
    }

    private boolean searchFindsMedication(Medication medication, String searchText) {
        return medication.getCommercialName().contains(searchText.toUpperCase());
    }

    public void onTableClicked(MouseEvent mouseEvent) {
        Long id = tableMeds.getSelectionModel().getSelectedItem().getId();
        stocksLabel.setText("Stocuri: " + server.getStocksById(id));
    }

    public void addToOrdersClick(MouseEvent mouseEvent) {
        if (tableMeds.getSelectionModel().getSelectedItem() != null) {
            Integer quantity = Integer.parseInt(quantityTextField.getText());
            if (quantity > 0) {
                Medication selectedMedication = tableMeds.getSelectionModel().getSelectedItem();
                logger.trace(selectedMedication);
                while(quantity > 0) {
                    orderList.add(selectedMedication);
                    tableMedsOrders.setItems(orderList);
                    quantity--;
                }
                alertMedicationAdded(selectedMedication);
            }
        }
    }

    private void alertMedicationAdded(Medication selectedMedication) {
        Alert addedAlert = new Alert(Alert.AlertType.CONFIRMATION,
                selectedMedication.getCommercialName() + " added to orders!");
        addedAlert.setTitle("Added to orders");
        addedAlert.show();
    }


    public void init() {
        loggedDoctorLabel.setText(loggedDoctor.getDoctorFullName().toUpperCase());

        initializeMedsTableColumns();

        initializeMedsTableOrdersColumns();

        ObservableList<Medication> medications = FXCollections.observableList(server.getAllMedications());

        FilteredList<Medication> filteredData = new FilteredList<>(FXCollections.observableList(medications));

        searchMedsTextField.textProperty().addListener((observable, oldValue, newValue) ->
                filteredData.setPredicate(createPredicate(newValue))
        );
        tableMeds.setItems(filteredData);
    }

    private void initializeMedsTableOrdersColumns() {
        initializeColumns(idMedOrdersColumn, commercialOrdersColumn, concentrationOrdersColumn, therapeuticOrdersColumn, prescriptionOrdersColumn, shelfLifeOrdersColumn);
    }

    private void initializeMedsTableColumns() {
        initializeColumns(idMedColumn, commercialColumn, concentrationColumn, therapeuticColumn, prescriptionColumn, shelfLifeColumn);
    }

    private static void initializeColumns(TableColumn<?, ?> idMedColumn, TableColumn<?, ?> commercialColumn, TableColumn<?, ?> concentrationColumn, TableColumn<?, ?> therapeuticColumn, TableColumn<?, ?> prescriptionColumn, TableColumn<?, ?> shelfLifeColumn) {
        idMedColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        commercialColumn.setCellValueFactory(new PropertyValueFactory<>("commercialName"));
        concentrationColumn.setCellValueFactory(new PropertyValueFactory<>("concentration"));
        therapeuticColumn.setCellValueFactory(new PropertyValueFactory<>("therapeuticEffect"));
        prescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("prescription"));
        shelfLifeColumn.setCellValueFactory(new PropertyValueFactory<>("shelfLife"));
    }

    public void exitClicked(MouseEvent mouseEvent) {
        Platform.exit();
    }

    public void handleViewChange(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() == orderButton) pnOrders.toFront();
        if (mouseEvent.getSource() == stocksButton) pnStock.toFront();
        if (mouseEvent.getSource() == medicationsButton) pnMedications.toFront();
    }

    @FXML
    private TableView<Medication> tableMeds;
    @FXML
    private TableColumn<?, ?> idMedColumn;
    @FXML
    private TableColumn<?, ?> commercialColumn;
    @FXML
    private TableColumn<?, ?> concentrationColumn;
    @FXML
    private TableColumn<?, ?> therapeuticColumn;
    @FXML
    private TableColumn<?, ?> prescriptionColumn;
    @FXML
    private TableColumn<?, ?> shelfLifeColumn;
    @FXML
    private JFXTextField searchMedsTextField;
    @FXML
    private Pane pnOrders;
    @FXML
    private Pane pnMedications;
    @FXML
    private Pane pnStock;
    @FXML
    private Button orderButton;
    @FXML
    private Button stocksButton;
    @FXML
    private Button medicationsButton;
    @FXML
    private Label stocksLabel;
    @FXML
    private JFXButton removeFromOrdersButton;
    @FXML
    private JFXButton sendOrderButton;
    @FXML
    private JFXButton addToOrdersButton;
    @FXML
    private TableView<Medication> tableMedsOrders;
    @FXML
    private TableColumn<?, ?> idMedOrdersColumn;
    @FXML
    private TableColumn<?, ?> commercialOrdersColumn;
    @FXML
    private TableColumn<?, ?> concentrationOrdersColumn;
    @FXML
    private TableColumn<?, ?> therapeuticOrdersColumn;
    @FXML
    private TableColumn<?, ?> prescriptionOrdersColumn;
    @FXML
    private TableColumn<?, ?> shelfLifeOrdersColumn;
    @FXML
    private JFXTextField quantityTextField;

    public void setServer(IPharmacyServices server) {
        this.server = server;
    }

    public void setDoctor(Doctor doctor) {
        this.loggedDoctor = doctor;
    }


    public void sendToOrderClick(MouseEvent mouseEvent) {
    }

    public void removeFromOrdersClick(MouseEvent mouseEvent) {
    }

    public void onTableOrdersClick(MouseEvent mouseEvent) {
    }
}
