package frc.robot.subsystems.swerve;

import com.ctre.phoenix6.hardware.CANcoder;
import com.ctre.phoenix6.hardware.Pigeon2;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.commands.PathPlannerAuto;
import com.pathplanner.lib.config.PIDConstants;
import com.pathplanner.lib.config.RobotConfig;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;
import com.pathplanner.lib.util.PathPlannerLogging;
import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.frc_java9485.motors.SparkOdometryThread;
import frc.frc_java9485.utils.MathUtils;
import frc.robot.Constants.Components;
import frc.robot.Constants.DriveConsts;
import frc.robot.Constants.RobotConsts;
import frc.robot.Constants.RobotConsts.RobotModes;
import frc.robot.subsystems.swerve.IO.GyroIOInputsAutoLogged;
import frc.robot.subsystems.swerve.IO.PigeonIO;
import frc.robot.subsystems.swerve.IO.SwerveIO;
import frc.robot.subsystems.vision.LimelightHelpers;
import java.io.File;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.DoubleSupplier;
import org.littletonrobotics.junction.Logger;
import swervelib.SwerveDrive;
import swervelib.SwerveModule;
import swervelib.parser.SwerveParser;
import swervelib.simulation.ironmaple.simulation.drivesims.SwerveDriveSimulation;
import swervelib.telemetry.SwerveDriveTelemetry;
import swervelib.telemetry.SwerveDriveTelemetry.TelemetryVerbosity;

public class Swerve extends SubsystemBase implements SwerveIO {
  public static final Lock odometryLock = new ReentrantLock();

  private final SwerveDrive swerveDrive;
  private final SwerveDriveKinematics kinematics;
  private final SwerveDrivePoseEstimator poseEstimator;

  private final Pigeon2 pigeon;
  private final PigeonIO pigeonIO;
  private final GyroIOInputsAutoLogged pigeonInputs;

  private final CANcoder[] encoders; // FL FR BL BR

  private SwerveDriveSimulation driveSimulator;

  private SwerveModule[] modules;
  private SwerveModuleState state[];

  private static Swerve m_instance;

  public static Swerve getInstance() {
    if (m_instance == null) 
      m_instance = new Swerve(new File(Filesystem.getDeployDirectory(), "swerve"));
    return m_instance;
  }

  private Swerve(File directory) {
    try {
      SwerveDriveTelemetry.verbosity = TelemetryVerbosity.HIGH;
      swerveDrive = new SwerveParser(directory).createSwerveDrive(DriveConsts.MAX_SPEED);

      encoders =
          new CANcoder[] {
            new CANcoder(DriveConsts.CANCODER_MODULE1_ID), // FL
            new CANcoder(DriveConsts.CANCODER_MODULE2_ID), // FR
            new CANcoder(DriveConsts.CANCODER_MODULE3_ID), // BL
            new CANcoder(DriveConsts.CANCODER_MODULE4_ID) // BR
          };

      pigeon = new Pigeon2(Components.PIGEON2);
      pigeonInputs = new GyroIOInputsAutoLogged();
      pigeonIO = new PigeonIO();

      setupPathPlanner();
      SparkOdometryThread.getInstance().start();

      kinematics = new SwerveDriveKinematics(DriveConsts.MODULES_TRANSLATION);
      poseEstimator =
          new SwerveDrivePoseEstimator(
              kinematics, getHeading(), swerveDrive.getModulePositions(), new Pose2d());

      if (RobotConsts.CURRENT_ROBOT_MODE == RobotModes.SIM) {
        swerveDrive.setHeadingCorrection(false);
        swerveDrive.setCosineCompensator(false);

        driveSimulator = swerveDrive.getMapleSimDrive().get();
        driveSimulator.setEnabled(true);

        resetOdometrySim(new Pose2d(0, 0, Rotation2d.fromDegrees(0)));
      }

    } catch (Exception e) {
      throw new RuntimeException("Erro criando Swerve!!!!\n", e);
    }
  }

  @Override
  public void periodic() {
    if (poseEstimator != null) {
      Pose2d poseEstimated = LimelightHelpers.getBotPose2d("");
      poseEstimator.update(pigeon.getRotation2d(), swerveDrive.getModulePositions());
      poseEstimator.addVisionMeasurement(poseEstimated, Timer.getFPGATimestamp());

      if (DriverStation.isDisabled()) {
        Logger.recordOutput(DriveConsts.SWERVE_STATES_LOG_ENTRY, new SwerveModuleState[] {});
      } else {
        Logger.recordOutput(DriveConsts.SWERVE_STATES_LOG_ENTRY, state);
      }
    }

    byte i = 0;
    for (CANcoder encoder : encoders) {
      i++;
      Logger.recordOutput(
          DriveConsts.CANCODER_MODULE_LOG_ENTRY + i, encoder.getAbsolutePosition().getValue());
    }

    switch (RobotConsts.CURRENT_ROBOT_MODE) {
      case SIM:
        if (driveSimulator != null) {
          Logger.recordOutput(
              DriveConsts.ODOMETRY_POSE2D_LOG_ENTRY, driveSimulator.getSimulatedDriveTrainPose());
        }
        break;
      case REAL:
        odometryLock.lock();
        pigeonIO.updateInputs(pigeonInputs);
        odometryLock.unlock();
        Logger.processInputs(DriveConsts.ODOMETRY_GYRO_LOG_ENTRY, pigeonInputs);
        Logger.recordOutput(DriveConsts.ODOMETRY_POSE2D_LOG_ENTRY, getPose());
        break;
    }
  }

