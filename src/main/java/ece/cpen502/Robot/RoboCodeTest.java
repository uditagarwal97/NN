package ece.cpen502.Robot;

import robocode.*;
import robocode.Robot;
import robocode.ScannedRobotEvent;
import robocode.control.BattlefieldSpecification;
import robocode.util.Utils;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import java.awt.*;
import java.awt.geom.Point2D;

public class RoboCodeTest extends AdvancedRobot {

    Boolean shouldFire = Boolean.TRUE;
    RobotStates CurrentState;
    RobotActions action;
    LookUpTable lut;
    QLearningAlgorithm learning;
    static double[] cumulative_reward_array=new double[1000];
    //LUT table initialization
    int[] total_states_and_actions=new int[8*6*4*4*4];
    String[][] LookUpTable=new String[total_states_and_actions.length][2];

    Double reward;

    public void run()
    {
        //set colors of robot
        setColors(Color.blue, Color.black, Color.red, Color.yellow, Color.red);

        //independent moving of gun and radar
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        CurrentState = new RobotStates();
        action = new RobotActions();
        lut = new LookUpTable();
        learning = new QLearningAlgorithm();
        reward = 0.0;

        while(true)
        {
            robotMovement();
            radarMovement();

            execute();
        }
    }

    public void robotMovement()
    {
        if (getNumRounds() > 10)
        {
            learning.ChangeToExplorationStrategy();
        }

        CurrentState.UpdateMyState(getX(), getY());
        RobotActions action = learning.SelectActionGreedy(lut, CurrentState);
        learning.Qlearn(lut, CurrentState, action, reward);

        /*
        action.SelectRandomAction();
        action.SelectRandomDistance();
        action.SelectRandomFireDecision();
         */

        shouldFire = action.FireDecision;

        switch(action.ActionType){
            case Forward:
                ahead(action.Distance);
                break;
            case Backward:
                back(action.Distance);
                break;
            case LeftForward:
                turnLeft(90);
                ahead(action.Distance);
                break;
            case RightForward:
                turnRight(90);
                ahead(action.Distance);
                break;
            case LeftBackward:
                turnLeft(90);
                back(action.Distance);
                break;
            case RightBackward:
                turnRight(90);
                back(action.Distance);
                break;
        }
    }

    public void radarMovement() {
        // Turn the radar if we have no more turn, starts it if it stops and at the start of round
        if (getRadarTurnRemaining() == 0.0)
            setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
    }

    public void onScannedRobot(ScannedRobotEvent e)
    {
        {
            CurrentState = new RobotStates();
            // First, set the current state:
            double myX = getX();
            double myY = getY();
            double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
            double enemyX = getX() + e.getDistance() * Math.sin(absoluteBearing);
            double enemyY = getY() + e.getDistance() * Math.cos(absoluteBearing);

            CurrentState.SetState(myX, myY, enemyX, enemyY, getEnergy());
        }

        if (shouldFire){
            // How much do we have to turn our gun to point towards the enemy?
            double angleToEnemy = getHeadingRadians() + e.getBearingRadians();
            double radarTurn = Utils.normalRelativeAngle( angleToEnemy - getRadarHeadingRadians() );
            double extraTurn = Math.min( Math.atan( 36.0 / e.getDistance() ), Rules.RADAR_TURN_RATE_RADIANS );

            // Find the minimum angle to turn the radar
            if (radarTurn < 0)
                radarTurn -= extraTurn;
            else
                radarTurn += extraTurn;

            //Turn the radar
            setTurnRadarRightRadians(radarTurn);

            // How to fire efficiently when you & your target is moving? Code lifted from: https://robowiki.net/wiki/Circular_Targeting
            double bulletPower = Math.min(3.0, getEnergy());
            double myX = getX();
            double myY = getY();
            double absoluteBearing = getHeadingRadians() + e.getBearingRadians();
            double enemyX = getX() + e.getDistance() * Math.sin(absoluteBearing);
            double enemyY = getY() + e.getDistance() * Math.cos(absoluteBearing);
            double oldEnemyHeading = 0; //correct this
            double enemyHeading = e.getHeadingRadians();
            double enemyHeadingChange = enemyHeading - oldEnemyHeading;
            double enemyVelocity = e.getVelocity();
            oldEnemyHeading = enemyHeading;

            double deltaTime = 0;
            double battleFieldHeight = getBattleFieldHeight(),
                    battleFieldWidth = getBattleFieldWidth();
            double predictedX = enemyX, predictedY = enemyY;
            while((++deltaTime) * (20.0 - 3.0 * bulletPower) <
                    Point2D.Double.distance(myX, myY, predictedX, predictedY)){
                predictedX += Math.sin(enemyHeading) * enemyVelocity;
                predictedY += Math.cos(enemyHeading) * enemyVelocity;
                enemyHeading += enemyHeadingChange;
                if(	predictedX < 18.0
                        || predictedY < 18.0
                        || predictedX > battleFieldWidth - 18.0
                        || predictedY > battleFieldHeight - 18.0){

                    predictedX = Math.min(Math.max(18.0, predictedX),
                            battleFieldWidth - 18.0);
                    predictedY = Math.min(Math.max(18.0, predictedY),
                            battleFieldHeight - 18.0);
                    break;
                }
            }
            double theta = Utils.normalAbsoluteAngle(Math.atan2(
                    predictedX - getX(), predictedY - getY()));

            setTurnRadarRightRadians(Utils.normalRelativeAngle(
                    absoluteBearing - getRadarHeadingRadians()));
            setTurnGunRightRadians(Utils.normalRelativeAngle(
                    theta - getGunHeadingRadians()));

            fire(1); //alter firepower with distance?
        }
    }

    public void onHitWall(HitWallEvent e)
    {
        reward += -2;
    }

    public void onBulletHit(BulletHitEvent e)
    {
        reward += e.getBullet().getPower() * 9;
    }

    public void onBulletMissed(BulletMissedEvent e)
    {
        reward += -e.getBullet().getPower();
    }

    public void onHitByBullet(HitByBulletEvent e)
    {
        double power = e.getBullet().getPower();
        reward += -(4 * power + 2 * (power - 1));
    }

    public void onHitRobot(HitRobotEvent e)
    {
        reward += -6.0;
    }

    public void saveCumulativeReward() {

        PrintStream w = null;
        try {
            w = new PrintStream(new RobocodeFileOutputStream(getDataFile("cumulative.txt")));
            for (int i=0;i<cumulative_reward_array.length;i++) {
                w.println(cumulative_reward_array[i]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            w.flush();
            w.close();
        }

    }//save

    public void saveLookUpTable() {

        PrintStream w = null;
        try {
            w = new PrintStream(new RobocodeFileOutputStream(getDataFile("LookUpTable.txt")));
            for (int i=0;i<LookUpTable.length;i++) {
                w.println(LookUpTable[i][0]+"    "+LookUpTable[i][1]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            w.flush();
            w.close();
        }

    }//save

    public void loadLookUpTable() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(getDataFile("LookUpTable.txt")));
        String cell = reader.readLine();
        try {
            int i=0;
            while (cell != null) {
                String[] splitLine = cell.split("    ");
                LookUpTable[i][0]=splitLine[0];
                LookUpTable[i][1]=splitLine[1];
                i++;
                cell= reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            reader.close();
        }
    }//load

}
