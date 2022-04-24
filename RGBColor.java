public class RGBColor
{	
	public int red;
	public int green ;
	public int blue ;
	
	public RGBColor( int r, int g,int b)
	{
		red =r;
		green = g;
		blue = b;
	}
	public int getIntColor()
	{	
		return red<<16 | green<<8 | blue ;
	}
	public static int interpolate(RGBColor a ,RGBColor b ,float t)
	{
		V3 aColor = new V3(a.red, a.green,a.blue);
		V3 bColor = new V3(b.red, b.green,b.blue);

		V3 fColor = V3.add(V3.sMul(t,bColor),V3.sMul(1-t,aColor));

		int red = fColor.x>255?255:(int)fColor.x;
		int green = fColor.y>255?255:(int)fColor.y;
		int blue = fColor.z>255?255:(int)fColor.z;
		return  red<<16 | green<<8 | blue ;
	}
	
}