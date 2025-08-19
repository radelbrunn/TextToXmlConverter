Change Java Version on Ubuntu

The fastest and most reliable way to switch between installed Java versions is with the update-alternatives command. It manages symbolic links for common commands, making it easy to change which executable a command points to.

1. List Available Java Versions

First, open your terminal and check which Java installations are available on your system.
Bash

sudo update-alternatives --config java

This command will display a list of all installed Java environments. You will see output similar to this:

There are 2 choices for the alternative java (providing /usr/bin/java).

Selection    Path                                      Priority   Status
------------------------------------------------------------
* 0            /usr/lib/jvm/java-11-openjdk-amd64/bin/java   1111      auto mode
  1            /usr/lib/jvm/java-11-openjdk-amd64/bin/java   1111      manual mode
  2            /usr/lib/jvm/java-21-openjdk-amd64/bin/java   2121      manual mode

Press <enter> to keep the current choice[*], or type selection number:

2. Select Your Desired Version

From the list, find the number corresponding to the Java version you want to use. In the example above, if you want to switch to Java 21, you would enter 2.

Type the number of your choice and press Enter.

Press <enter> to keep the current choice[*], or type selection number: 2

3. Verify the Change

After making your selection, you can immediately verify that the change was successful by checking your Java version.
Bash

java -version

The output should now show the version you just selected.

openjdk version "21.0.2" 2024-01-16
OpenJDK Runtime Environment (build 21.0.2+13-Ubuntu-1)
OpenJDK 64-Bit Server VM (build 21.0.2+13-Ubuntu-1, mixed mode, sharing)
