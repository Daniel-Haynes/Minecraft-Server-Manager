import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	
	ArrayList<String> versions;
	ArrayList<String> currentServers;
	Scanner in;
	int input;
	int serverNumber; //for the port in server.properties
	String serverName;
	String red = "\033[0;31m";
	String green = "\033[0;32m";
	String cyan = "\033[0;36m";
	String yellow = "\033[1;33m";
	String white = "\033[0m";

	
	public Main() {
		loadVersions();
		loadCurrentServers();
		in = new Scanner(System.in);
		
		serverNumber = currentServers.size() + 25580;
	}
	
	public void start() {
		System.out.println("What is the name of the server you would like to remove?");
		serverName = in.nextLine();
		for(int i = 0; i < currentServers.size(); i++) {
			if(currentServers.get(i).equals(serverName)) {
				break;
			}else if(i == currentServers.size() - 1) {
				System.out.println(red + "Error: " + white + "Server names does not exsist");
				serverName = in.nextLine();
			}
		}
		sendCommand("rm -r /home/daniel/minecraft/" + serverName + "/");
		removeFromServerManager();
	}
	
	public void removeFromServerManager() {
		System.out.println("Removing from server manager...");
		String line;
		ArrayList<String> lines = new ArrayList<String>();
		try {
            File f1 = new File("/home/daniel/scripts/mc_server_manager.sh");
            FileReader fr = new FileReader(f1);
            BufferedReader br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) {
                if (line.contains("servers=([1]")) {
                	line = line.replace("'" + serverName + "'", "");
                }
                lines.add(line);
            }
            fr.close();
            br.close();

            FileWriter fw = new FileWriter(f1);
            BufferedWriter out = new BufferedWriter(fw);
            for(String s : lines)
                 out.write(s + "\n");
            out.flush();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
	}
	
	public void loadCurrentServers() {
		currentServers = new ArrayList<String>();
		currentServers = sendCommand("ls /home/daniel/minecraft/");
		for(int i = 0; i < currentServers.size(); i++) {
			if(currentServers.get(i).equals("server_jars")) {
				currentServers.remove(i);
			}
		}
	}
	
	public void loadVersions() {
		versions = new ArrayList<String>();
		versions = sendCommand("ls /home/daniel/minecraft/server_jars/");
	}
	
	public ArrayList<String> sendCommand(String command) {
		ArrayList<String> output = new ArrayList<String>();
		try {
		    Process process = Runtime.getRuntime().exec(command);
		    process.waitFor();
		    BufferedReader reader = new BufferedReader(
		            new InputStreamReader(process.getInputStream()));
		    String line;
		    while ((line = reader.readLine()) != null) {
		    	output.add(line);
		    }
		    reader.close();
		} catch (IOException e) {
		    e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return output;
	}
}