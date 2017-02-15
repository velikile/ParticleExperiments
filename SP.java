import java.awt.Graphics;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;

//aka sharedPanel 
public class SP extends JPanel
{
		public BufferedImage [] sprites;
		private static final long serialVersionUID = 1L;
		SP(BufferedImage sprite)
		{
			sprites = new BufferedImage[10];
			sprites[0] = sprite;
			setFocusable(true);
			setFocusTraversalKeysEnabled(false);
		}
		@Override
		public void paintComponent(Graphics graphics)
		{	
			super.paintComponent(graphics);
			graphics.drawImage(BallPhysicsTest.fastersprite, 0, 0, BallPhysicsTest.width, BallPhysicsTest.height, null);
			BallPhysicsTest.fastersprite.flush();
			 for(BufferedImage sprite:sprites)
			 {
			 	if(sprite!=null)
			 	{
			 		graphics.drawImage(sprite,0,0,null);
			 		sprite.flush();
				}
			 }
		}
	
 
}