package net.eithon.library.exceptions;

import org.bukkit.command.CommandSender;

public class TryHandler {
	public static <T> T handleExceptions(CommandSender sender, ISupplier<T> provider){
		try {
			return provider.doIt();
			
		} catch (TryAgainException e) {
			sender.sendMessage(String.format("Try again later. (%s)", e.getMessage()));
			recursiveMessages(sender, e.getCause());
			e.printStackTrace();
		} catch (FatalException e) {
			sender.sendMessage(String.format("Fatal error. (%s)", e.getMessage()));
			recursiveMessages(sender, e.getCause());
			e.printStackTrace();
		} catch (PlayerException e) {
			sender.sendMessage(String.format("Player error. (%s)", e.getMessage()));
		} catch (Exception e) {
			sender.sendMessage(String.format("Unexpected error. (%s)", e.getMessage()));
			recursiveMessages(sender, e.getCause());
			e.printStackTrace();
		}
		return null;
	}
	
	private static void recursiveMessages(CommandSender sender, Throwable throwable) {
		if (throwable == null) return;
		sender.sendMessage(throwable.getMessage());
		recursiveMessages(sender, throwable.getCause());
	}

	public static void handleExceptions(IExecutor provider){
		try {
			provider.doIt();
			
		} catch (TryAgainException e) {
			e.printStackTrace();
		} catch (FatalException e) {
			e.printStackTrace();
		} catch (PlayerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void handleExceptions(CommandSender sender, IExecutor provider){
		try {
			provider.doIt();
			
		} catch (TryAgainException e) {
			sender.sendMessage(String.format("Try again later. (%s)", e.getMessage()));
			recursiveMessages(sender, e.getCause());
			e.printStackTrace();
		} catch (FatalException e) {
			sender.sendMessage(String.format("Fatal error. (%s)", e.getMessage()));
			recursiveMessages(sender, e.getCause());
			e.printStackTrace();
		} catch (PlayerException e) {
			sender.sendMessage(String.format("Player error. (%s)", e.getMessage()));
			recursiveMessages(sender, e.getCause());
		} catch (Exception e) {
			sender.sendMessage(String.format("Unexpected error. (%s)", e.getMessage()));
			recursiveMessages(sender, e.getCause());
			e.printStackTrace();
		}
	}
}
