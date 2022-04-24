
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.imageio.ImageIO;

import javax.swing.WindowConstants;
import javax.swing.JLabel;
import java.awt.Color;
import javax.swing.SwingUtilities;
import javax.swing.ImageIcon;
import java.awt.Graphics;

import java.io.File;
import java.util.Random;
import java.util.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.VolatileImage;
import java.awt.image.DataBufferByte;
import java.awt.image.BufferStrategy;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.geom.*;
import java.awt.*;
   
public class Render3d extends JFrame 
{
	
	public static Float fov  = (float)Math.PI * (1f/4f);

	public static V3 eProj = new V3(0,0,0.2f);
	public static V3 eTrans = new V3(0,0,1f);
	public static float nearP = 0.2f;
	public static float farP = 1000f;
	
	private static float epsilon = 0.08f;
	public static Camera cam = new Camera(new V3(0,0,-1),new V3(0f,0f,0.5f));
	public static Camera cam0 = new Camera(new V3(0,0,-2),new V3(0f,0f,1f));
	public static Camera cam1 = new Camera(new V3(0,0,-3),new V3(0f,0f,1f));
	public static String [] m = new String[10];
	public static boolean [] keys =  new boolean[1024];
	public static Cube bullet =null;
	static Cube[] cubes = new Cube[1024]; 
	static float [] cubesDistances= new float[1024];
	static float currentCubeIteration = -1;
	static float WorkingCubeCount =100;
	public static Camera camused= cam;
	public static V3 F=new V3(-300,-100,0);
	public static int height = 1050;
	public static int width  = 1680;
	public static BufferedImage sprite = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB); 
	public static V2 wh = new V2(width,height);
	public static volatile boolean space = false;
	public static VolatileImage CANVAS;// = new BufferedImage(600,600,BufferedImage.TYPE_3BYTE_BGR);
	public static int[] imagePixelData;
	public MyJPanel panel;
	public static boolean running = true;
	public static float randomArray[][]= new float[1024][4];
	public static Entity[] entities;
	public static Cube c;
	public static V3 [] points = generateSpherePoints(new V3(0,0,0),1f);
	public static float cubeXSpeed=0.01f;
	public static float cubeYSpeed=0.01f;
	public static float screenZ=1f;
	public static int cubeIndex=0;
	public static float thetaY;
	public static float cubeAngle=15;
	public static Graphics G;
	public static float eyeZ = 1f;
	public static V2 speed= new V2(0.05f,0.07f);
	public static float speedZ=0.5f;
	public static int Mx;
	public static int My;
	
	Render3d()
	{
		GraphicsConfiguration gc = getGraphicsConfiguration();
		running = true;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIgnoreRepaint( true );
		setSize(width,height);
		setTitle("Java Swing - JPanel Draw Filled Circle with Random Colors");
		imagePixelData = ((DataBufferInt)sprite.getRaster().getDataBuffer()).getData();
		CANVAS = gc.createCompatibleVolatileImage(getWidth(), getHeight());
		initAndClearRect();

		panel = new MyJPanel();
		Container container = getContentPane();
		container.add(panel);
		setVisible(true);
		Random r =new Random();
		for (int i = 0; i < randomArray.length; i++) 
		{
			randomArray[i][0]=r.nextFloat();
			randomArray[i][2]=r.nextFloat();
			randomArray[i][1]=r.nextFloat();
			randomArray[i][3]=r.nextFloat();
		}
		
		m[0]= 1+"";
		m[1]= 1+"";
		m[2]= 1+"";
		m[3]= 1+"";
		initCubes();
	}
	//Inner Class
	public class MyJPanel extends JPanel implements MouseListener,MouseMotionListener,KeyListener
	{	
		
		private static final long serialVersionUID = 1L;
		MyJPanel()
		{
			addMouseListener(this);
			//addKeyListener(new LineIntersectionTesterKeyListener(A,B,C,D));
			addKeyListener(this);
			//addKeyListener(new FireCubeIntoScene(bullet, this,camused));
			setFocusable(true);
			setFocusTraversalKeysEnabled(false);
			addMouseMotionListener(this);
		}
		//this is also an event listener it triggers when the repaint method is called
		@Override
		public void paintComponent(Graphics graphics)
		{	//super.paintComponent(graphics);
			//graphics.drawImage(CANVAS,0,0,null);
			graphics.drawImage(sprite,0,0,null);
		}
		public void mousePressed(MouseEvent e) 
		{
			Mx = e.getX();
			My = e.getY();
		}
		public void mouseReleased(MouseEvent e)
		{	
			//need to convert each point by the amount of movement of the pitch and roll and
			FsliderActive = false;
			ProjsliderActive = false;
		}
		public void mouseEntered(MouseEvent e)
		{
		}
		public void mouseMoved(MouseEvent e) 
		{
		}
		public void mouseDragged(MouseEvent e) 
		{
			//drawVector(new V3(e.getX(),e.getY(),0),new V3(0,0,0.5f),Color.black);
			if(Sfov.isPinBound(e.getX(), e.getY())||FsliderActive)
			{
				if (!Sfov.isPinBound(e.getX(), e.getY()))
					return;
				FsliderActive = true;
				Sfov.update(e.getX());
				fov = Sfov.getFValue();
				return;
			}
			if(SProj.isPinBound(e.getX(), e.getY()))
			{
				ProjsliderActive = true;
				SProj.update(e.getX());
				eProj=new V3(0,0,SProj.getFValue());
				return;
			}
			if(SeTrans.isPinBound(e.getX(), e.getY()))
			{
				eTranssliderActive = true;
				SeTrans.update(e.getX());
				eTrans=new V3(0,0,SeTrans.getFValue());
				return;
			}
			if(SfarP.isPinBound(e.getX(), e.getY()))
			{
				farPsliderActive = true;
				SfarP.update(e.getX());
				farP= SfarP.getFValue();
				return;
			}
			if(testNob.isPinBound(e.getX(),e.getY()))
			{
				testNob.update(e.getX(),e.getY());
				return;
			}
			
			float dx = ((Mx - e.getX())/(float)width);
			float dy = ((My - e.getY())/(float)height);
			
			camused.thetaP += dy;
			camused.thetaY += dx;
			// float yaw = camused.thetaY;
			// float pitch = camused.thetaP;
			/*camused.thetaY = yaw<0? (float)(2*Math.PI) + yaw:
						     yaw>(2*Math.PI)?0:yaw;
			// camused.thetaP = pitch<0? (float)(2*Math.PI) + pitch:
			// 			     pitch>(2*Math.PI)?0:pitch;
			*/
			camused.rotate(dx,dy);
			
			Mx=e.getX();
			My=e.getY();
			
		}
		
		public void mouseExited(MouseEvent e) {}
		public void mouseClicked(MouseEvent e)
		{
			if(e.getClickCount()==2)//double click 
			{
				if(R.in(e.getX(),50,100)&&R.in(e.getY(),30,50))					
				{
					int x = Integer.parseInt(m[0]);
							x++;
							m[0]=x+"";
					
				}
				else if(R.in(e.getX(),50,100)&&R.in(e.getY(),50,70))					
				{
					int x = Integer.parseInt(m[1]);
							x++;
							m[1]=x+"";
					
				}
				else if(R.in(e.getX(),50,100)&&R.in(e.getY(),70,90))					
				{
					int x = Integer.parseInt(m[1]);
							x++;
							m[2]=x+"";
					
				}
				
				Cube t = getDoubleClickedCube(e.getX(),e.getY());
				if(t!=null)
				{
					if(t==cubes[0])
						cubeIndex= 0; 
					else if(t==cubes[1])
						cubeIndex= 1;
					else if(t==cubes[2])
						cubeIndex= 2;
					else if(t==cubes[3])
						cubeIndex= 3;
					
				}
				
			}
			if(Sfov.isPinBound(e.getX(), e.getY()))
			{
			FsliderActive = true;
			Sfov.update(e.getX());
			fov = Sfov.getFValue();
			return;
			}
			if(SProj.isPinBound(e.getX(), e.getY()))
			{
			ProjsliderActive = true;
			SProj.update(e.getX());
			eProj=new V3(0,0,SProj.getFValue());
			return;
			}
			if(SeTrans.isPinBound(e.getX(), e.getY()))
			{
			eTranssliderActive = true;
			SeTrans.update(e.getX());
			eTrans=new V3(0,0,SeTrans.getFValue());
			return;
			}
			if(SfarP.isPinBound(e.getX(), e.getY()))
			{
				farPsliderActive = true;
				SfarP.update(e.getX());
				farP= SfarP.getFValue();
				return;
			}
			if(testNob.isPinBound(e.getX(),e.getY()))
			{
				testNob.update(e.getX(),e.getY());
			}
		}
		public void keyPressed(KeyEvent e) 
		{
			keys[e.getKeyCode()]=true;
		/*				
			 if(e.getKeyCode()== KeyEvent.VK_1)
			{
				camused = cam0;
				camused.direction.print();R.print(" ");camused.position.print();
				R.ln();
				repaint();
			}
			else if(e.getKeyCode()== KeyEvent.VK_2)
			{
				camused = cam1;
				camused.direction.print();R.print(" ");camused.position.print();
				R.ln();
			}
			else if(e.getKeyCode()== KeyEvent.VK_3)
			{
				camused = cam;
				camused.direction.print();R.print(" ");camused.position.print();
				R.ln();
			}			
			else if(e.getKeyCode() == KeyEvent.VK_A)
			{
				camused.thetaY +=0.01;
			}
			else if(e.getKeyCode() == KeyEvent.VK_D)
			{
				camused.thetaY -=0.01;
			}
			else if(e.getKeyCode() == KeyEvent.VK_H)
			{
				randomArray[cubeIndex][1]+=cubeXSpeed;
			}
			else if(e.getKeyCode() == KeyEvent.VK_F)
			{
				randomArray[cubeIndex][1]-=cubeXSpeed;
			}
			else if(e.getKeyCode() == KeyEvent.VK_E)
			{
				
			}
			else if(e.getKeyCode() == KeyEvent.VK_Q)
			{
				
			}
			else if(e.getKeyCode() == KeyEvent.VK_T)
			{
				randomArray[cubeIndex][2]-=cubeYSpeed;
			}
			else if(e.getKeyCode() == KeyEvent.VK_G)
			{
				randomArray[cubeIndex][2]+=cubeYSpeed;
			}
			
			else if(e.getKeyCode() == KeyEvent.VK_R)
			{
				if (cubeIndex>0)
					cubeIndex--;
			}
			else if(e.getKeyCode() == KeyEvent.VK_F)
			{
				if (cubeIndex<randomArray.length-1)
					cubeIndex++;
			}
			*/
			if(e.getKeyCode() == KeyEvent.VK_SPACE)
			{
				space = true;
			}
			
			
		}
		public void keyReleased(KeyEvent e) 
		{
			keys[e.getKeyCode()]=false;
		}
		public void keyTyped(KeyEvent e) 
		{}
	}
	public static class quadFiller extends Thread
	{
		private VolatileImage CANVAS;
		private BufferedImage sprite;
		private V2 FromXY;
		private V2 ToXY;
		private V2 StepHor;
		private V2 StepVer;
		private V2 vVer;
		private V2 vHor;
		private float sxy;
		private float syx;
		private V2 origin;
		
		public quadFiller(BufferedImage sprite, VolatileImage C,V2 o, V2 FromXY, V2 ToXY,V2 vHor,V2 vVer,float sxy,float syx,V2 StepHor,V2 StepVer)
		{
			this.CANVAS = C ;
			this.sprite = sprite;
			this.origin = o;
			this.FromXY = FromXY;
			this.ToXY = ToXY;
			this.vVer = vVer;
			this.vHor = vHor;
			this.sxy = sxy;
			this.syx = syx;
			this.StepHor = StepHor;
			this.StepVer = StepVer;
		}
		
		public void run()
		{
			int spriteWidth =sprite.getWidth() ;
			int spriteHeight =sprite.getHeight() ;
			for(float x = FromXY.x,xy =sxy;
					R.in(x,FromXY.x,ToXY.x);
					x+=StepHor.x,xy+=StepHor.y)
			{		
					for(float y = FromXY.y,yx = syx;
						R.in(y,FromXY.y,ToXY.y);
						y+=StepVer.y,yx+=StepVer.x)
					{
							float x2 = (x+yx);
							float y2 = (y+xy);
							
							int r = (int)((y/(vVer.y)) *(spriteHeight-1));
							int c = (int)((x/(vHor.x))*(spriteWidth-1));
							
							c = c<0?c*-1:c;
							r = r<0?r*-1:r;
							
							c = c>=spriteWidth ? spriteWidth-1: c;
							r = r>=spriteHeight ? spriteHeight-1: r;
							try
							{
								int X=(int)(x2+origin.x),Y =(int)(y2+origin.y);
								if (R.in(X,0,599) && R.in(Y,0,599))
								{
									int color =  sprite.getRGB(r,c);
									Graphics G = CANVAS.getGraphics();									
									G.setColor(new Color(color));
									G.fillRect(X, Y, 2, 1);
												
									//CANVAS.setRGB(X,Y,color);
									//CANVAS.setRGB(X+1,Y,color);
								}
								else break;
							}
							catch(ArrayIndexOutOfBoundsException e){
								R.println(e.toString());
							}							
					}
							
			}
			
		}
		
	}
	public static class Entity
	{
		public V2 hor =new V2(0f,0f);
		public V2 ver =new V2(0f,0f); 
		public V2 origin=new V2(0f,0f);
		public BufferedImage sprite;
		public void setPositionScale(V2 p,V2 p0,V2 p1,V2 p2)
		{
			hor = V2.sub(p0,p);
			ver = V2.sub(p2,p);
			origin = p;

		}
		public void Init(V2 hor,V2 ver,V2 origin,BufferedImage sprite)
		{
			this.hor = hor;
			this.ver = ver;
			this.origin = origin;
			this.sprite = sprite;
		}
		
	}
	public static class Base
	{
	public V2 x;
	public V2 y;
	public V2 origin;
	
	Base(V2 origin ,V2 x,V2 y){this.x = x;this.y=y;this.origin = origin;}
	}
	public void ToPixelCoordinates(V2 B,V2 B0,V2 B1,V2 B2)
	{
		B.x = Math.round(B.x*panel.getWidth());
		B.y = Math.round(B.y*panel.getHeight());
		B0.x =Math.round(B0.x*panel.getWidth());
		B0.y =Math.round(B0.y*panel.getHeight());
		B1.x = Math.round(B1.x*panel.getWidth());
		B1.y = Math.round(B1.y*panel.getHeight());
		B2.x = Math.round(B2.x*panel.getWidth());
		B2.y = Math.round(B2.y*panel.getHeight());
	}
	public void UpdateCanvas(int width, int height)
	{
				
				Graphics2D g  = CANVAS.createGraphics();
				g.clearRect(0,0,width,height);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
				int fontSize = 15;
				Font font = new Font("Comic Sans MS", Font.BOLD, fontSize);
				g.setFont(font);
				long startTime = System.nanoTime();
				
				
		for(int i = 0 ; i< entities.length;i++)
		{		
				V2 b=entities[i].origin;
				V2 b0=V2.add(entities[i].origin,entities[i].hor);
				
				V2 b2=V2.add(entities[i].origin,entities[i].ver);
				
				V2 b1=new V2(b0.x,b2.y);
				
				V2 vHor = V2.sub(b0,b);
				V2 vVer = V2.sub(b2,b);
				
				if(vHor.x==0)
						vHor = new V2(0.01f,vHor.y);
				if(vVer.y == 0)
						vVer = new V2(vVer.x,0.01f);
			
				float horLen  =  vHor.len();
				float vertLen =  vVer.len();
				int spriteHeight = sprite.getHeight();
				int spriteWidth  = sprite.getWidth();
				
				float stepXHor = vHor.x/horLen;
				float stepYHor = vHor.y/horLen;
				float stepXVert= vVer.x/vertLen;
				float stepYVert= vVer.y/vertLen;
				
				V2 StepHor = new V2(stepXHor,stepYHor);
				V2 StepVer = new V2(stepXVert,stepYVert);
				
				Base a =  new Base(b,vHor,vVer);
				
				V2 s = new V2(0,0);//start pos
				float sxy = 0;
				float syx =0;
				float stepxy = stepYHor;
				float stepyx = stepXVert;
				V2 step = new V2(stepXHor,stepYVert);
				V2 f = new V2(vHor.x,vVer.y/8f);//finish pos
				quadFiller q0 = new quadFiller(sprite, CANVAS, a.origin, s, f,vHor,vVer,sxy,syx,StepHor, StepVer);
				//renderQuad(s,sxy,syx,f,step,stepxy,stepyx,a.origin,CANVAS,sprite,vVer,vHor);
				q0.start();
				
				s=new V2(0f,vVer.y/8f);
				sxy = 0f;//hor.y
				syx = vVer.x/8f; //ver.x
				f=new V2 (vHor.x,vVer.y/4f);
				quadFiller q1 = new quadFiller(sprite, CANVAS, a.origin, s, f,vHor,vVer,sxy,syx,StepHor, StepVer);
				//renderQuad(s,sxy,syx,f,step,stepxy,stepyx,a.origin,CANVAS,sprite,vVer,vHor);
				q1.start();
				
				s=new V2(0,vVer.y/4f);
				sxy = 0;
				syx = vVer.x/4f;
				f=new V2 (vHor.x,3*vVer.y/8f);
				quadFiller q2 = new quadFiller(sprite, CANVAS, a.origin, s, f,vHor,vVer,sxy,syx,StepHor, StepVer);
				q2.start();
				//renderQuad(s,sxy,syx,f,step,stepxy,stepyx,a.origin,CANVAS,sprite,vVer,vHor);
				
				s=new V2(0,3f*vVer.y/8f);
				sxy = 0;
				syx = 3f*vVer.x/8f;
				f=new V2 (vHor.x,vVer.y/2f);
				quadFiller q3 = new quadFiller(sprite, CANVAS, a.origin, s, f,vHor,vVer,sxy,syx,StepHor, StepVer);
				q3.start();
				
				s=new V2(0,vVer.y/2f);
				sxy = 0;
				syx = vVer.x/2f;
				f=new V2 (vHor.x,5f*vVer.y/8f);
				quadFiller q4 = new quadFiller(sprite, CANVAS, a.origin, s, f,vHor,vVer,sxy,syx,StepHor, StepVer);
				//renderQuad(s,sxy,syx,f,step,stepxy,stepyx,a.origin,CANVAS,sprite,vVer,vHor);
				q4.start();
				
				s=new V2(0f,5f*vVer.y/8f);
				sxy = 0f;//hor.y
				syx = 5f*vVer.x/8f; //ver.x
				f=new V2 (vHor.x,3f*vVer.y/4f);
				quadFiller q5 = new quadFiller(sprite, CANVAS, a.origin, s, f,vHor,vVer,sxy,syx,StepHor, StepVer);
				//renderQuad(s,sxy,syx,f,step,stepxy,stepyx,a.origin,CANVAS,sprite,vVer,vHor);
				q5.start();
				
				s=new V2(0,3f*vVer.y/4f);
				sxy = 0;
				syx = 3*vVer.x/4f;
				f=new V2 (vHor.x,7f*vVer.y/8f);
				quadFiller q6 = new quadFiller(sprite, CANVAS, a.origin, s, f,vHor,vVer,sxy,syx,StepHor, StepVer);
				q6.start();
				//renderQuad(s,sxy,syx,f,step,stepxy,stepyx,a.origin,CANVAS,sprite,vVer,vHor);
				
				s=new V2(0,7f*vVer.y/8f);
				sxy = 0;
				syx = 7f*vVer.x/8f;
				f=new V2 (vHor.x,vVer.y);
				quadFiller q7 = new quadFiller(sprite, CANVAS, a.origin, s, f,vHor,vVer,sxy,syx,StepHor, StepVer);
				q7.start();
				//renderQuad(s,sxy,syx,f,step,stepxy,stepyx,a.origin,CANVAS,sprite,vVer,vHor);
				try
				{
					q0.join();
					q1.join();
					q2.join();
					q3.join();
					q4.join();
					q5.join();
					q6.join();
					q7.join();
				}
				catch(InterruptedException e){R.println(e.toString());}
		}
				long endTime = System.nanoTime();
				long duration = (endTime - startTime);
				g.drawString(""+1000*((600f*600f)/duration),500,500);
	}
	public void renderQuad(V2 s,float sxy,float syx,V2 f,V2 step,float stepxy,float stepyx,V2 origin,BufferedImage CANVAS,BufferedImage sprite,V2 vVer,V2 vHor)
	{
		int spriteHeight =sprite.getHeight();
		int spriteWidth  =sprite.getWidth();
		
		for(float x =s.x,xy =sxy;
					R.in(x,s.x,f.x);
					x+=step.x,xy+=stepxy)
				{		
					for(float y = s.y,yx = syx;
						R.in(y,s.y,f.y);
						y+=step.y,yx+=stepyx)
					{
							float x2 = (x+yx);
							float y2 = (y+xy);
							
							int r = (int)((y/(vVer.y)) *(spriteHeight-1));
							int c = (int)((x/(vHor.x))*(spriteWidth-1));
							
							c = c<0?c*-1:c;
							r = r<0?r*-1:r;
							
							c = c>=spriteWidth ? spriteWidth-1: c;
							r = r>=spriteHeight ? spriteHeight-1: r;
							//System.out.println(x2/horLen+","+y2/vertLen);
							//System.out.println(c+","+r);
							//System.out.println(horLen+","+vertLen);
							try
							{
								int X=(int)(x2+origin.x),Y =(int)(y2+origin.y);
								if (R.in(X,0,598) && R.in(Y,0,599))
								{
									int color =  sprite.getRGB(r,c);									
									CANVAS.setRGB(X,Y,color);
									CANVAS.setRGB(X+1,Y,color);
								}
								else break;
							}
							catch(ArrayIndexOutOfBoundsException e){
								System.out.println(e.toString());
							}							
					}
							
				}
		
		
	}
	public void updateEntites(V2 p,V2 p0,V2 p1,V2 p2)
	{
		for(int i = 0; i< entities.length;i++)
		{
			entities[i].setPositionScale(p,p0,p1,p2);
		}
	}
	
	public static void initAndClearRect(){
		if(G==null)
			G = sprite.getGraphics();	
		G.clearRect(0,0,1680,1050);
	}
	public static V3 getReflected(V3 b0,V3 b1,V3 i)
	{
		V3 base0 = b0.clone();
		V3 base1 = b1.clone();
		V3 incoming = i.clone();
		float b0Length =base0.normalize();
		float b1Length =base1.normalize();
		float incomingLength =incoming.normalize();
		float LengthOnBase0 = base0.dot(incoming);
		float LengthOnBase1 = base1.dot(incoming);
		base0.sMul(LengthOnBase0);
		base1.sMul(-LengthOnBase1);
		V3 res = V3.add(base0, base1);
		res.sMul(incomingLength);
		return res;
	}
	public static void initCubes()
	{
		V3 cPos = new V3(0.2f,0.2f,2f);
		cubes[0] = new Cube(new V3(cPos.x,cPos.y,cPos.z),2,2,2);
		
		cubes[1]= new Cube(new V3(3*cPos.x,10,cPos.z),2,2,2);
		
		cubes[2] = new Cube(new V3(cPos.x,cPos.y,3*cPos.z),1,1,2);
		
		cubes[3]= new Cube(new V3(3*cPos.x,10,5*cPos.z),3,3,2);
		
		for (int i = 4; i <= 100; i++) 
		{
			if(cubes[i]==null)
			{
				float size =randomArray[i][0];
				float x =randomArray[i][1];
				float y =randomArray[i][2];
				float z =randomArray[i][3];
				cubes[i] = new Cube(new V3(cPos.x+x+camused.position.x,cPos.y+y+camused.position.y,cPos.z+z+camused.position.z+i),x*x,y*y,z*size);
			}
		}
		
	}
	public static V3 [] GeneratePoints(float z,float d)
	{
			V3 [] points  = new V3[64];
			int n  =  points.length;
			V3 o = new V3(0,0,z);
			float t = (float)(Math.PI*2)/n;
			float th =0;
			for(int i = 0; i<n;i++)
			{
				points[i] = o.clone();
				points[i].x =(float)Math.cos(th)*d;
				points[i].y =(float)Math.sin(th)*d;
				th+=t;
			}
			return points;
	}
	public static void draw3dTriangles(V3 []points)
	{
		int n  = points.length;

		for(int i = 0; i<n-1;i++)
		{
			if (points[i]==null||points[0]==null)
				break;

			draw3dBeam(points[i],V3.sub(points[i+1],points[i]),Color.black);
			if (i+2<n && points[i+2]!=null)
				draw3dBeam(points[0],V3.sub(points[i+2],points[0]),Color.blue); 
			else
				break;
		}
	}
	public static V3[] generateSpherePoints(V3 center,float r)
	{
		V3 [] points = new V3[16384];
		int counter =0;
		for (float yaw = 0;yaw<=Math.PI;yaw+=0.02)
		{//101//cos(yaw),sin(yaw);
			for (float pitch = 0;pitch<=Math.PI;pitch+=0.02)	
			{//011
				for (float roll = 0;roll<=Math.PI;roll+=0.02)	
				{//110
					points[counter++] = 
					new V3((float)-Math.sin(yaw)-(float)Math.sin(roll),(float)-Math.sin(pitch)+(float)Math.cos(roll),(float)Math.cos(yaw)+(float)Math.cos(pitch));
					if (counter==points.length)
						return points;
				}
				
			}	
		}
		return points;
	}
	public static void draw3dScene()
	{
		//initAndClearRect();
		/*G.setColor(Color.blue);
		float r = 0.4f;
		Ellipse2D boundary =new Ellipse2D.Double(1680/2-1680*r,1050/2f-1050*r,2*1680*r,2*1050*r);
		
		((Graphics2D)G).draw(boundary);
		
		G.setColor(Color.red);
		Ellipse2D eyeE =new Ellipse2D.Double(1680*cam.position.x,1050*cam.position.y,20,20);
		((Graphics2D)G).fill(eyeE);
		*/
		CubeQsort(cubes,0,100);
		
		//cubes[0].rotate(cubeAngle++);
			V3 camPos = new V3(0,0,0);
//			V3 camPos = camused.position.clone();
//			camPos.sMul(-1);
//			camPos.add(camused.position);
			Cube camCube = new Cube(camPos,1,1,1);
			
		Color [] colors = new Color[]{Color.blue,Color.green,Color.cyan};
		//blue 2->3
		//		camused.position.print();ln();
		for (int i = 100;i>=0;i--)
		{
			int j = (i-100)*-1;
			//if(cubes[j]!=null&&cubes[j+1]!=null)
				//draw3dBeam(cubes[j].center,V3.sub(cubes[j+1].center,cubes[j].center),colors[j%3]);
			currentCubeIteration = i;
			if(cubes[i]!=null)
			{
				Cube cube = cubes[i].clone();
				if(i == cubeIndex)
				{
					G.setColor(Color.red);
					cube.draw(camused);
				}
				else
				{
					cube.translate(camused.position);
					draw3dCube(cube,Color.black);
				}
			}
		}
		if(bullet!=null)	
			draw3dCube(bullet,Color.blue);
		
		draw3dCube(camCube.clone(),Color.black);
		
//		V3 A= new V3(100,200,0),B = new V3(100,300,0),
//		C=new V3(100,200,0),D=new V3(300,-100,0),
//		E=new V3(400,300,0);
//		drawVector(A.toV2(),B.toV2(),Color.black);
//		drawVector(C.toV2(),D.toV2(),Color.black);
//		
//		
//		
//		
//		V3 point0 = R.IntersectionPoint(A,B,E,F);//start end start end
//		V3 point1 = R.IntersectionPoint(C,D,E,F);//start end start end
//		V3 X = getReflected(point0,D,F);
//		if (point0.x!=0 &&point0.y!=0)
//		{
//			drawVector(E.toV2(),V3.sub(point0, E).toV2(),Color.black);
//			drawVector(point0.toV2(),X.toV2(),Color.blue);
//
//		}
//		else if (point1.x!=0 &&point1.y!=0)
//		{
//			V3 Y = getReflected(D,B,F);
//			drawVector(E.toV2(),V3.sub(point1, E).toV2(),Color.black);
//			
//			V3 point2 = R.IntersectionPoint(A,B,point1,Y);//start end start end
//			drawVector(point1.toV2(),V3.sub(point2, point1).toV2(),Color.blue);
//			drawCircle(point2.toV2(), 4, Color.red);			
//			drawVector(E.toV2(),V3.sub(point1, E).toV2(),Color.black);
//			Y = getReflected(point2,D,V3.sub(point2, point1));
//			drawVector(point2.toV2(), Y.toV2(),Color.red);
//		}
//		else
//		{	
//			drawVector(E.toV2(),F.toV2(),Color.black);
//		}
//		drawCircle(point0.toV2(), 4, Color.blue);
//		drawCircle(point1.toV2(), 4, Color.blue);	
		
//		V3 p = new V3(-40,0,-40);
//
//		V3 p1 = new V3(80,0,0);
//		V3 p2 = new V3(0,0,80);
//		
//		Color gridColor = new Color(34443);
//		float dd =50;
//		V3 p1Interval = V3.sMul(1/dd, p1);
//		V3 p2Interval = V3.sMul(1/dd, p2);
//		V3 tpX = p.clone();
//		V3 tpY = p.clone();
//		
//		boolean inX=true;
//		boolean inY=true;
//		boolean inZ=true;
//		do
//		{
//			draw3dBeam(tpX,p2, gridColor);
//			tpX.add(p1Interval);  
//			
//			inX=R.in(tpX.x, p.x,p.x+ p1.x);
//			inY=R.in(tpX.y, p.y,p.y+ p1.y);
//			inZ=R.in(tpX.z, p.z,p.z+ p1.z);
//			
//		}while(inX||inY||inZ);
//		
//		do
//		{
//			draw3dBeam(tpY,p1, gridColor);
//			tpY.add(p2Interval);
//			inX=R.in(tpY.x, p.x,p.x+ p2.x);
//			inY=R.in(tpY.y, p.y,p.y+ p2.y);
//			inZ=R.in(tpY.z, p.z,p.z+ p2.z);
//			//System.out.println(inX+","+inY+","+inZ);
//		}while(inX||inY||inZ);
//		draw3dBeam(p,new V3(0,0,1), Color.blue);
//		draw3dBeam(p,new V3(0,1,0), Color.red);
//		draw3dBeam(p,new V3(1,0,0), Color.green);
		
		G.setColor(Color.black);
		
		G.fillRect(50, 30, 20, 20);
		G.fillRect(50, 51, 20, 20);
		G.fillRect(50, 72, 20, 20);
		
		drawString(m[0],Color.white,new V2(50,50),18);
		drawString(m[1],Color.white,new V2(50,70),18);
		drawString(m[2],Color.white,new V2(50,90),18);
		CameraInfo();
	}
	public static void CameraInfo()
	{
		drawString("yaw:"+camused.thetaY,Color.black,new V2(5,10),12);
		drawString("pitch:"+camused.thetaP,Color.black,new V2(5,23),12);
		drawString("roll"+camused.thetaR,Color.black,new V2(5,36),12);
	}
	public static void drawCameras()
	{
		
		if(camused!=cam0)
		{
			V3 cam0Pos = cam0.position.clone();
			cam0Pos.sMul(-1);
			cam0Pos.add(camused.position);
			draw3dCube(new Cube(cam0Pos,1,1,1),new Color(0.3f,0.5f,1f));
		}
		if(camused!=cam1)
		{	V3 cam1Pos = cam1.position.clone();
			cam1Pos.sMul(-1);
			cam1Pos.add(camused.position);
			draw3dCube(new Cube(cam1Pos,1,1,1),new Color(255,100,255));
		}
		if(camused!=cam)
		{V3 camPos = cam.position.clone();
			camPos.sMul(-1);
			camPos.add(camused.position);
			draw3dCube(new Cube(camPos,1,1,1),new Color(999421));
		}
		
		
	}
	public static void drawVector(V2 start,V2 direction,Color c) // s-d;
	{//in pixel space
		// if(c!=null)
		// 	G.setColor(c);
		G.drawLine((int)start.x,(int)start.y,(int)start.x +(int)direction.x,(int)start.y+(int)direction.y);
		//drawLine(start,direction);
	}
	public static V2 convertToCentric(V2 v)
	{
		return new V2(0.5f+v.x/width,0.5f+-v.y/height);
	}
	public static void drawCircle(V2 P,int rad,Color c)
	{
		if (c!=null)
			G.setColor(c);
		G.drawOval((int)P.x-rad, (int)P.y-rad, (int)rad*2, (int)rad*2);
	}
	
	public static void drawSlider(V2 start,V2 end,float sliderT,int height)
	{
		//the sliderT is the value to lerp between start and end
		drawVector(start, V2.sub(end, start),Color.black);
		drawVector(V2.add(start,V2.sMul(sliderT,V2.sub(end, start))),new V2(0,height),Color.black);
	}
	public static void drawLine(V2 start,V2 direction)
	{
		
		//this is done in centric coordinates 
		//the center of the screen is the 0,0 point
		//topright = 1,1
		//bottomleft = -1,-1
		float dLen = direction.len();
		//move by 1 and divide by two 
		//the y coordinate is inversed for convinience
		V2 s = new V2((start.x+1f)/2f,((-start.y+1f)/2f));

		V2 d = new V2(direction.x/2f,direction.y/-2f);
		V2 sd = V2.add(s,d);
		float sy = -1;
		float sx = -1;

		float sdy = -1;
		float sdx = -1;

		float tx0 = -(s.x/d.x);
		float tx1 = (1-s.x)/d.x;
		float ty0 = -(s.y/d.y);
		float ty1 = (1-s.y)/d.y;
		char bit = 0x00;
		if(R.in(tx0,0,1))
			bit|=1;
		if(R.in(tx1,0,1))
			bit|=2;
		if(R.in(ty0,0,1))
			bit|=4;
		if(R.in(ty1,0,1))
			bit|=8;
		switch(bit)
		{
			case 0:
				sx =s.x;
				sy = s.y;
				sdx = sd.x;
				sdy = sd.y;
				break;
			case 1://0001
				if(R.in(sd.x,0,1)){
					sx =0;
					sy = s.y+ tx0*d.y;
					sdx = sd.x;
					sdy = sd.y;
				}
				else
				{
					sdx =0;
					sdy = s.y+ tx0*d.y;
					sx = s.x;
					sy = s.y;
				}
				break;
			case 2://0010

				if(R.in(sd.x,0,1))
				{
					sx =1;
					sy = s.y+ tx1*d.y;
					sdx = sd.x;
					sdy = sd.y;
				}
				else
				{
					sdx =1;
					sdy = s.y+ tx1*d.y;
					sx = s.x;
					sy = s.y;
				}
				break;
			case 3://0011
				sx = 0;
				sy = s.y+ tx0*d.y;
				sdx =1;
				sdy = s.y+tx1*d.y;
				break;
			case 4://0100
				if(R.in(sd.y,0,1))
				{
					sy = 0;
					sx = s.x + ty0*d.x;
					sdx = sd.x;
					sdy = sd.y;
				}
				else 
				{
					sdy = 0;
					sdx = s.x + ty0*d.x;
					sx = s.x;
					sy = s.y;
				}
					break;			
			case 5://0101
				sx = 0;
				sy = s.y+ tx0*d.y;
				sdy =0;
				sdx = s.x+ty0*d.x;
				break;
			case 6: // 0110
				sx = 1;
				sy = s.y+ tx1*d.y;
				sdy =0;
				sdx = s.x+ty0*d.x;
				break;
			case 8://1000
				if(R.in(sd.y,0f,1f))
				{
					sy = 1;
					sx = s.x+ ty1*d.x;
					sdx = sd.x;
					sdy = sd.y;
				}
				else
				{
					sdy = 1;
					sdx = s.x+ ty1*d.x;
					sx = s.x;
					sy = s.y;
				}
				break;				
			case 9://1001
				sx = 0;
				sy = s.y+ tx0*d.y;
				sdy =1;
				sdx = s.x+ty1*d.x;
				break;
			case 10://1010
				sx = 1;
				sy = s.y+ tx1*d.y;
				sdy =1;
				sdx = s.x+ty1*d.x;
				break;	
			case 12://1100
				sy = 0;
				sx = s.x+ ty0*d.x;
				sdy =1;
				sdx = s.x+ty1*d.x;
				break;	
		}
		s.x=sx;
		s.y = sy;
		sd.x=sdx;
		sd.y = sdy;
		boolean sdInX = R.in(sd.x,0f,1f);
		boolean sdInY = R.in(sd.y,0f,1f);
		boolean sInX  =	R.in(s.x,0f,1f);
		boolean sInY  =	R.in(s.y,0f,1f);
		if((sInX && sInY) && (!sdInX || !sdInY))
		{
			V2 t = s;
			s = sd;
			sd = t;
		}
		int startX = (int)V2.hadProduct(s,wh).x;
		int startY = (int)V2.hadProduct(s,wh).y;

		int endX = (int)V2.hadProduct(wh,sd).x;
		int endY = (int)V2.hadProduct(wh,sd).y;

		float x = startX;
		float y = startY;
		int xDir= startX<endX?1:startX>endX? -1:0;
		int yDir= startY<endY?1:startY>endY? -1:0;
		
		float xStep =Math.abs(startX-endX)/(new V2(endX-startX,endY-startY).len());
		float yStep =Math.abs(startY-endY)/(new V2(endX-startX,endY-startY).len());

		if(startX == endX)
			while(R.in(y,startY,endY ))
			{	
				if(R.in(y,0,wh.y))
					y+=yStep*yDir;
				else
					break;
				int index = width*(int)y+(int)x;
				if (index>=0&&index<imagePixelData.length)
					imagePixelData[index] = 0x00;
				else 
					break;
			}
		else if(startY == endY)
			while(R.in(x,startX,endX))
			{
				if(R.in(x,0,wh.x))
					x+=xStep*xDir;
				else break;
				int index = width*(int)y+(int)x;
				if (R.in(index,0,imagePixelData.length-1))
					imagePixelData[index] = 0x00;
				else
					break;
			}
		else
			while(R.in(x,Math.max(startX,0),Math.min(endX,wh.x))
				&& R.in(y,Math.max(startY,0),Math.min(endY,wh.y)))
			{
				int index = width*(int)y+(int)x;
				if (R.in(index,0,imagePixelData.length-1))
					imagePixelData[index] = 0x00;
				if( R.in(y,0,wh.y))
					y+=yStep*yDir;
				if(R.in(x,0,wh.x))
					x+=xStep*xDir;
			}
	}
	public static void draw3dBeam (V3 start,V3 direction,Color c)
	{
		Quaternion rot = getRotationQuaternion(camused);
		
		V3 st =start.clone();
		V3 en = V3.add(start, direction);
		V3 CamSpaceAndeTrans = V3.add(camused.position,eTrans);

		st.add(camused.position);
		en.add(camused.position);
		
		st.rotate(rot);
		en.rotate(rot);
			
		V2 s = st.project2(fov,eProj,nearP,farP);
		V2 e = en.project2(fov,eProj,nearP,farP);

		drawLine(s,V2.sub(e,s));
		}
	  
	public static V3 GetPitchAxis(V3 YawDirection)
	{
		V3 dir = YawDirection.clone();
		
		float isX = V3.in(dir.x, -epsilon, epsilon)?0f:dir.x>0f?1f:-1;//zero
		float isY = V3.in(dir.y, -epsilon, epsilon)?0f:dir.y>0f?1f:-1;//zero
		float isZ = V3.in(dir.z, -epsilon, epsilon)?0f:dir.z>0f?-1f:1;//zero
		
		
		if(isX!=0f&&isZ!=0)
			return new V3(isZ,0,isX);
		else if(isX!=0f&&isY!=0)
			return new V3(isY,isX,0);
		else if(isZ!=0f&&isY!=0)
			return new V3(0,isZ,isY);
		if(isX!=0f)
			return new V3(0,0,isX);
		else if(isZ!=0)
			return new V3(isZ,0,0);
		else return new  V3(0,0,0);
		
	}
	public static void draw3dCube(Cube c0,Color C)
	{
		
		Quaternion rot = getRotationQuaternion(camused);

		Cube c = new Cube(c0);
		
		c.rotate(rot);

		c.translate(eTrans);			
		V2 cntr = c.center.project2(fov, eProj, nearP, farP); 
		V2 q = c.q.project2(fov,eProj,nearP,farP);
		V2 w = c.w.project2(fov,eProj,nearP,farP);
		V2 a = c.a.project2(fov,eProj,nearP,farP);
		V2 s = c.s.project2(fov,eProj,nearP,farP);
		
		V2 e = c.e.project2(fov,eProj,nearP,farP);
		V2 r = c.r.project2(fov,eProj,nearP,farP);
		V2 d = c.d.project2(fov,eProj,nearP,farP);
		V2 f = c.f.project2(fov,eProj,nearP,farP);
		
		if(q.isZero()||w.isZero()||a.isZero()||s.isZero()||e.isZero()||r.isZero()||f.isZero()||d.isZero())
			return;
			
		
		float moveBy = 0.5f;
		
		cntr.sAdd(moveBy);
		q.sAdd(moveBy);
		w.sAdd(moveBy);
		a.sAdd(moveBy);
		s.sAdd(moveBy);
		
		e.sAdd(moveBy);
		r.sAdd(moveBy);
		d.sAdd(moveBy);
		f.sAdd(moveBy);
		
		
		cntr = V2.hadProduct(wh, cntr);
		q = V2.hadProduct(wh, q);
		w = V2.hadProduct(wh, w);
		s= V2.hadProduct(wh, s);
		a = V2.hadProduct(wh, a);
		
		e = V2.hadProduct(wh, e);
		r = V2.hadProduct(wh, r);
		d= V2.hadProduct(wh, d);
		f = V2.hadProduct(wh, f);
		
	
		G.setColor(C);
		int Cr = 2;
		//drawCircle(cntr,Cr,C);
		drawRect(q,w,s,a);
		drawRect(e,r,f,d);
		 
		drawVector(q,V2.sub(e, q),C);
		drawVector(w,V2.sub(r, w),C);
		drawVector(s,V2.sub(f, s),C);
		drawVector(a,V2.sub(d, a),C);
		
		
		//(1680/2,1050/2f,Math.sqrt(1680*0.3+1050*0.3));
	}
	public static void drawRect(V2 q,V2 w, V2 s, V2 a)
	{//The color will be set before calling this function 
		float t = currentCubeIteration/WorkingCubeCount;
		int colorConstant = (int)(255f*(1f-t));
		G.setColor(new Color(colorConstant,colorConstant,colorConstant));
		G.fillPolygon(new Polygon(new int[]{(int) q.x,(int) w.x,(int) s.x,(int) a.x},new int[]{(int) q.y,(int) w.y,(int) s.y,(int) a.y},4));
		//G.setColor(Color.black);
		G.drawPolygon(new Polygon(new int[]{(int) q.x,(int) w.x,(int) s.x,(int) a.x},new int[]{(int) q.y,(int) w.y,(int) s.y,(int) a.y},4));
		
	}
	public static Quaternion getRotationQuaternion(Camera cam)
	{
		Quaternion rotP = new Quaternion(cam.thetaP,1,0,0);
		rotP.convert();
		Quaternion rotR = new Quaternion(cam.thetaR,0,0,1);
		rotR.convert();
		Quaternion rotY = new Quaternion(cam.thetaY,0,1,0);
		rotY.convert();

		Quaternion rot = rotP.mul(rotY);
		rot = rot.mul(rotR);
		return rot;
	}
	
	
	public static void draw3dRect(V3 q,V3 w, V3 s, V3 a)
	{//The color will be set before calling this function 
		Quaternion rot = getRotationQuaternion(camused);
		V3 CamSpaceAndeTrans = V3.add(camused.position,eTrans);
		q.add(CamSpaceAndeTrans);
		w.add(CamSpaceAndeTrans);
		s.add(CamSpaceAndeTrans);
		a.add(CamSpaceAndeTrans);
		q.rotate(rot);
		s.rotate(rot);
		a.rotate(rot);
		w.rotate(rot);
		
		V2 q2 = q.project2(fov,eProj,nearP,farP);
		V2 w2 = w.project2(fov,eProj,nearP,farP);
		V2 a2 = a.project2(fov,eProj,nearP,farP);
		V2 s2 = s.project2(fov,eProj,nearP,farP);
		if(q2.isZero()||w2.isZero()||a2.isZero()||s2.isZero())
			return;

		 drawLine(q2,V2.sub(w2, q2));
		 drawLine(w2,V2.sub(s2, w2));
		 drawLine(s2,V2.sub(a2,s2));
		 drawLine(a2,V2.sub(q2,a2));
	}
	public static V3 ColorInterpolation(V3 colorA,V3 colorB,float t)
	{
		return V3.add(colorA,V3.sMul(t, V3.sub(colorB, colorA)));
	}
	public static float get(V2 posA,V2 posB,V2 posM)
	{
		V2 A = V2.sub(posB,posA);
		V2 B = V2.sub(posM,posA);
		return B.len()/A.len();
	}
	public static void drawString(String text,Color c, V2 screenPos,int size)
	{
		if (text != null)
		{
			G.setColor(c);
			G.setFont(new Font("Tahoma",Font.BOLD,size));
			G.drawString(text,(int)screenPos.x,(int)screenPos.y);
		}
	}
	public static boolean FsliderActive = false;
	public static boolean ProjsliderActive = false;
	public static boolean eTranssliderActive = false;
	public static boolean farPsliderActive = true;
	static Slider Sfov=new Slider(20,0f,(float)Math.PI/10f,(float)Math.PI/2.1f,new V2(100,100),new V2(200,100));
	static Slider SProj=new Slider(20,0f,-0.2f,100f,new V2(220,100),new V2(320,100));
	static Slider SeTrans=new Slider(20,0f,-10f,100f,new V2(340,100),new V2(440,100));
	static Slider SfarP=new Slider(20,0f,0f,10000f,new V2(460,100),new V2(600,100));
	static Nob testNob = new Nob(new V2(100,200),40);	
	
	public static void qsort(float []a,int s,int f)
	{
		if(s>=f||s<0||f>=a.length)
			return;
		if(s+1==f)
		{
			if(a[s]>a[f])
				swap(a,s,f);
			return;
		}
		int q=partition(a,s, f);
		qsort(a,s,q-1);
		qsort(a,q+1,f);
	}
	public static void CubeQsort(Cube[]a,int s,int f)
	{
		if(s>=f||s<0||f>=a.length)
			return;
		int q=CubePartition(a,s, f);
		CubeQsort(a,s,q-1);
		CubeQsort(a,q+1,f);
	}
	
	public static int partition(float[]a,int start, int finish)
	{
		float t = a[start];
		int i=start;
		for (int j = finish; i<j;)
		{
			if (a[i]<t)
				i++;
			if(a[j]>t)
				j--;
			if(a[i]>a[j])
				swap(a,i,j);
			if (a[i]==a[j]&&j!=i)
				i++;
		}
		return i;
	}
	
	public static int CubePartition(Cube[]a,int start, int finish)
	{
		float t =V3.sub(a[start].center, V3.sMul(-1, camused.position)).len();
		int i=start;
		for (int j = finish; i<j;)
		{
			float iValue =V3.sub(a[i].center, V3.sMul(-1, camused.position)).len();
			float jValue =V3.sub(a[j].center, V3.sMul(-1, camused.position)).len();
			if (iValue<t)
				i++;
			if(jValue>t)
				j--;
			if(iValue>jValue)
				R.swap(a,i,j);
			if (iValue==jValue&&j!=i)
				i++;
		}
		cubesDistances[i] = t;
		return i;
	}	
	public static void swap(float []a,int i,int j)
	{
		float temp=a[j];
		a[j]=a[i];
		a[i]=temp;
	}
    public static void main(String[] args)
    {
		R.println("Render3d 1.0.190816");
		Render3d bb = new Render3d();
		while(bb.running)
		{
				InputHandler.Init(keys,camused);
				InputHandler.HandleInputs();

				try
				{
				Thread.sleep(33);
				}
				catch(InterruptedException e){}
			
			
			if(space)
			{ 
				new Thread(new Runnable(){

					@Override
					public void run() {
						
						V3 bulletPos=new V3(0,0,0);
						if(bullet!=null)return;
						bullet = new Cube(bulletPos,0.4f,0.4f,0.4f);
						V3 dir = camused.direction.clone();
						V3 up = camused.up.clone();
						Quaternion qt = new Quaternion(2.217f-camused.thetaP,dir.z,0,-dir.x);
						 qt.convert();
						up.rotate(qt);
						up.sMul(1f);
						// dir.sMul(1f);
						for(int i = 0 ; i<50;i++)
						{
							bullet.translate(up);
							//bullet.rotate(new Quaternion(2f,bulletPos,true));
							
							if(bullet.CubeTouches(cubes[0]))
							{
								cubeIndex= 0 ;
								break;
							}
							else if( bullet.CubeTouches(cubes[1]))
							{
								cubeIndex= 1;
								break;
							}
							else if(bullet.CubeTouches(cubes[2]))
							{
								cubeIndex= 2;
								break;
							}
							else if (bullet.CubeTouches(cubes[3]))
							{
								cubeIndex= 3;
								break;
							}
							try
							{	
								Thread.sleep(20);
							}
							catch(InterruptedException e){}
						}
						bullet=null;
						}
					}).start();
				
				space = false;
			}
			
			//draw3dScene();
			
			float t = 0 ;
			float th = 1f/height;
			RGBColor A = new RGBColor(0,255,0);
			RGBColor B = new RGBColor(255,0,0); 
			for (int y=0;y<height;y++)
			{
				for (int x=0;x<width;x++)
				{
					imagePixelData[y*width+x] = RGBColor.interpolate(A,B,t);
				}
				t+=th;
			}
			V3  a= new V3(0,0,0.25f);
			V3  b= new V3(0.5f,1f,0);
			V3  c= new V3(-0.5f,1f,0);
 			// drawTriangle(a.toV2(),b.toV2(),c.toV2());
 			// drawTriangle(new V2(-1f,-1f),new V2(0.2f ,0.5f),new V2(-.3f,.5f));
 			 //drawLine(new V2(.2f,.5f),new  V2(-0.5f ,0.f));
 			 //drawLine(new V2(-.3f,.5f),new  V2(0.3f ,-0.5f));

 			 // drawLine(new V2(.0f,.0f),new V2(0.3f ,-0.5f));

 			 // drawLine(new V2(.3f,-.5f),new  V2(0.3f ,.0f));

 			 // drawLine(new V2(.6f,-.5f),new V2(-0.6f ,0.5f));

 		// 	drawLine(new V2(-.5f,1f),new  V2(.0f ,-2.f));
			// drawLine(new V2(-.2f,1f),new  V2(.0f ,-2.f));
			// drawLine(new V2(-.3f,1f),new  V2(.0f ,-2.f));
			// drawLine(new V2(-.4f,1f),new  V2(.0f ,-2.f));
			// drawLine(new V2(-.1f,1f),new  V2(.0f ,-2.f));
			// drawLine(new V2(.0f ,1f), new V2(.0f ,-2.f));
			// drawLine(new V2(.1f ,1f), new V2(.0f ,-2.f));
			// drawLine(new V2(.2f ,1f), new V2(.0f ,-2.f));
			// drawLine(new V2(.3f ,1f), new V2(.0f ,-2.f));
			// drawLine(new V2(.4f ,1f), new V2(.0f ,-2.f));
			// drawLine(new V2(.5f ,1f), new V2(.0f ,-2.f));

			// drawLine(new V2(-1f,-.2f), new V2(2.f,0f));
			// drawLine(new V2(-1f,-.3f), new V2(2.f,0f));
			// drawLine(new V2(-1f,-.4f), new V2(2.f,0f));
			// drawLine(new V2(-1f,-.1f), new V2(2.f,0f));
			// drawLine(new V2(-1f,.0f ), new V2(2.f,0f));
			// drawLine(new V2(-1f,.1f ), new V2(2.f,0f));
			// drawLine(new V2(-1f,.2f ), new V2(2.f,0f));
			// drawLine(new V2(-1f,.3f ), new V2(2.f,0f));
			// drawLine(new V2(-1f,.4f ), new V2(2.f,0f));

			// drawLine(new V2(0.f,1.0f),new V2(.0f ,-2.0f));
			// drawLine(new V2(-1.3f ,1f),new V2(0.5f,-.5f));

			// drawLine(new V2(-1.f,-.2f),new V2(2.f ,1f));
			// drawLine(new V2(-1.f,-.3f),new V2(2.f ,1f));
			// drawLine(new V2(-1.f,-.4f),new V2(2.f ,1f));
			// drawLine(new V2(-1.f,-.5f),new V2(2.f ,1f));
			// drawLine(new V2(-1.f,-.6f),new V2(2.f ,1f));
			// drawLine(new V2(-1.f,-.7f),new V2(2.f ,1f));
			// drawLine(new V2(-1.f,-.8f),new V2(2.f ,1f));
			// drawLine(new V2(-1.f,-.9f),new V2(2.f ,1f));
			// drawLine(new V2(-1.f,-.9f),new V2(2.f ,1f));
			// drawLine(new V2(-1.f,-.9f),new V2(2.f ,1f));
			// drawLine(new V2(-1.f,-1f) ,new V2(2.f ,1f));
			// drawLine(new V2(-1.f,-1.1f),new V2(2.f ,1f));
			// drawLine(new V2(-1.f,-1.2f),new V2(2.f ,1f));
			// drawLine(new V2(-1.f,-1.3f),new V2(2.f ,1f));
			// drawLine(new V2(-1.f,-1.4f),new V2(2.f ,1f));
			// drawLine(new V2(-1.f,-1.5f),new V2(2.f ,1f));
			// drawLine(new V2(-1.f,-1.6f),new V2(2.f ,1f));
			// drawLine(new V2(-1.f,-1.7f),new V2(2.f ,1f));
			// drawLine(new V2(-1.f,-1.8f),new V2(2.f ,1f));
			// drawLine(new V2(-1.f,-1.9f),new V2(2.f ,1f));

			// drawLine(new V2(1.2f,-1.2f),new V2(-1f ,2.1f));
			// drawLine(new V2(1f,0),new V2(-1f,0f));
			//drawLine(new V2(0,1.2f),new V2(0f ,-2.5f));
			// drawLine(new V2(-1.f,0.5f),new V2(2f,1f));
			//drawLine(new V2(1f,0),new V2(-.3f ,-1.0f));

			// drawLine(new V2(1f,1f),new V2(-0.5f ,-.5f));
			drawLine(new V2(-1.1f,0f),new V2(0.5f ,1.2f));
			drawLine(new V2(-1.1f,0f),new V2(0.5f ,-1.2f));
			drawLine(new V2(1.1f,0f),new V2(-0.5f ,-1.2f));
			drawLine(new V2(1.1f,0f),new V2(-0.5f ,1.2f));
			drawLine(new V2(0f,1.1f),new V2(0f ,-2.2f));
			drawLine(new V2(-1.1f,0f),new V2(2.2f ,0f));
			// drawLine(new V2(-0.75f,0),new V2(0.5f,0.5f));
			//drawTriangle(new V2(0.8f,0.8f),new V2(1f,1f),new V2(1.2f,-0.5f));


			// for(int i = 0; i<100;i++)
			// {
			// 	V2 test = new V2(0.5f*(float)Math.random(),0.5f*(float)Math.random());		
			// 	drawTriangle(new V2(0,0),new V2(-test.x,-test.y),new V2(test.y,test.x));
				
			// }
			// draw3dRect(new V3(0,0,0),new V3(1,0,0),new V3(1,1,0),new V3(0,1,0));
			// draw3dRect(new V3(0,0,1),new V3(1,0,1),new V3(1,1,1),new V3(0,1,1));
			// draw3dGrid();

			// draw3dTriangles(new V3[]{a,b,c});			
 		// 	b = new V3(0.5f,1f,0.5f);
 		// 	c = new V3(-0.5f,1f,0.5f);
 		// 	draw3dTriangles(new V3[]{a,b,c});
 		// 	b = new V3(0.5f,1f,0.5f);
 		// 	c = new V3(0.5f,1f,0f);
 		// 	draw3dTriangles(new V3[]{a,b,c});
 		// 	b = new V3(-0.5f,1f,0.5f);
 		// 	c = new V3(-0.5f,1f,0f);
 		// 	draw3dTriangles(new V3[]{a,b,c});

			//drawCameras();
			 draw3dGrid();
			Sfov.draw();
			SProj.draw();
			SeTrans.draw();
			SfarP.draw();
			testNob.draw();
			drawString(Float.toString(Sfov.getFValue()),Color.blue,new V2(100,140),12);
			drawString(Float.toString(SProj.getFValue()),Color.blue,new V2(220,140),12);
			drawString(Float.toString(SeTrans.getFValue()),Color.blue,new V2(340,140),12);
			drawString(Float.toString(SfarP.getFValue()),Color.blue,new V2(460,140),12);
			drawString(Float.toString(testNob.vecAngle),Color.blue,new V2(100,200),12);
			
			//draw points of a cube by the getProjectPointFunctoin
			// camused.thetaR+=0.01;
			// drawCircle(getProjectedPoint(cubes[3].q.clone()),5,Color.blue);
			// drawCircle(getProjectedPoint(cubes[3].w.clone()),5,Color.blue);
			// drawCircle(getProjectedPoint(cubes[3].s.clone()),5,Color.blue);
			// drawCircle(getProjectedPoint(cubes[3].a.clone()),5,Color.blue);
			
			// drawCircle(getProjectedPoint(cubes[3].e.clone()),5,Color.green);
			// drawCircle(getProjectedPoint(cubes[3].r.clone()),5,Color.green);
			// drawCircle(getProjectedPoint(cubes[3].f.clone()),5,Color.green);
			// drawCircle(getProjectedPoint(cubes[3].d.clone()),5,Color.green);
			bb.repaint();
		}
    }
    public static void drawTriangle(V2 a,V2 b,V2 c)
    {
    		drawLine(a,V2.sub(b,a));
			drawLine(b,V2.sub(c,b));
			drawLine(c,V2.sub(a,c));
    }
    public static void draw3dGrid()
    {
    	//G.setColor(new Color(123,100,255));
		int itrI = 25;
		int itrJ = 25;
		for (int i = -25; i<itrI;i++)
		{
			for(int j = -25; j<itrJ;j++)
			{
			//G.setColor(new Color(5*i+125,5*i+125,5*i+125));
			draw3dRect(new V3(i,0,j),new V3(i,0,j+1),new V3(i+1,0,j+1),new V3(i+1,0,j));
			}
		}
    }
    
    public static V2 getProjectedPoint(V3 point)
    {// point is a copy of the point since this function doesn't copy it does act on the point object 
    	V2 ret = null;
    	Quaternion rot = getRotationQuaternion(camused);
		V3 cameraSpace =camused.position.clone();
		point.add(cameraSpace);
		point.rotate(rot);
    	point.add(eTrans);
    	ret = point.project2(fov,eProj,nearP,farP);
    	ret.add(new V2(0.5f,0.5f));
    	return V2.hadProduct(wh,ret);
    }
    public static Cube getDoubleClickedCube(int x, int y )
    {
    	for(Cube c : cubes)
    	{
    		if( c!=null)
    		{
    			V2 q=getProjectedPoint(c.q.clone());
    			V2 w=getProjectedPoint(c.w.clone());
    			V2 s=getProjectedPoint(c.s.clone());
    			V2 a=getProjectedPoint(c.a.clone());

    			V2 e=getProjectedPoint(c.e.clone());
    			V2 r=getProjectedPoint(c.r.clone());
    			V2 f=getProjectedPoint(c.f.clone());
    			V2 d=getProjectedPoint(c.d.clone());

    			int minx = (int) Math.min(Math.min(Math.min(s.x,a.x),Math.min(q.x, w.x)),Math.min(Math.min(e.x,r.x),Math.min(d.x, f.x)));
    			int miny = (int) Math.min(Math.min(Math.min(s.y,a.y),Math.min(q.y, w.y)),Math.min(Math.min(e.y,r.y),Math.min(d.y, f.y)));

    			int maxy = (int) Math.max(Math.max(Math.max(s.y,a.y),Math.max(q.y, w.y)),Math.max(Math.max(e.y,r.y),Math.max(d.y, f.y)));
    			int maxx = (int) Math.max(Math.max(Math.max(s.x,a.x),Math.max(q.x, w.x)),Math.max(Math.max(e.x,r.x),Math.max(d.x, f.x)));

				if(R.in(x,minx,maxx)&&R.in(y, miny, maxy))
				{
					return c;
				}
    		}
    	}
    	return null;	
    }	

}