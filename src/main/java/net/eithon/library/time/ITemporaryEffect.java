package net.eithon.library.time;

public interface ITemporaryEffect {
	public Object Do(Object... contextArgs);
	public void Undo(Object doReturnValue, Object... contextArgs);
}
