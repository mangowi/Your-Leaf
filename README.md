# YourLeaf
YourLeaf is an Android app designed for YourLeaf IoT platform. YourLeaf platform is used for monitoring and taking care of your plant remotely. The project is completely free.

## Target
This version of YourLeaf application is made for Android devices. The minimum sdk version is 14 and target sdk version is 28.

## Development

### Main
This version of YourLeaf application is native for android and the app is written in Java 9. (There is no Kotlin at all)

### Communication
The communication between the application and the server is done via REST on 4 main routes. Each route supports HTTP GET/POST methods for managing the YourLeaf system and obtaining certain information. For communication we use thread executors to get the data asynchronous over time. If you rework the YourLeaf platform and you are interested in a more frequent communication with may want to switch to Websockets.  

### External Libraries & Dependencies
YourLeaf Android application use only ButterKnife as an external dependency for DI of view components.

## Screenshots
![screenshot_20181224-161232](https://user-images.githubusercontent.com/16307530/50425171-ddfe3400-0879-11e9-9c1e-50eada9db752.png) ![screenshot_20181224-161257](https://user-images.githubusercontent.com/16307530/50425172-df2f6100-0879-11e9-9d3f-9a2c0977904b.png) ![screenshot_20181224-162052](https://user-images.githubusercontent.com/16307530/50425210-785e7780-087a-11e9-99ed-6e15870e67be.png)

## Twinck it
Modify, break, update and stay curious. Hit me up with your changes or questions and like awayse feel free to coppy and paste.

## Privacy
We truly respect your privacy and because of that, the entire platform is P2P oriented. When it comes to the information we store it is stored only locally at your device.
