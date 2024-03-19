Welcome to the Orders Desktop Application project developed as part of the Programming III Lab at the Islamic University of Gaza, Faculty of Information Technology.

Project Description
This JavaFX application is an Orders Management System designed for two types of users: admin and client. The system allows clients to register and place orders for products, while admins manage products, clients, orders, and invoices. Below is an organized overview of the application's functionalities and structure:

Functionality Overview

***Admin:

Manage Products (Add Product, Edit Product, Delete Product, View Products, Search for Product)

Manage Clients (View Clients, Delete Client, Search for Client)

Manage Orders (View Orders, Search for Order, Add Order)

Manage Invoices (Generate Invoices, Delete Invoice, View Invoices, Search for Invoice, Change Password)

Logout


***Client:

Profile (View Profile, Edit Profile)

Manage Orders (Add Order, Edit Order, Delete Order, View Orders, Search for Order)

View Invoices

Change Password

Logout

--------------------------

Database Schema

***Users Table

id (int, AI, PK)

name (varchar)

email (varchar, unique)

mobile (varchar)

password (varchar)

role (admin "0", client "1")


***Products Table
id (int, AI, PK)

name (varchar, unique)

category (varchar)

price (double)

quantity (int)

description (text)

***Orders Table

id (int, AI, PK)

product_id (FK)

user_id (FK)

quantity (int)

date (varchar)

***Invoices Table

id (int, AI, PK)

order_id (FK)

total_price (double)

date (varchar)

------------------------

GUI Structure

The GUI layout includes:

1- Login Screen with username (email), password, and login/register buttons.

2- Client Registration Page with a form (name, email, mobile, password, register button).

3- Menu Bar with three menus: File (exit), Format (font size, font family, background color), Help (about app).

4- Client Dashboard with options: Profile, Manage Orders, View Invoices, Change Password, Logout.

5- Admin Dashboard with options: Manage Products, Manage Orders, Manage Clients, Manage Invoices, Change Password, Logout.

Note that:

Each form includes a reset button to clear data.

Operations: validate inputs, check uniqueness, and display error messages.

Alerts confirm/cancel processes and display process status.

Use of DatePicker for date attributes.

-----------------

Project Structure

src/ : Contains the source code files.

controller/ : Controllers for each view.

view/ : FXML files for GUI layout.

images/ : Images used in the application.

----------------------

Project Implementation

Object-Oriented Programming concepts applied.

CRUD operations implemented using JavaFX Technology.

Appropriate collections and functional programming used.

Each operation reflects changes in the database.

Styling applied for a user-friendly interface.

