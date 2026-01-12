package frc.robot;

import edu.wpi.first.net.WebServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.Constants.FieldConsts;
import frc.robot.Constants.RobotConsts;
import frc.robot.Constants.RobotConsts.RobotModes;
import frc.robot.subsystems.swerve.Swerve;
import org.littletonrobotics.junction.LoggedRobot;
import org.littletonrobotics.junction.Logger;
import org.littletonrobotics.junction.networktables.NT4Publisher;
import org.littletonrobotics.junction.wpilog.WPILOGWriter;
import org.littletonrobotics.urcl.URCL;

public class Robot extends LoggedRobot {
  private Command m_autonomousCommand;

  private final Swerve swerve;
  private final RobotContainer m_robotContainer;

  public Robot() {
    switch (RobotConsts.CURRENT_ROBOT_MODE) {
      case REAL:
        Logger.addDataReceiver(new NT4Publisher());
        Logger.addDataReceiver(new WPILOGWriter(RobotConsts.LOGS_PATH));
        break;

      case SIM:
        Logger.addDataReceiver(new NT4Publisher());
        break;
    }

    Logger.registerURCL(URCL.startExternal());
    Logger.start();

    m_robotContainer = new RobotContainer();
    swerve = Swerve.getInstance();
  }

  @Override
  public void robotInit() {
    WebServer.start(5800, Filesystem.getDeployDirectory().getPath());
  }

  @Override
  public void robotPeriodic() {
    SmartDashboard.putNumber("Match Time", DriverStation.getMatchTime());
    CommandScheduler.getInstance().run();
  }

  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    if (m_autonomousCommand != null) {
      CommandScheduler.getInstance().schedule(m_autonomousCommand);
      ;
    }
  }

  @Override
  public void teleopInit() {
    if (RobotConsts.CURRENT_ROBOT_MODE == RobotModes.SIM) {
      var alliancePosition = DriverStation.getRawAllianceStation();

      switch (alliancePosition) {
        case Blue1:
          swerve.resetOdometrySim(FieldConsts.BLUE_LEFT_START_POSE);
          break;
        case Blue2:
          swerve.resetOdometrySim(FieldConsts.BLUE_CENTER_START_POSE);
          break;
        case Blue3:
          swerve.resetOdometrySim(FieldConsts.BLUE_RIGHT_START_POSE);
          break;

        case Red1:
          swerve.resetOdometrySim(FieldConsts.RED_LEFT_START_POSE);
          break;
        case Red2:
          swerve.resetOdometrySim(FieldConsts.RED_CENTER_START_POSE);
          break;
        case Red3:
          swerve.resetOdometrySim(FieldConsts.RED_RIGHT_START_POSE);
          break;

        case Unknown:
          swerve.resetOdometrySim(FieldConsts.FIELD_CENTER_POSE);
          break;
      }
    }

    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  @Override
  public void testInit() {
    CommandScheduler.getInstance().cancelAll();
  }
}
