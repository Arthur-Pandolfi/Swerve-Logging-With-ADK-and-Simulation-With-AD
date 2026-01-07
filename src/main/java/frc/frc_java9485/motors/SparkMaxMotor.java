package frc.frc_java9485.motors;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj.DriverStation;

public class SparkMaxMotor implements SparkMaxMotorIO{

    SparkMax motor;
    SparkMaxConfig config;
    int id;
    boolean isFollower;
    String name;

    double speed = 0;
    double porcentage = 0;
    double position = 0;

    public SparkMaxMotor(int id, String name){
        this(id, name, false);
    }

    public SparkMaxMotor(int id, String name, boolean isFollower){
        this.id = id;
        this.isFollower = isFollower;
        this.name = name;
        this.motor = new SparkMax(id, SparkMax.MotorType.kBrushless);
        this.config = new SparkMaxConfig();
    }

    @Override
    public void setSpeed(double speeds) {
        if(speeds != speed){
            motor.set(speeds);
            this.speed = speeds;
        }
    }

    @Override
    public void setPorcentage(double porcentage) {
        if(porcentage != this.porcentage){
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
    public void setReference(double reference) {
        if(reference != getPosition()){
            motor.getClosedLoopController().setReference(reference, ControlType.kPosition);
        }
    }

    @Override
    public void setRampRate(double ramp) {
        config
        .closedLoopRampRate(ramp)
        .openLoopRampRate(ramp);

        if(DriverStation.isEnabled()){
            System.out.println("ERRO! o motor não pode mudar se a driverStation estiver ligada");
            return;
        } else{
            motor.configure(config, ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);
        }
    }

    @Override
    public double getVoltage() {
        return motor.getBusVoltage();
    }

    @Override
    public SparkMax getSpark(){
        return motor;
    }

    @Override
    public void followMotor(int id){
        if(isFollower){
            config.follow(id);
        }
        motor.configure(config, ResetMode.kNoResetSafeParameters, PersistMode.kPersistParameters);
    }
}
