Patient Data App

TO RUN:

mvn clean package
mvn exec:exec

This project aims to solve the problem of managing several databases - of varying sizes - of information used by a hospital.

My solution satisfies all requirements (1 - 10). I have implemented the following core features: 
1 - A simple class Column.java to store a column of data
2 - A class, DataFrame.java to hold a collection of columns. 
3 - A DataLoader class that populates a DataFrame with data from both JSON and CSV files.
4 - A Model class to act as an interface for my Controller and View layer to interact with the model.
5 - Many servlets and JSPs to display the application. 
6 - The ability to search the data. Searching can be done in any column, or in a specific column. To do a search, click on the search button, and click on the column name / "ANY" tag that you would like to search in. Then, a search bar will appear, enter the search term, and press Go. The view of the rows will change to only show those matching the keyword.
7 - Sort, Edit, New, Update, Delete operations, and row count information which in combination with Search, allow the user to find query the database, ie the number of people living in Cambridge could be found by doing: 1- A search in the column CITY for 'Cambridge', and 2- Looking at the displayed row count tag. 
8 - The ability to Add, Edit or Delete a row of data. To add a patient, press the new button, and fill in the form. To edit an existing row of data, just click on a row link, and it will take you to a similar looking 'edit' page. Deletion is done through the edit page. To save these changes, press the Save File button, and confirm your selection.
9 - A JSONWriter (implemented in TableExporter) that allows the user to export their table in JSON format. 
10 - A variety of automatically generated charts (they are based on the current view of the data at the time the charts button is pressed), for all the different types of databases. Pie charts show categorical data, and histograms show numeric data, such as age distribution.

PROJECT HIGHLIGHTS (And Extra Additions): 

- The DataFrameView abstraction - or the idea that 'views' of the data are really references to a single data structure in which the data is stored, is an efficient storage pattern, especially for large databases, since the data is only stored in one location. It also nicely allows for deeper abstractions such as pagination, and the 'set view' feature. 

- Pagination (only rendering a small number of rows in an HTML table at one time) enables large databases (ie patients 100000) to be opened and run smoothly + A total row count tag, and page count tag.

- Sorting is done lexicographically (by the ordering of the alphabet) for non-numeric data, and numerically for numeric data (this is automatically detected).

- Graphs are drawn for EVERY file in the available set on github, not just patients100.csv. To see this, use the import feature to open another file. 

- Support for importing any file, given on github, or your own files saved through the export feature. Imports work on JSON files and CSV files.

- Files can be exported either to the server files (the /data directory) or the client's local machine. 

- The user has the option to restrict the number of columns they see in the data, and change the number of rows per page through the Set View feature.

- My servlets all inherit basic forward, redirect and forwardToError methods from a BaseServlet class - a nice OOP principle that avoided lots of repetition. 

- When importing files, the user is given the opportunity to select a primary key (which keys are valid primary keys is computed automatically). Then, these keys are shown as hyper-links to the individual records in the table. (Otherwise edit operations are done by clicking on a row number)

GUIDE TO THE UI: 

Operation | Details

Search | Found in menu bar. When clicked, clickable tags with the column names on them appear. Click on the tag corresponding to the column you would like to search in. A search bar will appear. Type your keyword in the search bar and press Go. 

Sort | Similar to search. But you are given the opportunity (through another set of tags) to search in ascending or descending order. 

New record / row | Click on the button in the menu bar. You will be taken to a form with a number of boxes for textual input. Enter your new record's data, and press enter. Ensure the ID field is filled and unique (the program will not allow you to continue if it is not present and unique).

Edit record / row | Click on the row number (in the row column) corresponding to the row you would like to edit. (Or, if you have defined a key when importing the file, clicking on the hyper-link in the ID field will have the same effect). This takes you to an edit page, where you can modify the details of the patient. (Submit your changes by pressing the update button.)

Delete record /row | The same as edit, but press the delete button instead at the end. 

Import file | Click on the button in the menu. It will show a list of tags with the names of the files available to import. Choose one (by clicking it) and it will show a new set of tags of the possible primary keys for that file. Click on a key (or if you dont want to press none), and it will load the file, with the new key. This works with JSON and CSV files.

Export file | Click on the button in menu. Tags giving you the option to export as a CSV or JSON file will appear. Choose one. Then tags to decide between downloading to the server files or as an attachment will come up. Again, choose one. Once you have done this, enter a file name in the provided text box and press go. 

Save file | Just click the button and press 'yes' toconfirm.

Delete file | Click the button and confirm. 

Set View | Click the button in the menu. This will bring up a set of check-box tags, and a text box for the page size. Click on the columns you would like to select in your new view, and enter your new page size. Press go and the view will update. 

Refresh View | Just press the button

Charts | Press the button and it will take you to the charts dashboard, press back to return to the data. 

Paging | Use the text box, and Go to set the page number, or use 'next' and 'previous' to navigate them sequentially.