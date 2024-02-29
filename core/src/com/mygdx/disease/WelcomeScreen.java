package com.mygdx.disease;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.w3c.dom.Text;

public class WelcomeScreen extends Stage {
    Label title;
    ParameterSlider deathRateSlider,infectionRateSlider,incubationTimeSlider,sicknessTimeSlider,immunityTimeSlider
            ,movementSpeedSlider,vaccinationRateSlider,spreadRadiusSlider;
    public WelcomeScreen(){
        super(new ScreenViewport());
        Gdx.input.setInputProcessor(this);
        Table root = new Table();
        this.addActor(root);
        root.setFillParent(true);
        title = new Label("Simulation Settings", TextureManager.mSkin,"big");
        title.setFontScale(1.0f);
        root.add(title).expandX().top().padTop(30).padBottom(30);
        root.row().colspan(1).fillX();

        ImageTextButton startButton = new ImageTextButton("Begin", TextureManager.mSkin);
        startButton.setScale(0.8f);
        startButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                DiseaseSimulator.simulating = true;
                DiseaseSimulator.w = new World();
            }
        });
        root.add(startButton).width(200).center();

        root.row().colspan(1).fillX();


        Table diseaseTable = new Table(TextureManager.mSkin);
        diseaseTable.setBounds(0,200,100,100);
        root.add(diseaseTable).left().pad(50).fillY().colspan(1);

        Label diseaseHeader = new Label("Pathogen Settings", TextureManager.mSkin);
        diseaseHeader.setFontScale(1.5f);
        diseaseTable.add(diseaseHeader).expandX().left();

        Label behavioralSettingsLabel = new Label("Behavioral Settings", TextureManager.mSkin);
        behavioralSettingsLabel.setFontScale(1.5f);
        diseaseTable.add(behavioralSettingsLabel).right();
        diseaseTable.row();

        deathRateSlider = new ParameterSlider("Death Rate: ","This is " +
                "the probability that an infected human\nwith a normal immune system dies of the disease.",0.0f,1.0f){
            @Override
            public void variableModifier() {
               Parameters.deathRate = slider.getValue();
            }
        };
        diseaseTable.add(deathRateSlider).height(80).left();

        movementSpeedSlider = new ParameterSlider("Movemennt speed: ","This how fast the humans move in the simulation. " +
                "May affect\nthe rate of disease spread.",1.0f,5.0f){
            @Override
            public void variableModifier() {
                Parameters.moveSpeed = slider.getValue();
            }
        };
        movementSpeedSlider.slider.setValue(2.0f);
        movementSpeedSlider.update();
        diseaseTable.add(movementSpeedSlider).height(80).left();
        diseaseTable.row();
        infectionRateSlider = new ParameterSlider("Rate of infection: ","This is the probability that if an infected human\n" +
                "spent an entire day with another human they would be infected.",0.1f,1.0f){
            @Override
            public void variableModifier() {
                Parameters.infectiousnessPerDay = slider.getValue();
            }
        };
        diseaseTable.add(infectionRateSlider).height(80).left();


        vaccinationRateSlider = new ParameterSlider("Vaccination rate: ","If an individual is vaccinated, their immune system allows\nthem to beat the disease twice as fast.Vaccinated people\nare also less likely to spread disease.",0.0f,1.0f){
            @Override
            public void variableModifier() {
                Parameters.percentVaccinated = slider.getValue();
            }
        };
        vaccinationRateSlider.slider.setValue(2.0f);
        vaccinationRateSlider.update();
        diseaseTable.add(vaccinationRateSlider).height(80).left();
        diseaseTable.row();



        incubationTimeSlider = new ParameterSlider("Length of incubation period: ","This is the amount of time " +
                "before a person begins\nto show symptoms, but while they're still infectious.",1.0f,21.0f){
            @Override
            public void variableModifier() {
                Parameters.incubationTime = slider.getValue();
            }
        };
        incubationTimeSlider.slider.setValue(3.0f);
        incubationTimeSlider.update();
        diseaseTable.add(incubationTimeSlider).height(80).left();

        final CheckBox quarantineSetting = new CheckBox("Enable quarantine", TextureManager.mSkin);
        quarantineSetting.addListener(new TextTooltip("If checked, humans who realize that they are sick (not carriers, but sick)\nwill walk to a separated portion of the map.",DiseaseSimulator.tooltipManager,TextureManager.mSkin));
        quarantineSetting.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Parameters.quarantine = quarantineSetting.isChecked();
                System.out.println(Parameters.quarantine);
            }
        });
        diseaseTable.add(quarantineSetting).left();
        quarantineSetting.setFillParent(false);
        diseaseTable.row();

        sicknessTimeSlider = new ParameterSlider("Length of sickness period: ","This is the amount of time " +
                "after a person begins\n to show symptoms before they get cured.\nThey have a chance to pass away during this time period.",1.0f,21.0f){
            @Override
            public void variableModifier() {
                Parameters.sicknessTime = slider.getValue();
            }
        };
        sicknessTimeSlider.slider.setValue(3.0f);
        sicknessTimeSlider.update();
        diseaseTable.add(sicknessTimeSlider).height(80).left();

        final CheckBox masksSetting = new CheckBox("Enable masks", TextureManager.mSkin);
        masksSetting.addListener(new TextTooltip("Masks halve the radius that the disease is able to spread.",DiseaseSimulator.tooltipManager,TextureManager.mSkin));
        masksSetting.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Parameters.masks = masksSetting.isChecked();
                System.out.println(Parameters.quarantine);
            }
        });
        diseaseTable.add(masksSetting).left();
        masksSetting.setFillParent(false);

        diseaseTable.row();

        immunityTimeSlider = new ParameterSlider("Length of immunity period: ","After the infection takes its course,this is the time\n period " +
                "when the host is resistant to the pathogen, and can't get sick.",1.0f,21.0f){
            @Override
            public void variableModifier() {
                Parameters.curedTime = slider.getValue();
            }
        };
        immunityTimeSlider.slider.setValue(3.0f);
        immunityTimeSlider.update();
        diseaseTable.add(immunityTimeSlider).height(80).left();
        diseaseTable.row();

        spreadRadiusSlider = new ParameterSlider("Spread radius: ","Also affects how infectious the disease. This allows it so stay\nairborne for longer, allowing it to infect people further away.",1.0f,3.0f){
            @Override
            public void variableModifier() {
                Parameters.infectiousRadius = slider.getValue();
            }
        };
        spreadRadiusSlider.slider.setValue(1.5f);
        spreadRadiusSlider.update();
        diseaseTable.add(spreadRadiusSlider).height(80).left();
        diseaseTable.row();





    }

    public void resize(int width, int height){
        this.getViewport().update(width, height,true);
        this.getCamera().update();
    }
}
