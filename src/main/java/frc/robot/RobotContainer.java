package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;

import frc.robot.Constants.DriveConsts;
import frc.robot.subsystems.swerve.Swerve;
import frc.frc_java9485.autonomous.AutoChooser;
import frc.frc_java9485.joystick.driver.DriverJoystick;

public class RobotContainer {
  private final Swerve swerveSubsystem;
  private final DriverJoystick driverController;

  private final AutoChooser autoChooser;

  public RobotContainer() {
    DriverStation.silenceJoystickConnectionWarning(true);

    this.swerveSubsystem = Swerve.getInstance();
    this.driverController = DriverJoystick.getInstance();

    autoChooser = new AutoChooser("Autonomous Chooser", "DIREITA");

    swerveSubsystem.setDefaultCommand(
        swerveSubsystem.driveCommand(
            () -> driverController.getLeftY(),
            () -> driverController.getLeftX(),
            () -> driverController.getRightX(),
            DriveConsts.FIELD_ORIENTED));

    configureBindings();
  }

  private void configureBindings() {}

  public Command getAutonomousCommand() {
    return swerveSubsystem.getAutonomousCommand(autoChooser.getSelectedOption(), true);
  }
}
