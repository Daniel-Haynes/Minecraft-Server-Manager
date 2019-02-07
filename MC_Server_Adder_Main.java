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
		loadCurrentServers();
		in = new Scanner(System.in);
		serverNumber = currentServers.size() + 25580;
		System.out.println("Current Servers sizse: " + currentServers.size());
		System.out.println("Server Number: " + serverNumber);
	}
	
	public void start() {
		System.out.println(yellow + "What type of server would you like to set up?" + white);
		System.out.println("1) Vanilla");
		System.out.println("2) Forge");
		System.out.println("3) Spigot");
		getValidInt(3,1);
		int serverType = input;
		loadVersions();
		System.out.println(yellow + "Which Version of Minecraftwould you like to setup?" + white);
		for(int i = 0; i < versions.size(); i++) { //List all of the available servers
			System.out.println(i + ") " + versions.get(i));
		}
		getValidInt(versions.size() - 1, 0); //Gets valid server version to set up
		System.out.println(yellow + "What is the name of the server?" + white);
		serverName = in.nextLine(); //Gets the name of the server
		serverName = in.nextLine(); //Gets the name of the server
		System.out.println("Creating Directory...");
		sendCommand("mkdir /home/daniel/minecraft/" + serverName);
		System.out.println("Moving files...");
		switch(serverType) {
			case 1 :
				sendCommand("cp /home/daniel/minecraft/server_jars/vanilla/" + versions.get(input) + " /home/daniel/minecraft/" + serverName + "/");
				break;
			case 2 :
				sendCommand("cp /home/daniel/minecraft/server_jars/forge/" + versions.get(input) + " /home/daniel/minecraft/" + serverName + "/");
				break;
			case 3 :
				sendCommand("cp /home/daniel/minecraft/server_jars/spigot/" + versions.get(input) + " /home/daniel/minecraft/" + serverName + "/");
				break;
		}
		sendCommand("cp /home/daniel/scripts/eula.txt /home/daniel/minecraft/" + serverName + "/");
		sendCommand("cp /home/daniel/scripts/server.properties /home/daniel/minecraft/" + serverName + "/");
		sendCommand("mv /home/daniel/minecraft/" + serverName + "/" + versions.get(input) + " " + "/home/daniel/minecraft/" + serverName + "/server.jar");
		updateServerProperties();
		addToServerManager();
		
		System.out.println(green + "Success: " + white + "Set up complete");
	}
	
	public void addToServerManager() {
		System.out.println("Adding to server manager...");
		String line;
		ArrayList<String> lines = new ArrayList<String>();
		try {
            File f1 = new File("/home/daniel/scripts/mc_server_manager.sh");
            FileReader fr = new FileReader(f1);
            BufferedReader br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) {
                if (line.contains("servers=([1]")) {
                	line = line.substring(0, line.length()-1);
                	line = line + " '" + serverName + "')";
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
	
	public void updateServerProperties() {
		System.out.println("Updating server.properties");
		String line;
		ArrayList<String> lines = new ArrayList<String>();
		try {
            File f1 = new File("/home/daniel/minecraft/" + serverName + "/server.properties");
            FileReader fr = new FileReader(f1);
            BufferedReader br = new BufferedReader(fr);
            while ((line = br.readLine()) != null) {
                if (line.contains("server-port=25565"))
                    line = line.replace("server-port=25565", "server-port=" + serverNumber);
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
	
	public void getValidInt(int max, int min) {
		input = in.nextInt();
		while(!((input <= max) && (input >= min))) {
			System.out.println(red + "Error: " + white + "Invalid Input");
			input = in.nextInt();
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
		switch(input) {
			case 1 :
				versions = sendCommand("ls /home/daniel/minecraft/server_jars/vanilla");
				break;
			case 2 : 
				versions = sendCommand("ls /home/daniel/minecraft/server_jars/forge");
				break;
			case 3 : 
				versions = sendCommand("ls /home/daniel/minecraft/server_jars/spigot");
				break;
		}
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