# QuickBillPro

QuickBillPro is a modern, user-friendly desktop application for inventory, billing, and credit management, designed for small and medium businesses. Built with JavaFX, it provides a complete solution for managing products, sales, purchases, outlets, inlets, credits, payments, and reporting.

---

## Features

- **Dashboard**: Visual summary of sales, purchases, profit, credits, payments, products, outlets, inlets, tax, and low stock alerts.
- **User Management**: Secure login and account creation (admin passcode required for new accounts).
- **Product Management**: Add, update, and search products; manage HSN, tax rates, and stock quantities; view purchase history.
- **Inlet & Outlet Management**: Add, update, and manage suppliers (inlets) and customers (outlets) with contact, address, GSTIN, and email.
- **Inventory Management**: Track stock levels, low stock alerts, and purchase entries.
- **Billing**: Generate GST-compliant bills with product-wise tax, HSN, and remarks; supports cash and credit sales; restricts duplicate items and enforces item limits per bill.
- **Soft Copy & Print**: Generate, print, and export high-resolution soft copies of bills; send bills via email to outlets.
- **Credit Management**: Track outlet credits, due dates, statuses (pending, partially paid, paid, overdue), and manage payments against credits.
- **Payments**: Record, filter, and view payments by outlet and date; supports multiple payment modes (Cash, Bank Transfer, UPI, Cheque).
- **Reports**: Generate sales and bill reports by date, outlet, month, or year; view and print bill details; export bills to Excel.
- **Settings**: Export all bills to Excel, clear logs, and reset all data (admin passcode required).

---

## Installation & Setup

### Prerequisites
- **Java 21** or later (tested with JDK 21)
- **JavaFX 21** (bundled in `javafx-lib/` and `javafx-bin/`)
- Windows OS (batch file and installer provided)

### Dependencies
All required JARs are included in the `lib/`, `javafx-lib/`, and `javafx-bin/` folders:
- JavaFX (controls, fxml, graphics, base, swing, web, media)
- SQLite JDBC
- ControlsFX
- Apache POI (Excel export)
- Jakarta Mail & Activation (for email)
- JFreeChart (for charts)
- Log4j (logging)
- Commons IO/Collections/Compress

### Running the Application
1. **Using the Installer**:
   - Run `QuickBillProInstaller.exe` (if provided) to install.
   - Use the desktop/start menu shortcut to launch.
2. **Using the Batch File**:
   - Ensure Java 21 is installed at `C:\Program Files\Java\jdk-21` (or update the path in `QuickBillPro.bat`).
   - Double-click `QuickBillPro.bat` to start the application.

The application will create a data directory in your user home folder for the SQLite database and logs.

---

## Usage

- **Login**: Enter your username and password. Only registered users can log in.
- **Create Account**: Click "Create Account" (admin passcode required: `9290872634`).
- **Dashboard**: View business KPIs and charts.
- **Products**: Add, update, and search products; manage stock and purchase history.
- **Inlets/Outlets**: Manage suppliers and customers.
- **Billing**: Create new bills, add items, select payment type, and generate/print/email bills.
- **Credit Management**: Track and manage outstanding credits and payments.
- **Payments**: View and filter all payments.
- **Reports**: Generate and export sales/bill reports.
- **Settings**: Export all bills to Excel, clear logs, or reset data (admin passcode required).

---

## Data & Security
- All data is stored locally in an SQLite database under your user home directory (`QuickBillPro` folder).
- Admin passcode is required for sensitive actions (account creation, data reset).
- Email sending uses a pre-configured sender (update credentials in `EmailUtil.java` if needed).

---

## Support
For issues or feature requests, please contact the developer or open an issue in the repository.

---

## License
This project is proprietary. All rights reserved.