  @Override
  public Pose2d getPose() {
    return poseEstimator.getEstimatedPosition();
  }

  @Override
  public void resetOdometry(Pose2d pose) {
    poseEstimator.resetPose(pose);
  }

  @Override
  public Pose2d getPoseSim() {
    return driveSimulator.getSimulatedDriveTrainPose();
  }

  @Override
  public void resetOdometrySim(Pose2d pose) {
    driveSimulator.setSimulationWorldPose(pose);
  }

  @Override
  public ChassisSpeeds getRobotRelativeSpeeds() {
    return swerveDrive.getRobotVelocity();
  }

  @Override
  public void driveFieldOriented(ChassisSpeeds speed) {
    swerveDrive.driveFieldOriented(speed);
  }

  @Override
  public void resetSwerve() {
    pigeon.reset();
    swerveDrive.zeroGyro();
  }

  @Override
  public Rotation2d getHeading() {
    return Rotation2d.fromDegrees(MathUtils.scope0To360(pigeon.getYaw().getValueAsDouble()));
  }

  @Override
  public Command getAutonomousCommand(String path, boolean altern) {
    if (altern) {
      return AutoBuilder.buildAuto(path);
    }
    return new PathPlannerAuto(path);
  }

  @Override
  public Command driveCommand(
      DoubleSupplier X, DoubleSupplier Y, DoubleSupplier omega, boolean fieldOriented) {
    return run(
        () -> {
          double Xcontroller = Math.pow(X.getAsDouble(), 3);
          double Ycontroller = Math.pow(Y.getAsDouble(), 3);
          double rotation = omega.getAsDouble();
          double td = 0.02;

          ChassisSpeeds speeds =
              fieldOriented
                  ? ChassisSpeeds.fromFieldRelativeSpeeds(
                      Xcontroller * swerveDrive.getMaximumChassisVelocity(),
                      Ycontroller * swerveDrive.getMaximumChassisVelocity(),
                      rotation * swerveDrive.getMaximumChassisAngularVelocity(),
                      pigeon.getRotation2d())
                  : new ChassisSpeeds(
                      Xcontroller * swerveDrive.getMaximumChassisVelocity(),
                      Ycontroller * swerveDrive.getMaximumChassisVelocity(),
                      rotation * swerveDrive.getMaximumChassisAngularVelocity());

          ChassisSpeeds discretize = ChassisSpeeds.discretize(speeds, td);
          state = swerveDrive.kinematics.toSwerveModuleStates(discretize);
          SwerveDriveKinematics.desaturateWheelSpeeds(state, DriveConsts.MAX_SPEED);
          modules = swerveDrive.getModules();

          for (int i = 0; i < state.length; i++) {
            modules[i].setDesiredState(state[i], true, true);
          }
        });
  }

  private void setupPathPlanner() {
    RobotConfig config;
    try {
      config = RobotConfig.fromGUISettings();
      AutoBuilder.configure(
          RobotConsts.CURRENT_ROBOT_MODE == RobotModes.REAL ? this::getPose : this::getPoseSim,
          RobotConsts.CURRENT_ROBOT_MODE == RobotModes.REAL
              ? this::resetOdometry
              : this::resetOdometrySim,
          this::getRobotRelativeSpeeds,
          (speeds, feedforwards) -> driveFieldOriented(speeds),
          new PPHolonomicDriveController(
              new PIDConstants(
                  DriveConsts.AUTO_TRANSLATION_kP,
                  DriveConsts.AUTO_TRANSLATION_kI,
                  DriveConsts.AUTO_TRANSLATION_kD), // Translation
              new PIDConstants(
                  DriveConsts.AUTO_ROTATION_kP,
                  DriveConsts.AUTO_ROTATION_kI,
                  DriveConsts.AUTO_ROTATION_kD) // Rotation
              ),
          config,
          () -> {
            var alliance = DriverStation.getAlliance();
            if (alliance.isPresent()) {
              return alliance.get() == DriverStation.Alliance.Red;
            }
            return false;
          },
          this);

      PathPlannerLogging.setLogActivePathCallback(
          (activePath) -> {
            Logger.recordOutput(
                DriveConsts.ACTIVE_TRACJECTORY_LOG_ENTRY,
                activePath.toArray(new Pose2d[activePath.size()]));
          });

      PathPlannerLogging.setLogTargetPoseCallback(
          (targetPose) -> {
            Logger.recordOutput(DriveConsts.TRAJECTORY_SETPOINT_LOG_ENTRY, targetPose);
          });

    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
}
