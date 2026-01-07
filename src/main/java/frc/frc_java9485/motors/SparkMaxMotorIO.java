package frc.frc_java9485.motors;

import com.revrobotics.spark.SparkMax;

public interface SparkMaxMotorIO {
    
    void setSpeed(double speeds);

    void setPorcentage(double porcentage);

    double getPosition();

    double getRate();

    void setReference(double reference);

    void setRampRate(double ramp);

    double getVoltage();

    void followMotor(int id);

    SparkMax getSpark();


}
