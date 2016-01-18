package com.cargoexchange.cargocity.cargocity.models;

import android.text.Spanned;

/**
 * Created by root on 18/1/16.
 */
public class NavigationInstructionModel
{
    Spanned instruction;
    String maneuver;
    public NavigationInstructionModel(Spanned instruction,String maneuver)
    {
        this.instruction = instruction;
        this.maneuver = maneuver;
    }

    public Spanned getInstruction() {
        return instruction;
    }

    public void setInstruction(Spanned instruction) {
        this.instruction = instruction;
    }

    public String getManeuver() {
        return maneuver;
    }

    public void setManeuver(String maneuver) {
        this.maneuver = maneuver;
    }
}
