import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.box.sdk.BoxAPIConnection;
import com.box.sdk.BoxCollaboration;
import com.box.sdk.BoxFolder;
import com.box.sdk.BoxItem;
import com.box.sdk.BoxUser;

public class Box_API_InviteUsers {
	
	//Box Java API Help - http://opensource.box.com/box-java-sdk/
	
	// 1 = Use Dev Token	2 = Use Client ID/Secret/Auth Code
	private static int api_toggle = 1;
	
	//---API Authentication Method Vars--------------------------------------------------
	private static final String DEVELOPER_TOKEN = "";
	//To get Auth Code: https://api.box.com/oauth2/authorize?response_type=code&client_id=CLIENT_ID&state=authenticated&redirect_uri=http://127.0.0.1
	private static final String CLIENT_ID = "";
	private static final String CLIENT_SECRET = "";
	private static final String CLIENT_Auth_CODE = "";
	
	//---Global Vars-----------------------------------------------------------------------
	private static final String csvUserFileToRead  = "C:\\_DUMP\\DelloiteUsers_6-15-17.csv";
	public static BoxAPIConnection api = null;
	
	public static void main(String[] args) {
		
		System.out.print("\n-------------------- BOX API BEGIN --------------------\n\n");
					
		if (api_toggle == 1) {
			api = new BoxAPIConnection(DEVELOPER_TOKEN);
		} else {
			api = new BoxAPIConnection(CLIENT_ID, CLIENT_SECRET, CLIENT_Auth_CODE);
		}

        //---Get current Logged In User--------------------------------------------------------
        BoxUser.Info userInfo = BoxUser.getCurrentUser(api).getInfo();
        System.out.format("Logged In/Running As:  %s <%s> !\n\n", userInfo.getName(), userInfo.getLogin());

        //---This method does the following---------------------------------------------------
        // Pre-Reqs: 
        //		- A parent Box Folder containing X number of Sub-Folders which are named as Users
        //		- You obtained the Folder ID of the above mentioned parent Folder (Can be seen in the URL)
        //		- You have a CSV file containing 2 columns - The User's Name (Matching the Box Folder Names) & their Email Address
        // Steps:
        //	1 - Read in and create Hash Map of the CSV File [User's Name (Key), and their Email Address (Value)]
        //	2 - Loop through all the Sub-Folders of the specified parent Folder
        //			- Rename the Sub-Folder with the specified Pre-Fix
        //			- Find a match for the Sub-Folder Name and the Key in the Hash Map & Invite the corresponding User via their Email Address as an Editor
        inviteUsers(api);

        System.out.print("\n\n-------------------- BOX API END --------------------\n");
        
	}
	
	private static void inviteUsers(BoxAPIConnection api) {
		//Prefix you want to add to the beginning of all the Sub-Folders.  If not necessary, change this to "";
		String folderPrefix = "001 RDQ Training Files - ";
		//Folder ID of the parent Folder containing all the Sub-Folders named as the User's Name
		String mainFolderID = "29455169053";
		
		String line = "";
		Map<String, String> userDictionary = new HashMap<String, String>();
		//Read the CSV File into Memory line by line & populate the userDictionary Hash Map
        try (BufferedReader br = new BufferedReader(new FileReader(csvUserFileToRead))) {
        	//user[0] is the User's Full Name in the csv File [Matching the Sub-Folder Name in Box]
        	//user[1] is the User's Email in the csv File
        	while ((line = br.readLine()) != null) {
        		String[] user = line.split(",");
        		userDictionary.put(user[0],user[1]);
        	}
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        //Get the parent Folder Box object
        BoxFolder parentFolder = new BoxFolder(api, mainFolderID); 

        //Loop through all the Sub-Folders of the parent Folder
		for (BoxItem.Info itemInfo : parentFolder.getChildren()) {
		    if (itemInfo instanceof BoxFolder.Info) {
		        BoxFolder.Info folderInfo = (BoxFolder.Info) itemInfo;
		        BoxFolder tempFolder = new BoxFolder(api, itemInfo.getID()); 
		        
		        //Get Sub-Folder Name & ID
		        String folderName = itemInfo.getName();
		        String folderID = itemInfo.getID();
		        
		        //Rename Sub-Folder with Prefix
		        System.out.println("Renaming Folder to: " + folderPrefix + folderName);
		        tempFolder.rename(folderPrefix + folderName);
		        
		        //Match the User's Name with the user/email Mapping
		        String emailToInvite = userDictionary.get(folderName.replaceAll(", ", "#"));
		        
		        System.out.println("Inviting " + emailToInvite + " to Folder: " + tempFolder.getInfo().getName() + " with ID: " + folderID);
		        
		        //Invite the matching user as a EDITOR
		        try {
		        	BoxFolder folder = new BoxFolder(api, folderID);
		        	folder.collaborate(emailToInvite, BoxCollaboration.Role.EDITOR);
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
		        
		        System.out.println("");
		    }
		}
		
		
	}
	

	
}
