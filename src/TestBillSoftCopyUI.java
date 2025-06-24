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
        BillItem item3 = new BillItem(3, 1234, 1003, "Rice", 5, 50.0, 5.0, 5.0, 2301);
        BillItem item4 = new BillItem(4, 1234, 1004, "Cooking Oil", 2, 150.0, 12.0, 12.0, 2302);
        BillItem item5 = new BillItem(5, 1234, 1005, "Salt", 1, 20.0, 5.0, 5.0, 2301);
        BillItem item6 = new BillItem(6, 1234, 1006, "Tea Powder", 1, 120.0, 9.0, 9.0, 2306);
        BillItem item7 = new BillItem(7, 1234, 1007, "Milk", 3, 25.0, 2.5, 2.5, 2307);
        BillItem item8 = new BillItem(8, 1234, 1008, "Biscuits", 4, 15.0, 5.0, 5.0, 2308);
        BillItem item9 = new BillItem(9, 1234, 1009, "Toor Dal", 2, 90.0, 6.0, 6.0, 2309);
        BillItem item10 = new BillItem(10, 1234, 1010, "Detergent", 1, 80.0, 12.0, 12.0, 2310);
        BillItem item11 = new BillItem(11, 1234, 1011, "Toothpaste", 1, 40.0, 5.0, 5.0, 2311);
        BillItem item12 = new BillItem(12, 1234, 1012, "Shampoo", 1, 70.0, 9.0, 9.0, 2312);

        List<BillItem> items = List.of(
                item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, item11, item12
        );

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
        controller.setBillData(bill, "A-One Supermarket and general stores", "Pune, MH", "27ABCDE1234F2Z5");

        // Setup and show the stage
        primaryStage.setTitle("Bill UI Test");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
