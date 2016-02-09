package org.usfirst.frc.team2635.robot;

import com.analog.adis16448.frc.ADIS16448_IMU;

import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

public class AngleXPIDSource implements PIDSource{

	ADIS16448_IMU gyroscope;
	@Override
	public void setPIDSourceType(PIDSourceType pidSource) {
		// TODO Auto-generated method stub
		
	}

	public AngleXPIDSource(ADIS16448_IMU gyroscope) {
		super();
		this.gyroscope = gyroscope;
	}

	@Override
	public PIDSourceType getPIDSourceType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double pidGet() {
		return gyroscope.getAngleX();
	}

}
