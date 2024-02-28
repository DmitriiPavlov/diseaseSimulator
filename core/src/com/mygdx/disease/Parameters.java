package com.mygdx.disease;

import com.badlogic.gdx.math.MathUtils;

import static com.badlogic.gdx.math.MathUtils.log2;

public class Parameters {
    public static float worldWidth = 84;
    public static float worldHeight = 60;
    //from 0 to 1
    public static float percentVaccinated;

    //time in carrier phase
    //from 0 to 15
    public static float incubationTime = 5;
    //time in sick phase
    //from 0 to 15
    public static float sicknessTime = 12;
    //time spend in cured phase
    public static float curedTime = 50;

    //this is the length of a day in milliseconds
    public static long dayLength = 1000 * 1 ;

    //the probability that a person dies on a given day
    public static float deadlinessPerDay = 0.1f;

    public static float deadlinessPerFrame;

    //the probability that a person infects another person per day, if that person spent the entire day
    //in the other person's circle
    public static float infectiousnessPerDay = 0.9f;

    public static float infectiousnessPerFrame;
    public static float moveSpeed = 2;

    //the radius around each character where they are capable of infecting another character
    public static float infectiousRadius = 1;

    //determines whether the radius is doubled
    public static boolean masks;

    //determines whether a character runs away from others
    public static boolean quarantine;

    //dayLength, infectiousnessperday must all be initialized
    public static void computeInfectiousness(){
        //P(infection in one day) = 1 - P(not infection in one interaction)^numberofinteractions per day
        //1-P(infection) = P(not infection in one interaction)^numberofinteractions
        //log(1-P(infection) = numberofinteractions*log(P(not infection in one interaction)
        //we convert days to seconds
        // 1 day * dayLength in milliseconds * 1 second/ 1000 milliseconds * 60 interactions per second
        float numberOfInteractions = (float) (1 * dayLength * 1/1000 * 60);
        infectiousnessPerFrame = 1.0f - (float) Math.pow(1-infectiousnessPerDay,1.0f/numberOfInteractions);
        System.out.println(infectiousnessPerFrame);
    }

    //dayLength, deadlinessperday must all be initialized
    public static void computeDeadliness(){
        //P(infection) = 1 - P(not infection in one interaction)^numberofinteractions
        //1-P(infection) = P(not infection in one interaction)^numberofinteractions
        //log(1-P(infection) = numberofinteractions*log(P(not infection in one interaction)
        //we convert days to seconds
        // 1 day * dayLength in milliseconds * 1 second/ 1000 milliseconds * 60 interactions per second
        float numberOfInteractions = (float) (1 * dayLength * 1/1000 * 60);
        deadlinessPerFrame = 1.0f - (float) Math.pow(1-deadlinessPerDay,1.0f/numberOfInteractions);
    }

    public static void computeCuredTime(){
        curedTime += incubationTime + sicknessTime;
    }


}
