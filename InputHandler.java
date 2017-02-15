
import java.awt.event.KeyEvent;
public class InputHandler
{
	static boolean [] keys = null;
	static Camera camused = null;
	static float factor = 0;

	public static void Init(boolean [] keysa,Camera cam)
	{
			keys = keysa;
			camused = cam;
	}
	public static void HandleInputs()
	{

		if(keys[KeyEvent.VK_Q])
		{
			camused.thetaR+=0.01;
		}
		if(keys[KeyEvent.VK_E])
		{
			camused.thetaR-=0.01;
		}
		if(keys[KeyEvent.VK_W])
		{
			V3 dir = camused.direction.clone();
			V3 right = new V3(-dir.z,0,dir.x);
			Quaternion p= new Quaternion(2*camused.thetaP,right);
			p.convert();
			dir.rotate(p);
			camused.position.sub(dir);
		}
		if(keys[KeyEvent.VK_S])
		{
			V3 dir = camused.direction.clone();
			V3 right = new V3(-dir.z,0,dir.x);
			Quaternion p= new Quaternion(2*camused.thetaP,right);
			p.convert();
			dir.rotate(p);
			camused.translate(dir);
		}
		if(keys[KeyEvent.VK_A])
		{
			V3 left = new V3(camused.direction.z,0,-camused.direction.x);
			camused.translate(left);
		}
		if(keys[KeyEvent.VK_D])
		{
			V3 right = new V3(-camused.direction.z,0,camused.direction.x);
			camused.translate(right);
		}
		if(keys[KeyEvent.VK_DOWN])
		{
			V3 down = V3.sMul(-1f,camused.up);
			camused.translate(down);
		}
		if(keys[KeyEvent.VK_UP])
		{
			V3 up = camused.up.clone();
			camused.translate(up);
		}
		
			
	}
}