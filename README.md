# SalesMate - Sales & Inventory Management System

SalesMate is a comprehensive Java-based sales and inventory management system designed for small to medium businesses. It provides a complete solution for managing inventory, sales transactions, employee tracking, and business analytics.

## 👥 Development Team

| Name                    | MSSV      | Email                        | GitHub Username         |
|-------------------------|-----------|------------------------------|-------------------------|
| Nguyễn Ngọc Mai Anh     | 23520065  | 23520065@gm.uit.edu.vn    | https://github.com/maianhUIT29            |
| Nguyễn Hải Thiện        | 23521481  | 23521481@gm.uit.edu.com        | https://github.com/adamwhite625              |
| Nguyễn Thanh Nhân       | 21521219  | 21521219@gm.uit.edu.vn | https://github.com/uit-ntn         |

### 📦 Inventory Management
- Real-time inventory tracking
- Low stock alerts and automatic reorder suggestions
- Product categorization and search
- Barcode scanning support
- Stock movement history

### 👥 Employee Management
- Employee attendance tracking
- User role permissions (Admin, Manager, Sales, Warehouse)
- Work schedule management
- Performance tracking
- Salary management

### 🛒 Sales Management
- Create and manage sales orders and invoices
- Real-time POS (Point of Sale) interface
- Customer management and purchase history
- Discount and promotion handling
- Multiple payment methods support
- Sales return and refund processing
- Daily sales summary and receipts
- Integration with inventory for automatic stock updates

### 📈 Analytics & Reporting
- Sales performance metrics
- Inventory turnover analysis
- Revenue and profit margin reports
- AI-powered sales forecasting
- Export reports to Excel and PDF

### 🤖 AI Integration
- Stock-out prediction with machine learning models
- Sales trend analysis
- Intelligent inventory recommendations
- Customer purchase pattern recognition

## 🛠️ System Requirements

- JDK 19 or higher
- 4GB RAM minimum (8GB recommended)
- 1GB free disk space
- 1280×720 screen resolution (minimum)
- Windows 7/10/11 or Linux (Ubuntu 18.04+)
- Internet connection (for updates and some features)
- Optional: Python 3.8+ (for AI forecasting features)

## 📥 Installation & Setup


### Developer Setup
1. Clone the repository: `git clone https://github.com/yourusername/salesmate.git`
2. Navigate to the project directory: `cd salesmate`
3. Build with Maven: `mvn clean install`
4. Run the application: `mvn exec:java`

### Database Configuration
1. Open `config/database.properties`
2. Configure your database connection parameters:
```
db.url=jdbc:oracle:thin:@localhost:1521:xe
db.user=salesmate
db.password=yourpassword
```
3. Run the database initialization script if needed: `java -cp salesmate.jar com.salesmate.util.DatabaseSetup`

## 🚀 Quick Start Guide

1. **Login** to the system with the default admin credentials:
   - Username: `admin`
   - Password: `admin`

2. **Setup Inventory**:
   - Add your products under the Inventory tab
   - Import existing inventory from CSV/Excel or add products manually
   - Set initial stock quantities

3. **Configure Users**:
   - Create user accounts for your staff
   - Assign appropriate roles and permissions

4. **Start Selling**:
   - Use the POS interface to begin processing sales
   - Generate invoices and process payments

## 📊 Stock Forecast API Integration

SalesMate integrates with Python FastAPI to predict products that will soon be out of stock on the Dashboard.

### Setup the Forecasting API:

1. **Install Python Libraries**:
   - Run `install_api_requirements.bat` to install necessary Python libraries

2. **Create Sample Prediction Models**:
   - Run `create_sample_models.bat` to generate sample prediction models

3. **Start the API**:
   - Run `start_api.bat` to launch the Python FastAPI on http://localhost:8000
   - API Documentation is available at http://localhost:8000/docs

4. **Use with SalesMate**:
   - Launch SalesMate normally
   - The Dashboard will display "Products Running Low (AI Prediction)" using forecasts from the API

### Troubleshooting:

- If the API is unavailable, SalesMate will automatically fall back to simple database-based predictions
- If no model is found for a product, a rule-based prediction will be used

## 🔧 Technology Stack

- **Backend**: Java 8
- **UI Framework**: Java Swing with FlatLaf for modern UI
- **Database**: Oracle Database
- **ORM**: Hibernate
- **Reporting**: JasperReports for PDF generation
- **Excel Import/Export**: Apache POI
- **Barcode/QR**: ZXing
- **Logging**: SLF4J with Logback
- **Predictive Analytics**: Python with FastAPI, Prophet, SARIMA

## 📄 License

SalesMate is released under the MIT License. See [LICENSE](LICENSE) file for details.

## 🤝 Support & Contact

For support, please raise an issue on our GitHub repository or contact our support team at support@salesmate-app.com.


© 2025 SalesMate Team. All rights reserved.
