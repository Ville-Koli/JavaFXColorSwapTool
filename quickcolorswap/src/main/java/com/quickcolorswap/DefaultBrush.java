package com.quickcolorswap;

public class DefaultBrush extends Brush{
    public DefaultBrush(){
        super(10);
    }
    /*
     * Function used to calculate the brush area
     * @return calculated area of brush
     */
    @Override
    public boolean[][] calculateBrushArea() {
        int brushSize = getBrushSize();
        boolean[][] brushArea = new boolean[brushSize][brushSize];
        // calculate a square of size brush size * brush size
        for(int x = 0; x < brushSize; ++x){
            for(int y = 0; y < brushSize; ++y){
                brushArea[x][y] = true;
            }
        }
        return brushArea;
    }
    /*
     * Uses brush and applies it to the mask while setting the applied parts to boolean set
     * @param Point point - current mouse position
     * @param boolean[][] mask - current mask where the brush is being applied
     * @param boolean set - sets brushed values to set
     */
    @Override
    public void applyBrushToMask(Point point, boolean[][] mask, boolean set){
        boolean[][] brushArea = getBrush();
        int brushSize = getBrushSize();
        int mask_width = mask.length;
        if(mask_width <= 0 || brushArea.length == 0){ return; }

        int mask_height = mask[0].length;
        int half = brushSize/2;
        for(int i = 0; i < brushSize; ++i){
            for(int j = 0; j < brushSize; ++j){
                int current_x = point.GetX() - half + i;
                int current_y = point.GetY() - half + j;
                if(brushArea[i][j]
                && 0<= current_x && current_x < mask_width
                && 0<= current_y && current_y < mask_height){
                    mask[current_x][current_y] = set;
                }
            }
        }
    }
}