package frc.frc_java9485.motors;

import com.revrobotics.ResetMode;
import com.revrobotics.PersistMode;
import com.revrobotics.spark.SparkMax;
import edu.wpi.first.wpilibj.DriverStation;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.config.SparkMaxConfig;

public class SparkMaxMotor implements SparkMaxMotorIO {

  SparkMax motor;
  SparkMaxConfig config;
  int id;
  boolean isFollower;
  
  double speed = 0;
  double porcentage = 0;
  double position = 0;

  public SparkMaxMotor(int id, String name) {
    this(id, name, false);
  }

  public SparkMaxMotor(int id, String name, boolean isFollower) {
    this.id = id;
    this.isFollower = isFollower;
    this.motor = new SparkMax(id, SparkMax.MotorType.kBrushless);
    this.config = new SparkMaxConfig();
  }

  @Override
  public void setSpeed(double speeds) {
    if (speeds != speed) {
      motor.set(speeds);
      this.speed = speeds;
    }
  }

  @Override
  public void setPorcentage(double porcentage) {
    if (porcentage != this.porcentage) {
      this.porcentage = porcentage;
      motor.set(porcentage);
    }
  }

  @Override
  public double getPosition() {
    return motor.getEncoder().getPosition();
  }

  @Override
  public double getRate() {
    return motor.getEncoder().getVelocity();
  }

  @Override
  public void setSetpoint(double setpoint) {
    if (setpoint != getPosition()) {
      motor.getClosedLoopController().setSetpoint(setpoint, ControlType.kPosition);
    }
  }

  @Override
  public void setRampRate(double ramp) {
    config.closedLoopRampRate(ramp).openLoopRampRate(ramp);

    if (DriverStation.isEnabled()) {
      System.out.println("ERRO! o motor não pode mudar se a driverStation estiver ligada");
      return;
    } else {
      motor.configure(config, ResetMode.kNoResetSafeParameters, PersistMode.kNoPersistParameters);
    }
  }

  @Override
  public double getVoltage() {
    return motor.getBusVoltage();
  }

  @Override
  public SparkMax getSpark() {
    return motor;
  }

  @Override
  public void followMotor(int id) {
    if (isFollower) {
      config.follow(id);
    }
    motor.configure(config, ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);
  }
}
