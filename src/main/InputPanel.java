package main;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayDeque;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class InputPanel extends JPanel implements MouseListener, KeyListener, WindowListener{
	private Robot rob;
	private ScheduledFuture<?> future;
	private boolean isRunning = false;
	Command cmd;
	Connection connection;
	ArrayDeque<Integer> adx;
	ArrayDeque<Integer> ady;

	private int my;
	private double rho = 0.0;

	private boolean lw=false,la=false,ls=false,ld=false;
	private boolean rw=false,ra=false,rs=false,rd=false;


	private static final long serialVersionUID = -2902945983105394353L;

	public InputPanel(JFrame jf) {
		this.setCursor((new Cursor(Cursor.CROSSHAIR_CURSOR)));
		this.setFocusable(true);
		try {
			rob = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
		}

		this.cmd = new Command();
		this.connection = new SerialConnection(cmd);

		this.setFocusTraversalKeysEnabled(false);
		this.enableInputMethods(false);

		this.addMouseListener(this);
		this.addKeyListener(this);
		jf.addWindowListener(this);

		this.connection.start();

	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		switch (e.getButton()) {
			case MouseEvent.BUTTON3://右クリック
				cmd.releaseButton(Command.ZL);
				break;

			case MouseEvent.BUTTON2:
				cmd.pressButton(Command.Y);
				break;

			case MouseEvent.BUTTON1://左クリック
				cmd.pressButton(Command.ZR);
				break;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		switch (e.getButton()) {
		case MouseEvent.BUTTON3://右クリック
			cmd.pressButton(Command.ZL);
			break;
		case MouseEvent.BUTTON2:
			cmd.releaseButton(Command.Y);
			break;
		case MouseEvent.BUTTON1:
			cmd.releaseButton(Command.ZR);
			break;
	}

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

	public void start() {

		Point p = this.getLocationOnScreen();
		p.translate(this.getWidth()/2,this.getHeight()/2);
		rob.mouseMove(p.x, p.y);

		future = SwitchController.scheduler.scheduleAtFixedRate(()->{

			Point zero = this.getLocationOnScreen();
			Point point = MouseInfo.getPointerInfo().getLocation();
			point.translate(-this.getWidth()/2-zero.x, -this.getHeight()/2-zero.y);

			my += point.y;
			my = Math.abs(my)>4000?(int)Math.signum(my)*4000:my;

			//球面座標系での計算を考える
			double r = 2000.0;

			//仰角rhの計算
			double rh = Math.atan2(my,r);

			//グローバル座標系上の微小オイラー角変化量(=角速度)
			double dR = 0.0;
			double dP = rh-rho;
			double dY = -point.getX()/r;

			//inverse of
			//([
			//[1,sin(r)tan(t),cos(r)tan(t)],
			//[0,cos(r),-sin(r)],
			//[0,sin(r)sec(t),cos(r)sec(t)]
			//])
			double omegaX = dR*1.0 - Math.sin(rho)*dY;
			double omegaY = dP;
			double omegaZ = Math.cos(rho)*dY;

			short gx = (short) (omegaX*66635.0*3.0/Math.PI);
			short gy = (short) (omegaY*65535.0*3.0/Math.PI);
			short gz = (short) (omegaZ*65535.0*3.0/Math.PI);

//			short gx = 0;
//			short gy = (short) ((rh-rho)*65535.0*3.0/Math.PI);
//			short gz = (short) ((th-theta)*65535.0*3.0/Math.PI);


//			System.out.printf("%d\t%d\r\n", gy,gz);
			cmd.moveGyro(gx, gy, gz);

			rho = rh;

			Point zeropoint = this.getLocationOnScreen();
			zeropoint.translate(this.getWidth()/2,this.getHeight()/2);
			rob.mouseMove(zeropoint.x, zeropoint.y);

		}, 0, 15, TimeUnit.MILLISECONDS);

	}

	@SuppressWarnings("unused")
	private int mapping(int x) {
		if(x == 0) {
			return  Command.SCENTER;
		}
		x = x*1152/1024;

		x += x>0 ? 256:-256;

		if(2047<x) x = 2047;
		if(x<-2048) x = -2048;

		x += Command.SCENTER;
		return x;
	}

	public void stop() {
		future.cancel(true);
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_C :
            	if (e.isControlDown() && isRunning) {
            		isRunning = false;
            		stop();
            	}else if(e.isControlDown() && !isRunning) {
            		isRunning = true;
            		start();
            	}
                break;

        	case KeyEvent.VK_F:
        		cmd.pressButton(Command.A);
        		break;

        	case KeyEvent.VK_V:
        		cmd.pressButton(Command.B);
        		break;

        	case KeyEvent.VK_R:
        		cmd.pressButton(Command.R);
        		break;

        	case KeyEvent.VK_E:
        		cmd.pressButton(Command.X);
        		break;

        	case KeyEvent.VK_Z:
        		cmd.pressButton(Command.MINUS);
        		break;

        	case KeyEvent.VK_X:
        		cmd.pressButton(Command.PLUS);
        		break;

        	case KeyEvent.VK_TAB:
        		cmd.pressButton(Command.ZL);
        		break;

        	case KeyEvent.VK_SHIFT:
        		cmd.pressButton(Command.ZL);
        		break;

        	case KeyEvent.VK_SPACE:
        		cmd.pressButton(Command.B);
        		break;

        	case KeyEvent.VK_ESCAPE:
        		cmd.pressButton(Command.HOME);
        		break;

        	case KeyEvent.VK_T:
        		cmd.pressButton(Command.RS);
        		break;

        	case KeyEvent.VK_G:
        		cmd.pressButton(Command.LS);
        		break;

        	case KeyEvent.VK_Q:
        		cmd.pressButton(Command.L);
        		if(e.isControlDown()) {
        			System.exit(0);
        		}
        		break;

            case KeyEvent.VK_W :
            	cmd.moveLeftStickY(Command.SMAX);
            	lw=true;
                break;

            case KeyEvent.VK_A :
            	cmd.moveLeftStickX(Command.SMIN);
            	la=true;
                break;

            case KeyEvent.VK_S :
            	cmd.moveLeftStickY(Command.SMIN);
            	ls=true;
                break;

            case KeyEvent.VK_D :
            	cmd.moveLeftStickX(Command.SMAX);
            	ld=true;
                break;

            case KeyEvent.VK_1:
            	cmd.pressButton(Command.UP);
            	break;
            case KeyEvent.VK_2:
            	cmd.pressButton(Command.RIGHT);
            	break;
            case KeyEvent.VK_3:
            	cmd.pressButton(Command.DOWN);
            	break;
            case KeyEvent.VK_4:
            	cmd.pressButton(Command.LEFT);
            	break;

            case KeyEvent.VK_UP:
            	cmd.moveRightStickY(Command.SMAX);
            	rw=true;
            	break;
            case KeyEvent.VK_LEFT:
            	cmd.moveRightStickX(Command.SMIN);
            	ra=true;
            	break;
            case KeyEvent.VK_DOWN:
            	cmd.moveRightStickY(Command.SMIN);
            	rs=true;
            	break;
            case KeyEvent.VK_RIGHT:
            	cmd.moveRightStickX(Command.SMAX);
            	rd=true;
            	break;

        }
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
        switch (key) {
        	case KeyEvent.VK_F:
        		cmd.releaseButton(Command.A);
        		break;

        	case KeyEvent.VK_V:
        		cmd.releaseButton(Command.B);
        		break;

        	case KeyEvent.VK_R:
        		cmd.releaseButton(Command.R);
        		break;

        	case KeyEvent.VK_E:
        		cmd.releaseButton(Command.X);
        		break;

        	case KeyEvent.VK_Z:
        		cmd.releaseButton(Command.MINUS);
        		break;

        	case KeyEvent.VK_X:
        		cmd.releaseButton(Command.PLUS);
        		break;

        	case KeyEvent.VK_TAB:
        		cmd.releaseButton(Command.ZL);
        		break;

        	case KeyEvent.VK_SHIFT:
        		cmd.releaseButton(Command.ZL);
        		break;

        	case KeyEvent.VK_SPACE:
        		cmd.releaseButton(Command.B);
        		break;

        	case KeyEvent.VK_ESCAPE:
        		cmd.releaseButton(Command.HOME);
        		break;

        	case KeyEvent.VK_T:
        		cmd.releaseButton(Command.RS);
        		break;

        	case KeyEvent.VK_G:
        		cmd.releaseButton(Command.LS);
        		break;

        	case KeyEvent.VK_Q:
        		cmd.releaseButton(Command.L);
        		break;

            case KeyEvent.VK_W :

	        	if (ls==true) {
	        		cmd.moveLeftStickY(Command.SMIN);
	        	}else {
	        		cmd.moveLeftStickY(Command.SCENTER);
	        	}
            	lw=false;
                break;

            case KeyEvent.VK_A :
            	if (ld==true) {
            		cmd.moveLeftStickX(Command.SMAX);
            	}else {
            		cmd.moveLeftStickX(Command.SCENTER);
            	}
            	la=false;
                break;

            case KeyEvent.VK_S :
            	if (lw==true) {
            		cmd.moveLeftStickY(Command.SMAX);
            	}else {
            		cmd.moveLeftStickY(Command.SCENTER);
            	}
            	ls=false;
                break;

            case KeyEvent.VK_D :
            	if (la==true) {
            		cmd.moveLeftStickX(Command.SMIN);
            	}else {
            		cmd.moveLeftStickX(Command.SCENTER);
            	}
            	ld=false;
                break;

            case KeyEvent.VK_1:
            	cmd.releaseButton(Command.UP);
            	break;
            case KeyEvent.VK_2:
            	cmd.releaseButton(Command.RIGHT);
            	break;
            case KeyEvent.VK_3:
            	cmd.releaseButton(Command.DOWN);
            	break;
            case KeyEvent.VK_4:
            	cmd.releaseButton(Command.LEFT);
            	break;

            case KeyEvent.VK_UP :
	        	if (rs==true) {
	        		cmd.moveRightStickY(Command.SMIN);
	        	}else {
	        		cmd.moveRightStickY(Command.SCENTER);
	        	}
            	rw=false;
                break;

            case KeyEvent.VK_LEFT :
            	if (rd==true) {
            		cmd.moveRightStickX(Command.SMAX);
            	}else {
            		cmd.moveRightStickX(Command.SCENTER);
            	}
            	ra=false;
                break;

            case KeyEvent.VK_DOWN :
            	if (rw==true) {
            		cmd.moveRightStickY(Command.SMAX);
            	}else {
            		cmd.moveRightStickY(Command.SCENTER);
            	}
            	rs=false;
                break;

            case KeyEvent.VK_RIGHT :
            	if (ra==true) {
            		cmd.moveRightStickX(Command.SMIN);
            	}else {
            		cmd.moveRightStickX(Command.SCENTER);
            	}
            	rd=false;
                break;

        }
	}

	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {
		connection.stop();
		connection.close();
	}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowDeactivated(WindowEvent e) {}
}
