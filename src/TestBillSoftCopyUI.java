import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import models.Bill;
import models.BillItem;
import controllers.BillSoftCopyController;
import javafx.collections.FXCollections;

import java.util.Date;
import java.util.List;

public class TestBillSoftCopyUI extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("views/bill_soft_copy.fxml")); // Make sure this path is correct
        Parent root = loader.load();

        // Get the controller
        BillSoftCopyController controller = loader.getController();

        // Prepare sample BillItem list
        BillItem item1 = new BillItem(1, 1234, 1001, "Sugar", 2, 45.0, 9.0, 9.0, 2301);
        BillItem item2 = new BillItem(2, 1234, 1002, "Wheat Flour", 1, 60.0, 9.0, 9.0, 2302);
        BillItem item3 = new BillItem(2, 1234, 1002, "Wheat Flour", 1, 60.0, 9.0, 9.0, 2302);
        BillItem item4 = new BillItem(2, 1234, 1002, "Wheat Flour", 1, 60.0, 9.0, 9.0, 2302);
        BillItem item5 = new BillItem(2, 1234, 1002, "Wheat Flour", 1, 60.0, 9.0, 9.0, 2302);
        BillItem item6 = new BillItem(2, 1234, 1002, "Wheat Flour", 1, 60.0, 9.0, 9.0, 2302);
        BillItem item7 = new BillItem(2, 1234, 1002, "Wheat Flour", 1, 60.0, 9.0, 9.0, 2302);
        BillItem item8 = new BillItem(2, 1234, 1002, "Wheat Flour", 1, 60.0, 9.0, 9.0, 2302);
        BillItem item9 = new BillItem(2, 1234, 1002, "Wheat Flour", 1, 60.0, 9.0, 9.0, 2302);
        BillItem item10 = new BillItem(2, 1234, 1002, "Wheat Flour", 1, 60.0, 9.0, 9.0, 2302);
        BillItem item11 = new BillItem(2, 1234, 1002, "Wheat Flour", 1, 60.0, 9.0, 9.0, 2302);

        List<BillItem> items = List.of(item1, item2, item1, item2, item1, item2, item1, item2, item1, item2, item1, item2, item1, item2, item1, item2);

        // Create sample Bill
        Bill bill = new Bill(
                1234,
                501,
                18.0,   // total CGST
                18.0,   // total SGST
                186.0,  // total amount
                new Date(),
                "Cash",
                "Regular Customer"
        );
        bill.setBillItems(FXCollections.observableArrayList(items));

        // Set the sample data into the controller
        controller.setBillData(bill, "A-One Supermarket", "Pune, MH", "27ABCDE1234F2Z5");

        // Setup and show the stage
        primaryStage.setTitle("Bill UI Test");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
