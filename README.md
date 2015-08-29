# EithonLibrary

Eithon java code library for Minecraft.

## Varibles

* eithon.UseMarkUpForMessages: Use the same markup language for colors as EDocs for ConfigurableMessage
* eithon.UseWrappingForMessages: Use the same wrapping (and center text) as EDocs for ConfigurableMessage

## Spigot

* Set up [BuildTools](http://www.spigotmc.org/wiki/buildtools/)

## Release history

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
