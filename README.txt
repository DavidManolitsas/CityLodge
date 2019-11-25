COSC1295 ADVANCED PROGRAMMING ASSIGNMENT 2
DAVID MANOLITSAS S3779380

In my main method I have included methods that are commented out. These methods involve functionality with the database. The functions include:
- Run connection to database
- Drop all tables
- Create the Hotel Room table
- Create the Hiring Record table
- Insert 6 preset room into the database
- Insert 2 records for standard room R_001 and insert 2 records for suite S_001

Generating data for 6 rooms:
I have made 2 options for generating the data for the 6 existing rooms
    1. Import the Hotel Rooms from using the import_data.txt file
    2. Uncomment the methods in the main method
        - CLDB.addPresetRoom();
        - CLDB.addPresetRecords();

