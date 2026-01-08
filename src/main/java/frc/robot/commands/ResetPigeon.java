package frc.robot.commands;

import com.ctre.phoenix6.hardware.Pigeon2;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.Components;
import frc.robot.subsystems.swerve.Swerve;

public class ResetPigeon extends Command {

  Pigeon2 pigeon2;
  Swerve swerveSubsystem;

  public ResetPigeon() {
    this.pigeon2 = new Pigeon2(Components.PIGEON2);
    this.swerveSubsystem = Swerve.getInstance();
    addRequirements(swerveSubsystem);
  }

  @Override
  public void initialize() {
    System.out.println("resetando o pigeon");
  }

  @Override
  public void execute() {
    swerveSubsystem.resetSwerve();
  }

  @Override
  public void end(boolean interrupted) {}

  @Override
  public boolean isFinished() {
    return pigeon2.getYaw().getValueAsDouble() == 0;
  }
}
