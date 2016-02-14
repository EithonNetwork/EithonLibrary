# EithonLibrary

Eithon java code library for Minecraft.

## Varibles

* eithon.UseMarkUpForMessages: Use the same markup language for colors as EDocs for ConfigurableMessage
* eithon.UseWrappingForMessages: Use the same wrapping (and center text) as EDocs for ConfigurableMessage

## Spigot

* Set up [BuildTools](http://www.spigotmc.org/wiki/buildtools/)

## Release history

### 4.2 (2016-02-??)

* NEW: Added MySQL database support.
* NEW: Added repeatEveryHour for AlarmTrigger.

### 4.1 (2016-02-13)

* NEW: Added ICommandSyntax.addKeyWords().
* CHANGE: Now can tab complete the ambiguity between "restart <time>" and "restart cancel" after just writing "restart".

### 4.0.2 (2016-01-24)

* BUG: Optional arguments did not work when adding a valuegetter as default value.

### 4.0.1 (2016-01-24)

* BUG: Debug printouts were left in the code.

### 4.0 (2016-01-24)

* NEW: Went to new major release number due to EithonCommand being a big change.

### 3.9.1 (2016-01-24)

* BUG: Did not handle ambiguous commands, such as "root <parameter>" combined with "root sub <parameter>"
* BUG: tabComplete could return NULL which resulted in strange completions.
* BUG: parseCommandSyntax did not always return the deepest CommandSyntax.

### 3.9 (2016-01-23)

* NEW: Added syntax support when parsing of a user command failed.
* NEW: Added BukkitValueLists for Players, Worlds, etc.
* CHANGE: tabComplete now gives both the hint and a value
* BUG: Now handles REST arguments.

### 3.8 (2016-01-22)

* NEW: Better control over command hints.
* BUG: The REST type for command parameters didn't work

### 3.7 (2016-01-20)

* NEW: Added asEithonPlayer() and asOfflinePlayer() to the command argument class.
* CHANGE: Now uses ':' as an alternative to '=' for named parameters
* CHANGE: PlayerCollection now can handle OfflinePlayer
* BUG: Hints were parsed as keywords.

### 3.6 (2016-01-19)

* NEW: Entirely new command interpreter with tab completion.
* CHANGE: Some refactoring of bungee to make it easier to test.

### 3.5.3 (2016-01-13)

* BUG: Leading literal bracket did not get color.

### 3.5.2 (2016-01-12)

* BUG: SimpleMarkUp could not handle nested brackets properly.

### 3.5.1 (2016-01-09)

* BUG: Bungee messages was kept too long.

### 3.5 (2015-12-29)

* NEW: Added EithonPlayerMoveHalfBlockEvent

### 3.4 (2015-12-27)

* CHANGE: TimeMisc.secondsToString now supports non-integer seconds.
* CHANGE: TimeMisc.secondsToString now can print mm:ss.
* CHANGE: EithonPlayerMoveOneBlockEvent now stores the from-to as locations instead of blocks.
* CHANGE: EithonPlayerMoveOneBlockEvent now ignores cancelled move events.

### 3.3 (2015-12-26)

* NEW: Added VaultFacade

### 3.2 (2015-12-25)

* NEW: Added TemporaryEffect

### 3.1 (2015-11-28)

* NEW: Added getEntry() to PlayerCollection.

### 3.0 (2015-11-13)

* CHANGE: Optimization of PlayerMoveEvent

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
