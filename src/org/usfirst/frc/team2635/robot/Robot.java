
package org.usfirst.frc.team2635.robot;

import com.analog.adis16448.frc.ADIS16448_IMU;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
    final String defaultAuto = "Default";
    final String customAuto = "My Auto";
    String autoSelected;
    SendableChooser chooser;
    
    SendableChooser controlSchemeChooser;
    
    final String XBOXARCADE = "Xbox Arcade";
    final String XBOXTANK = "Xbox Tank";
    final String FLIGHTSTICKARCADE = "Flightstick Arcade";
    final String FLIGHTSTICKTANK = "FlightStick Tank";
    final String ANGLEFROMDASHBOARD = "Angle Drive";
    
	CANTalon rearRight;
	CANTalon frontRight;
	CANTalon rearLeft;
	CANTalon frontLeft;
	RobotDrive drive;
	PIDController anglePID;
	Joystick xboxController;
	Joystick rightStick;
	Joystick leftStick;
	
    CameraServer server;
    ADIS16448_IMU gyroscope;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
    	xboxController = new Joystick(0);
    	rightStick = new Joystick(1);
    	leftStick = new Joystick(2);
    	
    	rearRight = new CANTalon(3);
    	frontRight = new CANTalon(4);
    	rearLeft = new CANTalon(1);
    	frontLeft = new CANTalon(2);
    	
    	drive = new RobotDrive(frontLeft, rearLeft, frontRight, rearRight);
    	SmartDashboard.putNumber("P", 0.0);
    	SmartDashboard.putNumber("I", 0.0);
    	SmartDashboard.putNumber("D", 0.0);
    	SmartDashboard.putNumber("Angle Setpoint", 0.0);
    	
    	controlSchemeChooser = new SendableChooser();
    	controlSchemeChooser.addDefault("Xbox Arcade", XBOXARCADE);
    	controlSchemeChooser.addObject("Xbox Tank", XBOXTANK);
    	controlSchemeChooser.addObject("Flight Stick Arcade", FLIGHTSTICKARCADE);
    	controlSchemeChooser.addObject("Flight Stick Tank", FLIGHTSTICKTANK);
    	controlSchemeChooser.addObject("Angle from dashboard", ANGLEFROMDASHBOARD);
    	SmartDashboard.putData("Drive Scheme choices", controlSchemeChooser);
    	
    	chooser = new SendableChooser();
        chooser.addDefault("Default Auto", defaultAuto);
        chooser.addObject("My Auto", customAuto);
        SmartDashboard.putData("Auto choices", chooser);
        
        server = CameraServer.getInstance();
        server.setQuality(0);
        
        
        //the camera name (ex "cam0") can be found through the roborio web interface
        server.startAutomaticCapture("cam0");
        
        gyroscope = new ADIS16448_IMU();
        gyroscope.calibrate();
        gyroscope.reset();
    	anglePID = new PIDController(SmartDashboard.getNumber("P"), SmartDashboard.getNumber("I"), SmartDashboard.getNumber("D"), 0.0, new AngleXPIDSource(gyroscope), new RobotDrivePIDOutput(drive));

    }
    
	/**
	 * This autonomous (along with the chooser code above) shows how to select between different autonomous modes
	 * using the dashboard. The sendable chooser code works with the Java SmartDashboard. If you prefer the LabVIEW
	 * Dashboard, remove all of the chooser code and uncomment the getString line to get the auto name from the text box
	 * below the Gyro
	 *
	 * You can add additional auto modes by adding additional comparisons to the switch structure below with additional strings.
	 * If using the SendableChooser make sure to add them to the chooser code above as well.
	 */
    public void autonomousInit() {
    	autoSelected = (String) chooser.getSelected();
//		autoSelected = SmartDashboard.getString("Auto Selector", defaultAuto);
		System.out.println("Auto selected: " + autoSelected);
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
    	switch(autoSelected) {
    	case customAuto:
        //Put custom auto code here   
            break;
    	case defaultAuto:
    	default:
    	//Put default auto code here
            break;
    	}
    }
    public void teleopInit()
    {
    	anglePID.setPID(SmartDashboard.getNumber("P"), SmartDashboard.getNumber("I"), SmartDashboard.getNumber("D"));
    }
    /**
     * This function is called periodically during operator control
     */
    boolean pidControl = false;
    public void teleopPeriodic() {
    	Double X;
    	Double Y;
        String controlSchemeSelected = (String)controlSchemeChooser.getSelected();
        //Entering pid control
        if(controlSchemeSelected == ANGLEFROMDASHBOARD && !pidControl)
        {
        	gyroscope.reset();
        	anglePID.enable();
        	pidControl = true;
        }
        //Leaving pid control
        else if(controlSchemeSelected != ANGLEFROMDASHBOARD && pidControl)
        {
        	anglePID.disable();
        	pidControl = false;
        }
        
        switch(controlSchemeSelected)
        {
        case XBOXARCADE:
        	X = xboxController.getRawAxis(1);
        	Y = xboxController.getRawAxis(0);
        	drive.arcadeDrive(Y, X);
        	break;
        case XBOXTANK:
        	X = xboxController.getRawAxis(1);
        	Y = xboxController.getRawAxis(5);
        	drive.tankDrive(X, Y);
        	break;
        case FLIGHTSTICKARCADE:
        	X = rightStick.getRawAxis(0);
        	Y = rightStick.getRawAxis(1);
        	drive.arcadeDrive(-Y, -X);
        	break;
        case FLIGHTSTICKTANK:
        	X = rightStick.getRawAxis(1);
        	Y = leftStick.getRawAxis(1);
        	drive.tankDrive(-X, -Y);
        	break;
        case ANGLEFROMDASHBOARD:
        	anglePID.setSetpoint(SmartDashboard.getNumber("Angle Setpoint", 0.0));
        	
        default:
        	X = 0.0;
        	Y = 0.0;
        }
        SmartDashboard.putNumber("X", X);
        SmartDashboard.putNumber("Y", Y);
        
        SmartDashboard.putNumber("AngleX", gyroscope.getAngleX());
     
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    
    }
    
}
