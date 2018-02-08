package comp557.a1;

import com.jogamp.opengl.GLAutoDrawable;

public class CharacterCreator {

	static public String name = "Happy the Teapot Ballerina - Michel Kassis AND 260662779";
	
	/** 
	 * Creates a character.
	 * @return root DAGNode
	 */
	static public DAGNode create() {
		// TODO: use for testing, and ultimately for creating a character
		// Here we just return null, which will not be very interesting, so write
		// some code to create a charcter and return the root node.
				
		FreeJoint root = new FreeJoint("Root");
		
		HingeJoint neckBone = new HingeJoint("Neck" , 0, 0, 0, 1, 0, 0, 0, 20);
		BallJoint headBone = new BallJoint("Head" , 0, 0, 0, 1, 1, 1, -180, 180, -180, 180,-180, 180) ;
		
		BallJoint leftShoulderBone = new BallJoint("Left Shoulder" , 0.6, 0.3, 0, 1, 1, 1, -360, -180, -80, 80, -180, 180) ;
		BallJoint rightShoulderBone = new BallJoint("Right Shoulder" , -0.6, 0.3, 0, 1, 1, 1, -360, -180, -80, 80, -180, 180) ;

		BallJoint leftElbowBone = new BallJoint("Left Elbow" , -0.3, 1.2, 0.1, 1, 1, 1, -180, 180, -180, 180,180, 270);
		BallJoint rightElbowBone = new BallJoint("Right Elbow" , 0.3, 1.2 , 0.1, 1, 1, -1, -180, 180, -180, 180,180, 270);

		HingeJoint hipBone = new HingeJoint("Hip Bone" , 0, -0.4, 0, 0, 1, 0, -180, 180);

		
		BallJoint upperLeftLegBone = new BallJoint("Upper Left Leg" , 0.3, -0.5, 0.1, 1, 1, 1, -180, 180, -180, 180, 180, 270);
		BallJoint upperRightLegBone = new BallJoint("Upper Right Leg" , -0.3, -0.5, 0.1, 1, 1, -1, -180, 180, -180, 180,180, 270);
		
		BallJoint leftKneeBone = new BallJoint("Left Knee" , -0.3, 1.2, 0.1, 1, 1, 1, -180, 180, -180, 180,180, 270);
		BallJoint rightKneeBone = new BallJoint("Right Knee" , 0.3, 1.2 , 0.1, 1, 1, -1, -180, 180, -180, 180,180, 270);
		
		
		BallJoint leftFootBone = new BallJoint("Left Foot" , 0, -1, 0, 1, 1, 1, -180, 180, -180, 180,180, 270);
		BallJoint rightFootBone = new BallJoint("Right Foot" , 0, -1 , 0., 1, 1, -1, -180, 180, -180, 180,180, 270);
		
		//BallJoint rightTeapotHand = new BallJoint("Right Hand" , 0,  , 0., 1, 1, -1, -180, 180, -180, 180,180, 270);

		


		
		Geometry torso = new Geometry( "Torso", Geometry.Shape.Cube, 
				0, 0, 0,
				0, 0, 0,
				5, 5, 5,
				0.0f, 1.0f, 0.0f);
		
		Geometry neck = new Geometry( "Neck", Geometry.Shape.Sphere, 
				0, 0.6, 0,
				0, 0, 0,
				0.15,0.3,0.3,
				1.0f,1.0f,1.0f);
		
		
		
		Geometry head = new Geometry( "Head", Geometry.Shape.Cube,
				0, 2, 0,
				0, 0, 0,
				4, 3, 3,
				0.0f, 0.0f, 1.0f);
		
		Geometry eye1 = new Geometry( "eye1", Geometry.Shape.Sphere,
				-0.2, 0.2, 0.5,
				0, 0, 0,
				0.1,0.1,0.2,
				0.0f,0.0f,0.0f);
		
		
		Geometry eye2 = new Geometry( "eye2", Geometry.Shape.Sphere,
				0.2, 0.2, 0.5,
				0, 0, 0,
				0.1,0.1,0.2,
				0.0f,0.0f,0.0f);
		
		Geometry upperLeftArm = new Geometry( "Upper Left Arm", Geometry.Shape.Sphere,
				0.0 ,0.2, 0,
				0, 0, 0,
				0.15,0.4,0.2,
				1.0f,0.0f,0.0f);
		
		
		
		Geometry upperRightArm = new Geometry( "Upper Right Arm", Geometry.Shape.Sphere,
				0.0 ,0.2, 0,
				0, 0, 0,
				0.15,0.4,0.2,
				1.0f,0.0f,0.0f);
				
		Geometry leftElbow = new Geometry( "Left Elbow", Geometry.Shape.Sphere, 
				0, 0, 0,
				0, 0, 0,
				0.8,0.4,0.8,
				1.0f, 1.0f, 1.0f);
		
		Geometry rightElbow = new Geometry( "Right Elbow", Geometry.Shape.Sphere, 
				0, 0, 0,
				0, 0, 0,
				0.8,0.4,0.8,
				1.0f, 1.0f, 1.0f);
		
		Geometry lowerLeftArm = new Geometry( "Lower Left Arm", Geometry.Shape.Sphere,
				0 ,-2.3, 0.2,
				0, 0, 0,
				1.1,2,1.2,
				0.0f,0.0f,1.0f);
			
		Geometry lowerRightArm = new Geometry( "Lower Right Arm", Geometry.Shape.Sphere,
				0 ,-2.3, 0.2,
				0, 0, 0,
				1.1,2,1.2,
				0.0f,0.0f,1.0f);
		
		Geometry hip= new Geometry( "Hip", Geometry.Shape.Sphere, 
				0, 0, 0,
				0, 0, 0,
				0.5, 0.5, 0.5,
				0.9f, 0.0f, 0.7f);
		
		Geometry teapot = new Geometry( "Teapot", Geometry.Shape.Teapot,
				2.0 ,-1.8, 0.0,
				0, 0, 0,
				1,1,1,
				1.0f,1.0f,1.0f);
		
		
		Geometry upperLeftLeg = new Geometry( "Upper Left Leg", Geometry.Shape.Sphere, 
				0, 0.6, 0,
				0, 0, 0,
				0.3,0.6,0.3,
				0.3f,0.8f,1.0f);
		
		Geometry upperRightLeg = new Geometry( "Upper Right Leg", Geometry.Shape.Sphere, 
				0, 0.6, 0,
				0, 0, 0,
				0.3,0.6,0.3,
				0.3f,0.8f,1.0f);
		
		Geometry leftKnee = new Geometry( "Left Knee", Geometry.Shape.Sphere, 
				0, 0, 0,
				0, 0, 0,
				0.7,0.6,1,
				0.9f, 0.0f, 0.7f);
		
		Geometry rightKnee = new Geometry( "Right Knee", Geometry.Shape.Sphere, 
				0, 0, 0,
				0, 0, 0,
				0.7,0.6,1,
				0.9f, 0.0f, 0.7f);
		
		Geometry lowerLeftLeg = new Geometry( "Lower Left Leg", Geometry.Shape.Sphere, 
				0.2, -1.3, 0,
				0, 0, 0,
				0.8,2,0.8,
				0.3f,0.8f,1.0f);
		
		Geometry lowerRightLeg = new Geometry( "Lower Right Leg", Geometry.Shape.Sphere, 
				-0.2, -1.3, 0,
				0, 0, 0,
				0.8,2,0.8,
				0.3f,0.8f,1.0f);
		
		Geometry leftFoot = new Geometry( "Left Foot", Geometry.Shape.Sphere, 
				0, 0, 0,
				0, 0, 0,
				0.7,0.6,1,
				1.0f, 1.0f, 1.0f);
		
		Geometry rightFoot = new Geometry( "Right Foot", Geometry.Shape.Sphere, 
				0, 0, 0,
				0, 0, 0,
				0.7,0.6,1,
				1.0f, 1.0f, 1.0f);
		
						
		root.add(torso);
		torso.add(neckBone);
		neckBone.add(neck);
		neck.add(headBone);
		headBone.add(head);
		head.add(eye2);
		head.add(eye1);
		
		
		torso.add(leftShoulderBone);
		leftShoulderBone.add(upperLeftArm);
		upperLeftArm.add(leftElbowBone);
		leftElbowBone.add(leftElbow);
		leftElbow.add(lowerLeftArm);
	
		torso.add(rightShoulderBone);
		rightShoulderBone.add(upperRightArm);
		upperRightArm.add(rightElbowBone);
		rightElbowBone.add(rightElbow);
		rightElbow.add(lowerRightArm);
		lowerRightArm.add(teapot);
		
		torso.add(hipBone);
		hipBone.add(hip);
		
		hip.add(upperRightLegBone);
		hip.add(upperLeftLegBone);
		
		
		upperLeftLegBone.add(upperLeftLeg);
		upperRightLegBone.add(upperRightLeg);
		
		upperLeftLeg.add(leftKneeBone);
		upperRightLeg.add(rightKneeBone);
		
		leftKneeBone.add(leftKnee);
		rightKneeBone.add(rightKnee);
		leftKnee.add(lowerLeftLeg);
		rightKnee.add(lowerRightLeg);
		lowerLeftLeg.add(leftFootBone);
		lowerRightLeg.add(rightFootBone);
		leftFootBone.add(leftFoot);
		rightFootBone.add(rightFoot);
		
		return root;
	}
}
