package main;

import java.io.IOException;
import java.io.OutputStream;

import com.fazecast.jSerialComm.SerialPort;

public class SerialConnection extends Connection{

	OutputStream os;
	SerialPort sp;

	public SerialConnection(Command cmd) {
		super(cmd);
		sp = SerialPort.getCommPort("COM3");
		sp.setBaudRate(38400);
		sp.openPort();
		os = sp.getOutputStream();
	}

	@Override
	protected void write(byte[] data) {
		// TODO 自動生成されたメソッド・スタブ
		try {
			os.write(data);
			os.flush();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}

	@Override
	public void close() {
		sp.closePort();
	}

}
