package frc.robot.commands;

import frc.robot.subsystems.swerve.Swerve;
import edu.wpi.first.wpilibj2.command.Command;

public class ResetPigeon extends Command {

  Swerve swerve;

  public ResetPigeon() {
    swerve = Swerve.getInstance();
    addRequirements(swerve);
  }

  @Override
  public void initialize() {}

  @Override
  public void execute() {
    swerve.resetSwerve();
  }

  @Override
  public void end(boolean interrupted) {}

  @Override
  public boolean isFinished() {
    return swerve.getHeading().getDegrees() == 0;
  }
}
