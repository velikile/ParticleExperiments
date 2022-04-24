import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

public class KeysState implements KeyListener
{	
	public volatile short [] buttons;
	public long LastTimePressed =0;
	public KeysState(int numberOfButtons)
	{
		buttons = new short[numberOfButtons];
	}
	public void keyPressed(KeyEvent e) 
	{
		//R.println(e.getKeyCode());
		int keycode = e.getKeyCode();
		if(keycode<buttons.length)
			buttons[keycode]=1;
	}
	public void keyReleased(KeyEvent e) 
	{
		int keycode = e.getKeyCode();
		if(keycode<buttons.length)
			buttons[keycode]=0;
	}
	public void keyTyped(KeyEvent e)
	{
		int keycode = e.getKeyCode();
		if(keycode<buttons.length)
		{
			buttons[keycode]++;
			buttons[keycode]--;
		}	
	}

}