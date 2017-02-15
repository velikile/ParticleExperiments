public class V3	
	{
		public float x;
		public float y;
		public float z;
		public V3(){x= 0;y=0;z=0;}
		public V3(float x, float y,float z){this.x = x; this.y =y;this.z = z;}
		public V3(V3 p) {
			x = p.x;
			y = p.y;
			z = p.z;
		}
		public void set(V3 a){x=a.x;y=a.y;z=a.z;}
		public void print(){R.print(x+","+y+","+z);}
		public String toString(){return x+","+y+","+z;}
		public boolean inCircle(V3 center,float radius)
		{
			V3 res = V3.sub(this,center);
			return res.x2()+res.y2() <= R.sq(radius);
		}
		public boolean inSquere(V3 q,V3 w,V3 s,V3 a,V3 p)
		{
			V3 qw = V3.sub(q, w);
			V3 qa = V3.sub(q,a);
			V3 ws = V3.sub(w, s);
			V3 wq = V3.sub(w,q);
			
			qw.normalize();
			qa.normalize();
			ws.normalize();
			wq.normalize();
		
			float  n = p.normalize();
			
			float centricCoordinates = p.x+p.y+p.z;
			
			if (centricCoordinates >0&&centricCoordinates<=1)
				return true;
			
			p.sMul(n);
			return false;
		}
		public float normalize(){
			
		float l= len();
		if(l!=0)
		{
			x/=l;y/=l;z/=l;
		}
		return l;
		}
		public float Yaw()
		{
			return y==0? Math.signum(z)*(float)Math.PI/2f : (float) Math.atan((double)z/x);
		}
		public float Roll()
		{
			return x==0? Math.signum(y)*(float)Math.PI/2f : (float) Math.atan((double)y/x);
		}
		public float Pitch()
		{
			return z==0? Math.signum(y)*(float)Math.PI/2f : (float) Math.atan((double)y/z);
		}
		public void rotate(M3x3 rm)
		{
			V3 v = rm.mul(clone());
			x= v.x;
			y= v.y;
			z= v.z;
		}
		public void rotate(Quaternion q)
		{
			Quaternion qi = q.conjugate();
			V3 vec = clone();
			
			float l= vec.normalize();
			
			Quaternion p = new Quaternion(0,vec);
			q = q.mul(p).mul(qi);
			V3 r = q.getAxis();
			x = r.x*l;
			y = r.y*l;
			z = r.z*l;
		}
		
		public void add(V3 a){ x+=a.x; y+=a.y;z+=a.z;}
		public void sub(V3 a){ x-=a.x; y-=a.y;z-=a.z;}
		public void sMul(float scalar){x*=scalar; y*=scalar; z*=scalar;}
		public void sAdd(float s){x+=s;y+=s;z+=s;}
		public void sMulXY(float scalar){x*=scalar; y*=scalar;}
		public V2 toV2(){return new V2(x,y);}
		public float dot(V3 a){return x*a.x + y*a.y + z*a.z;}
		public float len(){return (float)Math.sqrt(x*x+y*y+z*z);}
		public float x2(){return x*x;}
		public float y2(){return y*y;}
		public V2 project(V3 eye,float nearZ,float farZ)
		{ 
			//V2 res = V2.sMul(1f/(z-eye.z),new V2(eye.x-x,eye.y-y));
			V3 res3 = new V3(x-eye.x,y-eye.y,z-eye.z);
			
			//if(res3.z>0)
			//	return new V2(0,0);
			res3.normalize();
			res3.sMul(nearZ - eye.z);
			V2 res = res3.toV2();
			//(eye-p)/(z-eye.z);
			//V2 res = V2.sMul(1f/(z),new V2(x,y));
			return res;
		}
		public V2 project2(float fov,V3 center, float nearZ,float farZ)
		{ 
			float tanT = (float)Math.tan(fov);
			
			float l = tanT*farZ;
			float s = tanT*nearZ;
			if(z>l)
				return new V2(0,0);
			else if (z<s)
			{
				return center.toV2();
			}
			x =x/((center.z+z)*tanT);
			y =y/((center.z+z)*tanT);

			x = R.in(x,-1,1)?x:x>1?1:-1;
			y = R.in(y,-1,1)?y:y>1?1:-1;
			return toV2();
		}
		
		public V3 l90(){return new V3(-y,x,z);}// 2d concept rotate 90 degrees to the left
		public V3 clone(){return new V3(x,y,z);}
		public static boolean eq(V3 a,V3 b,float e){ return R.eq(a.x,b.x,e)&&
															R.eq(a.y,b.y,e)&&
															R.eq(a.z,b.z,e);}
		public static boolean eqXY(V3 a,V3 b,float e){ return R.eq(a.x,b.x,e)&&
														      R.eq(a.y,b.y,e);}															
		public static boolean eq(V3 a,V3 b){ return a.x==b.x&&a.y==b.y&&a.z==b.z;}
		public static boolean eqXY(V3 a,V3 b){ return a.x==b.x && a.y==b.y;}
		public static boolean in(float subj, float l ,float r){return r<l && subj<=l && subj>r || r>l && subj>=l && subj<r ;}
		public static V3 zero(){return new V3(0,0,0);}
		public static float dot(V3 a ,V3 b){return a.x*b.x + a.y*b.y + a.z*b.z;}
		public static V3 add(V3 a,V3 b){return new V3(a.x+b.x,a.y+b.y,a.z+b.z);}
		public static V3 sMul(float scalar,V3 v){return new V3(scalar*v.x,scalar*v.y,scalar*v.z);}
		public static V3 sub(V3 a, V3 b ){return new V3(a.x-b.x,a.y-b.y,a.z-b.z);}
		public static V3 sub(V3 a, V2 b){return new V3(a.x-b.x,a.y-b.y,a.z);}
		public static V3 hadProduct(V3 a ,V3 b){return new V3(a.x*b.x,a.y*b.y,a.z*b.z);}
	
		public static V3 cross(V3 v0 ,V3 v1)
		{//axb = det xyz  x = bf-ce
		 //          abc  y = cd-af
		 //          def  z = ae-bd 
			return new V3(v0.y*v1.z - v0.z*v1.y,
						  v0.z*v1.x-v0.x*v1.z,
						  v0.x*v1.y-v0.y*v1.x);
		}
		
	}