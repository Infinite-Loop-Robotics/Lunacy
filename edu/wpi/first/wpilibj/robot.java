/*----------------------------------------------------------------------------*
* Basic Robot Code Compiled By Blake from Team 2648 
* Questions? blake (at) team2648 [dot] com
/*----------------------------------------------------------------------------*/

package edu.wpi.first.wpilibj;

/* 
	Imports are not needed when the package is edu.wpi.first.wpilibj; 
	contrary to that of the basic netbeans frc project import edu.wpi.first.wpilibj.RobotDrive;
*/

public class LunacyBot extends SimpleRobot 
{
	private RobotDrive drivetrain;	// The robot drivetrain. 4 wheels
	private Joystick leftStick;		// One Joystick controls the robot
	private DigitalInput index; 	// This is a digital input from our limit switch, allows for the indexing of the lift
	private Jaguar horn; 			// Oh Yes we have a horn!
	private Victor lift; 			// The actual lift, to raise the balls to the kicker
	private Victor roller; 			// Brings the Orbit balls into the front of the base of the lift
	private Victor kicker; 			// The "chainsaw" typeish kicker that throws the balls out of the top of our robot.
	private boolean debug = false;	//set to true to enable debugging output

	public LunacyBot ()
	{
		drivetrain = new RobotDrive(1, 2); 	// create RobotDrive
		leftStick = new Joystick(1);		// and joysticks, to control the robot
		index = new DigitalInput(1);		// and the Digital IO, to keep track of where the lift is
		horn = new Jaguar(4); 				// and the Horn, to make loud noises
		lift = new Victor(3); 				// and the lift, its only a victor because we burnt out like 3 Jaguars
		roller = new Victor(5); 			// and the rollers to intake the moonrocks from the floor
		kicker = new Victor(6)				// and our chainsaw, to eject the moonrocks at the top of the lift
	}

	public void autonomous() 
	{
		getWatchdog().setEnabled(false); // Stupid WATCHDOG
		stop();		// turn all motors off
		for(int i = 0; i < 4; i++) // drive forward for 4 seconds
		{
			d("FWD");
			drivetrain.setLeftRightMotorSpeeds(1.0, 1.0);
			Timer.delay(1);
		}
		for(int i = 0; i < 11; i++	// spin for 11 seconds      
		{ 
			d("Spinning");
			drivetrain.setLeftRightMotorSpeeds(-1.0, 1.0);
			Timer.delay(1);
		}
		d("done");
		stop();		// turn all motors off
	}

	public void operatorControl() 
	{
		getWatchdog().setEnabled(false); // Stupid WATCHDOG
		while (isOperatorControl() && isEnabled()) //Lets loop while in operator control
		{
			roller.set(1); //run front rolers always
			
			// pushing the trigger would index the lift. Holding the button would keep the lift moving.
			if (leftStick.getTrigger())
				lift.set(1);
			else if (index.get())
				lift.set(1);
			else
				lift.set(0); //Kicker code

			// the kicker is the belt on the top of the lift.
			// When button 7 is pressed the direction changes and the moon rocks are ejected out of the robot,
			// otherwise the direction defaults to -1, which means move in the direction to put the moonrocks into the hopper.
			if(leftStick.getRawButton(7))
				kicker.set(1);
			else
				kicker.set(-1);

			//Horn code 
			if (leftStick.getRawButton(3))
			{
				//Since the throttle returns a value on the range [-1 1] and we really want a number on the range [0 1]
				//we can use some math to remap the range of numbers.
				horn.set(map(leftStick.getThrottle(), -1, 1, 1, 0));
			}
			else
				horn.set(0);

			//make mr robo drive
			drivetrain.arcadeDrive(leftStick.getY(), leftStick.getX());// drive w/joysticks 
			//slow down the iterations, seems to work smoothly
			Timer.delay(0.005);

		}
		d("END OC");
		stop();
	}

	/**
	 * Stops all motors
	 */
	public void stop()
	{
		drivetrain.drive(0.0, 0.0);
		lift.set(0);
		roller.set(0);
		kicker.set(0);
		horn.set(0);
	}

	/**
	 * Only System.out.println(str) when debug = true.
	 * @param str string to print
	 */
	public void d(String str)
	{
		if(debug)
			System.out.println(str);
	}
	/**
	 * map a number from one range to another
	 * @param  {num} value   the value to be mapped
	 * @param  {num} old_min the minimum of value
	 * @param  {num} old_max the maximum of value
	 * @param  {num} new_min the new minimum value
	 * @param  {num} new_max the new maximum value
	 * @return {num}         the value remaped on the range [new_min new_max]
	 */
	public double map(double value, double old_min, double old_max, double new_min, double new_max) {
		return (value - old_min) / (old_max - old_min) * (new_max - new_min) + new_min;
	}
}
