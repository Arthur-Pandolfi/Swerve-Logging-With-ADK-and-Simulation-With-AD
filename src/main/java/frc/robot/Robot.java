package frc.robot;

import edu.wpi.first.net.WebServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.frc_java9485.utils.AllianceFlipUtil;
import frc.robot.Constants.FieldConsts;
import frc.robot.Constants.Logging;
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
    switch (Logging.CURRENT_ROBOT_MODE) {
      case REAL:
        Logger.addDataReceiver(new NT4Publisher());
        Logger.addDataReceiver(new WPILOGWriter(Logging.LOGS_PATH));
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
    CommandScheduler.getInstance().run();
  }

  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }

  @Override
  public void teleopInit() {
    var alliance = DriverStation.getAlliance();
    if (alliance.isPresent()) {
      swerve.resetOdometrySim(
          alliance.get() == Alliance.Blue
              ? FieldConsts.BLUE_CENTER_START_POSE
              : AllianceFlipUtil.flipToRed(FieldConsts.BLUE_CENTER_START_POSE));
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
