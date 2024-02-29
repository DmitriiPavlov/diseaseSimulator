package com.mygdx.disease;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.*;

public class ParameterSlider extends Table {
    public Slider slider;
    public Label text;
    public String labelText;
    public ParameterSlider(String labelText,String tooltipText, float min, float max){
        this.labelText = labelText;
        slider = new Slider(min,max,(max-min)/100,false, TextureManager.mSkin);
        text = new Label(labelText + slider.getValue(),TextureManager.mSkin);
        slider.setBounds(0,25.0f,140.0f,30.0f);
        text.setPosition(0,0);
        this.add(slider).left();
        this.row();
        this.add(text).left();
        text.addListener(new TextTooltip(tooltipText, DiseaseSimulator.tooltipManager,TextureManager.mSkin));
    }


    public  void variableModifier(){
    }

    public void act(float deltaTime){
        if (slider.isDragging()){
            text.setText(labelText + String.format("%.2f",slider.getValue()));
            variableModifier();
        }

    }

    public void update(){
        text.setText(labelText + String.format("%.2f",slider.getValue()));
        variableModifier();
    }
}
