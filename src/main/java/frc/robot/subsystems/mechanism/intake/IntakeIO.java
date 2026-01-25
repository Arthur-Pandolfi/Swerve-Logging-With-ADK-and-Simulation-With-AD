package frc.robot.subsystems.mechanism.intake;

import org.littletonrobotics.junction.AutoLog;



public interface IntakeIO {

  @AutoLog
  public static class IntakeInputs{
    public double speed = 0;
    public boolean isColecting = false;
    public double voltage = 0;
  }

  public void catchBall(double speed);

  public double getSpeed();

  public void throwIntake(double angle);

  public void updateInputs(IntakeInputs inputs);

  public boolean isColecting();

  public double getVoltage();
}
