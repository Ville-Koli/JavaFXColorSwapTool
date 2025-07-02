package com.quickcolorswap;

public abstract class Brush {
    private boolean[][] brushArea;
    private int brushSize;

    /*
     * Function used to calculate the brush area
     * @return calculated area of brush
     */
    public abstract boolean[][] calculateBrushArea();
    /*
     * Uses brush and applies it to the mask while setting the applied parts to boolean set
     * @param Point point - current mouse position
     * @param boolean[][] mask - current mask where the brush is being applied
     * @param boolean set - sets brushed values to set
     */
    public abstract void applyBrushToMask(Point point, boolean[][] mask, boolean set);

    public Brush(int brushSize){
        this.brushSize = brushSize;
        this.brushArea = calculateBrushArea();
    }
    /*
     * Function that gets brush area
     * @return boolean[][] - returns brush area
     */
    public boolean[][] getBrush(){
        return brushArea;
    }
    /*
     * Function that gets brush size
     * @return int - returns brush area
     */
    public int getBrushSize(){
        return brushSize;
    }
    /*
     * Function that updates brush
     */
    public void UpdateBrush(){
        brushArea = calculateBrushArea();
    }
    /*
     * Function that updates a brush size and brush area
     * @param int brushSize - new brush size
     */
    public void setBrushSize(int brushSize){
        this.brushSize = brushSize;
        UpdateBrush();
    }
}