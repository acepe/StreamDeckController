###### Linux

On linux, the udev subsystem blocks access to the StreamDeck without some special configuration. Save the following to /etc/udev/rules.d/50-elgato.rules and reload the rules with sudo udevadm control --reload-rules


```SUBSYSTEM=="input", GROUP="input", MODE="0666"
SUBSYSTEM=="usb", ATTRS{idVendor}=="0fd9", ATTRS{idProduct}=="0060", MODE:="666", GROUP="plugdev"
SUBSYSTEM=="usb", ATTRS{idVendor}=="0fd9", ATTRS{idProduct}=="0063", MODE:="666", GROUP="plugdev"
SUBSYSTEM=="usb", ATTRS{idVendor}=="0fd9", ATTRS{idProduct}=="006c", MODE:="666", GROUP="plugdev"
```
Unplug and replug the device and it should be usable

