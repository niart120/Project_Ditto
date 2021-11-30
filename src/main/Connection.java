package main;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

abstract class Connection {
	private Command cmd;
	private ScheduledFuture<?> future;

	public Connection(Command cmd) {
		this.cmd = cmd;
	}

	public void start() {
		future = SwitchController.scheduler.scheduleAtFixedRate(()->{
			byte[] data = new byte[21];
			//sum:21bytes
			//3bytes
			int btn = cmd.cmd[0];
			data[0] = (byte)btn;
			data[1] = (byte)(btn>>>8);
			data[2] = (byte)(btn>>>16);

			//3bytes
			byte[] lstick = new byte[3];
			packShorts(cmd.cmd[1],cmd.cmd[2],lstick);
			data[3] = lstick[0];
			data[4] = lstick[1];
			data[5] = lstick[2];

			//3bytes
			byte[] rstick = new byte[3];
			packShorts(cmd.cmd[3],cmd.cmd[4],rstick);
			data[6] = rstick[0];
			data[7] = rstick[1];
			data[8] = rstick[2];

			//signed value
			data[9] = (byte) cmd.acc[0];
			data[10] = (byte)(cmd.acc[0]>>8);
			data[11] = (byte) cmd.acc[1];
			data[12] = (byte)(cmd.acc[1]>>8);
			data[13] = (byte) cmd.acc[2];
			data[14] = (byte)(cmd.acc[2]>>8);

			//singed value
			data[15] = (byte) cmd.gyro[0];
			data[16] = (byte)(cmd.gyro[0]>>8);
			data[17] = (byte) cmd.gyro[1];
			data[18] = (byte)(cmd.gyro[1]>>8);
			data[19] = (byte) cmd.gyro[2];
			data[20] = (byte)(cmd.gyro[2]>>8);

			//System.out.println(System.currentTimeMillis());
			write(data);
		}, 0, 15, TimeUnit.MILLISECONDS);
	}

	public void stop() {
		future.cancel(true);
	}

	abstract protected void write(byte[] data);
	abstract public void close();

	private void packShorts(int short1, int short2, byte[] pack) {
		pack[0] = (byte) (short1 & 0xFF);
		pack[1] = (byte) (((short2<<4) & 0xF0) | ((short1>>>8) & 0x0F));
		pack[2] = (byte) ((short2>>>4)&0xFF);
	}

}
