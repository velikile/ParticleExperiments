import java.io.IOException;
import java.io.FileWriter;
public class R {
	
	public static <T> void println (T val)
	{
		if(val == null)
		{
			System.out.println();
		}
		System.out.println(val);
	}
	public static <T> void print (T val)
	{
		System.out.print(val);
	}
	public static void ln()
	{
		System.out.println();
	}
	public static V2 midPoint(V2 A,V2 B){A.add(B);A.sMul(1/2f);return A;}
	public static float sq(float a){return a*a;}
	public static float max(float a,float b){return (float)Math.max(a,b);}
	public static float min(float a,float b){return (float)Math.min(a,b);}
	public static boolean eq(float a,float b,float e){return Math.abs(a-b)<=e;}
	public static float avg (float a,float b){return (a+b)/2f;}
	public static float abs(float a){return a>=0?a:-a;}
	public static boolean in(float subj, float l ,float r)
	{//inclusive on both sides of the range specified by l and r 
		return r==l?r==subj:r<l && subj<=l && subj>=r || r>l && subj>=l && subj<=r ;
	}
	public static <T> void swap(T[] a,int i,int j)
	{
		T temp = a[i];
		a[i] = a[j];
		a[j] = temp;
	}

	public static void OpenAndWriteToFile(String fileName,String text)
	{
		FileWriter out = null;
		try
		{
			out = new FileWriter(fileName);
			out.write(text);
		}
		catch(IOException e)
		{
			println(e);
			if(out!=null)
				try
				{
					out.close();
				}
				catch(IOException ei)
				{
					println(ei);
				}
		}
		if(out!=null)
		{
			try
			{
				out.close();
			}
			catch(IOException e)
			{
				R.println(e);
			}
		}
	}
	public static V3 IntersectionPoint(V3 a ,V3 b,V3 c,V3 d)
	{

		//a and c are initial points and b and d are direction vectors going out of a and c 
		float u = d.toV2().cross(b.toV2());
		V2 ac = V3.sub(a, c).toV2();
		float s =ac.cross(d.toV2());
		float t = s/u;
		float w = ac.cross(b.toV2())/u;
		if(u==0 && s==0)
			return a;
		else if(u==0 && s!=0)
			return c;
		else if (t>=0&&t<=1f&&w>=0&&w<1f)
			return V3.add(a, V3.sMul(t, b));
		
		return new V3(0,0,0);
	}

	public static int LineHash(V2 a, V2 b)
	{
		return (int)(a.dot(b) + a.x *7 + b.y* 14 + a.y*2 + b.x*2);
	}
}
