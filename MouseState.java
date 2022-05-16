
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class MouseState implements MouseListener,MouseMotionListener
{
	public V2 position = new V2(0,0);
	public volatile  boolean [] buttons;
	public long LastTimeClicked =0;
	public MouseState(int numberOfButtons)
	{
		buttons = new boolean[numberOfButtons];
	}
        public void mouseMoved(MouseEvent e) 
	{
		position.x = e.getX();
		position.y = e.getY();
	}
	public void mouseDragged(MouseEvent e) 
	{
		position.x = e.getX();
		position.y = e.getY();
	}
	public void mouseExited(MouseEvent e) {}
	public void mouseClicked(MouseEvent e)
	{
		buttons[e.getButton()-1] = false;
	}
	public void mouseEntered(MouseEvent e){}
	public void mouseReleased(MouseEvent e){
		buttons[e.getButton()-1] = false;
	}
	public void mousePressed(MouseEvent e)
	{
		LastTimeClicked = System.nanoTime();
		buttons[e.getButton()-1] = true;
	}

}
