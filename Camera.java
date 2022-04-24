class Camera
{
	public V3 position;
	public V3 direction;
	public Quaternion rot;
	public V3 up;
	public float thetaP=0;
	public float thetaY=0;
	public float thetaR=0;
	public Camera(V3 pos, V3 dir)
	{
		position = pos;
		direction = dir;
		up = new V3(0,1,0);
		rot = new Quaternion(0,0,0,0);
		
	}
	public void translate(V3 velocity)
	{
		position.add(velocity);
	}
	public void rotate(float yaw,float pitch)
	{
			//pitch*=2*Math.PI;
			//yaw  *=2*Math.PI;
			Quaternion Y= new Quaternion(-yaw,0,1,0);// about the y axis
			Y.convert();
			direction.rotate(Y);
			Quaternion X= new Quaternion(-pitch,1,0,0);//about the x;
			X.convert();
			//up.rotate(X);

			rot = Quaternion.mul2(X, Y);
	}

	//stages 1. create points in the world space NOTE(create cube works in world space out of the box)
	//       2. move all the points in to the view space aka camera space
	//       3. project the points to the screen in texel coordinates 
	//		 4. convert points to pixel points 
	//		 5. draw pixels
	//
}