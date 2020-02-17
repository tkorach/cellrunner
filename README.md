# cellrunner
A plugin for Eclipse to run a cell in [JavaNotebook](https://github.com/tkorach/javanotebook)

# Installation
1. Compile using Eclipse Plugin Development suite, or download the Jar file from the releases tab. 
1. Place the file in the *<eclipse installation directory>/dropins* directory. 
1. Restart Eclipse.

A menu "Cell runner" and a toolbar icon should appear in Eclipse's UI. 

# Use
The plugin includes a menu and toolbar icon to execute a cell (the method enclosing the current cursor position) or an expression. 
The kernel launch configuration's name must start with "JavaNotebook".
If a text is selected, it will be submitted as an expression. 
Else, the plugin will attempt to recognize the method in which the cursor is located. 

Ctrl+6 is the initial key binding.
