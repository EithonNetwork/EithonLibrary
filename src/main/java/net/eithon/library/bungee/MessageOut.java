package net.eithon.library.bungee;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

class MessageOut {
	private ByteArrayDataOutput _out;

	MessageOut() {
		this._out = ByteStreams.newDataOutput();
	}

	MessageOut add(String... strings) {
		for (String argument : strings) {
			if (argument != null) {
				this._out.writeUTF(argument);
			}
		}
		return this;
	}

	MessageOut add(byte[] byteArray) {
		if (byteArray == null) return this;
		this._out.writeShort(byteArray.length);
		if (byteArray.length > 0) this._out.write(byteArray);
		return this;
	}
	
	byte[] toByteArray() { return this._out.toByteArray(); }
}
