# remoTer
Access your local system's Terminal(may be behind a NAT) through your android device in Real time.
___

##### NO DATABASES
#### NO HTTP(S) REQUESTS
### ONLY SSH
___

This repository is divided into 3 parts:
1. Android App
2. Virtual Machine
3. Local System

For this System to work you would need a Virtual Machine configured(on AWS, Azure, GCP or any other platform). Install java in your machine. Make sure you have the following data about your virtual machine:
- [ ] Virtual Machine Username
- [ ] Virtual Machine Password
- [ ] Allot any non-standard port for Reverse SSH Connection. Example: 9754 
- [ ] Make sure the SSH port is still 22 on both Virtual Machine and on Local Machine.

# Local System
```bash
ssh -R <reverse_ssh_port>:localhost:<local_host_port> <VM_username>@<VM_ip>
```
Use Sudo if Permission issue occurs.

# Android App
Before running the android app change the Configuration settings - [config.properties](https://github.com/Zemotacqy/remoTer/blob/Deploy/Android/app/src/main/res/raw/config.properties#L2)

# Virtual Machine
Assuming VM is a Linux Machine. 
1. Install [Java](https://itsfoss.com/install-java-ubuntu/) in your  Virtual Machine.
2. Copy the `VM` folder in your Virtual Machine. Assume it is `~/remoTer`
3. Change the Configuration Settings - [config.properties](https://github.com/Zemotacqy/remoTer/blob/Deploy/VM/config.properties)
4. Compile the `~/remoTer/RemoTer.java` using the following command:
```bash
javac -cp ".:./jars/jsch.jar" RemoTer.java
```
___

# Technology Used
1. JSch (Java SSH Library)
2. Sockets (For Testing and Debugging Purposes)
___

And you are good to go.
Run the android app and access your Local system's Terminal.
