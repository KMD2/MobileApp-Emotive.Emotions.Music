# EmoMusic 
*Developed by: Duoaa Khalifa & Nadya Abdel Madjid*


![EmoMusic Logo](EmoMusic.png)

# Description

EmoMusic: Emotions + Music

An interactive mobile application along with a desktop application and an EEG headset (Emotiv). The mobile application allows the user to view his/her emotion levels at the moment, display statistics of each emotion for the past one month, explore the personalized happiness heatmap based on the user's all-time recorded data or common happiness heatmap based on all user's data. Finally, play music tracks and notice the change in the emotions' levels.

![Description](Description.png)

# Installation and Setup
## Desktop Application and Emotiv Headset

1. Clone the repository.
2. Unzip the file **emotiv** and go to the release-builds directory. 
3. Run the proper file According to your operating system.
4. After a successful installation, the application will launch automatically. (see figure below)

![Desktop start](EmoMusic_Desktop_start.JPG)

5. Switch on your Emotiv headset.
6. Via bluetooth, connect the headset manually to your computer.

![Bluetooth connection](Add_Device_Blutooth.JPG)

7. Go back to the desktop application. It should display he  message "Your headset is connected".

![Connected headset]()

8. Put on the headset and click on **Start Recording**.

## Mobile Application
 1. Sign-up and login to the mobile application. The Id is your Emotiv headset unique id.
 
 ![Login page]()
 
 2. The main page consists of 8 buttons. The forth till the eighth button each corresponds to one of the six emotions: Interest, Stress, Relaxation, Excitement, Engagement and Focus. Each button displays the current level of the intended emotion in percentages.
 
  ![Main page emotions]()
  
 3. The **Map** button directs the user to the **Happinesses Map**, by default set to **My Map** which visualizes the level of happiness in the places the user visited while wearing the headset.The user have the option to Choose between two radii, gradients and opacities The Happiness level is calculated based on the following formula:
  
    Happiness Level = (15% * Interest Level) + (15% * Engagement Level) + (35% Interest Level) + (35% 		Relaxation Level) 

![My Map heatmap]() 

4. From the Drop-down list, the user can choose **Common Map**, which is the **Happiness Map** but based on all users level of happiness (In places the all users visited while wearing the headset).

![Common heatmap]()

5. Back to the main menu, the **Play** button directs the user to a playlist of the songs retrieved from the user's mobile phone. The user can play a song then go back to the main menu and observe the change in the emotion levels based on the music track s/he is playing. The user can play, pause, fast-forward, rewind, skip to the next song, back to the previous song and user can leave the application whilst the music track is still playing (A notification will show the current playing song, by tapping on it, the user will be redirected to the application). If the user wishes to stop the music track, s/he can use the **X** icon on the top most-right of the page.   

![Music player]()

# Connection Map (Distributed systems)

![DBS](DBS.png)









	
	












