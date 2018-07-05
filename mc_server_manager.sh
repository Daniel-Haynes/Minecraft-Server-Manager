#!/bin/bash
#Variables
servers=([1]='spellbound_caves' 'modded_server' 'survival_world' 'forge' 'fort_craft' 'lethamyr'  'waking_up'  'forge_daniel')
length=${#servers[@]}
red='\033[0;31m'
green='\033[0;32m'
cyan='\033[0;36m'
yellow='\033[1;33m'
white='\033[0m'
#Functions
# List all of the servers and their current states
list_servers (){
	count=1
        while [ $count -le ${length} ]; do
                echo "("$count") " ${servers[count]}
                let count=count+1
        done
}
list_servers_status (){
	count=1
	while [ $count -le ${length} ]; do
        	#if screen -list | grep -oh ${servers[count]} >/dev/null; then
		if screen -ls ${servers[count]} | grep -o "^\s*[0-9]*\."${servers[count]}"[ "$'\t'"](" --color=NEVER -m 1 | grep -oh "[0-9]*\."${servers[count]} --color=NEVER -m 1 -q >/dev/null; then
                	echo -e ${servers[count]} $green"\n\t\t online"$white
        	else
                	echo -e ${servers[count]} $red"\n\t\t offline"$white
        	fi
        	let count=count+1
	done
}
# Based off of parameters determines if the input is within a specified range
# (input, max, min)
valid_input (){
	if [ $1 -gt  $2 ] || [ $1 -lt $3 ]; then
		return 0
	else
		return 1
	fi
}

#Main
while true; do
clear
echo -e $cyan"MINECRAFT SERVER MANAGER"$white
echo -e "Enter Ctrl + C to exit\n"
list_servers_status
echo -e $yellow"What would you like to do?"$white
echo "(1) Start a server"
echo "(2) Stop a server"
echo "(3) Refresh servers"
echo "(4) Attach to server"
echo "(5) Add new server"
echo "(6) Remove server"
echo "(7) Manage plugins"
read input
while valid_input $input 7 1; do
	echo "Error: Invalid option"
	read input
done
case $input in
	1)
		echo -e $yellow"Which server would you like to start?"$white
		list_servers
		read input
		count=1
		while [ $count -le ${length} ] || valid_input $input ${length} 1; do
        		if valid_input $input ${length} 1; then
                		echo -e $red"Error: "$white"Invalid Server"
                		count=0
                		read input
        		elif screen -list | grep ${servers[input]} >/dev/null; then
                		echo -e $red"Error:"$white ${servers[input]} "is already running"
                		count=0
                		read input
        		fi
        		let count=count+1
		done
		let server_port=$input+25579
		echo -e $yellow"Enter the amount of RAM in GB to use (Minimum: 1, Maximum: 14)"$white
		read ram
		while [ $ram -gt 14 ] || [ $ram -lt 1 ]; do
        		echo "ERROR: Not within requirements"
        		echo "Enter the amount of RAM in GB to use (Minimum: 1, Maximum: 14)"
        		read ram
		done
		cd /home/daniel/minecraft/${servers[input]}
		echo "Starting "${servers[input]}
		screen -dmS ${servers[input]} bash -c "java -Xmx"$ram"G -Xms"$ram"G -jar server.jar nogui"
		sleep 5
		echo "Allowing port in firewall"
		ufw allow $server_port
		echo $server_port
		sleep 1
		echo -e $green"Success:"$white ${servers[input]} "started"
		sleep 1
	;;
	2)
		echo "Which server would you like to stop?"
		list_servers
		read input
                count=1
                while [ $count -le ${length} ] || valid_input $input ${length} 1; do
                        if valid_input $input ${length} 1; then
                                echo -e $red"Error: "$white"Invalid Server"
                                count=0
                                read input
                        elif ! screen -list | grep ${servers[input]} >/dev/null; then
                                echo -e $red"Error:"$white ${servers[input]} "is not running"
                                count=0
                                read input
                        fi
                        let count=count+1
                done
		let server_port=$input+25579
		echo "Stopping "${servers[input]}
		screen -S ${servers[input]} -p 0 -X stuff "stop^M"
		sleep 5
		ufw deny $server_port
		sleep 1
		echo -e $green"Success:"$white ${servers[input]} "stopped"
		sleep 1
	;;
	3)
		clear
	;;
	4)
		echo "Which server would you like to attach to?"
		count=1
		end=0
		while [ $count -le ${length} ]; do
			if screen -list | grep ${servers[count]} >/dev/null; then
				break
			elif [ $count -eq ${length} ]; then
				end=1
				break
			fi
			let count=count+1
		done
		if [ $end -eq 1 ]; then
			echo -e $red"Error: "$white"No servers to attach to"
			sleep 2
			continue
		fi
		list_servers
		read input
                count=1
                while [ $count -le ${length} ] || valid_input $input ${length} 1; do
                        if valid_input $input ${length} 1; then
                                echo -e $red"Error: "$white"Invalid Server"
                                count=0
                                read input
                        elif ! screen -list | grep ${servers[input]} >/dev/null; then
                                echo -e $red"Error:"$white ${servers[input]} "is not running"
                                count=0
                                read input
                        fi
                        let count=count+1
                done
		screen -r ${servers[input]}
	;;
	5)
		sudo java -Xmx1G -Xms1G -jar MC_Server_Adder.jar nogui
		sleep 1
		sudo bash mc_server_manager.sh
		exit
	;;
	6)
		sudo java -Xmx1G -Xms1G -jar /home/daniel/scripts/MC_Server_Remover.jar nogui
		sleep 1
		sudo bash /home/daniel/scripts/mc_server_manager.sh
		exit
	;;
	7)
		sudo java -Xmx1G -Xms1G -jar /home/daniel/scripts/MC_Plugin_Manager.jar nogui
                sleep 1
	;;
esac
done
