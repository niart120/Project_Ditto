package main;

public class Command {
	public static final int Y = 0x000001;
	public static final int X = 0x000002;
	public static final int B = 0x000004;
	public static final int A = 0x000008;
	public static final int R = 0x000040;
	public static final int ZR = 0x000080;

	public static final int MINUS = 0x000100;
	public static final int PLUS = 0x000200;
	public static final int RS = 0x000400;
	public static final int LS = 0x000800;
	public static final int HOME = 0x001000;
	public static final int CAP = 0x002000;
	public static final int GRIP = 0x008000;

	//HAT and L Trigger
	public static final int DOWN = 0x010000;
	public static final int UP = 0x020000;
	public static final int RIGHT = 0x040000;
	public static final int LEFT = 0x080000;
	public static final int L = 0x400000;
	public static final int ZL = 0x800000;


	public static final int SMIN = 0;
	public static final int SCENTER = 0x800;
	public static final int SMAX = 0xFFF;

	int[] cmd = {GRIP,SCENTER,SCENTER,SCENTER,SCENTER};
	short[] acc = {0,0,0};
	short[] gyro = {0,0,0};

	public Command() {

	}

	public void pressButton(int key) {
		cmd[0] |= key;
	}

	public void releaseButton(int key) {
		cmd[0] &= (0xFFFFFF^key);
	}

	public void moveLeftStick(int lx, int ly) {
		cmd[1]=lx;
		cmd[2]=ly;
	}

	public void moveLeftStickX(int lx) {
		cmd[1]=lx;
	}

	public void moveLeftStickY(int ly) {
		cmd[2]=ly;
	}

	public void moveRightStick(int rx, int ry) {
		cmd[3]=rx;
		cmd[4]=ry;
	}

	public void moveRightStickX(int lx) {
		cmd[3]=lx;
	}

	public void moveRightStickY(int ly) {
		cmd[4]=ly;
	}

	public void moveGyro(short dx, short dy, short dz) {
		gyro[0] = dx;
		gyro[1] = dy;
		gyro[2] = dz;
	}

}
