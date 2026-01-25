package frc.robot.subsystems.mechanism.intake;

import static edu.wpi.first.units.Units.Volts;
import org.littletonrobotics.junction.Logger;
import edu.wpi.first.units.measure.Voltage;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.frc_java9485.constants.IntakeConsts;
import frc.frc_java9485.motors.spark.SparkFlexMotor;
import frc.frc_java9485.motors.spark.SparkMaxMotor;
import frc.frc_java9485.utils.TunableControls.TunableProfiledController;

public class Intake extends SubsystemBase implements IntakeIO {

  private static Intake m_instance;

  private final SparkFlexMotor catchBall;
  private final SparkMaxMotor throwIntake;

  private final TunableProfiledController controller;

  private final Encoder throwIntakeEncoder;

  private double setpoint = 0;
  private double speed = 0;
  private boolean isCollecting = false;
  private double angulo = 0;
  private Voltage volts = Volts.of(0);

  private final IntakeInputsAutoLogged inputs;

  public static Intake getInstance() {
    if (m_instance == null) m_instance = new Intake();
    return m_instance;
  }

  private Intake() {
    catchBall = new SparkFlexMotor(IntakeConsts.CATCH_BALL_ID, "pega bola");
    throwIntake = new SparkMaxMotor(IntakeConsts.THROW_INTAKE_ID, "desce intake");

    controller = new TunableProfiledController(IntakeConsts.TUNABLE_CATCH_BALL_CONST);

    throwIntakeEncoder = new Encoder(IntakeConsts.A_ENCODER_CHANNEL,
                                     IntakeConsts.B_ENCODER_CHANNEL);
    throwIntakeEncoder.setDistancePerPulse(360.0/2048.0);
    throwIntakeEncoder.setReverseDirection(IntakeConsts.ENCODER_INVERTED);
    throwIntakeEncoder.reset();

    inputs = new IntakeInputsAutoLogged();
  }

  @Override
  public void periodic() {
    SmartDashboard.putNumber("Angulo:", throwIntakeEncoder.getDistance());
    updateInputs(inputs);
    Logger.processInputs("Intake", inputs);
  }

  @Override
  public void catchBall(double speed) {
    this.speed = speed;
    catchBall.setSpeed(speed);
  }

  @Override
  public void throwIntake(double angle) {
    this.setpoint = angle;
    controller.setGoal(angle);

    angulo = throwIntakeEncoder.getDistance();

    volts = Volts.of(controller.calculate(angulo));

    System.out.println("Angulo: " + angulo);
    System.out.println("Temperatura: " + throwIntake.getTemperature() + "ºC");
    System.out.println("voltagem: " + volts + "\n");

    throwIntake.setVoltage(volts);
  }

  @Override
  public double getSpeed() {
    return speed;
  }

  @Override
  public boolean isColecting() {
      return isCollecting;
  }

  @Override
  public double getVoltage() {
      return volts.in(Volts);
  }

  @Override
  public void updateInputs(IntakeInputs inputs) {
      inputs.speed = getSpeed();
      inputs.isColecting = isColecting();
      inputs.voltage = getVoltage();
  }
}
