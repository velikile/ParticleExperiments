public class V2	
	{
		public float x;
		public float y;
		private static float epsilon = 0.000001f;
		public V2(){x= 0;y=0;}
		public V2(float x, float y){this.x = x; this.y =y;}
		public void add(V2 a){ x+=a.x; y+=a.y;}
		public void sMul(float scalar){x*=scalar; y*=scalar;}
		public float dot(V2 a){return  x*a.x + y*a.y;}
		public V2 reverse(){return new V2(-x,-y);}
		public float cross(V2 a){return x*a.y-y*a.x;}
		public static float dot(V2 a ,V2 b){return a.x*b.x + a.y*b.y;}
		public static V2 add(V2 a,V2 b){return new V2(a.x+b.x,a.y+b.y);}
		
		public static V2 sMul(float scalar,V2 v){return new V2(scalar*v.x,scalar*v.y);}
		
		public static V2 sub(V2 a, V2 b ){return roundToZero(new V2(a.x-b.x,a.y-b.y));}
		public static V2 roundToZero(V2 a){return new V2(R.in(a.x,-epsilon,epsilon)?0:a.x,R.in(a.y,-epsilon,epsilon)?0:a.y);}
		public float x2(){return x*x;}
		public float y2(){return y*y;}
		public boolean inCircle(V2 center,float radius)
		{
			V2 res = V2.sub(this,center);
			return res.x2()+res.y2() <= R.sq(radius);
		}
		public V2 rotate(float rad){
			float sinRad =(float) Math.sin(rad);
			float cosRad =(float) Math.cos(rad);
			float l = len();
			return new V2(l*cosRad,-l*sinRad);
		}
		public static V2 hadProduct(V2 a ,V2 b){return new V2(a.x*b.x,a.y*b.y);}
		public V3 toV3(){return new V3(x,y,0);}
		public float V2Len(V2 a){return (float)Math.sqrt(a.x*a.x+a.y*a.y);}
		public boolean isZero(){return x==0 &&y==0;}
		public boolean eq(V2 a){return a.x==x && a.y == y;}
		public boolean eq(V2 a,float epsilon){return R.abs(a.x-x)<=epsilon && R.abs(a.y - y)<=epsilon;}
		public float len(){return (float)Math.sqrt(x*x+y*y);}
		public void print(){R.println(x+","+y);}
		public String toString(){return x+","+y;}
		public void sAdd(float f) {
			 x+=f;
			 y+=f;
		}
		public V2 clone(){return new V2(x,y);}
	}