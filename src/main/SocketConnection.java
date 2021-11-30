package main;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class SocketConnection extends Connection{
	private OutputStream os;
	private Socket s;

	public SocketConnection(Command cmd) {
		super(cmd);
		try {
			s = new Socket("raspberrypi.local",25050);
			os = s.getOutputStream();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void write(byte[] data) {
		try {
			os.write(data);
			os.flush();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}

}
