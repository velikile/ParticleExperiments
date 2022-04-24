import java.awt.Color;

public class Nob {
public float radius;
public V2 pos;
public float vecAngle = 0;//radians

public Nob(V2 pos,float radius)
{	
	this.pos =pos ;
	this.radius =radius;
}
public boolean isPinBound(int x,int y){
	V2 click = new V2(x,y);
	return V2.sub(click, pos).len()<=radius;
}
public void update(int x,int y){
	V2 tmp  = new V2(pos.x-x,pos.y-y);
	
	vecAngle = (float)Math.atan(tmp.y/tmp.x);
	if (tmp.x<0)
		vecAngle+=Math.PI;
	if (tmp.x>0&&tmp.y<0)
		vecAngle+=2*Math.PI;
	
}
public void draw()
{
	V2 nobHead = new V2(0,radius).rotate((float) (vecAngle+Math.PI));
	Render3d.drawCircle(pos, (int)radius, Color.black);
	Render3d.drawVector(pos,nobHead, Color.red);
	V2 normal = new V2(nobHead.y,-nobHead.x);
	Render3d.drawVector(V2.add(pos,nobHead),normal, Color.blue);
	Render3d.drawVector(V2.add(pos,nobHead),normal.reverse(), Color.blue);
}
}
