package ece.cpen502.Robot;

import org.picocontainer.annotations.Inject;
import robocode.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;



public class minRobot extends AdvancedRobot {
    @Inject
            RobotStates states;

    int[] action = new int[RobotActions.ActionTypes.values().length];
    int[] total_states_actions = new int[states.x.length * states.y.length * states.bearing_angle.length * states.distance_to_enemy.length * action.length];
    String[][] LUT=new String[total_states_actions.length][2];

    boolean greedy=true;
    public void run() {
        //set color
        setColors(Color.black, Color.black, Color.orange, Color.cyan, Color.blue);

        while (true) {

        }
    }
    //save to file
    public void save(){
        PrintStream w = null;
        try {
            w = new PrintStream(new RobocodeFileOutputStream(getDataFile("LookUpTable.txt")));
            for (int i=0;i<LUT.length;i++) {
                w.println(LUT[i][0]+"    "+LUT[i][1]);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            w.flush();
            w.close();
        }

    }

    public void load() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(getDataFile("LookUpTable.txt")));
        String line = reader.readLine();
        try {
            int r = 0;
            while (line != null) {
                String splitLine[] = line.split("    ");
                LUT[r][0]=splitLine[0];
                LUT[r][1]=splitLine[1];
                r=r+1;
                line= reader.readLine();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            reader.close();
        }
    }
}
