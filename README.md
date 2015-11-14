# EithonLibrary

Eithon java code library for Minecraft.

## Varibles

* eithon.UseMarkUpForMessages: Use the same markup language for colors as EDocs for ConfigurableMessage
* eithon.UseWrappingForMessages: Use the same wrapping (and center text) as EDocs for ConfigurableMessage

## Spigot

* Set up [BuildTools](http://www.spigotmc.org/wiki/buildtools/)

## Release history

### 3.0 (2015-11-13)

* CHANGE: Optimization of PlayerMoveEvent
* CHANGE: Renamed Logger to EithonLogger

### 2.3.2 (2015-10-18)

* BUG: Trying to fix BungeeChord.

### 2.3.1 (2015-10-18)

* BUG: Used the wrong method.
* BUG: Never initiated bungeeServerName.
* BUG: Null bungeeServer name should be considered prime server.

### 2.3 (2015-10-18)

* CHANGE: Refactoring

### 2.2.1 (2015-10-18)

* BUG: SourceServerName was a constant.

### 2.2 (2015-10-18)

* NEW: Added possibility to update the debug level without reloading the configuration file.

### 2.1 (2015-10-13)

* NEW: Now has Bungee broadcast message.

### 2.0.1 (2015-10-12)

* BUG: Player name was empty on remote server where the user never has been logged in.

### 2.0 (2015-10-12)

* NEW: Added an API

### 1.8.2 (2015-10-12)

* BUG: Messages could queue up if no player was on a server.

### 1.8.1 (2015-10-12)

* BUG: BungeeController was sending the wrong subchannel.

### 1.8 (2015-10-11)

* NEW: Added EithonBungeeQuitEvent
* CHANGE: Now has delay for join event.

### 1.7.2 (2015-10-11)

* BUG: Wrong server name was shown at join.

### 1.7.1 (2015-10-11)

* BUG: Fixed numerous bugs for the EithonBungeeJoinEvent.

### 1.7 (2015-10-11)

* NEW: Added EithonBungeeJoinEvent

### 1.6.1 (2015-10-11)

* BUG: Added verbose logging to find bugs.

### 1.6 (2015-10-11)

* NEW: Added BungeeCord support

### 1.5 (2015-09-17)

* NEW: CommandParser now can read boolean.
* BUG: LineWrapper added null before and after lines that were not centered.

### 1.4 (2015-09-10)

* NEW: Support for weekly alarms.
* BUG: Irritating message, "Did not expect file xxx to exist."
* BUG: Line wrapping could be off by 1 character when centering lines.

### 1.3.1 (2015-08-29)

* BUG: Always returned false when checking if players current world was OK for flying.

### 1.3 (2015-08-29)

* NEW: Added method for checking if a string is in a collection.

### 1.2.2 (2015-08-29)

* BUG: Wrapped lines with several colors ended up with the wrong color on new lines.

### 1.2.1 (2015-08-28)

* BUG: Removed the "Did not expect file ... warning message.

### 1.2 (2015-08-28)

* CHANGE: Should work better with plugman.

### 1.1.2 (2015-08-23)

* BUG: Could not read PermissionBasedMultiplier if using plugman

### 1.1.1 (2015-08-16)

* BUG: Could not read PermissionBasedMultiplier

### 1.1 (2015-08-16)

* NEW: Introducing permission based multipliers
* NEW: Added support for lists of TimeSpans in configuration files

### 1.0.2 (2015-08-11)

* BUG: getSeconds() did always return 0

### 1.0.1 (2015-08-11)

* BUG: stringToSeconds had a null pointer exception.

### 1.0 (2015-04-18)

* NEW: First release
