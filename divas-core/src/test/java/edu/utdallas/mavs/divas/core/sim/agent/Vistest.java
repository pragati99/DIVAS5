//package edu.utdallas.mavs.divas.core.sim.agent;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import com.jme3.app.SimpleApplication;
//import com.jme3.material.Material;
//import com.jme3.math.ColorRGBA;
//import com.jme3.math.FastMath;
//import com.jme3.math.Vector3f;
//import com.jme3.scene.Geometry;
//import com.jme3.scene.shape.Line;
//
//
//
//
//
//public class Vistest extends SimpleApplication {
//
//
//	    public static void main(String[] args){
//	    	Vistest app = new Vistest();
//	        app.start(); // start the game
//	    }
//		
//		int						case3Rays			= 10;
//		int						NumOfCircles		= 10;
//
//		int						obs3Rays			= 5;
//		int						obsNumOfCircles		= 5;
//		@Override
//		
//		public void simpleInitApp() {
//			
//			
//			getFlyByCamera().setEnabled(true);
//			
//			getFlyByCamera().setDragToRotate(true);
//			getFlyByCamera().setMoveSpeed(50);
//			getFlyByCamera().setRotationSpeed(3);
//			List<RaySegment> rays;
//			rays = new ArrayList<RaySegment>();
//			for(int z = 0; z < obs3Rays * obsNumOfCircles + 1; z++)
//			{
//				rays.add(new RaySegment());
//			}
//
////			System.out.println(rays.size());
//			generateRayEndPoints(rays);
//			for(int i = 0; i < rays.size(); i++)
//			{
//				Line box1 = new Line(new Vector3f(0,0,0), rays.get(i).end);
//				Geometry blue = new Geometry("Box", box1);
//				Material mat1 = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
//				mat1.setColor("Color", ColorRGBA.Blue);
//				blue.setMaterial(mat1);
//				rootNode.attachChild(blue);
//			}
//		}
//		
//		private void generateRayEndPoints(List<RaySegment> rays)
//		{
//			int z = 0;
//			
//			Vector3f agentLocation = new Vector3f(0,0,0).add(new Vector3f(0, 1.8f, 0)); // TODO use real agent dimensions
//			Vector3f agentHeading = new Vector3f(1,0,0);
//			Vector3f w = agentHeading.normalize();
//			Vector3f rayOrigEndPoints[] = new Vector3f[obs3Rays * obsNumOfCircles + 1];
//
//			Vector3f circleCenter = agentLocation.add(agentHeading.mult(150));
//
//			// u and v represent the axis of the circle (with w being the circles normal)
//			Vector3f u = new Vector3f();
//			Vector3f v = new Vector3f();
//			// calculate u & v (orthonormal to w)
//			if(FastMath.abs(w.x) >= FastMath.abs(w.y))
//			{
//				float factor = 1 / FastMath.sqrt(w.x * w.x + w.z * w.z);
//				u.x = -w.z * factor;
//				u.y = 0;
//				u.z = w.x * factor;
//			}
//			else
//			{
//				float factor = 1 / FastMath.sqrt(w.y * w.y + w.z * w.z);
//				u.x = 0;
//				u.y = w.z * factor;
//				u.z = -w.y * factor;
//			}
//			v = w.cross(u);
//
//			float angle1;
//			float angle2;
//			int i = 0;
//			for(int circleCount = 0; circleCount <= (obsNumOfCircles - 1); circleCount++)
//			{
//				i = 0;
//
//				angle1 = 70 / 2 * ((obsNumOfCircles - 1f - circleCount + 1f) / obsNumOfCircles); // make smaller circles (part of cone)
//				angle2 = 90 - angle1; // we know 1 angle of triangle is 90 degrees
//
//				// float angle3;
//				// float angle4;
//				// angle3 = self.getHorizontalFOV() / 2 * ((NumOfCircles -1f - circleCount+1f) / NumOfCircles); // make smaller circles (part of cone)
//				// angle4 = 90 - angle3; // we know 1 angle of triangle is 90 degrees
//				// logger.debug("Current Angles3/4: "+ angle3 +" , " + angle4);
//
//				// logger.debug("Current Angles1/2: "+ angle1 +" , " + angle2);
//
//				angle1 = angle1 * FastMath.DEG_TO_RAD; // So others must be angle 1 and 90 minus angle 1
//				angle2 = angle2 * FastMath.DEG_TO_RAD;
//
//				float radius = 150 * FastMath.sin(angle1) / FastMath.sin(angle2);
//				// logger.debug("rad: "+radius);
//
//				for(float t = 0; t < (2 * FastMath.PI); t = t + (2 * FastMath.PI) / obs3Rays) // case 3 rays config variable.
//				{
//
//					// Let C be the point (center of circle) and let W be the unit-length normal at that point. Choose unit-length vectors U and V so that {U,V,W} is orthonormal (mutually
//					// perpendicular, unit-length vectors). If the circle radius is r, then the circle is parameterized by
//					// X(t) = C + (r*cos(t))*U + (r*sin(t))*V
//					// logger.debug(i);
//					rayOrigEndPoints[i + circleCount * obs3Rays] = circleCenter.add(u.mult((radius * FastMath.cos(t)))).add(v.mult((radius * FastMath.sin(t))));
//					i++;
//					// logger.debug(myEndPoint);
//				}
//				i = 0;
//
//				// logger.debug(agentLocation);
//
//				// scale to VD (Else agents see too far.)
//				while(i < obs3Rays)
//				{
//					Vector3f scaled = rayOrigEndPoints[i + circleCount * obs3Rays].subtract(agentLocation);
//					scaled.normalizeLocal();
//					scaled.multLocal(150);
//					rays.get(i + circleCount * obs3Rays).end = agentLocation.add(scaled);
//					
//					
//					System.out.println("rays "+z + ", Point = "+rays.get(i + circleCount * obs3Rays).end + " obs ");
//					
//					z++;
//					i++;
//				}
//			}
//		}
//		
//		
//}
