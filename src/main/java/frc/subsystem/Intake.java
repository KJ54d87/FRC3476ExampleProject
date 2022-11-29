package frc.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.StatorCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;

import static frc.utility.Pneumatics.getPneumaticsHub;

public class Intake extends AbstractSubsystem {
    private static Intake instance = new Intake();
    private static final int INTAKE_SOLENOID_CHANNEL = 3;

    private static final int INTAKE_MOTOR_DEVICE_ID = 40;

    public static final double INTAKE_SPEED = -1; // Why is intaking -1?

    private final Solenoid intakeSol;
    private TalonFX intakeMotorFalcon;

    public Intake() {
        super(-1);
        intakeSol = getPneumaticsHub().makeSolenoid(INTAKE_SOLENOID_CHANNEL);
        intakeMotorFalcon = new TalonFX(INTAKE_MOTOR_DEVICE_ID);
        //IDK what these two bellow do, but they seem to set a limit for the motors apparently and I don't want to accidently blow something up.
        intakeMotorFalcon.configStatorCurrentLimit(new StatorCurrentLimitConfiguration(false, 40, 70, 0), 1000);
        intakeMotorFalcon.configOpenloopRamp(0.2, 1000);
    }

    public enum IntakeSolState {
        OPEN, CLOSE
    }


    @Override
    public void close() throws Exception {
        intakeSol.close();
        instance = new Intake();
    }

    /** returns true if the intake is extended, false if not **/
    private boolean solState(){
        return !intakeSol.get();
    }
    public synchronized void toggleSolState(){
        if (solState()){
            setIntakeSolState(IntakeSolState.CLOSE);
        } else {
            setIntakeSolState(IntakeSolState.OPEN);
        }
    }

    private void setIntakeSolState(IntakeSolState intakeSolState) {
        // lol who needs debugging logs (right?)
        switch (intakeSolState) {
            case OPEN:
                intakeSol.set(true);
                break;
            case CLOSE:
                intakeSol.set(false);
        }
    }

    private void setIntakeMotor(double speed) {
        intakeMotorFalcon.set(ControlMode.PercentOutput, speed);
    }

    public enum IntakeState {
        INTAKE, EJECT, OFF
    }

    public void setMotor(IntakeState intakeState){
        //I could use a switch, but we only have three states and I don't want the entire thing to be copy and paste
        if (intakeState == IntakeState.INTAKE){
            if (solState()){ // only run if intake is out
                setIntakeMotor(INTAKE_SPEED);
            }
        } else if (intakeState == IntakeState.EJECT){
            if (solState()) { // only run if intake is out
                setIntakeMotor(-INTAKE_SPEED);
            }
        } else if (intakeState == IntakeState.OFF){
            setIntakeMotor(0);
        }
    }


}
