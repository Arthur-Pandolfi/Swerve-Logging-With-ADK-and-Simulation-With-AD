package frc.frc_java9485.joystick.driver;

import edu.wpi.first.math.MathUtil;
import frc.robot.Constants.JoysticksConsts;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class DriverJoystick implements DriverJoystickIO {
  CommandXboxController joystick;
  private static DriverJoystick mInstance = null;
  
  private DriverJoystick() {
    this.joystick = new CommandXboxController(JoysticksConsts.DRIVER_PORT);
  }

  public static DriverJoystick getInstance() {
    if (mInstance == null) {
      mInstance = new DriverJoystick();
    }
    return mInstance;
  }

  @Override
  public double getPerfomanceByAlliance(double speed) {
    var alliance = DriverStation.getAlliance().get();
    if (DriverStation.getAlliance().isPresent() && alliance == Alliance.Red) {
      return -speed;
    }
    return speed;
  }

  @Override
  public double getLeftY() {
    if (isTurboMode()) {
      return MathUtil.applyDeadband(
              getPerfomanceByAlliance(joystick.getLeftY()), JoysticksConsts.DRIVER_DEADBAND)
          * 1;
    } else if (isLowMode()) {
      return MathUtil.applyDeadband(
              getPerfomanceByAlliance(joystick.getLeftY()), JoysticksConsts.DRIVER_DEADBAND)
          * 0.2;
    } else {
      return MathUtil.applyDeadband(
              getPerfomanceByAlliance(joystick.getLeftY()), JoysticksConsts.DRIVER_DEADBAND)
          * 0.7;
    }
  }

  @Override
  public double getLeftX() {
    if (isTurboMode()) {
      return MathUtil.applyDeadband(
              getPerfomanceByAlliance(joystick.getLeftX()), JoysticksConsts.DRIVER_DEADBAND)
          * 1;
    } else if (isLowMode()) {
      return MathUtil.applyDeadband(
              getPerfomanceByAlliance(joystick.getLeftX()), JoysticksConsts.DRIVER_DEADBAND)
          * 0.2;
    } else {
      return MathUtil.applyDeadband(
              getPerfomanceByAlliance(joystick.getLeftX()), JoysticksConsts.DRIVER_DEADBAND)
          * 0.7;
    }
  }

  @Override
  public double getRightX() {
    if (isTurboMode()) {
      return MathUtil.applyDeadband(
              getPerfomanceByAlliance(joystick.getRightX()), JoysticksConsts.DRIVER_DEADBAND)
          * 1;
    } else if (isLowMode()) {
      return MathUtil.applyDeadband(
              getPerfomanceByAlliance(joystick.getRightX()), JoysticksConsts.DRIVER_DEADBAND)
          * 0.2;
    } else {
      return MathUtil.applyDeadband(
              getPerfomanceByAlliance(joystick.getRightX()), JoysticksConsts.DRIVER_DEADBAND)
          * 0.7;
    }
  }

  @Override
  public double getRightY() {
    if (isTurboMode()) {
      return MathUtil.applyDeadband(
              getPerfomanceByAlliance(joystick.getRightY()), JoysticksConsts.DRIVER_DEADBAND)
          * 1;
    } else if (isLowMode()) {
      return MathUtil.applyDeadband(
              getPerfomanceByAlliance(joystick.getRightY()), JoysticksConsts.DRIVER_DEADBAND)
          * 0.2;
    } else {
      return MathUtil.applyDeadband(
              getPerfomanceByAlliance(joystick.getRightY()), JoysticksConsts.DRIVER_DEADBAND)
          * 0.7;
    }
  }

  @Override
  public boolean isTurboMode() {
    return joystick.rightBumper().getAsBoolean();
  }

  @Override
  public boolean isLowMode() {
    return joystick.leftBumper().getAsBoolean();
  }

  @Override
  public Trigger a() {
    return joystick.a();
  }

  @Override
  public Trigger b() {
    return joystick.b();
  }

  @Override
  public Trigger y() {
    return joystick.y();
  }

  @Override
  public Trigger x() {
    return joystick.x();
  }

  @Override
  public Trigger rightBumper() {
    return joystick.rightBumper();
  }

  @Override
  public Trigger leftBumper() {
    return joystick.leftBumper();
  }
}
