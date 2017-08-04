# Box_API_Recipes
Miscellaneous Box API Recipes
------------------------------

-----------------------------------------------------------------
IniviteUsers.java

Pre-Reqs: 
  - A parent Box Folder containing X number of Sub-Folders which are named as Users
  - You obtained the Folder ID of the above mentioned parent Folder (Can be seen in the URL)
  - You have a CSV file containing 2 columns - The User's Name (Matching the Box Folder Names) & their Email Address

Steps:
  - Read in and create Hash Map of the CSV File [User's Name (Key), and their Email Address (Value)]
  - Loop through all the Sub-Folders of the specified parent Folder
    - Rename the Sub-Folder with the specified Pre-Fix
    - Find a match for the Sub-Folder Name and the Key in the Hash Map & Invite the corresponding User via their Email Address as an Editor
-----------------------------------------------------------------
